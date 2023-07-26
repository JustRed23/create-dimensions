package dev.JustRed23.createdimensions.blocks.blockentities;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ItemTransporterEntity extends TransporterEntity {

    public ItemTransporterEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    protected boolean trySync(TransporterEntity blockEntity) {
        return false;
    }

    protected void trySyncContents(TransporterEntity blockEntity) {

    }

    protected void onConnectionRemoved(boolean keepContents) {

    }

    protected void onModeChanged(TransportationMode mode) {

    }
}
