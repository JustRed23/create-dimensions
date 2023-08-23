package dev.JustRed23.createdimensions.items;

import com.simibubi.create.foundation.utility.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChunkLoaderCard extends Item {

    public ChunkLoaderCard(Properties pProperties) {
        super(pProperties);
    }

    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Components.translatable("tooltip.createdimensions.transporter_chunk_upgrade").withStyle(ChatFormatting.DARK_GRAY));
    }

    public boolean isFoil(ItemStack pStack) { // Enchantment glint
        return true;
    }
}
