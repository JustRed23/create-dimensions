package dev.JustRed23.createdimensions.gui.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.JustRed23.createdimensions.blocks.blockentities.FluidTransporterEntity;
import dev.JustRed23.createdimensions.gui.TransporterScreen;
import dev.JustRed23.createdimensions.register.CDGuiTextures;
import dev.JustRed23.createdimensions.utils.RenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class FluidTransporterScreen extends TransporterScreen<FluidTransporterMenu> {

    public FluidTransporterScreen(FluidTransporterMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    protected void init() {
        CDGuiTextures bg = CDGuiTextures.FLUID_TRANSPORTER;
        setWindowSize(Math.max(AllGuiTextures.PLAYER_INVENTORY.width, bg.width), AllGuiTextures.PLAYER_INVENTORY.height + bg.height + 2);
        super.init();
        clearWidgets();
    }

    protected void renderBg(@NotNull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        final CDGuiTextures menu = CDGuiTextures.FLUID_TRANSPORTER;
        renderPlayerInventory(pPoseStack, menu);

        int x = leftPos;
        int y = topPos;

        FluidTransporterEntity transporter = ((FluidTransporterEntity) getMenu().contentHolder);

        menu.render(pPoseStack, x, y, this);
        RenderUtils.renderFluidBox(pPoseStack, x + 67, y + 21, 42, 58, transporter.getTank().getFluid(), transporter.getTank().getCapacity());
        CDGuiTextures.FLUID_TANK_MEASUREMENT.render(pPoseStack, x + 66, y + 20);
        drawTitle(pPoseStack);
    }
}
