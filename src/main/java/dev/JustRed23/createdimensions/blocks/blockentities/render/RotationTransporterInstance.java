package dev.JustRed23.createdimensions.blocks.blockentities.render;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import dev.JustRed23.createdimensions.blocks.blockentities.RotationTransporterEntity;
import dev.JustRed23.createdimensions.register.CDPartialModels;

public class RotationTransporterInstance extends SingleRotatingInstance<RotationTransporterEntity> {

    protected RotatingData additionalShaft;

    public RotationTransporterInstance(MaterialManager materialManager, RotationTransporterEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    public void init() {
        super.init();

        Instancer<RotatingData> shaft = getRotatingMaterial().getModel(CDPartialModels.ROTATION_TRANSPORTER_SHAFT, blockState);
        additionalShaft = setup(shaft.createInstance(), blockEntity.getSpeed());
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
