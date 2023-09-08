package dev.JustRed23.createdimensions.gui.impl;

import dev.JustRed23.createdimensions.behaviour.ISync;
import dev.JustRed23.createdimensions.blocks.blockentities.FluidTransporterEntity;
import dev.JustRed23.createdimensions.gui.TransporterMenu;
import dev.JustRed23.createdimensions.register.CDGuiTextures;
import dev.JustRed23.createdimensions.register.CDMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FluidTransporterMenu extends TransporterMenu {

    public FluidTransporterMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public FluidTransporterMenu(MenuType<?> type, int id, Inventory inv, FluidTransporterEntity contentHolder) {
        super(type, id, inv, contentHolder);
    }

    public static FluidTransporterMenu create(int id, Inventory inv, FluidTransporterEntity be) {
        return new FluidTransporterMenu(CDMenus.FLUID_TRANSPORTER.get(), id, inv, be);
    }

    protected ISync createOnClient(FriendlyByteBuf extraData) {
        ClientLevel level = Minecraft.getInstance().level;
        BlockEntity be = level.getBlockEntity(extraData.readBlockPos());
        if (be instanceof FluidTransporterEntity fluidTransporter) {
            fluidTransporter.readClient(extraData.readNbt());
            return fluidTransporter;
        }
        return null;
    }

    protected void initAndReadInventory(ISync contentHolder) {}

    protected void addSlots() {
        addUpgradeSlot(contentHolder, 20, (CDGuiTextures.FLUID_TRANSPORTER.height / 2) - 8);
        addPlayerSlots(8, CDGuiTextures.FLUID_TRANSPORTER.height + 20);
    }

    protected void saveData(ISync contentHolder) {}
}
