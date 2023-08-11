package dev.JustRed23.createdimensions.blocks.blockentities.render;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import dev.JustRed23.createdimensions.blocks.blockentities.RotationTransporterEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class RotationTransporterRenderer extends KineticBlockEntityRenderer<RotationTransporterEntity> {

    public RotationTransporterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(RotationTransporterEntity be, BlockState state) {
        return CachedBufferer.partial(AllPartialModels.SHAFTLESS_COGWHEEL, state);
    }

}
