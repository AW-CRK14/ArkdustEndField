package com.landis.arkdust.registry;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.operator.AbstractOperator;
import com.landis.arkdust.operator.OperatorType;
import com.landis.arkdust.operator.skill.AbstractOperatorSkill;
import com.landis.arkdust.registry.regtype.ArkdustRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class OperatorRegistry {
    public static final DeferredRegister<OperatorType<? extends AbstractOperator>> OPERATOR_TYPE = DeferredRegister.create(ArkdustRegistry.OPERATOR_TYPE, Arkdust.MODID);
    public static final DeferredRegister<AbstractOperatorSkill> OPERATOR_SKILL = DeferredRegister.create(ArkdustRegistry.OPERATOR_SKILL, Arkdust.MODID);

    //在这里添加干员与技能的注册




    public static <T extends OperatorType<? extends AbstractOperator>> DeferredHolder<OperatorType<? extends AbstractOperator>,T> operator(String name, Function<ResourceLocation,T> provider){
        return OPERATOR_TYPE.register(name,()-> provider.apply(new ResourceLocation(OPERATOR_TYPE.getNamespace(),name)));
    }
}
