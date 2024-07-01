package com.landis.breakdowncore.module.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.landis.breakdowncore.helper.StringHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class ExpandLanguageProvider implements DataProvider, IAgencyProvider<ExpandLanguageProvider>{
    public static final Logger LOGGER = LogManager.getLogger("BREA:Datagen:Language");
    private final Map<String, String> data = new TreeMap<>();
    public final PackOutput output;
    public final String modid;
    public final String locale;

    private final List<ExpandLanguageProvider> agency = new ArrayList<>();

    public ExpandLanguageProvider(PackOutput output, String modid, String locale) {
        this.output = output;
        this.modid = modid;
        this.locale = locale;
    }

    @Override
    public void addAgency(ExpandLanguageProvider instance) {
        agency.add(instance);
    }

    @Override
    public List<ExpandLanguageProvider> getAgency() {
        return agency;
    }

    @Override
    public void execute(ExpandLanguageProvider instance) {
        instance.addTranslations();
        data.putAll(instance.data);
    }

    public void addTranslations(){
        agency();
    };

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        addTranslations();

        Path path = this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(this.modid).resolve("lang").resolve(this.locale + ".json");
        JsonObject json = new JsonObject();

        if (Files.exists(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                JsonObject obj = new Gson().fromJson(reader, JsonObject.class);
                obj.asMap().forEach(json::add);
            } catch (IOException e) {
                LOGGER.warn("Can't read info from existed file:" + e);
            }
        }

        data.forEach(json::addProperty);

        return DataProvider.saveStable(cache, json, path);
    }

    @Override
    public @NotNull String getName() {
        return "Languages: " + locale + " for mod: " + modid;
    }

    public void add(Block block, boolean addExplains) {
        add(block, StringHelper.convertToTitleCase(BuiltInRegistries.BLOCK.getKey(block).getPath().toString()));
        if (addExplains) {
            add(block.getDescriptionId() + ".explain", " ");
            add(block.getDescriptionId() + ".way", " ");
        }
    }

    public void add(Item item, boolean addExplains) {
        add(item, StringHelper.convertToTitleCase(BuiltInRegistries.ITEM.getKey(item).getPath().toString()));
        if (addExplains) {
            add(item.getDescriptionId() + ".explain", " ");
            add(item.getDescriptionId() + ".way", " ");
        }
    }

    public void add(Block key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void add(Item key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void add(Enchantment key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void add(MobEffect key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void add(EntityType<?> key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void add(String key, String value) {
        String fall = data.put(key, value);
        if (fall != null)
            LOGGER.warn("Duplicate translation key(key={}) was found. The new one(value={}) will cover the old one(value={}).", key, value, fall);
    }
}
