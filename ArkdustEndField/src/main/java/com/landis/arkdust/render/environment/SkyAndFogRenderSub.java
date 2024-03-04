package com.landis.arkdust.render.environment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ViewportEvent;

import java.util.ArrayList;
import java.util.List;

/**SkyAndFogRenderSub天空与云雾渲染监听器
 * 这个类用于控制额外的天空与云雾渲染。要推送一个渲染，您需要使用{@link SkyAndFogRenderSub#push(ISkyAndFogRenderer)}方法推送一个实现了指定接口的目标 {@link ISkyAndFogRenderer} <br>
 * 这个类包含了优先级与云雾水平等参数。在被推送后，目标将会按照优先级被插入列表，同一优先级目标会覆盖上一个目标 <br>
 * 对于天气控制的天空，雾距离与颜色的变化，将从优先级最高的目标(数值越大 优先级越高)开始向下遍历。当多个目标的{@link ISkyAndFogRenderer#blendPara()}达到总量0.99以上将会结算最终颜色。 <br>
 * 当一个实例不再需要被渲染时，您需要使用{@link SkyAndFogRenderSub#pull(ISkyAndFogRenderer)}方法拉出该目标。每次{@link SkyAndFogRenderSub#pull(ISkyAndFogRenderer)}或{@link SkyAndFogRenderSub#push(ISkyAndFogRenderer)}后将进行一次刷新，如果与上一次的结果不一致将开始一次新的过渡变化尝试。 <br>
 * 请不要直接对存储列表进行操作，这有可能导致渲染的错误。如果要获取内容，请使用{@link SkyAndFogRenderSub#copyRenders()} <br>
 * 在退出游戏时内容将进行一次清空。数据均不会被保存。应当只在客户端进行。
 * */
@Mod.EventBusSubscriber(Dist.CLIENT)
public class SkyAndFogRenderSub {
    private final static List<ISkyAndFogRenderer> RENDERERS = new ArrayList<>();

    public static List<ISkyAndFogRenderer> copyRenders(){
        return List.copyOf(RENDERERS);
    }


    /**用于推送一个新的渲染项目
     * @param renderer 你需要推送的项目
     * @return 该项目最终被推送到的索引位置。注意，该值在其它推送进行后可能会发生变化。仅供测试使用。
     * */
    public static int push(ISkyAndFogRenderer renderer){
        //TODO
        return 1;
    }


    /**用于拉出一个已有的渲染项目
     * @param renderer 你需要拉出的项目
     * @return 如果成功被拉出，将返回true，否则为false
     * */
    public static boolean pull(ISkyAndFogRenderer renderer){
        //TODO
        return false;
    }

    @SubscribeEvent
    public static void renderExtraFog(ViewportEvent.RenderFog event){
        LocalPlayer player = Minecraft.getInstance().player;
//        Biome biome = player.level.getBiome(player.blockPosition());
//        if(biome.getRegistryName().equals(BiomeRegistry.CW$FAULT_LINE.get().getRegistryName()) && (event.getInfo().getEntity().level.isRaining()||event.getInfo().getEntity().level.isThundering())){
//            if(counter<1000) counter++;
//        }else {
//            if(counter>0) counter--;
//        }
//        if(counter>0 && event.getType() == FogRenderer.FogType.FOG_TERRAIN){
//            RenderSystem.pushMatrix();
//
//            float base = 0.032F*counter + event.getFarPlaneDistance()*(1-counter/1000F);
//
//            RenderSystem.fogStart(base*0.75F);
//            RenderSystem.fogEnd(base);
//
//            RenderSystem.popMatrix();
//        }
    }
}
