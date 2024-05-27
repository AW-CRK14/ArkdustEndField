package com.landis.breakdowncore.module.blockentity.container;

import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public enum SlotType {
    ITEM(null,null),
    INPUT(ITEM,Direct.INPUT),
    OUTPUT(ITEM,Direct.OUTPUT),
    ENERGY(null,null),
    ENERGY_INPUT(ENERGY,Direct.INPUT),
    ENERGY_OUTPUT(ENERGY,Direct.OUTPUT),
    LIQUID(null,null),
    LIQUID_INPUT(LIQUID,Direct.INPUT),
    LIQUID_OUTPUT(LIQUID,Direct.OUTPUT),
    CATALYZER(null,Direct.OPTIONAL),
    UPGRADE(null,Direct.INPUT),
    MARK(null,Direct.MARK),
    CONSUMABLE(null,null),
    BYPRODUCT(null,Direct.OUTPUT),
    PLAYER_INVENTORY(ITEM,Direct.CONTAIN),
    CONTAINER_INVENTORY(ITEM,Direct.CONTAIN),
    DEFAULT(null,null);

    public final SlotType root;
    public final Direct direct;

    SlotType(@Nullable SlotType root, @Nullable Direct direct){
        this.root = root;
        this.direct = direct;
    }

    public boolean isFor(@Nonnull SlotType type){
        if(this == type){
            return true;
        } else if (root == null) {
            return false;
        }else {
            return root.isFor(type);
        }
    }

    public enum Direct{
        INPUT,
        OUTPUT,
        FALLBACK,
        CONTAIN,
        MARK,
        OPTIONAL
    }
}
