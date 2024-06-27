package com.landis.breakdowncore.module.codec;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface NumberChecker {

    Codec<NumberChecker> CODEC = ExtraCodecs.xor(ValueExact.CODEC, ValueRange.CODEC)
            .xmap(n -> n.map(a -> a, b -> b), i -> i instanceof ValueExact l ? Either.left(l) : Either.right((ValueRange) i));

    boolean is(double value);

    Codec<? extends NumberChecker> codec();

    record ValueExact(double v) implements NumberChecker {
        public static final Codec<ValueExact> CODEC = ExtraCodecs.withAlternative(
                Codec.DOUBLE, Codec.DOUBLE.fieldOf("value").codec()
        ).xmap(ValueExact::new, ValueExact::v);

        @Override
        public boolean is(double value) {
            return value == v;
        }

        @Override
        public Codec<? extends NumberChecker> codec() {
            return CODEC;
        }

        @Override
        public String toString() {
            return "{" + v + "}";
        }
    }


    //    @SuppressWarnings("all")
    class ValueRange implements NumberChecker {
        public static final Codec<ValueRange> CODEC = RecordCodecBuilder.create(n -> n.group(
                Codec.DOUBLE.optionalFieldOf("min").forGetter(ins -> ins.valueA),
                Codec.DOUBLE.optionalFieldOf("max").forGetter(ins -> ins.valueB),
                Codec.BOOL.fieldOf("includeMin").orElse(true).forGetter(ins -> ins.includeA),
                Codec.BOOL.fieldOf("includeMax").orElse(false).forGetter(ins -> ins.includeB)
        ).apply(n, ValueRange::new));
        public final Optional<Double> valueA;
        public final Optional<Double> valueB;

        public final boolean includeA;
        public final boolean includeB;

        public ValueRange(Optional<Double> valueA, Optional<Double> valueB) {
            this(valueA, valueB, true, false);
        }

        public ValueRange(Optional<Double> valueA, Optional<Double> valueB, boolean includeA, boolean includeB) {
            if (valueA.isPresent() && valueB.isPresent()) {
                if (valueA.get() > valueB.get()) {
                    Optional<Double> d = valueB;
                    valueB = valueA;
                    valueA = d;
                } else if (valueA.get().equals(valueB.get())) {
                    includeA = includeB = true;
                }
            } else {
                if (valueA.isEmpty()) includeA = false;
                if (valueB.isEmpty()) includeB = false;
            }

            this.valueA = valueA;
            this.valueB = valueB;
            this.includeA = includeA;
            this.includeB = includeB;
        }

        @Override
        public boolean is(double value) {
            return (valueA.isEmpty() || value > valueA.get() || (includeA && value == valueA.get())) &&
                    (valueB.isEmpty() || value < valueB.get() || (includeB && value == valueB.get()));
        }

        @Override
        public Codec<? extends NumberChecker> codec() {
            return CODEC;
        }

        @Override
        public String toString() {
            return (includeA ? "[" : "(") +
                    (valueA.isPresent() ? valueA.get() : "-∞") +
                    "," +
                    (valueB.isPresent() ? valueB.get() : "+∞") +
                    (includeB ? "]" : ")");
        }
    }

    @Deprecated(forRemoval = true)
    public static final class RegistryTargetsCodec<R> extends MapCodec<RegistryTargetsCodec<R>.Result> {
        public final Registry<R> registry;
        public final String field;
        public final boolean mergeResults;
        public final boolean nonempty;

        public RegistryTargetsCodec(Registry<R> registry, String field, boolean mergeResults,
                                    boolean nonempty) {
            this.registry = registry;
            this.field = field;
            this.mergeResults = mergeResults;
            this.nonempty = nonempty;
        }

        @Override
        public <T> DataResult<Result> decode(DynamicOps<T> ops, MapLike<T> input) {

            final DataResult<Set<Holder<R>>> elements = NeoForgeExtraCodecs.singularOrPluralCodec(registry.holderByNameCodec(), field, field).decode(ops, input);

            final DataResult<Set<TagKey<R>>> tags = NeoForgeExtraCodecs.singularOrPluralCodec(TagKey.codec(registry.key()), field + "_tag", field + "_tag").decode(ops, input);

            if (!mergeResults && tags.result().isPresent() && elements.result().isPresent())
                return DataResult.error(() -> "Multi codecs decode successfully, but the instance required for only one.");

            if (tags.result().isEmpty() && elements.result().isEmpty())
                return nonempty ? DataResult.error(() -> "None codecs decode successfully, but the instance required for nonempty.") : DataResult.success(new Result(Optional.empty(), Optional.empty(), ImmutableSet.of()));

            ImmutableSet.Builder<Holder<R>> result = new ImmutableSet.Builder<>();
            elements.result().ifPresent(result::addAll);
            tags.result().ifPresent(n -> n.stream().map(registry::getTag).filter(Optional::isPresent).map(Optional::get).flatMap(HolderSet.ListBacked::stream).forEach(result::add));

            return DataResult.success(new Result(elements.result(), tags.result(), result.build()));


        }

        @Override
        public <T> RecordBuilder<T> encode(Result input, DynamicOps<T> ops, RecordBuilder<T> prefix) {

            final MapCodec<Set<TagKey<R>>> tags = NeoForgeExtraCodecs.singularOrPluralCodec(TagKey.codec(registry.key()), field + "_tag", field + "_tag");

            input.elements.ifPresent(holders -> {
                final MapCodec<Set<Holder<R>>> elements = NeoForgeExtraCodecs.singularOrPluralCodec(registry.holderByNameCodec(), field, field);
                elements.encode(holders, ops, prefix);
            });
            input.tags.ifPresent(holders -> tags.encode(holders, ops, prefix));

            return prefix;
        }

        @SuppressWarnings("all")
        public class Result {
            private final Optional<Set<Holder<R>>> elements;
            private final Optional<Set<TagKey<R>>> tags;
            public final ImmutableSet<Holder<R>> results;

            private Result(Optional<Set<Holder<R>>> elements, Optional<Set<TagKey<R>>> tags, ImmutableSet<Holder<R>> results) {
                this.elements = elements;
                this.tags = tags;
                this.results = results;
            }
        }


        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return null;
        }


        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (RegistryTargetsCodec) obj;
            return Objects.equals(this.registry, that.registry) &&
                    Objects.equals(this.field, that.field) &&
                    this.mergeResults == that.mergeResults &&
                    this.nonempty == that.nonempty;
        }

        @Override
        public int hashCode() {
            return Objects.hash(registry, field, mergeResults, nonempty);
        }

        @Override
        public String toString() {
            return "registryTargetsCodec[" +
                    "registry=" + registry + ", " +
                    "field=" + field + ", " +
                    "mergeResults=" + mergeResults + ", " +
                    "nonempty=" + nonempty + ']';
        }

    }
}
