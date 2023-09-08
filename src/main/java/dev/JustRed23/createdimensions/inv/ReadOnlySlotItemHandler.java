package dev.JustRed23.createdimensions.inv;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ReadOnlySlotItemHandler extends SlotItemHandler {

    public ReadOnlySlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public boolean mayPlace(@NotNull ItemStack pStack) {
        return false;
    }

    public boolean mayPickup(@NotNull Player pPlayer) {
        return false;
    }
}
