package dev.JustRed23.createdimensions.blocks.blockentities;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class TransporterEntity extends SmartBlockEntity {

    protected BlockPos connectedTo;
    protected ResourceKey<Level> dimension;
    private TransportationMode mode = TransportationMode.INSERT;
    private boolean preventSync = false; // Prevents a sync loop when syncing with connected block

    protected abstract boolean trySync(TransporterEntity blockEntity);
    protected abstract void trySyncContents(TransporterEntity blockEntity);
    protected abstract void onConnectionRemoved(boolean keepContents);
    protected abstract void onModeChanged(TransportationMode mode);

    public TransporterEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void initialize() {
        super.initialize();
        if (isConnected()) connectTo(connectedTo, dimension); // Attempt to reconnect with the data we got from nbt
    }

    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);

        if (!clientPacket) {
            if (!tag.contains("RemoteConnection"))
                return;

            CompoundTag connectionTag = tag.getCompound("RemoteConnection");
            if (!connectionTag.contains("pos") || !connectionTag.contains("dimension"))
                return;

            int[] pos = connectionTag.getIntArray("pos");
            connectedTo = new BlockPos(pos[0], pos[1], pos[2]);

            dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(connectionTag.getString("dimension")));
        }

        mode = NBTHelper.readEnum(tag, "Mode", TransportationMode.class);
        setMode(mode); // Actually update the mode
    }

    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);

        if (!clientPacket) {
            if (!isConnected()) return;

            CompoundTag connectionTag = new CompoundTag();
            connectionTag.putIntArray("pos", new int[]{connectedTo.getX(), connectedTo.getY(), connectedTo.getZ()});
            connectionTag.putString("dimension", dimension.location().toString());
            tag.put("RemoteConnection", connectionTag);
        }

        NBTHelper.writeEnum(tag, "Mode", mode);
    }

    public void destroy() {
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

        if (other.preventSync) return;

        other.preventSync = true;
        trySyncContents(other);
        other.preventSync = false;
    }

    public ConnectionStatus connectTo(BlockPos pos, ResourceKey<Level> dimension) {
        if (pos == null || dimension == null) return ConnectionStatus.INVALID_DATA;
        if (Objects.equals(dimension, this.dimension) && Objects.equals(pos, this.connectedTo)) return ConnectionStatus.ALREADY_CONNECTED; // Already connected to this block

        if (Objects.equals(getBlockPos(), pos) && Objects.equals(getLevel(), dimension))
            return ConnectionStatus.CANNOT_CONNECT_TO_SELF;

        ServerLevel dimensionLevel = getLevel().getServer().getLevel(dimension);
        if (dimensionLevel == null) return ConnectionStatus.INVALID_LEVEL;

        final BlockEntity blockEntity = dimensionLevel.getBlockEntity(pos);
        if (!(blockEntity instanceof TransporterEntity other))
            return ConnectionStatus.INVALID_BLOCK;

        if (!trySync(other))
            return ConnectionStatus.SYNC_FAILURE;

        this.connectedTo = pos;
        this.dimension = dimension;

        other.connectedTo = getBlockPos();
        other.dimension = getLevel().dimension();

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
    }

    public void clearConnection(boolean keepContents) {
        clearConnection(keepContents, false);
    }

    public void clearConnection(boolean keepContents, boolean keepBoth) {
        if (!isConnected()) return;

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

    public enum TransportationMode {
        EXTRACT, INSERT
    }

    public enum ConnectionStatus {
        INVALID_DATA("provided data is invalid"),
        ALREADY_CONNECTED("already connected to this block"),
        CANNOT_CONNECT_TO_SELF("cannot connect to self"),
        INVALID_LEVEL("invalid world"),
        INVALID_BLOCK("invalid block"),
        SYNC_FAILURE("something went wrong while trying to sync"),
        SUCCESS;

        final String description;

        ConnectionStatus() {
            this("");
        }

        ConnectionStatus(String description) {
            this.description = description;
        }

        public String description() {
            return description;
        }
    }
}
