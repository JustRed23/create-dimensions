package dev.JustRed23.createdimensions.items;

import com.simibubi.create.foundation.utility.Components;
import dev.JustRed23.createdimensions.behaviour.ISync;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChunkLoaderCard extends Item {

    public ChunkLoaderCard(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResult useOn(UseOnContext pContext) {
        if (!(pContext.getLevel() instanceof ServerLevel))
            return InteractionResult.SUCCESS;

        ItemStack hand = pContext.getItemInHand();

        final BlockEntity blockEntity = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
        if (blockEntity instanceof ISync sync && sync.getUpgradeInventory().getStackInSlot(0).isEmpty()) {
            sync.getUpgradeInventory().setStackInSlot(0, hand.split(1));
            blockEntity.setChanged();
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Components.translatable("tooltip.createdimensions.transporter_chunk_upgrade").withStyle(ChatFormatting.DARK_GRAY));
    }

    public boolean isFoil(@NotNull ItemStack pStack) { // Enchantment glint
        return true;
    }
}
