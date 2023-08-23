package dev.JustRed23.createdimensions.items;

import com.simibubi.create.foundation.utility.NBTHelper;
import dev.JustRed23.createdimensions.behaviour.ISync;
import dev.JustRed23.createdimensions.blocks.blockentities.TransporterEntity;
import dev.JustRed23.createdimensions.register.CDItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SynchronizerCard extends Item {

    public SynchronizerCard(Properties pProperties) {
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
        if (blockEntity != null && ISync.class.isAssignableFrom(blockEntity.getClass()) && hand.is(CDItems.TRANSPORTER_SYNCHRONIZER_CARD.get())) {
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
            } else storeData(hand, pContext.getClickedPos(), serverLevel.dimension());
        }

        return InteractionResult.PASS;
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

    private void storeData(ItemStack stack, BlockPos pos, ResourceKey<Level> dimension) {
        CompoundTag tag = new CompoundTag();
        tag.put("pos", NbtUtils.writeBlockPos(pos));
        tag.putString("dimension", dimension.location().toString());
        stack.getOrCreateTag().put("RemoteConnection", tag);
    }
}
