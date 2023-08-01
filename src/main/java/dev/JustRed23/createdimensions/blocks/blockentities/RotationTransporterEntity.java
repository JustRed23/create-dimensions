package dev.JustRed23.createdimensions.blocks.blockentities;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RotationTransporterEntity extends TransporterEntity {

    public RotationTransporterEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    protected boolean tryConnect(TransporterEntity blockEntity) {
        if (!(blockEntity instanceof RotationTransporterEntity other)) // Cannot connect if the block entity is not of the same type
            return false;

        if (this.isVirtual() || other.isVirtual()) // Cannot connect if either of the blocks are virtual
            return false;

        if (this.isConnected() || other.isConnected()) // Cannot connect if either of the blocks are already connected
            return false;

        //Todo: check if has rotation

        return false;
    }

    protected void trySyncContents(TransporterEntity blockEntity) {

    }

    protected void onConnectionRemoved(boolean keepContents) {

    }

    protected void onModeChanged(TransportationMode mode) {

    }
}
