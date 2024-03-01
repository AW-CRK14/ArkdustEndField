package com.arkdust.registry.worldgen.level;

import com.arkdust.worldgen.dimension.SarconDimension;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.OptionalLong;

public class DimensionTypeRegistry {
    public static void bootstrap(BootstapContext<DimensionType> context){
        context.register(SarconDimension.TYPE,new DimensionType(OptionalLong.empty(),true,false,true,false,0.00001,true,false,
            0,304,304, BlockTags.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS,0,new DimensionType.MonsterSettings(true,true, UniformInt.of(0, 6),9)));
    }

//    public static final DeferredRegister<DimensionType> REGISTER = DeferredRegister.create(Registries.DIMENSION_TYPE, Arkdust.MODID);
//    public static final DeferredHolder<DimensionType,DimensionType> SARCON = REGISTER.register(SarconDimension.TYPE.location().getPath(),()->new DimensionType(OptionalLong.empty(),true,false,false,false,0.00001,true,false,
//            0,464,464, BlockTags.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS,0,new DimensionType.MonsterSettings(true,true, UniformInt.of(0, 6),9)
//    ));
}
