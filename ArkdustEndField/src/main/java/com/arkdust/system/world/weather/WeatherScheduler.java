package com.arkdust.system.world.weather;

import com.arkdust.registry.regtype.ArkdustRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.common.util.INBTSerializable;

/**WeatherScheduler天气计划器<br>
 * 天气计划器是连接气候参数与天气的中间控制器。在一条天气控制线路到达顶端时，将会调用此线路选择的天气计划器<br>
 * 天气计划器将根据当前的气候参数进行运算，并生成一个{@link com.arkdust.system.world.weather.Weather.ActivatedStateInServer}。同时，对于一条线路上生成的天气是否转化为天灾变种也由WeatherScheduler决定。<br>
 * 在给出的气候参数中，如果尝试获取了一个没有被放入的{@link com.arkdust.system.world.weather.ClimateParameter.ActivatedState}，将会返回其对应气候参数的默认值。<br>
 * 如果您想将一个气候参数设置为非默认值但是不变更，请使用{@link com.arkdust.system.world.weather.ClimateParameter.ActivatedState#ActivatedState(ClimateParameter,int value)}
 * */
public class WeatherScheduler implements INBTSerializable<CompoundTag> {
    private int lastAog;
    private WeatherStateProvider provider;
    public WeatherScheduler(WeatherStateProvider provider){
        this.provider = provider;
    }

    public WeatherScheduler(CompoundTag tag){
        deserializeNBT(tag);
    }

    public Weather.ServerState createNextState(ServerLevel level, WeatherSavedData.ClimateParaHandle climateParaHandle, RandomSource random){
        return provider.getState(level, climateParaHandle, random);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("aog",lastAog);
        tag.putString("provider", ArkdustRegistry.WEATHER_PROVIDER.getKey(provider).toString());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.lastAog = nbt.getInt("aog");
        this.provider = ArkdustRegistry.WEATHER_PROVIDER.get(new ResourceLocation(nbt.getString("provider")));
    }

    public interface WeatherStateProvider{
        Weather.ClearState getState(ServerLevel level, WeatherSavedData.ClimateParaHandle climateParaHandle, RandomSource random);
    }
}
