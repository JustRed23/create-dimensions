package dev.JustRed23.createdimensions.utils;

import dev.JustRed23.createdimensions.register.CDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class TransporterUtils {

    public static boolean handleItemUse(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        final ItemStack hand = pPlayer.getItemInHand(pHand);
        if (hand.isEmpty())
            return false;

        //useOn gets called on the item automatically, just return
        if (CDItems.TRANSPORTER_SYNCHRONIZER.isIn(hand))
            return true;
        if (CDItems.TRANSPORTER_CHUNK_UPGRADE.isIn(hand))
            return true;
        //TODO: custom item handling, API?

        return false;
    }
}
