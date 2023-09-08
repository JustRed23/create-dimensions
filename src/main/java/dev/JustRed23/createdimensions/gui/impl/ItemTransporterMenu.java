package dev.JustRed23.createdimensions.gui.impl;

import com.simibubi.create.foundation.item.SmartInventory;
import dev.JustRed23.createdimensions.behaviour.ISync;
import dev.JustRed23.createdimensions.blocks.blockentities.ItemTransporterEntity;
import dev.JustRed23.createdimensions.gui.TransporterMenu;
import dev.JustRed23.createdimensions.inv.ReadOnlySlotItemHandler;
import dev.JustRed23.createdimensions.register.CDGuiTextures;
import dev.JustRed23.createdimensions.register.CDMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class ItemTransporterMenu extends TransporterMenu {

    public ItemTransporterMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public ItemTransporterMenu(MenuType<?> type, int id, Inventory inv, ItemTransporterEntity contentHolder) {
        super(type, id, inv, contentHolder);
    }

    public static ItemTransporterMenu create(int id, Inventory inv, ItemTransporterEntity be) {
        return new ItemTransporterMenu(CDMenus.ITEM_TRANSPORTER.get(), id, inv, be);
    }

    protected ISync createOnClient(FriendlyByteBuf extraData) {
        ClientLevel level = Minecraft.getInstance().level;
        BlockEntity be = level.getBlockEntity(extraData.readBlockPos());
        if (be instanceof ItemTransporterEntity itemTransporter) {
            itemTransporter.readClient(extraData.readNbt());
            return itemTransporter;
        }
        return null;
    }

    protected void initAndReadInventory(ISync contentHolder) {}

    protected void addSlots() {
        addUpgradeSlot(contentHolder, 20, (CDGuiTextures.ITEM_TRANSPORTER.height / 2) - 8);

        int hw = CDGuiTextures.ITEM_TRANSPORTER.width / 2;
        int hh = CDGuiTextures.ITEM_TRANSPORTER.height / 2;
        int padding = 1;

        SmartInventory syncInv = ((ItemTransporterEntity) contentHolder).getSyncInventory();
        addSlot(new ReadOnlySlotItemHandler(syncInv, 0, hw - (16 + padding), hh - (16 + padding))); //TL
        addSlot(new ReadOnlySlotItemHandler(syncInv, 1, hw + padding, hh - (16 + padding)));        //TR
        addSlot(new ReadOnlySlotItemHandler(syncInv, 2, hw - (16 + padding), hh + padding));        //BL
        addSlot(new ReadOnlySlotItemHandler(syncInv, 3, hw + padding, hh + padding));               //BR

        addPlayerSlots(8, CDGuiTextures.ITEM_TRANSPORTER.height + 20);
    }

    protected void saveData(ISync contentHolder) {}

    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        Slot slot = this.slots.get(pIndex);
        if (slot instanceof ReadOnlySlotItemHandler)
            return ItemStack.EMPTY;

        return super.quickMoveStack(pPlayer, pIndex);
    }
}
