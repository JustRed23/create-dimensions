package dev.JustRed23.createdimensions.items;

import dev.JustRed23.createdimensions.blocks.blockentities.TransporterEntity;
import dev.JustRed23.createdimensions.register.CDItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SynchronizerCard extends Item {


    public SynchronizerCard(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResult useOn(UseOnContext pContext) {
        if (!(pContext.getLevel() instanceof ServerLevel serverLevel))
            return InteractionResult.SUCCESS;

        ItemStack hand = pContext.getItemInHand();

        final BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
        if (blockEntity instanceof TransporterEntity entity && hand.is(CDItems.TRANSPORTER_SYNCHRONIZER_CARD.get())) {
            if (hasStoredData(hand)) {
                final CompoundTag tag = hand.getTag().getCompound("RemoteConnection");

                int[] pos = tag.getIntArray("pos");
                BlockPos connectTo = new BlockPos(pos[0], pos[1], pos[2]);

                ResourceLocation location = new ResourceLocation(tag.getString("dimension"));
                TransporterEntity.ConnectionStatus status = entity.connectTo(connectTo, ResourceKey.create(Registry.DIMENSION_REGISTRY, location));

                if (status != TransporterEntity.ConnectionStatus.SUCCESS) {
                    pContext.getPlayer().displayClientMessage(new TranslatableComponent("message.createdimensions.transporter_synchronizer_card.fail", status.description()).withStyle(ChatFormatting.RED), true);
                    return InteractionResult.FAIL;
                } else hand.setTag(null);

                pContext.getPlayer().displayClientMessage(new TranslatableComponent("message.createdimensions.transporter_synchronizer_card.success").withStyle(ChatFormatting.GREEN), true);
                return InteractionResult.SUCCESS;
            } else {
                BlockPos clickedPos = pContext.getClickedPos();
                ResourceKey<Level> dimension = serverLevel.dimension();

                storeData(hand, clickedPos, dimension);
                pContext.getPlayer().displayClientMessage(new TranslatableComponent("message.createdimensions.transporter_synchronizer_card.stored"), true);
            }
        }

        return InteractionResult.PASS;
    }

    private boolean hasStoredData(ItemStack stack) {
        return stack.getOrCreateTag().contains("RemoteConnection");
    }

    private void storeData(ItemStack stack, BlockPos pos, ResourceKey<Level> dimension) {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        tag.putString("dimension", dimension.location().toString());
        stack.getOrCreateTag().put("RemoteConnection", tag);
    }
}
