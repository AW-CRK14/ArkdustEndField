package com.landis.arkdust.datagen;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.registry.CreativeTabRegistry;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public record ItemExplainGen(PackOutput output) implements DataProvider {
    public static final List<Pair<DeferredItem<?>, Boolean>> ITEMS = new ArrayList<>();
    public static final List<Pair<DeferredBlock<?>, Boolean>> BLOCKS = new ArrayList<>();

    public static void addItem(DeferredItem<?> item, boolean explain) {
        ITEMS.add(Pair.of(item, explain));
    }

    public static void addBlock(DeferredBlock<?> block, boolean explain) {
        BLOCKS.add(Pair.of(block, explain));
    }

    public static final Logger LOGGER = LogManager.getLogger(Arkdust.getLogName("DataGen.I18nTrans"));

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {

        if (!ITEMS.isEmpty() || !BLOCKS.isEmpty())
            return save(cache, this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(Arkdust.MODID).resolve("lang").resolve("en_us.json"));

        return CompletableFuture.allOf();
    }

    @Override
    public String getName() {
        return "Languages: en_us for mod: Arkdust (WithExplain)";
    }

    private CompletableFuture<?> save(CachedOutput cache, Path target) {
        JsonObject json = new JsonObject();


        for (Pair<DeferredItem<?>, Boolean> pair : ITEMS) {
            json.addProperty(pair.getFirst().get().getDescriptionId(), defaultName(pair.getFirst().getId().getPath()));
            if (pair.getSecond()) {
                json.addProperty("explain." + pair.getFirst().get().getDescriptionId(), pair.getFirst().getId().getPath());
                json.addProperty("explain." + pair.getFirst().get().getDescriptionId() + ".way", pair.getFirst().getId().getPath() );
            }
        }
        for (Pair<DeferredBlock<?>, Boolean> pair : BLOCKS) {
            json.addProperty(pair.getFirst().get().getDescriptionId(), defaultName(pair.getFirst().getId().getPath()));
            if (pair.getSecond()) {
                json.addProperty("explain." + pair.getFirst().get().getDescriptionId(), pair.getFirst().getId().getPath());
                json.addProperty("explain." + pair.getFirst().get().getDescriptionId() + ".way", pair.getFirst().getId().getPath());
            }
        }

        json.addProperty("explain.arkdust.util.way","§4Way To Get:");
        json.addProperty("explain.arkdust.util.explain","§4Explanation:");
//        json.addProperty("explain.arkdust.util.test","§4TEST:");

        //tab trans
        for(DeferredHolder<CreativeModeTab, ? extends CreativeModeTab> reg : CreativeTabRegistry.TABS.getEntries()){
            json.addProperty("tab.arkdust." + reg.getId().getPath(),defaultTab(reg));
        }



        //extend
        if(Files.exists(target)){
            try (BufferedReader reader = Files.newBufferedReader(target)){
                JsonObject obj = new Gson().fromJson(reader,JsonObject.class);
                for(Map.Entry<String, JsonElement> i : obj.entrySet()){
                    json.add(i.getKey(),i.getValue());
                }
            } catch (IOException e) {
                LOGGER.warn("Can't read info from existed file:" + e);
            }
        }

        return DataProvider.saveStable(cache, json, target);
    }

    private static String defaultName(String regName){
        StringBuilder sb=new StringBuilder();
        for(String s:regName.replace("_"," ").split(" ")) {
            sb.append(s.substring(0, 1).toUpperCase() + s.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    private static String defaultTab(DeferredHolder<CreativeModeTab, ? extends CreativeModeTab> reg){
        return "Arkdust: " + defaultName(reg.getId().getPath());
    }
}
