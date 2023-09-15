package dev.JustRed23.createdimensions.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public final class RenderUtils {

    public static void renderFluidBox(PoseStack ms, int x, int y, int width, int height, FluidStack stack, int maxAmount) {
        int right = x + width;
        int bottom = y + height;

        final Fluid fluid = stack.getFluid();
        boolean lighterThanAir = fluid.getAttributes().isLighterThanAir();

        int amount = stack.getAmount();
        if (amount > maxAmount)
            amount = maxAmount;

        int filled = (int) (amount / (float) maxAmount * height);

        if (lighterThanAir) bottom = y + filled;
        int top = lighterThanAir ? y : y + height - filled;

        render2DTextured(ms, x, right, bottom, top, stack);
    }

    public static void render2DColored(PoseStack ms, int left, int right, int bot, int top, int color) {
        render2DColored(ms, left, right, bot, top, color, 0, 1, 0, 1);
    }

    public static void render2DColored(PoseStack ms, int left, int right, int bot, int top, int color, int u0, int u1, int v0, int v1) {
        int red = (color >> 16 & 255);
        int green = (color >> 8 & 255);
        int blue = (color & 255);
        int alpha = (color >> 24 & 255);

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f pose = ms.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(pose, (float) left , (float) bot, 0).color(red, green, blue, alpha).uv(u0, v1).endVertex();
        bufferbuilder.vertex(pose, (float) right, (float) bot, 0).color(red, green, blue, alpha).uv(u1, v1).endVertex();
        bufferbuilder.vertex(pose, (float) right, (float) top, 0).color(red, green, blue, alpha).uv(u1, v0).endVertex();
        bufferbuilder.vertex(pose, (float) left , (float) top, 0).color(red, green, blue, alpha).uv(u0, v0).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.disableBlend();
    }

    public static void render2DTextured(PoseStack ms, int left, int right, int bot, int top, FluidStack stack) {
        if (stack.isEmpty())
            return;

        FluidAttributes attr = stack.getFluid().getAttributes();
        int color = attr.getColor();
        int red = (color >> 16 & 255);
        int green = (color >> 8 & 255);
        int blue = (color & 255);
        int alpha = (color >> 24 & 255);

        final TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(attr.getStillTexture(stack));
        RenderSystem.setShaderTexture(0, texture.atlas().location());
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        Matrix4f pose = ms.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(pose, (float) left , (float) bot, 0).uv(texture.getU0(), texture.getV1()).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(pose, (float) right, (float) bot, 0).uv(texture.getU1(), texture.getV1()).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(pose, (float) right, (float) top, 0).uv(texture.getU1(), texture.getV0()).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(pose, (float) left , (float) top, 0).uv(texture.getU0(), texture.getV0()).color(red, green, blue, alpha).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
    }
}
