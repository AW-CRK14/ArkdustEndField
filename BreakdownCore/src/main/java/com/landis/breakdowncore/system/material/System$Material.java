package com.landis.breakdowncore.system.material;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.landis.breakdowncore.BreakdownCore;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**System$Material<br>
 * System$Material是Material系统的额外内容总控中心，用于统一管理额外处理与额外的表生成。*/
public class System$Material {
    public static final Logger LOGGER = LogManager.getLogger("BREA:Material:SystemController");





    /**以下内容为flag标志，值为true时表示阶段可用。其具体作用请参考introduction.md文件。*/
    public static boolean dataGatherStageFlag() {
        return dataG;
    }
    static boolean dataG = true;

    public static boolean infoBuildStageFlag() {
        return infoB;
    }
    static boolean infoB = true;

    public static boolean releaseFlag() {
        return release;
    }
    static boolean release = false;



    /**额外处理器({@link Handler$Material})的统一控制区*/
    private static List<Consumer<Handler$Material>> MHList = new ArrayList<>();
    public static void addMaterialExtraHandle(Consumer<Handler$Material> consumer){
        if(dataG){
            MHList.add(consumer);
        }else{
            LOGGER.error("Can not add MaterialExtraHandle consumer as the dataGather flag is false. These should be done in ModLoading stage or in MFLCommonSetupEvent.");
            LOGGER.error(new IllegalStateException("Flags right now:{dataG=" + dataG + ",infoB=" + infoB + ",release=" + release + "}"));
        }
    }
    static void gatherData(){
        if(MHList == null) return;
        dataG = false;
        Handler$Material handler = new Handler$Material();
        for(Consumer<Handler$Material> consumer : MHList){
            consumer.accept(handler);
        }
        MHList = null;
    }

    static Multimap<ResourceLocation,IMaterialFeature<?>> MF4M_ADDITION = HashMultimap.create();
    static Multimap<ResourceLocation,Holder<MaterialItemType>> MIT4MF_ADDITION = HashMultimap.create();
    static List<Pair<Holder<Item>, Supplier<ITypedMaterialObj>>> I2TMI_PRE = new ArrayList<>();
    static List<Pair<Pair<Holder<Material>,Holder<MaterialItemType>>,Supplier<ItemStack>>> M_MIT2I_PRE = new ArrayList<>();



    /**在所有注册任务全部完成后的内容。此时infoB将被设置为false。在这些内容结束后，release将被设置为true*/
    static ImmutableMap<Class<?>,MaterialFeatureType<?>> MF_CLASS2MFH;
    static ImmutableMap<Item,ITypedMaterialObj> I2TMI;
    static ImmutableMap<Material,ImmutableMap<MaterialItemType,ItemStack>> M_MIT2I;
    static ImmutableMap<Material, TextureAtlasSprite> M2TEXTURE;

    static void init(){
        infoB = false;

        Map<Class<?>,MaterialFeatureType<?>> a = new HashMap<>();
        for(MaterialFeatureType<?> handle : Registry$Material.MATERIAL_FEATURE){
            a.put(handle.clazz(),handle);
        }
        MF_CLASS2MFH = ImmutableMap.copyOf(a);

        Map<Item,ITypedMaterialObj> b = new HashMap<>();
        for (Pair<Holder<Item>, Supplier<ITypedMaterialObj>> i : I2TMI_PRE){
            if(!b.containsKey(i.getA().value())){
                b.put(i.getA().value(),i.getB().get());
            }
        }
        I2TMI = ImmutableMap.copyOf(b);

        Map<Material,Map<MaterialItemType,ItemStack>> c = new HashMap<>();
        for(Pair<Pair<Holder<Material>, Holder<MaterialItemType>>, Supplier<ItemStack>> i : M_MIT2I_PRE){
            Material m = i.getA().getA().value();
            MaterialItemType t = i.getA().getB().value();
            ItemStack s = i.getB().get();

            if(c.containsKey(m)){
                Map<MaterialItemType,ItemStack> mi = c.get(m);
                if(!mi.containsKey(t)){
                    mi.put(t,s);
                }
            }else {
                Map<MaterialItemType,ItemStack> mi = new HashMap<>();
                mi.put(t,s);
                c.put(m,mi);
            }
        }
        ImmutableMap.Builder<Material,ImmutableMap<MaterialItemType,ItemStack>> builder = new ImmutableMap.Builder<>();
        c.forEach((m,mit)-> builder.put(m,ImmutableMap.copyOf(mit)));
        M_MIT2I = builder.build();

        MF4M_ADDITION = null;
        MIT4MF_ADDITION = null;
        M_MIT2I_PRE = null;
        I2TMI_PRE = null;

        release = true;
    }

