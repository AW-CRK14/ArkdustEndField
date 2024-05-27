package com.landis.arkdust.system;

import com.google.common.collect.HashMultimap;
import com.landis.arkdust.helper.ListAndMapHelper;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.world.SimpleContainer;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FeaturedContainer extends SimpleContainer {
    public final int length;
    public final List<SlotType> typeList;
    private HashMultimap<SlotType,Integer> indexMap;

    public FeaturedContainer(int length){
        this(length,SlotType.ITEM);
    }

    public FeaturedContainer(int length,SlotType type){
        this.length = length;
        this.typeList = Collections.nCopies(length, type);
    }

    public FeaturedContainer(List<SlotType> types){
        this.length = types.size();
        this.typeList = types;
    }

    public FeaturedContainer(SlotType... types){
        this.length = types.length;
        this.typeList = List.of(types);
    }

    public HashMultimap<SlotType,Integer> getOrCreateIndexMap(){
        if(indexMap == null){
            indexMap = HashMultimap.create();
            for(int i = 0 ; i < length; i++){
                indexMap.put(typeList.get(i),i);
            }
        }
        return indexMap;
    }

    public Set<Integer> indexes(SlotType type){
        return getOrCreateIndexMap().get(type);
    }

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

        SlotType(@Nullable SlotType root,@Nullable Direct direct){
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
}
