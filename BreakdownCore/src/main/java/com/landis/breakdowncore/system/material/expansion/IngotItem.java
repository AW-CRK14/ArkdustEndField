package com.landis.breakdowncore.system.material.expansion;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.system.material.Material;
import com.landis.breakdowncore.system.material.TypedMaterialItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class IngotItem extends TypedMaterialItem {
    public final Material material;
    public IngotItem(Material material) {
        super(Registries.MaterialReg.INGOT);
        this.material = material;
    }

    @Override
    public Optional<Material> getMaterial(ItemStack stack) {
        if(stack.is(this)){
            return Optional.of(material);
        }
        return Optional.empty();
    }

    @Override
    public ResourceLocation getMaterialId(ItemStack stack) {
        if(stack.is(this)){
            return material.id;
        }
        return new ResourceLocation(BreakdownCore.MODID,"fallback");
    }

    @Override
    public void setMaterial(ItemStack stack, Material material) {
    }
}
