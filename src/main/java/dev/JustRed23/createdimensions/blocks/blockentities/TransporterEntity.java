package dev.JustRed23.createdimensions.blocks.blockentities;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.utility.NBTHelper;
import dev.JustRed23.createdimensions.behaviour.ISync;
import dev.JustRed23.createdimensions.utils.TransporterUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class TransporterEntity extends SmartBlockEntity implements ISync {

    protected BlockPos connectedTo;
    protected ResourceKey<Level> dimension;
    private TransportationMode mode = TransportationMode.INSERT;
    private boolean chunkLoaded = false;
    private boolean preventSync = false; // Prevents a sync loop when syncing with connected block

    protected abstract boolean tryConnect(TransporterEntity blockEntity);
    protected abstract void trySyncContents(TransporterEntity blockEntity);
    protected abstract void onConnectionRemoved(boolean keepContents);
    protected void dropContents() {}
    protected abstract void onModeChanged(TransportationMode mode);

    public TransporterEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);

        if (tag.contains("Mode")) {
            mode = NBTHelper.readEnum(tag, "Mode", TransportationMode.class);
            onModeChanged(mode);
        }

        if (!tag.contains("RemoteConnection")) {
            this.connectedTo = null;
            this.dimension = null;
            return;
        }

        CompoundTag connectionTag = tag.getCompound("RemoteConnection");
        if (!connectionTag.contains("pos") || !connectionTag.contains("dimension"))
            return;

        connectedTo = NbtUtils.readBlockPos(connectionTag.getCompound("pos"));
        dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(connectionTag.getString("dimension")));

        chunkLoaded = tag.getBoolean("ChunkLoaded");
        if (chunkLoaded) TransporterUtils.handleChunkLoading(this, getLevel(), getUpgradeInventory());
    }

    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);

        NBTHelper.writeEnum(tag, "Mode", mode);
        tag.putBoolean("ChunkLoaded", chunkLoaded);

        if (!isConnected()) return;
        CompoundTag connectionTag = new CompoundTag();
        connectionTag.put("pos", NbtUtils.writeBlockPos(connectedTo));
        connectionTag.putString("dimension", dimension.location().toString());
        tag.put("RemoteConnection", connectionTag);
    }

    public void destroy() {
        TransporterUtils.handleDestroyed(this, getLevel());
        clearConnection(false);
        super.destroy();
    }

    public void syncWithConnected() {
        if (!(level instanceof ServerLevel) || !isConnected()) return;

        ServerLevel dimensionLevel = level.getServer().getLevel(dimension);
        if (dimensionLevel == null)
            return;

        if (!(dimensionLevel.getBlockEntity(connectedTo) instanceof TransporterEntity other)) {
            clearConnection(true);
            return;
        }

        if (this.preventSync || other.preventSync) return;

        other.preventSync = true;
        trySyncContents(other);
        other.preventSync = false;
    }

    public ConnectionStatus connectTo(BlockPos pos, ResourceKey<Level> dimension) {
        if (pos == null || dimension == null) return ConnectionStatus.INVALID_DATA;
        if (Objects.equals(dimension, this.dimension) && Objects.equals(pos, this.connectedTo)) return ConnectionStatus.ALREADY_CONNECTED; // Already connected to this block

        if (Objects.equals(getBlockPos(), pos) && Objects.equals(getLevel().dimension(), dimension))
            return ConnectionStatus.CANNOT_CONNECT_TO_SELF;

        ServerLevel dimensionLevel = getLevel().getServer().getLevel(dimension);
        if (dimensionLevel == null) return ConnectionStatus.INVALID_LEVEL;

        final BlockEntity blockEntity = dimensionLevel.getBlockEntity(pos);
        if (!(blockEntity instanceof TransporterEntity other) || !this.getClass().equals(other.getClass()))
            return ConnectionStatus.INVALID_BLOCK;

        if (!tryConnect(other))
            return ConnectionStatus.CONNECT_FAILURE;

        this.connectedTo = pos;
        this.dimension = dimension;

        other.connectedTo = getBlockPos();
        other.dimension = getLevel().dimension();

        this.notifyUpdate();
        other.notifyUpdate();

        syncWithConnected();
        return ConnectionStatus.SUCCESS;
    }

    public boolean isConnected() {
        return dimension != null && connectedTo != null;
    }

    private void disconnect(boolean keepContents) {
        this.connectedTo = null;
        this.dimension = null;
        onConnectionRemoved(keepContents);
        notifyUpdate();
    }

    public void clearConnection(boolean keepContents) {
        clearConnection(keepContents, false);
    }

    public void clearConnection(boolean keepContents, boolean keepBoth) {
        if (!isConnected()) {
            dropContents();
            return;
        }

        ServerLevel dimensionLevel = level.getServer().getLevel(this.dimension);
        if (dimensionLevel == null) return;

        if (!(dimensionLevel.getBlockEntity(connectedTo) instanceof TransporterEntity other)) return;

        other.disconnect(keepBoth || !keepContents);
        disconnect(keepBoth || keepContents);
    }

    public BlockPos getConnection() {
        return connectedTo;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public void setMode(@NotNull TransportationMode mode) {
        if (mode.equals(this.mode)) return;
        this.mode = mode;
        onModeChanged(mode);
    }

    public TransportationMode getMode() {
        return mode;
    }

    public void switchMode() {
        setMode(getMode() == TransportationMode.INSERT ? TransportationMode.EXTRACT : TransportationMode.INSERT);
    }

    public boolean isChunkLoaded() {
        return chunkLoaded;
    }

    public void setChunkLoaded(boolean chunkLoaded) {
        this.chunkLoaded = chunkLoaded;
    }

    public enum TransportationMode {
        INSERT, EXTRACT
    }

    public enum ConnectionStatus {
        INVALID_DATA, ALREADY_CONNECTED, CANNOT_CONNECT_TO_SELF, INVALID_LEVEL, INVALID_BLOCK, CONNECT_FAILURE, SUCCESS;
    }
}
