package com.landis.breakdowncore.system.material;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;

public class Material {
    public static final Logger LOGGER = LogManager.getLogger("BREA:Material/M");
    public final ResourceLocation id;
    public final int x16color;
    public final ImmutableList<IMaterialFeature<?>> fIns;
    private ImmutableMap<MaterialFeatureType<?>,IMaterialFeature<?>> toFeature;
    private ImmutableSet<MaterialItemType> toTypes;
    public Material(ResourceLocation id, int x16color , IMaterialFeature<?>... fIns){
        this.id = id;
        this.x16color = x16color;
        ImmutableList.Builder<IMaterialFeature<?>> builder = new ImmutableList.Builder<>();
        builder.addAll(Arrays.asList(fIns));
        builder.addAll(System$Material.MF4M_ADDITION.get(id));
        this.fIns = builder.build();
    }

    public ImmutableMap<MaterialFeatureType<?>,IMaterialFeature<?>> getOrCreateFeatures(){
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

    public IMaterialFeature<?> getFeature(MaterialFeatureType<?> handle){
        return getOrCreateFeatures().get(handle);
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
