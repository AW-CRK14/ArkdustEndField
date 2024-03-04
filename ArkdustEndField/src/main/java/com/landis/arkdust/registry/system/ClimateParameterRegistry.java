package com.landis.arkdust.registry.system;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.registry.regtype.ArkdustRegistry;
import com.landis.arkdust.system.world.weather.ClimateParameter;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ClimateParameterRegistry {
    public static final DeferredRegister<ClimateParameter> REGISTER = DeferredRegister.create(ArkdustRegistry.CLIMATE_PARAMETER, Arkdust.MODID);

    public static final DeferredHolder<ClimateParameter,ClimateParameter> HUMIDITY = REGISTER.register("humidity",()->new ClimateParameter(40,5,0.5F,0.8F,0.2F));
    public static final DeferredHolder<ClimateParameter,ClimateParameter> WINDY = REGISTER.register("windy",()->new ClimateParameter(40,0,5F,0.8F,0.6F));
    public static final DeferredHolder<ClimateParameter,ClimateParameter> BIOACTIVITY = REGISTER.register("bioactivity",()->new ClimateParameter(100,-50,0.5F,0.4F));
    public static final DeferredHolder<ClimateParameter,ClimateParameter> ORIROCK_ACTIVITY = REGISTER.register("orirock_activity",()->new ClimateParameter(100,-100F,0F,0.6F,0.6F,0.2F));
}
