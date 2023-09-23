package dev.JustRed23.createdimensions.gui;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import dev.JustRed23.createdimensions.behaviour.ISync;
import dev.JustRed23.createdimensions.inv.UpgradeSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class TransporterMenu extends MenuBase<ISync> {

    public TransporterMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public TransporterMenu(MenuType<?> type, int id, Inventory inv, ISync contentHolder) {
        super(type, id, inv, contentHolder);
    }

    protected void addUpgradeSlot(ISync contentHolder, int x, int y) {
        addSlot(new UpgradeSlot(contentHolder.getUpgradeInventory(), 0, x, y));
    }

    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < this.contentHolder.getContainerSize()) { //extract from entity
                if (!this.moveItemStackTo(itemstack1, this.contentHolder.getContainerSize(), this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else { //insert into entity
                if (!this.moveItemStackTo(itemstack1, 0, 1, false))
                    return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }

        return itemstack;
    }
}
