package dev.JustRed23.createdimensions.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import dev.JustRed23.createdimensions.register.CDGuiTextures;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.Arrays;

public abstract class TransporterScreen<T extends TransporterMenu> extends AbstractSimiContainerScreen<T> {

    public TransporterScreen(T container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    protected void renderPlayerInventory(PoseStack ms, CDGuiTextures... backgrounds) {
        int invX = getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.width);
        int totalHeight = Arrays.stream(backgrounds).mapToInt(bg -> bg.height).sum();
        int invY = topPos + totalHeight + 2;
        renderPlayerInventory(ms, invX, invY);
    }
}
