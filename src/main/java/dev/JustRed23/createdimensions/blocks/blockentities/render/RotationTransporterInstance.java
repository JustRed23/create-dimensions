package dev.JustRed23.createdimensions.blocks.blockentities.render;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import dev.JustRed23.createdimensions.blocks.blockentities.RotationTransporterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class RotationTransporterInstance extends SingleRotatingInstance<RotationTransporterEntity> {

    protected RotatingData additionalShaft;

    public RotationTransporterInstance(MaterialManager materialManager, RotationTransporterEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    public void init() {
        super.init();

        float speed = blockEntity.getSpeed();
        Direction.Axis axis = Direction.Axis.Y;
        BlockPos pos = blockEntity.getBlockPos();
        float offset = BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos);
        Direction facing = Direction.DOWN;
        Instancer<RotatingData> half = getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockState, facing);

        additionalShaft = setup(half.createInstance(), speed);
        additionalShaft.setPosition(pos.getX(), pos.getY() + 0.25f, pos.getZ());
        additionalShaft.setRotationOffset(offset);
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        return getRotatingMaterial().getModel(AllPartialModels.SHAFTLESS_COGWHEEL, blockState);
    }

    @Override
    public void update() {
        super.update();
        if (additionalShaft != null) {
            updateRotation(additionalShaft);
            additionalShaft.setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos));
        }
    }

    @Override
    public void updateLight() {
        super.updateLight();
        if (additionalShaft != null)
            relight(pos, additionalShaft);
    }

    @Override
    public void remove() {
        super.remove();
        if (additionalShaft != null)
            additionalShaft.delete();
    }

}
