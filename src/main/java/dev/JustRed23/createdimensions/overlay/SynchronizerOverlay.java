package dev.JustRed23.createdimensions.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;
import com.simibubi.create.foundation.gui.Theme;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.outliner.Outliner;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CClient;
import dev.JustRed23.createdimensions.DimensionsAddon;
import dev.JustRed23.createdimensions.behaviour.ISync;
import dev.JustRed23.createdimensions.items.SynchronizerItem;
import dev.JustRed23.createdimensions.register.CDItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.JustRed23.createdimensions.utils.ItemUtils.isHoldingSynchronizerCard;

public class SynchronizerOverlay {

    public static final IIngameOverlay OVERLAY = SynchronizerOverlay::renderOverlay;

    private static final Map<Object, Outliner.OutlineEntry> outlines = CreateClient.OUTLINER.getOutlines();

    public static int hoverTicks = 0;

    public static void renderOverlay(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int width,
                                     int height) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        HitResult objectMouseOver = mc.hitResult;
        if (!(objectMouseOver instanceof BlockHitResult)) {
            hoverTicks = 0;
            return;
        }

        for (Outliner.OutlineEntry entry : outlines.values()) {
            if (!entry.isAlive())
                continue;
            Outline outline = entry.getOutline();
            if (outline instanceof ValueBox && !((ValueBox) outline).isPassive)
                return;
        }

        BlockHitResult result = (BlockHitResult) objectMouseOver;
        ClientLevel world = mc.level;
        BlockPos pos = result.getBlockPos();

        hoverTicks++;

        BlockEntity be = world.getBlockEntity(pos);

        List<Component> tooltip = new ArrayList<>();

        if (be instanceof ISync sync && isHoldingSynchronizerCard()) {
            if (sync.isConnected()) {
                Lang.builder(DimensionsAddon.MODID)
                        .translate("gui.transporter.connected")
                        .style(ChatFormatting.GREEN)
                        .forGoggles(tooltip);
                Lang.builder(DimensionsAddon.MODID)
                        .translate("gui.transporter.connected.to", sync.getConnection().toShortString(), sync.getDimension().location())
                        .style(ChatFormatting.GRAY)
                        .forGoggles(tooltip, 1);
            } else {
                Lang.builder(DimensionsAddon.MODID)
                        .translate("gui.transporter.not_connected")
                        .style(ChatFormatting.RED)
                        .forGoggles(tooltip);

                final ItemStack itemInHand = mc.player.getItemInHand(InteractionHand.MAIN_HAND);

                if (SynchronizerItem.isSameBlockAsError(itemInHand, be)) {
                    Lang.builder(DimensionsAddon.MODID)
                            .translate("gui.transporter.error." + itemInHand.getTag().getCompound("ConnectionError").getString("error"))
                            .style(ChatFormatting.RED)
                            .forGoggles(tooltip, 1);
                } else {
                    Lang.builder(DimensionsAddon.MODID)
                            .translate(SynchronizerItem.isSameBlockAsStored(itemInHand, be) ? "gui.transporter.stored_connection" : "gui.transporter.right_click_to_connect")
                            .style(ChatFormatting.GRAY)
                            .forGoggles(tooltip, 1);
                }
            }
        }

        if (tooltip.isEmpty()) {
            hoverTicks = 0;
            return;
        }

        poseStack.pushPose();

        int tooltipTextWidth = 0;
        for (FormattedText textLine : tooltip) {
            int textLineWidth = mc.font.width(textLine);
            if (textLineWidth > tooltipTextWidth)
                tooltipTextWidth = textLineWidth;
        }

        int tooltipHeight = 8;
        if (tooltip.size() > 1) {
            tooltipHeight += 2; // gap between title lines and next lines
            tooltipHeight += (tooltip.size() - 1) * 10;
        }

        CClient cfg = AllConfigs.client();
        int posX = width / 2 + cfg.overlayOffsetX.get();
        int posY = height / 2 + cfg.overlayOffsetY.get();

        posX = Math.min(posX, width - tooltipTextWidth - 20);
        posY = Math.min(posY, height - tooltipHeight - 20);

        float fade = Mth.clamp((hoverTicks + partialTicks) / 24f, 0, 1);
        Boolean useCustom = cfg.overlayCustomColor.get();
        Color colorBackground = useCustom ? new Color(cfg.overlayBackgroundColor.get())
                : Theme.c(Theme.Key.VANILLA_TOOLTIP_BACKGROUND)
                .scaleAlpha(.75f);
        Color colorBorderTop = useCustom ? new Color(cfg.overlayBorderColorTop.get())
                : Theme.c(Theme.Key.VANILLA_TOOLTIP_BORDER, true)
                .copy();
        Color colorBorderBot = useCustom ? new Color(cfg.overlayBorderColorBot.get())
                : Theme.c(Theme.Key.VANILLA_TOOLTIP_BORDER, false)
                .copy();

        if (fade < 1) {
            poseStack.translate(Math.pow(1 - fade, 3) * Math.signum(cfg.overlayOffsetX.get() + .5f) * 8, 0, 0);
            colorBackground.scaleAlpha(fade);
            colorBorderTop.scaleAlpha(fade);
            colorBorderBot.scaleAlpha(fade);
        }

        RemovedGuiUtils.drawHoveringText(poseStack, tooltip, posX, posY, width, height, -1, colorBackground.getRGB(),
                colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);

        ItemStack item = CDItems.TRANSPORTER_SYNCHRONIZER.asStack();
        GuiGameElement.of(item)
                .at(posX + 10, posY - 16, 450)
                .render(poseStack);
        poseStack.popPose();
    }
}