    static void initTexture(TextureAtlas atlas){
        TextureAtlasSprite missing = atlas.missingSprite;

        Map<Material,TextureAtlasSprite> d = new HashMap<>();
        TextureAtlasSprite sprite;
        for(Material material : Registry$Material.MATERIAL){
            sprite = atlas.getSprite(material.id.withPath(id -> "brea/material/" + id));
            if(sprite.equals(missing)){
                sprite = atlas.getSprite(new ResourceLocation(BreakdownCore.MODID,"brea/material/missing"));
            }
            d.put(material,sprite);
        }
        M2TEXTURE = ImmutableMap.copyOf(d);
    }


    public static <I extends IMaterialFeature<I>> MaterialFeatureType<I> getMFH(Class<I> c){
        if(release){
            return (MaterialFeatureType<I>) MF_CLASS2MFH.get(c);
        }
        LOGGER.error("Can't get MFH for class:{}, the maps hasn't present right now.",c);
        LOGGER.error("If you want to get the correctly MFH, please call this after RegistryEvent(Low priority).");
        throw new IllegalStateException("Map has not present. Flags right now:{dataG=" + dataG + ",infoB=" + infoB + ",release=" + release + "}");
    }
    private static final ImmutableMap<MaterialItemType,ItemStack> EMPTY = ImmutableMap.<MaterialItemType, ItemStack>builder().build();
    public static @Nullable ITypedMaterialObj getMaterialInfo(Item item){
        if(!release){
            LOGGER.warn("I2TMI map has not present so the result may lead to some unexpected error.");
            return item instanceof ITypedMaterialObj i ? i : null;
        }
        return I2TMI.getOrDefault(item,item instanceof ITypedMaterialObj i ? i : null);
    }
    public static ItemStack getFromMAndMIT(Material material,MaterialItemType type){
        ItemStack itemStack = null;
        if(release){
            itemStack = M_MIT2I.getOrDefault(material,EMPTY).get(type);
        }else {
            LOGGER.warn("M_MIT2I map has not present so the result may lead to some unexpected error.");
        }
        if(itemStack == null){
            itemStack = material.createItem(type);
            if(itemStack == null){
                return type.createItem(material);
            }
            return itemStack;
        }
        return itemStack;
    }

    public static TextureAtlasSprite getTexture(Material material){
        if(release){
            return M2TEXTURE.get(material);
        }else {
            LOGGER.warn("M2TEXTURE map has not present.");
        }
        return null;
    }



    public static ResourceLocation basicModel(ResourceLocation original){
        return MIT_BASIC_MODEL_LOCATION.apply(original);
    }
    public static ModelResourceLocation trans2ModelLocation(ResourceLocation original){
        return new ModelResourceLocation(original,"inventory");
    }
    public static final UnaryOperator<ResourceLocation> MIT_BASIC_MODEL_LOCATION = location -> location.withPrefix("mit_basic/");


    public static ResourceLocation combineForAtlasID(Material material, MaterialItemType type){
        return new ResourceLocation("brea" + material.id.getNamespace() + "_" + type.id.getNamespace(),material.id.getPath() + "_" + type.id.getPath());
    }
}
