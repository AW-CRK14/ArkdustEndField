package com.landis.arkdust.registry.regtype;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.operator.AbstractOperator;
import com.landis.arkdust.operator.OperatorType;
import com.landis.arkdust.operator.skill.AbstractOperatorSkill;
import com.landis.breakdowncore.system.weather.ClimateParameter;
import com.landis.breakdowncore.system.weather.Weather;
import com.landis.breakdowncore.system.weather.WeatherScheduler;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ArkdustRegistry {
    public static final Registry<OperatorType<? extends AbstractOperator>> OPERATOR_TYPE = new RegistryBuilder<>(Keys.OPERATOR_TYPE).sync(true).create();
    public static final Registry<AbstractOperatorSkill> OPERATOR_SKILL = new RegistryBuilder<>(Keys.OPERATOR_SKILL).sync(true).create();

    public static class Keys{
        public static final ResourceKey<Registry<OperatorType<? extends AbstractOperator>>> OPERATOR_TYPE =  create("operator");
        public static final ResourceKey<Registry<AbstractOperatorSkill>> OPERATOR_SKILL =  create("operator_skill");

        public static <T> ResourceKey<Registry<T>> create(String name){
            return ResourceKey.createRegistryKey(new ResourceLocation(Arkdust.MODID,name));
        }
    }
}
