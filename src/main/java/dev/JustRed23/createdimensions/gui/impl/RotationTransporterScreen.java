package dev.JustRed23.createdimensions.gui.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.JustRed23.createdimensions.gui.TransporterScreen;
import dev.JustRed23.createdimensions.register.CDGuiTextures;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class RotationTransporterScreen extends TransporterScreen<RotationTransporterMenu> {

    public RotationTransporterScreen(RotationTransporterMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    protected void init() {
        CDGuiTextures bg = CDGuiTextures.ROTATION_TRANSPORTER;
        setWindowSize(Math.max(AllGuiTextures.PLAYER_INVENTORY.width, bg.width), AllGuiTextures.PLAYER_INVENTORY.height + bg.height + 2);
        super.init();
        clearWidgets();
    }

    protected void renderBg(@NotNull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        final CDGuiTextures menu = CDGuiTextures.ROTATION_TRANSPORTER;
        renderPlayerInventory(pPoseStack, menu);

        int x = leftPos;
        int y = topPos;

        menu.render(pPoseStack, x, y, this);
        drawTitle(pPoseStack);
    }
}
