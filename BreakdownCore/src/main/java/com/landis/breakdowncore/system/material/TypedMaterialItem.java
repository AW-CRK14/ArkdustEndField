package com.landis.breakdowncore.system.material;

import com.landis.breakdowncore.Registries;
import com.landis.breakdowncore.module.textures.MutableTextureBakedModelWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**TypedMaterialItem材料类型物品<br>
 * 材料类型物品是一个独立的物品，其内部包含的nbt数据将使其在不同材料条件下变成不同的样式。也就是说，所有这个形态的材料物品都是同一个物品。<br>
 * 因此请不要直接通过物品判断来决定它是否为你需要的物品。这有可能导致，比如说，你需要下届合金齿轮，但匹配到一个地狱岩齿轮。
 * */
public class TypedMaterialItem extends Item implements ITypedMaterialObj{
    public final Supplier<? extends MaterialItemType> type;
    private Renderer renderer;

    public TypedMaterialItem(Supplier<? extends MaterialItemType> type) {
        super(new Properties().fireResistant());
        this.type = type;
    }

    public void setMaterial(ItemStack itemStack,Material material){
        if(itemStack.is(this)){
            itemStack.getOrCreateTag().putString("brea_mat",material.id.toString());
        }
    }


    @Override
    public ResourceLocation getMaterialId(ItemStack stack) {
        if(stack.is(this)){
            return new ResourceLocation(stack.getOrCreateTag().getString("brea_mat"));
        }
        return null;
    }

    @Override
    public MaterialItemType getMIType() {
        return type.get();
    }

    public void initializeClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
        Renderer r = new Renderer(TypedMaterialItem.this);
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return r;
            }
        });
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public static class Renderer extends BlockEntityWithoutLevelRenderer {
        public final TypedMaterialItem item;
        private BakedModel basic;
        private MutableTextureBakedModelWrapper<? extends BakedModel> cache;
        public Renderer(TypedMaterialItem item) {
            super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
            this.item = item;
        }

        public BakedModel getBasicModel() {
            if(this.basic == null){
                this.basic = Minecraft.getInstance().getModelManager().getModel(BuiltInRegistries.ITEM.getKey(item));
            }
            return basic;
        }

        public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
            if(pStack.getItem() == this.item){
                MaterialItemType type = item.getMIType();
                Material material = item.getMaterial(pStack).orElse(Registries.MaterialReg.FALLBACK.get());
                ModelManager manager = Minecraft.getInstance().getModelManager();
                BakedModel coverModel = manager.getModel(material.id.withPath(s -> "brea/matem/" + s + type.id.getPath()));
                if(!coverModel.equals(manager.getMissingModel())){
                    Minecraft.getInstance().getItemRenderer().render(pStack,pDisplayContext,false,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,coverModel);
                }else{
                    if(cache == null){
                        cache = new MutableTextureBakedModelWrapper<>(getBasicModel(),true);
                    }

                    cache.setTexture(System$Material.getTexture(material));
                    Minecraft.getInstance().getItemRenderer().render(pStack,pDisplayContext,false,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,cache);
                    BakedModel extra = System$Material.getCoverModel(type);
                    if(extra!=null) {
                        Minecraft.getInstance().getItemRenderer().render(pStack, pDisplayContext, false, pPoseStack, pBuffer, pPackedLight, pPackedOverlay + 1, extra);
                    }
                }
            }
        }
    }
}
