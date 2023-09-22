package dev.JustRed23.createdimensions.gui.impl;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.ILightingSettings;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Lang;
import dev.JustRed23.createdimensions.DimensionsAddon;
import dev.JustRed23.createdimensions.blocks.blockentities.RotationTransporterEntity;
import dev.JustRed23.createdimensions.gui.TransporterScreen;
import dev.JustRed23.createdimensions.register.CDGuiTextures;
import dev.JustRed23.createdimensions.register.CDPartialModels;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec3;
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
        //Gear rendering
        RotationTransporterEntity be = ((RotationTransporterEntity) getMenu().contentHolder);
        int scale = 40;
        int xOffset = x + (menu.width / 2) - (scale / 2);
        int yOffset = y + (menu.height / 2) + (scale / 2);

        float angle = ((AnimationTickHolder.getRenderTime() * be.getSpeed() * 3f / 10) % 360);
        renderCogElement(AllPartialModels.SHAFTLESS_COGWHEEL, pPoseStack, scale, xOffset, yOffset, angle);
        renderCogElement(CDPartialModels.ROTATION_TRANSPORTER_SHAFT, pPoseStack, scale, xOffset, yOffset, angle);

        final MutableComponent speed = Lang.builder(DimensionsAddon.MODID).translate("gui.rotation_transporter.speed")
                .add(Lang.number(Math.abs(be.getSpeed())).space().translate("generic.unit.rpm"))
                .component();

        final MutableComponent stress = Lang.builder(DimensionsAddon.MODID).translate("gui.rotation_transporter.stress")
                .add(
                        Lang.number(be.getStress())
                                .translate("generic.unit.stress")
                                .space().text("/").space()
                                .add(
                                        Lang.number(be.getMaxStress())
                                                .translate("generic.unit.stress")
                                )
                ).component();

        pPoseStack.pushPose();
        float textScale = 0.8f;
        pPoseStack.translate(x + (menu.width / 2f), y + (menu.height / 2f) + 28, 0);
        pPoseStack.scale(textScale, textScale, textScale);

        if (be.hasNetwork()) {
            font.draw(pPoseStack, speed.getVisualOrderText(), -(float)(font.width(speed.getVisualOrderText()) / 2), 0, 0x404040);
            font.draw(pPoseStack, stress.getVisualOrderText(), -(float)(font.width(stress.getVisualOrderText()) / 2), 10, 0x404040);
        }

        pPoseStack.popPose();

        drawTitle(pPoseStack);
    }

    private void renderCogElement(PartialModel model, PoseStack pPoseStack, int scale, int x, int y, float rotationAngle) {
        GuiGameElement.of(model)
                .scale(scale)
                .withRotationOffset(new Vec3(0.5, 0.5, 0.5))
                .rotate(90, rotationAngle, 0)
                .lighting(ILightingSettings.DEFAULT_FLAT)
                .render(pPoseStack, x, y);
    }
}
