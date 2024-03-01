package com.arkdust.item;

import com.arkdust.network.InfoPayload;
import com.arkdust.system.world.weather.WeatherSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**TestItem是一个用于测试的物品 内容随时可能变化。
 * */
public class TestItem extends Item {
    public TestItem() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(pPlayer instanceof ServerPlayer player){
//            UIManager.getInstance().
//            MuiModApi.openScreen(new AbstractArkdustInfoUI());
            InfoPayload.LevelStateGUI.send(player);
        }
        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }

    private void climateTest(Level level,Player player){
//        if(!level.isClientSide()){
//            WeatherSavedData.getInstance((ServerLevel) level).ifPresent(data-> {
//                player.sendSystemMessage(Component.literal(data.save(new CompoundTag()).toString()));
//                data.climateParaTick((ServerLevel) level);
//            });
//        }
    }
}
