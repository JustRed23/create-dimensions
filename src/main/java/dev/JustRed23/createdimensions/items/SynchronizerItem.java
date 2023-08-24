package dev.JustRed23.createdimensions.items;

import com.simibubi.create.foundation.utility.Components;
import dev.JustRed23.createdimensions.behaviour.ISync;
import dev.JustRed23.createdimensions.blocks.blockentities.TransporterEntity;
import dev.JustRed23.createdimensions.register.CDItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SynchronizerItem extends Item {

    public SynchronizerItem(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack hand = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide())
            return InteractionResultHolder.success(hand);

        if (hasStoredData(hand) && pPlayer.isShiftKeyDown()) {
            hand.setTag(null);
            pPlayer.displayClientMessage(new TranslatableComponent("message.createdimensions.transporter_synchronizer_card.cleared"), true);
            return InteractionResultHolder.success(hand);
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public InteractionResult useOn(UseOnContext pContext) {
        if (!(pContext.getLevel() instanceof ServerLevel serverLevel))
            return InteractionResult.SUCCESS;

        ItemStack hand = pContext.getItemInHand();

        final BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
        if (blockEntity != null && ISync.class.isAssignableFrom(blockEntity.getClass()) && !((ISync) blockEntity).isConnected() && hand.is(CDItems.TRANSPORTER_SYNCHRONIZER.get())) {
            if (hasStoredData(hand)) {
                final CompoundTag tag = hand.getTag().getCompound("RemoteConnection");

                BlockPos connectTo = NbtUtils.readBlockPos(tag.getCompound("pos"));
                ResourceLocation location = new ResourceLocation(tag.getString("dimension"));
                TransporterEntity.ConnectionStatus status = ((ISync) blockEntity).connectTo(connectTo, ResourceKey.create(Registry.DIMENSION_REGISTRY, location));

                if (status != TransporterEntity.ConnectionStatus.SUCCESS) {
                    CompoundTag errorTag = new CompoundTag();
                    errorTag.putString("error", status.name().toLowerCase());
                    errorTag.put("pos", NbtUtils.writeBlockPos(pContext.getClickedPos()));
                    errorTag.putString("dimension", pContext.getLevel().dimension().location().toString());
                    hand.getOrCreateTag().put("ConnectionError", errorTag);
                    return InteractionResult.FAIL;
                } else hand.setTag(null);

                return InteractionResult.SUCCESS;
            } else storeData(hand, blockEntity.getBlockState().getBlock().getName().getString(), pContext.getClickedPos(), serverLevel.dimension());
        }

        return InteractionResult.PASS;
    }

    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Components.translatable("tooltip.createdimensions.transporter_synchronizer_card").withStyle(ChatFormatting.DARK_GRAY));
        if (hasStoredData(pStack)) {
            CompoundTag data = pStack.getTag().getCompound("RemoteConnection");
            pTooltipComponents.add(Components.immutableEmpty());
            pTooltipComponents.add(Components.translatable("tooltip.createdimensions.transporter_synchronizer_card.stored").withStyle(ChatFormatting.GRAY));
            final MutableComponent indent = Components.literal("  ").withStyle(ChatFormatting.DARK_GRAY);
            pTooltipComponents.add(indent.copy().append(Components.translatable("tooltip.createdimensions.transporter_synchronizer_card.block", data.getString("name"))));
            pTooltipComponents.add(indent.copy().append(Components.translatable("tooltip.createdimensions.transporter_synchronizer_card.level", data.getString("dimension"))));
            pTooltipComponents.add(indent.copy().append(Components.translatable("tooltip.createdimensions.transporter_synchronizer_card.pos", NbtUtils.readBlockPos(data.getCompound("pos")).toShortString())));
        }
    }

    public boolean isFoil(ItemStack pStack) {
        return hasStoredData(pStack) || hasError(pStack);
    }

    public static boolean hasStoredData(ItemStack stack) {
        return stack.getOrCreateTag().contains("RemoteConnection");
    }

    public static boolean isSameBlockAsStored(ItemStack itemInHand, BlockEntity be) {
        if (!hasStoredData(itemInHand))
            return false;
        final CompoundTag remoteConnection = itemInHand.getTag().getCompound("RemoteConnection");
        final BlockPos storedBlockPos = NbtUtils.readBlockPos(remoteConnection.getCompound("pos"));
        final ResourceKey<Level> storedDimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(remoteConnection.getString("dimension")));
        return be.getBlockPos().equals(storedBlockPos) && (be.getLevel() != null && be.getLevel().dimension().equals(storedDimension));
    }

    public static boolean hasError(ItemStack stack) {
        return stack.getOrCreateTag().contains("ConnectionError");
    }

    public static boolean isSameBlockAsError(ItemStack itemInHand, BlockEntity be) {
        if (!hasError(itemInHand))
            return false;
        final CompoundTag remoteConnection = itemInHand.getTag().getCompound("ConnectionError");
        final BlockPos storedBlockPos = NbtUtils.readBlockPos(remoteConnection.getCompound("pos"));
        final ResourceKey<Level> storedDimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(remoteConnection.getString("dimension")));
        return be.getBlockPos().equals(storedBlockPos) && (be.getLevel() != null && be.getLevel().dimension().equals(storedDimension));
    }

    private void storeData(ItemStack stack, String name, BlockPos pos, ResourceKey<Level> dimension) {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.put("pos", NbtUtils.writeBlockPos(pos));
        tag.putString("dimension", dimension.location().toString());
        stack.getOrCreateTag().put("RemoteConnection", tag);
    }
}
