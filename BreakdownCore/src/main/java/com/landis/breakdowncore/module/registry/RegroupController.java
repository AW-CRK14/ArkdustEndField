package com.landis.breakdowncore.module.registry;

import com.landis.breakdowncore.module.datagen.ExpandItemModelProvider;
import com.landis.breakdowncore.module.datagen.ExpandLanguageProvider;
import com.landis.breakdowncore.module.datagen.ExpandSpriteSourceProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RegroupController {
    public static final Logger LOGGER = LogManager.getLogger("BREA:RegGroup");
    public static final Runnable RN = () -> {
    };
    public final String modid;

    private RegroupController(String modid) {
        this.modid = modid;

        this.REG_ITEM = DeferredRegister.createItems(modid);
    }

    protected final DeferredRegister.Items REG_ITEM;
    protected final List<Consumer<ItemModelProvider>> ITEM_MODEL_DATAGEN = new ArrayList<>();
    protected final List<Consumer<ExpandLanguageProvider>> I18N_DATAGEN = new ArrayList<>();
    protected final List<Consumer<ExpandSpriteSourceProvider>> SPRITE_DATAGEN = new ArrayList<>();

    public ExpandItemModelProvider itemModelProvider;
    public ExpandLanguageProvider i18nProvider;
    public ExpandSpriteSourceProvider spriteProvider;

    public void registry(GatherDataEvent event) {//TODO
        DataGenerator generator = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        itemModelProvider = new ExpandItemModelProvider(output, modid, fileHelper) {
            public void registerModels() {
                super.registerModels();
                ITEM_MODEL_DATAGEN.forEach(c -> {
                    try {
                        c.accept(this);
                    } catch (NullPointerException e) {
                        LOGGER.warn("Can't find target texture when executing add item model datagen element. Ignored.");
                        LOGGER.warn("在添加物品模型数据生成元素时未能找到目标材质。已忽略此项目。");
                        LOGGER.warn("Cause by: ", e);
                    }
                });
            }
        };

        i18nProvider = new ExpandLanguageProvider(output, modid, "en_us") {
            public void addTranslations() {
                super.addTranslations();
                I18N_DATAGEN.forEach(c -> c.accept(this));
            }
        };

        spriteProvider = new ExpandSpriteSourceProvider(output, lookup, modid, fileHelper) {
            public void gather() {
                super.gather();
                SPRITE_DATAGEN.forEach(c -> c.accept(this));
            }
        };

        bootstrapExtraDatagen(event,this);

        generator.addProvider(event.includeClient(), itemModelProvider);
        generator.addProvider(event.includeClient(), i18nProvider);
        generator.addProvider(event.includeClient(), spriteProvider);

        ITEM_MODEL_DATAGEN.clear();
        I18N_DATAGEN.clear();
        SPRITE_DATAGEN.clear();
    }

    public abstract void bootstrapExtraDatagen(GatherDataEvent event, RegroupController i);


    public ItemRegroupBuilder<Item> item(String id) {
        return new ItemRegroupBuilder<>(REG_ITEM.registerSimpleItem(id));
    }

    public <T extends Item> ItemRegroupBuilder<T> item(String id, Supplier<T> itemSupplier) {
        return new ItemRegroupBuilder<>(REG_ITEM.register(id, itemSupplier));
    }

    public static RegroupController create(IEventBus bus, String modid) {
        return create(bus,modid,(event,controller)->{});
    }

    public static RegroupController create(IEventBus bus, String modid, BiConsumer<GatherDataEvent,RegroupController> bootstrapExtraHandler) {
        RegroupController controller = new RegroupController(modid){
            public void bootstrapExtraDatagen(GatherDataEvent event, RegroupController i) {
                bootstrapExtraHandler.accept(event,i);
            }
        };
        controller.REG_ITEM.register(bus);
        bus.addListener(GatherDataEvent.class, controller::registry);
        return controller;
    }

    public class ItemRegroupBuilder<T extends Item> {
        protected DeferredHolder<Item, T> root;
        private Runnable modelCache = RN;

        private Runnable i18nCache = RN;
        private Runnable spriteCache = RN;

        public ItemRegroupBuilder(DeferredHolder<Item, T> root) {
            this.root = root;
            i18nCache = () -> I18N_DATAGEN.add(e -> e.add(root.get(), false));
        }

        public ItemRegroupBuilder<T> noI18n() {
            i18nCache = RN;
            return this;
        }

        public ItemRegroupBuilder<T> addExplainPre() {
            i18nCache = () -> I18N_DATAGEN.add(e -> e.add(root.get(), true));
            return this;
        }

        public ItemRegroupBuilder<T> texture() {
            modelCache = () -> ITEM_MODEL_DATAGEN.add(gen -> gen.basicItem(root.getId()));
            return this;
        }

        public ItemRegroupBuilder<T> texture(String model) {
            modelCache = () -> ITEM_MODEL_DATAGEN.add(gen -> gen.getBuilder(root.getId().toString())
                    .parent(new ModelFile.UncheckedModelFile(model))
                    .texture("layer0", root.getId().withPrefix("item/")));
            return this;
        }

        public ItemRegroupBuilder<T> texture(String model, ResourceLocation texture) {
            modelCache = () -> {
                ITEM_MODEL_DATAGEN.add(gen -> gen.getBuilder(root.getId().toString())
                        .parent(new ModelFile.UncheckedModelFile(model))
                        .texture("layer0", texture));
            };
            if (!texture.getPath().startsWith("item/") && !texture.getPath().startsWith("block/"))
                this.spriteCache = () -> SPRITE_DATAGEN.add(gen -> gen.add(texture));

            return this;
        }

        public ItemRegroup<T> build() {
            modelCache.run();
            i18nCache.run();
            spriteCache.run();
            return new ItemRegroup<>(root);
        }
    }
}
