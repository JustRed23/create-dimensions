package dev.JustRed23.createdimensions.blocks.blockentities;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ItemTransporterEntity extends TransporterEntity {

    public ItemTransporterEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this)
                .allowingBeltFunnels()
                .onlyInsertWhen(side -> side != Direction.DOWN && getMode() == TransportationMode.INSERT)
        );
    }

    protected boolean trySync(TransporterEntity blockEntity) {
        if (!(blockEntity instanceof ItemTransporterEntity other)) // Cannot connect if the block entity is not of the same type
            return false;

        if (this.isVirtual() || other.isVirtual()) // Cannot connect if either of the blocks are virtual
            return false;

        if (this.isConnected() || other.isConnected()) // Cannot connect if either of the blocks are already connected
            return false;

        //Todo: check if inventories are equal if not empty

        return false; //Todo: return true when item above is implemented
    }

    protected void trySyncContents(TransporterEntity blockEntity) {
        if (!(blockEntity instanceof ItemTransporterEntity other)) return;

        //Todo: implement inventory content sync
        other.setChanged();
    }

    protected void onConnectionRemoved(boolean keepContents) {
        /* Todo: if (!keepContents)
            inventory.clearContent();*/
        setChanged();
    }

    protected void onModeChanged(TransportationMode mode) {
        /* Todo: switch (mode) {
            case INSERT -> {
                inventory.allowInsertion();
                inventory.forbidExtraction();
            }
            case EXTRACT -> {
                inventory.forbidInsertion();
                inventory.allowExtraction();
            }
        }*/
        setChanged();
    }

    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        //Todo: inventory.deserializeNBT(tag.getCompound("Inventory"));
    }

    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        //Todo: tag.put("Inventory", inventory.serializeNBT());
    }
}
