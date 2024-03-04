package com.landis.arkdust.registry;

import com.landis.arkdust.Arkdust;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class DataAttachmentRegistry {
    public static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Arkdust.MODID);

    public static final DeferredHolder<AttachmentType<?>,AttachmentType<Integer>> HEAT = REGISTER.register("heat",()-> AttachmentType.builder(()->0).serialize(Codec.INT).build());
}
