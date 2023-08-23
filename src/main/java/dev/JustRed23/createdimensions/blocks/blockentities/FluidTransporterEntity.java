package dev.JustRed23.createdimensions.blocks.blockentities;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import dev.JustRed23.createdimensions.DimensionsAddon;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static dev.JustRed23.createdimensions.utils.ItemUtils.isHoldingSynchronizerCard;

public class FluidTransporterEntity extends TransporterEntity implements IHaveGoggleInformation {

    private SmartFluidTankBehaviour tank;

    public FluidTransporterEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 2000).whenFluidUpdates(super::syncWithConnected));
        setMode(TransportationMode.INSERT);
        tank.getPrimaryHandler().setValidator(fluid -> isConnected()); // Only allow fluid to be inserted/extracted if the block is connected
    }

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && isConnected())
            return tank.getCapability().cast();
        return super.getCapability(cap, side);
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (isHoldingSynchronizerCard())
            return false;

        Optional<? extends IFluidHandler> resolve = tank.getCapability().resolve();
        if (resolve.isEmpty())
            return false;

        IFluidHandler tank = resolve.get();
        if (tank.getTanks() == 0)
            return false;

        LangBuilder mb = Lang.translate("generic.unit.millibuckets");
        Lang.builder(DimensionsAddon.MODID).translate("gui.fluid_transporter.title")
                .forGoggles(tooltip);

        boolean isEmpty = true;
        for (int i = 0; i < tank.getTanks(); i++) {
            FluidStack fluidStack = tank.getFluidInTank(i);
            if (fluidStack.isEmpty())
                continue;

            Lang.fluidName(fluidStack)
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip, 1);

            Lang.builder()
                    .add(Lang.number(fluidStack.getAmount())
                            .add(mb)
                            .style(ChatFormatting.GOLD))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(Lang.number(tank.getTankCapacity(i))
                            .add(mb)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);

            isEmpty = false;
        }

        if (tank.getTanks() > 1) {
            if (isEmpty)
                tooltip.remove(tooltip.size() - 1);
            return true;
        }

        if (!isEmpty)
            return true;

        Lang.translate("gui.goggles.fluid_container.capacity")
                .add(Lang.number(tank.getTankCapacity(0))
                        .add(mb)
                        .style(ChatFormatting.GOLD))
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);

        return true;
    }

    protected boolean tryConnect(TransporterEntity blockEntity) {
        if (!(blockEntity instanceof FluidTransporterEntity other)) // Cannot connect if the block entity is not of the same type
            return false;

        if (this.isVirtual() || other.isVirtual()) // Cannot connect if either of the blocks are virtual
            return false;

        if (this.isConnected() || other.isConnected()) // Cannot connect if either of the blocks are already connected
            return false;

        if (!this.tank.isEmpty() && !other.tank.isEmpty()) // Cannot connect if both tanks are not empty
            return this.tank.getPrimaryHandler().getFluid().isFluidStackIdentical(other.tank.getPrimaryHandler().getFluid()); // Cannot connect if the fluids are not the same

        return true;
    }

    protected void trySyncContents(TransporterEntity blockEntity) {
        if (!(blockEntity instanceof FluidTransporterEntity other)) return;

        other.tank.getPrimaryHandler().setFluid(this.tank.getPrimaryHandler().getFluid());
        other.setChanged();
    }

    protected void onConnectionRemoved(boolean keepContents) {
        if (!keepContents)
            tank.getPrimaryHandler().setFluid(FluidStack.EMPTY);
    }

    protected void onModeChanged(TransportationMode mode) {
        switch (mode) {
            case INSERT -> {
                tank.allowInsertion();
                tank.forbidExtraction();
            }
            case EXTRACT -> {
                tank.forbidInsertion();
                tank.allowExtraction();
            }
        }
        setChanged();
    }

    public LerpedFloat getFluidLevel() {
        return tank.getPrimaryTank().getFluidLevel();
    }

    public SmartFluidTank getTank() {
        return tank.getPrimaryHandler();
    }
}
