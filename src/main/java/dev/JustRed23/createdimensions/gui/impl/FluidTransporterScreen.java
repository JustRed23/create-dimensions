package dev.JustRed23.createdimensions.gui.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.Lang;
import dev.JustRed23.createdimensions.blocks.blockentities.FluidTransporterEntity;
import dev.JustRed23.createdimensions.gui.TransporterScreen;
import dev.JustRed23.createdimensions.register.CDGuiTextures;
import dev.JustRed23.createdimensions.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    protected void renderForeground(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        if (isHovering(67, 21, 42, 58, mouseX, mouseY))
            renderComponentTooltip(ms, getTooltipFromFluid(), mouseX, mouseY);
    }

    private List<Component> getTooltipFromFluid() {
        List<Component> components = Lists.newArrayList();
        FluidTransporterEntity transporter = ((FluidTransporterEntity) getMenu().contentHolder);

        if (transporter.getTank().isEmpty())
            return components;

        components.add(Lang.fluidName(transporter.getTank().getFluid()).style(ChatFormatting.GRAY).component());
        components.add(Lang
                .number(transporter.getTank().getFluid().getAmount()).style(ChatFormatting.GOLD)
                    .add(Lang.translate("generic.unit.millibuckets").style(ChatFormatting.GOLD))
                .add(Lang.text(" / ").style(ChatFormatting.GRAY))
                .add(Lang.number(transporter.getTank().getCapacity()).style(ChatFormatting.DARK_GRAY))
                    .add(Lang.translate("generic.unit.millibuckets").style(ChatFormatting.DARK_GRAY))
                .component());

        return components;
    }
}
