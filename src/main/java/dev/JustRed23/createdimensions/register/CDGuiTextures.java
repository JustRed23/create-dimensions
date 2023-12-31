package dev.JustRed23.createdimensions.register;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.utility.Color;
import dev.JustRed23.createdimensions.DimensionsAddon;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum CDGuiTextures implements ScreenElement {

    ITEM_TRANSPORTER("item_transporter", 176, 100),
    FLUID_TRANSPORTER("fluid_transporter", 176, 100),
    ROTATION_TRANSPORTER("rotation_transporter", 176, 100),

    FLUID_TANK_MEASUREMENT("fluid_transporter", 176, 0, 10, 60);

    public final ResourceLocation location;
    public final int width, height;
    public final int startX, startY;

    private CDGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    private CDGuiTextures(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    private CDGuiTextures(String location, int startX, int startY, int width, int height) {
        this(DimensionsAddon.MODID, location, startX, startY, width, height);
    }

    private CDGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = new ResourceLocation(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PoseStack ms, int x, int y) {
        bind();
        GuiComponent.blit(ms, x, y, 0, startX, startY, width, height, 256, 256);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack ms, int x, int y, GuiComponent component) {
        bind();
        component.blit(ms, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack ms, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(ms, c, x, y, startX, startY, width, height);
    }

}
