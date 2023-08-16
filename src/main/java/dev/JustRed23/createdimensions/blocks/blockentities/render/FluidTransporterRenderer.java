package dev.JustRed23.createdimensions.blocks.blockentities.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;

import dev.JustRed23.createdimensions.blocks.blockentities.FluidTransporterEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class FluidTransporterRenderer extends SafeBlockEntityRenderer<FluidTransporterEntity> {

    public FluidTransporterRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(FluidTransporterEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        LerpedFloat fluidLevel = be.getFluidLevel();
        if (fluidLevel == null)
            return;

        FluidStack fluidStack = be.getTank().getFluid();

        if (fluidStack.isEmpty())
            return;

        float min = 2 / 16f;
        float wMax = 1 - min;
        float hMax = min + fluidLevel.getValue(partialTicks) * (1 - 2 * min);

        ms.pushPose();
        FluidRenderer.renderFluidBox(fluidStack, min, min, min, wMax, hMax, wMax, buffer, ms, light, true);
        ms.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull FluidTransporterEntity be) {
        return true;
    }
}
