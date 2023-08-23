package dev.JustRed23.createdimensions.blocks;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import dev.JustRed23.createdimensions.blocks.blockentities.RotationTransporterEntity;
import dev.JustRed23.createdimensions.register.CDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

public class RotationTransporter extends HorizontalKineticBlock implements IBE<RotationTransporterEntity>, ICogWheel, IWrenchable {

    public RotationTransporter(Properties pProperties) {
        super(pProperties);
    }

    public Class<RotationTransporterEntity> getBlockEntityClass() {
        return RotationTransporterEntity.class;
    }

    public BlockEntityType<? extends RotationTransporterEntity> getBlockEntityType() {
        return CDBlockEntities.ROTATION_TRANSPORTER.get();
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

    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }
}
