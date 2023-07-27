package dev.JustRed23.createdimensions.blocks;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import dev.JustRed23.createdimensions.blocks.blockentities.TransporterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractTransporter<E extends TransporterEntity> extends Block implements IWrenchable, IBE<E> {

    public AbstractTransporter(Properties pProperties) {
        super(pProperties);
    }

    public void onRemove(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
    }

    public @NotNull InteractionResult use(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (pHand.equals(InteractionHand.OFF_HAND))
            return InteractionResult.PASS;

        ItemStack held = pPlayer.getItemInHand(pHand);
        if (held.isEmpty()) {
            if (pPlayer.isShiftKeyDown()) {
                return onBlockEntityUse(pLevel, pPos, blockEntity -> {
                    if (!blockEntity.isConnected())
                        return InteractionResult.PASS;

                    if (!pLevel.isClientSide()) {
                        blockEntity.switchMode();
                        pPlayer.displayClientMessage(new TranslatableComponent("message.createdimensions.transporter.mode-changed", blockEntity.getMode().name().toLowerCase()), true);
                    }


                    return InteractionResult.sidedSuccess(pLevel.isClientSide());
                });
            } else {
                return onBlockEntityUse(pLevel, pPos, blockEntity -> {
                    if (!blockEntity.isConnected())
                        pPlayer.displayClientMessage(new TextComponent("I am not connected to any block"), true);
                    else
                        pPlayer.displayClientMessage(new TextComponent("I am connected to " + blockEntity.getConnection() + " in " + blockEntity.getDimension()), true);

                    return InteractionResult.sidedSuccess(pLevel.isClientSide());
                });
            }
        }

        return InteractionResult.PASS;
    }
}
