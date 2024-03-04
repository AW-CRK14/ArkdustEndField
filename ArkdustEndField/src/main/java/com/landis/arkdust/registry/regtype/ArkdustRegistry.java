package com.landis.arkdust.registry.regtype;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.system.world.weather.ClimateParameter;
import com.landis.arkdust.system.world.weather.Weather;
import com.landis.arkdust.system.world.weather.WeatherScheduler;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ArkdustRegistry {
    public static final Registry<ClimateParameter> CLIMATE_PARAMETER = new RegistryBuilder<>(Keys.CLIMATE_PARAMETER).sync(true).create();
    public static final Registry<Weather> WEATHER = new RegistryBuilder<>(Keys.WEATHER).sync(true).create();
    public static final Registry<WeatherScheduler.WeatherStateProvider> WEATHER_PROVIDER = new RegistryBuilder<>(Keys.WEATHER_PROVIDER).sync(true).create();


    public static class Keys{
        public static final ResourceKey<Registry<ClimateParameter>> CLIMATE_PARAMETER = create("climate_parameter");
        public static final ResourceKey<Registry<Weather>> WEATHER = create("weather");
        public static final ResourceKey<Registry<WeatherScheduler.WeatherStateProvider>> WEATHER_PROVIDER = create("weather_provider");


        public static <T> ResourceKey<Registry<T>> create(String name){
            return ResourceKey.createRegistryKey(new ResourceLocation(Arkdust.MODID,name));
        }
    }
}
