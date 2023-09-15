package dev.JustRed23.createdimensions.blocks;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import dev.JustRed23.createdimensions.blocks.blockentities.ItemTransporterEntity;
import dev.JustRed23.createdimensions.register.CDBlockEntities;
import dev.JustRed23.createdimensions.utils.TransporterUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemTransporter extends HorizontalDirectionalBlock implements IBE<ItemTransporterEntity>, IWrenchable {

    public ItemTransporter(Properties pProperties) {
        super(pProperties);
    }

    public Class<ItemTransporterEntity> getBlockEntityClass() {
        return ItemTransporterEntity.class;
    }

    public BlockEntityType<? extends ItemTransporterEntity> getBlockEntityType() {
        return CDBlockEntities.ITEM_TRANSPORTER.get();
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

    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (pLevel.isClientSide) return InteractionResult.SUCCESS;
        if (TransporterUtils.handleItemUse(pState, pLevel, pPos, pPlayer, pHand, pHit)) return InteractionResult.PASS;
        withBlockEntityDo(pLevel, pPos, be -> NetworkHooks.openGui((ServerPlayer) pPlayer, be, be::sendToMenu));
        return InteractionResult.SUCCESS;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
        super.createBlockStateDefinition(pBuilder);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
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

    public int getAnalogOutputSignal(@NotNull BlockState pBlockState, @NotNull Level pLevel, @NotNull BlockPos pPos) {
        return getBlockEntityOptional(pLevel, pPos)
                .map(vte -> vte.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY))
                .map(lo -> lo.map(ItemHelper::calcRedstoneFromInventory)
                        .orElse(0))
                .orElse(0);
    }
}
