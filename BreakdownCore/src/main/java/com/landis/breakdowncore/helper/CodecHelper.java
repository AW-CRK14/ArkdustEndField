package com.landis.breakdowncore.helper;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.*;
import java.util.stream.Stream;

public class CodecHelper {

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
                return nonempty ? DataResult.error(() -> "None codecs decode successfully, but the instance required for nonempty.") : DataResult.success(new Result(Optional.empty(),Optional.empty(),ImmutableSet.of()));

            ImmutableSet.Builder<Holder<R>> result = new ImmutableSet.Builder<>();
            elements.result().ifPresent(result::addAll);
            tags.result().ifPresent(n -> n.stream().map(registry::getTag).filter(Optional::isPresent).map(Optional::get).flatMap(HolderSet.ListBacked::stream).forEach(result::add));

            return DataResult.success(new Result(elements.result(), tags.result(), result.build()));


        }

        @Override
        public <T> RecordBuilder<T> encode(Result input, DynamicOps<T> ops, RecordBuilder<T> prefix) {

            final MapCodec<Set<TagKey<R>>> tags = NeoForgeExtraCodecs.singularOrPluralCodec(TagKey.codec(registry.key()), field + "_tag", field + "_tag");

            input.elements.ifPresent(holders ->{
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
