package com.landis.breakdowncore.system.thermodynamics;

import com.landis.breakdowncore.BreakdownCore;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class Capability$Thermo {
    public static final ResourceLocation CAP_ID = new ResourceLocation(BreakdownCore.MODID,"thermo");
    public static final BlockCapability<IThermoBackground,Void> THERMO_BLOCK = BlockCapability.createVoid(CAP_ID, IThermoBackground.class);
}
