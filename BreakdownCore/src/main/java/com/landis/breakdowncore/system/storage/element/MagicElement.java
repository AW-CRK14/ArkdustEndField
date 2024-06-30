package com.landis.breakdowncore.system.storage.element;

import net.minecraft.resources.ResourceLocation;

public class MagicElement {
    private final String name;
    private final ResourceLocation texture;

    public MagicElement(String name, ResourceLocation texture) {
        this.name = name;
        this.texture = texture;
    }

    public String getName() {
        return name;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

}
