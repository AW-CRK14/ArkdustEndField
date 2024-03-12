package com.landis.breakdowncore.material;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
        dataG = false;
        Handler$Material handler = new Handler$Material();
        for(Consumer<Handler$Material> consumer : MHList){
            consumer.accept(handler);
        }
        MHList = null;
    }

    static Multimap<ResourceLocation,IMaterialFeature<?>> MF4M_ADDITION = HashMultimap.create();
    static Multimap<ResourceLocation,Holder<MaterialItemType>> MIT4MF_ADDITION = HashMultimap.create();
    static List<Pair<Holder<Item>, Supplier<ITypedMaterialObj>>> I2TMIPre = new ArrayList<>();
    static List<Pair<Pair<Holder<Material>,Holder<MaterialItemType>>,Supplier<ItemStack>>> M_MIT2IPre = new ArrayList<>();



    /**在所有注册任务全部完成后的内容。此时infoB将被设置为false。在这些内容结束后，release将被设置为true*/
    static ImmutableMap<Class<?>,MaterialFeatureHandle<?>> MF_CLASS2MFH;
    static ImmutableMap<Item,ITypedMaterialObj> I2TMI;
    static ImmutableMap<Material,ImmutableMap<MaterialItemType,ItemStack>> M_MIT2I;

    static void init(){
        infoB = false;

        Map<Class<?>,MaterialFeatureHandle<?>> a = new HashMap<>();
        for(MaterialFeatureHandle<?> handle : Registry$Material.MATERIAL_FEATURE){
            a.put(handle.clazz,handle);
        }
        MF_CLASS2MFH = ImmutableMap.copyOf(a);

        Map<Item,ITypedMaterialObj> b = new HashMap<>();
        for (Pair<Holder<Item>, Supplier<ITypedMaterialObj>> i : I2TMIPre){
            if(!b.containsKey(i.getA().value())){
                b.put(i.getA().value(),i.getB().get());
            }
        }
        I2TMI = ImmutableMap.copyOf(b);

        Map<Material,Map<MaterialItemType,ItemStack>> c = new HashMap<>();
        for(Pair<Pair<Holder<Material>, Holder<MaterialItemType>>, Supplier<ItemStack>> i : M_MIT2IPre){
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

        release = true;
    }

    public static <I extends IMaterialFeature<I>> MaterialFeatureHandle<I> getMFH(Class<I> c){
        if(release){
            return (MaterialFeatureHandle<I>) MF_CLASS2MFH.get(c);
        }
        LOGGER.error("Can't get MFH for class:{}, the maps hasn't present right now.",c);
        LOGGER.error("If you want to get the correctly MFH, please call this after RegistryEvent(Low priority).");
        throw new IllegalStateException("Map has not present. Flags right now:{dataG=" + dataG + ",infoB=" + infoB + ",release=" + release + "}");
    }
    private static final ImmutableMap<MaterialItemType,ItemStack> EMPTY = ImmutableMap.<MaterialItemType, ItemStack>builder().build();
    public static ITypedMaterialObj getMaterialInfo(Item item){
        ITypedMaterialObj obj = null;
        if(release){
            obj = I2TMI.get(item);
        }else {
            LOGGER.warn("I2TMI map has not present so the result may lead to some unexpected error.");
        }
        if(obj == null){
            if(item instanceof ITypedMaterialObj){
                return (ITypedMaterialObj) item;
            }
            return null;//也许之后会添加额外的事件处理
        }
        return obj;
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


}
