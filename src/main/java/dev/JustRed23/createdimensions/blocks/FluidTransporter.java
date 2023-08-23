package dev.JustRed23.createdimensions.blocks;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import dev.JustRed23.createdimensions.blocks.blockentities.FluidTransporterEntity;
import dev.JustRed23.createdimensions.register.CDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

public class FluidTransporter extends HorizontalDirectionalBlock implements IBE<FluidTransporterEntity>, IWrenchable {

    public FluidTransporter(Properties pProperties) {
        super(pProperties);
    }

    public Class<FluidTransporterEntity> getBlockEntityClass() {
        return FluidTransporterEntity.class;
    }

    public BlockEntityType<? extends FluidTransporterEntity> getBlockEntityType() {
        return CDBlockEntities.FLUID_TRANSPORTER.get();
    }

    public void onRemove(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
    }

    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        return onBlockEntityUse(context.getLevel(), context.getClickedPos(), be -> {
            boolean clientSide = context.getLevel().isClientSide();

            if (!clientSide) {
                be.switchMode();

                if (context.getPlayer() != null)
                    context.getPlayer().displayClientMessage(new TranslatableComponent("message.createdimensions.transporter.mode-changed", be.getMode().name().toLowerCase()), true);
            }

            return InteractionResult.sidedSuccess(clientSide);
        });
    }

    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 5;
    }

    public @NotNull PushReaction getPistonPushReaction(@NotNull BlockState pState) {
        return PushReaction.BLOCK;
    }

    public boolean hasAnalogOutputSignal(@NotNull BlockState pState) {
        return true;
    }

    public int getAnalogOutputSignal(@NotNull BlockState blockState, @NotNull Level pWorld, @NotNull BlockPos pPos) {
        return ComparatorUtil.levelOfSmartFluidTank(pWorld, pPos);
    }
}
