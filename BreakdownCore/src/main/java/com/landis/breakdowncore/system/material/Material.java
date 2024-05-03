package com.landis.breakdowncore.system.material;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class Material {
    public static final Logger LOGGER = LogManager.getLogger("BREA:Material/M");
    public final ResourceLocation id;
    public final int x16color;
    public final boolean intermediateProduct;
    public final ImmutableList<IMaterialFeature<?>> fIns;
    public final ImmutableSet<ResourceLocation> featureIDs;
    private ImmutableMap<MaterialFeatureType<? extends IMaterialFeature<?>>,IMaterialFeature<?>> toFeature;
    private ImmutableSet<MaterialItemType> toTypes;

    public Material(ResourceLocation id, int x16color, boolean isIntermediateProduct, IMaterialFeature<?>... fIns){
        this.id = id;
        this.x16color = x16color;
        List<IMaterialFeature<?>> l = new ArrayList<>();
        l.addAll(Arrays.asList(fIns));
        l.addAll(System$Material.MF4M_ADDITION.get(id));

        HashSet<ResourceLocation> fids = (HashSet<ResourceLocation>) l.stream().map(i -> i.getType().getId()).collect(Collectors.toSet());
        List<Integer> indexes = new ArrayList<>();

        for(int i = 0 ; i < l.size() ; i++){
            IMaterialFeature<?> m = l.get(i);
            if(m.dependencies() != null){
                boolean flag = false;
                for(ResourceLocation d : m.dependencies()){
                    if(!fids.contains(d)){
                        LOGGER.warn("Unable to find depended MaterialFeature(id={}) in material(id={}), this feature will be skipped. Required by MaterialFeature(id={}).",d,this.id,m.getType().getId());
                        flag = true;
                    }
                };
                if(flag){
                    fids.remove(m.getType().getId());
                    indexes.add(i);
                }
            }
        }

        for(int index : indexes){
            l.set(index,null);
        }
        this.featureIDs = ImmutableSet.copyOf(fids.iterator());
        this.fIns = ImmutableList.copyOf(l.stream().filter(Objects::nonNull).toList());
        this.intermediateProduct = isIntermediateProduct;
    }

    public Material(ResourceLocation id, int x16color, IMaterialFeature<?>... fIns){
        this(id,x16color,false,fIns);
    }

    public Material(ResourceLocation id, boolean isIntermediateProduct, IMaterialFeature<?>... fIns){
        this(id,0xEBEEF0,isIntermediateProduct,fIns);
    }

    public ImmutableMap<MaterialFeatureType<? extends IMaterialFeature<?>>,IMaterialFeature<?>> getOrCreateFeatures(){
        if(toFeature == null){
            if(!System$Material.release){
                LOGGER.warn("Can't get CLASS2MFH map as it's not exist right now. It will be automatically created in method System$Material#init()");
                LOGGER.warn("Details: called by Material(id={})",id);
                return null;
            }
            ImmutableMap.Builder<MaterialFeatureType<?>,IMaterialFeature<?>> builder = new ImmutableMap.Builder<>();
            for(IMaterialFeature<?> feature : fIns){
                builder.put(System$Material.getMFH(feature.getClass()),feature);
            }
            toFeature = builder.build();
        }
        return toFeature;
    }

    public <I extends IMaterialFeature<I>> IMaterialFeature<I> getFeature(MaterialFeatureType<I> handle){
        return (IMaterialFeature<I>) getOrCreateFeatures().get(handle);
    }

    public ImmutableSet<MaterialItemType> getOrCreateTypes(){
        if(toTypes == null){
            ImmutableSet.Builder<MaterialItemType> builder = new ImmutableSet.Builder<>();
            for(IMaterialFeature<?> feature : fIns){
                builder.addAll(feature.forItemTypes());
            }
            toTypes = builder.build();
        }
        return toTypes;
    }

    //当返回值为null时，将会继续尝试使用对应的ItemType获取结果。
    public @Nullable ItemStack createItem(MaterialItemType type){
        if(getOrCreateTypes().contains(type)){
            return null;
        }
        return new ItemStack(Items.AIR);
    }

    @Override
    public String toString() {
        return super.toString() + "{id=" + id + "}";
    }
}
