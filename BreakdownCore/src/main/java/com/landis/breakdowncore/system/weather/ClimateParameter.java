package com.landis.breakdowncore.system.weather;

import com.landis.breakdowncore.BREARegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Range;
import org.joml.Math;

import java.util.Random;

/**
 * ClimateParameter气候参数<br>
 * 气候参数是Arkdust添加的天气系统中的元单位，用于决定天气的生成。<br>
 * 气候参数需要以下内容：默认值，默认变化率，范围<br>
 * 气候参数执行的行为包括：在每个气候参数周期根据变化率改变值，并修正变化率<br>
 *<br>
 * 气候突变机会系数 mutationChanceCoe 决定了气候突变的概率。在abs(range)=range时，机会为0.2；range=0时，机会为0.8。在这一基础上乘以突变系数将会得到突变几率。<br>
 * 气候突变强度系数 mutationStrengthCoe 决定了气候突变的强度。
 */
public record ClimateParameter(float range, float defaultValue, float defaultRate, float mutationChanceCoe, float mutationStrengthCoe, float rateFactor) {
    public ClimateParameter(float range, float defaultValue, float defaultRate,@Range(from = 0L,to = 1L) float mutation) {
        this(range,defaultValue,defaultRate,mutation,mutation,1);
    }

    public ClimateParameter(float range, float defaultValue, float defaultRate, float mutationChanceCoe, float mutationStrengthCoe) {
        this(range,defaultValue,defaultRate,mutationChanceCoe,mutationStrengthCoe,1);
    }

    public static Component trans(ResourceLocation id){
        return Component.translatable("climate." + id.getNamespace() + "." + id.getPath());
    }

    public Component trans(){
        return trans(BREARegistries.CLIMATE_PARAMETER.getKey(this));
    }

    public static class ActivatedState implements INBTSerializable<CompoundTag> {
        private ClimateParameter climate;
        public float value;
        public float rate;
        public float offset;//offset参数用于在获取actuallyValue时进行偏移计算，便于在不同的环境下匹配同一种weather
        public boolean changeable;
        public float rateFactor;

        public ActivatedState(ClimateParameter climate, float defaultValue, float defaultRate, float offset,float rateFactor){
            this.climate = climate;
            this.value = defaultValue;
            this.rate = defaultRate;
            this.rateFactor = rateFactor;
            this.offset = offset;
            this.changeable = true;
        }

        public ActivatedState(ClimateParameter climate, float defaultValue, float defaultRate, float offset){
            this(climate, defaultValue, defaultRate, offset,1);
        }

        public ActivatedState(ClimateParameter climate, float defaultValue, float defaultRate){
            this(climate,defaultValue,defaultRate,0);
        }

        public ActivatedState(ClimateParameter climate){
            this(climate, climate.defaultValue, climate.defaultRate,0,climate.rateFactor);
        }

        /**注意：这个方法将构造一个静止的ActivatedState<br>
         * 其数值将保持在此值不变化
         * */
        public ActivatedState(ClimateParameter climate,int value){
            this.climate = climate;
            this.value = value;
            this.rate = 0;
            this.offset = 0;
            this.changeable = false;
        }

        public ActivatedState(CompoundTag tag){
            deserializeNBT(tag);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("climate", BREARegistries.CLIMATE_PARAMETER.getKey(climate).toString());
            tag.putFloat("value",value);
            tag.putFloat("rate",rate);
            tag.putFloat("offset",offset);
            tag.putBoolean("changeable",changeable);
            tag.putFloat("rate_fac",rateFactor);
            return tag;
        }

        @Override
        public String toString() {
            return "ActivatedState{" +
                    "climate=" + BREARegistries.CLIMATE_PARAMETER.getKey(climate).toString() +
                    ", value=" + value +
                    ", rate=" + rate +
                    ", rate_fac=" + rateFactor +
                    ", offset=" + offset +
                    ", changeable=" + changeable +
                    '}';
        }

        ClimateParameter getClimate(){
            return climate;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.climate = BREARegistries.CLIMATE_PARAMETER.get(new ResourceLocation(tag.getString("climate")));
            this.value = tag.getFloat("value");
            this.rate = tag.getFloat("rate");
            this.offset = tag.getFloat("offset");
            this.changeable = tag.getBoolean("changeable");
            this.rateFactor = tag.getFloat("rate_factor");
        }

        public void climateTick(){
            if(!changeable) return;

            Random random = new Random();
            this.value = Math.clamp(-climate.range,climate.range,value + rate);
            //初阶速率变换 向0方向偏折0.2至-0.1
            rate -= value * (random.nextFloat() * 0.15F + 0.15F);

            //速率突变
            if(random.nextFloat() > (0.2F + 0.6F * java.lang.Math.abs(value / climate.range)) * (1-climate.mutationChanceCoe)){
                rate += (random.nextBoolean() ? 1 : -1) * climate.range * ( 0.2F + 0.1F * random.nextFloat()) * climate.mutationStrengthCoe;
            }

            rate*=rateFactor;
            rate = Math.clamp(-0.4F*climate.range,0.4F*climate.range,rate);
        }

        public float getActuallyValue(){
            return value + offset;
        }
    }
}
