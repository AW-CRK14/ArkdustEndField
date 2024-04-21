package com.landis.arkdust.registry.render;

import com.landis.arkdust.Arkdust;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public class RenderTypeRegistry {
    public static void bootstrap(){}

    private static final ResourceLocation SPIRIT_PORTAL_BACKGROUND_RESOURCE = new ResourceLocation(Arkdust.MODID,"textures/extra/spirit_portal.png");
    private static final ResourceLocation SPIRIT_PORTAL_COVER_SOURCE = new ResourceLocation(Arkdust.MODID,"textures/extra/portal_cover.png");


    public static ShaderInstance SHADERINS_SPIRIT_PORTAL;
    public static final RenderStateShard.ShaderStateShard RENDERTYPE_SPIRIT_PORTAL = new RenderStateShard.ShaderStateShard(
            ()->SHADERINS_SPIRIT_PORTAL
    );
    public static RenderType SPIRIT_PORTAL = RenderType.create("arkdust_spirit_portal",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_SPIRIT_PORTAL)
                    .setTextureState(
                            RenderStateShard.MultiTextureStateShard.builder()
                                    .add(SPIRIT_PORTAL_BACKGROUND_RESOURCE, false, false)
                                    .add(SPIRIT_PORTAL_COVER_SOURCE, false, false)
                                    .build()
                    )
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
//                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .createCompositeState(false)
    );
}
