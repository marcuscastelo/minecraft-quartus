package io.github.marcuscastelo.quartus.network;

import io.github.marcuscastelo.quartus.Quartus;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class QuartusFloppyDiskUpdateC2SPacket implements QuartusPacket {
    public static final Identifier ID = Quartus.id("floppy_disk_update_c2s");

    private BlockPos compilerPos;
    private CompoundTag compoundTag;


    public CompoundTag getCompoundTag() {
        return compoundTag;
    }

    public BlockPos getCompilerPos() {
        return compilerPos;
    }

    public QuartusFloppyDiskUpdateC2SPacket() {}

    public QuartusFloppyDiskUpdateC2SPacket(BlockPos compilerPos, ItemStack floppyItemStack) {
        this.compilerPos = compilerPos;
        this.compoundTag = floppyItemStack.getOrCreateTag();
    }

    @Override
    public void read(PacketByteBuf buf) {
        this.compilerPos = buf.readBlockPos();
        this.compoundTag = buf.readCompoundTag();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(compilerPos);
        buf.writeCompoundTag(compoundTag);
    }

    @Override
    public void send(PacketByteBuf buf) {
        ClientSidePacketRegistry.INSTANCE.sendToServer(ID, buf);
    }
}
