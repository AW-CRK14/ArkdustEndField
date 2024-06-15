package com.landis.breakdowncore.system.weather;

import com.landis.breakdowncore.BreakdownCore;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class Registry$Weather {
    public static final Registry<ClimateParameter> CLIMATE_PARAMETER = new RegistryBuilder<>(WeatherReg.CLIMATE_PARAMETER).sync(true).create();
    public static final Registry<Weather> WEATHER = new RegistryBuilder<>(WeatherReg.WEATHER).sync(true).create();
    public static final Registry<WeatherScheduler.WeatherStateProvider> WEATHER_PROVIDER = new RegistryBuilder<>(WeatherReg.WEATHER_PROVIDER).sync(true).create();

    public static class WeatherReg{
        public static final ResourceKey<Registry<ClimateParameter>> CLIMATE_PARAMETER = create("climate_parameter");
        public static final ResourceKey<Registry<Weather>> WEATHER = create("weather");
        public static final ResourceKey<Registry<WeatherScheduler.WeatherStateProvider>> WEATHER_PROVIDER = create("weather_provider");


        public static <T> ResourceKey<Registry<T>> create(String name){
            return ResourceKey.createRegistryKey(new ResourceLocation(BreakdownCore.MODID,name));
        }
    }
}
