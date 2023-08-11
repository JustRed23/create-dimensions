package dev.JustRed23.createdimensions.blocks.blockentities.render;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import dev.JustRed23.createdimensions.blocks.blockentities.RotationTransporterEntity;

public class RotationTransporterInstance extends SingleRotatingInstance<RotationTransporterEntity> {

    public RotationTransporterInstance(MaterialManager materialManager, RotationTransporterEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        return getRotatingMaterial().getModel(AllPartialModels.SHAFTLESS_COGWHEEL, blockEntity.getBlockState());
    }
}
