package com.landis.arkdust.dataattach;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PlayerInfo {
    public static final Codec<PlayerInfo> CODEC = RecordCodecBuilder.create(i ->
            i.group(Codec.BOOL.fieldOf("first_login").forGetter(PlayerInfo::isFirstLoginInit))
                    .apply(i, PlayerInfo::new)
    );
    private boolean firstLoginInit;

    public PlayerInfo() {
        this.firstLoginInit = false;
    }

    public PlayerInfo(boolean firstLoginInit) {
        this.firstLoginInit = firstLoginInit;
    }

    public boolean isFirstLoginInit() {
        return firstLoginInit;
    }
    public void init() {
        this.firstLoginInit = true;
    }

}
