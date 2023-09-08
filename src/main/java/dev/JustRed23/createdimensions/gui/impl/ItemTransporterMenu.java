package dev.JustRed23.createdimensions.gui.impl;

import dev.JustRed23.createdimensions.behaviour.ISync;
import dev.JustRed23.createdimensions.blocks.blockentities.ItemTransporterEntity;
import dev.JustRed23.createdimensions.gui.TransporterMenu;
import dev.JustRed23.createdimensions.register.CDGuiTextures;
import dev.JustRed23.createdimensions.register.CDMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

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
        addPlayerSlots(8, CDGuiTextures.ITEM_TRANSPORTER.height + 20);
    }

    protected void saveData(ISync contentHolder) {}
}
