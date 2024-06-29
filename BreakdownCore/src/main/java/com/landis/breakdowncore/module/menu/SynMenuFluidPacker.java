package com.landis.breakdowncore.module.menu;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.module.network.IClientCustomPacketPayload;
import com.landis.breakdowncore.module.network.IServerCustomPacketPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record SynMenuFluidPacker(int index, FluidStack stack) implements IServerCustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(BreakdownCore.MODID, "menu_syn/fluid");

    public SynMenuFluidPacker(FriendlyByteBuf byteBuf) {
        this(byteBuf.readInt(), FluidStack.loadFluidStackFromNBT(byteBuf.readNbt()));
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(index);
        pBuffer.writeNbt(stack.writeToNBT(new CompoundTag()));
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void consume(PlayPayloadContext context) {
        Player p = context.player().get();
        boolean flag = false;
        if (p.containerMenu instanceof ExpandedContainerMenu<?> expanded) {
            expanded.setFluid(index, stack);
            flag = true;
        }
//        System.out.println("Client got pack. flag = " + flag);
        context.replyHandler().send(new Feedback(index, flag));
    }

    public record Feedback(int index, boolean consumed) implements IClientCustomPacketPayload {
        public static final ResourceLocation ID = new ResourceLocation(BreakdownCore.MODID, "menu_syn/fluid/feedback");

        public Feedback(FriendlyByteBuf buf) {
            this(buf.readInt(), buf.readBoolean());
        }

        @Override
        public void consume(PlayPayloadContext context) {
            Player p = context.player().get();
            if (p.containerMenu instanceof ExpandedContainerMenu<?> expanded) {
                expanded.fluidNetworkFeedback(index, consumed);
            }
        }

        @Override
        public void write(FriendlyByteBuf pBuffer) {
            pBuffer.writeInt(index);
            pBuffer.writeBoolean(consumed);
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }
}
