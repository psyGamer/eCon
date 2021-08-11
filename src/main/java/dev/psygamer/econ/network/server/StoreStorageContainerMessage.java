package dev.psygamer.econ.network.server;

import dev.psygamer.econ.block.container.StoreStorageContainer;
import dev.psygamer.econ.block.tileentity.StoreTileEntity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;

import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class StoreStorageContainerMessage {
	
	private final BlockPos storePos;
	
	public StoreStorageContainerMessage(final BlockPos storePos) {
		this.storePos = storePos;
	}
	
	public StoreStorageContainerMessage(final PacketBuffer buffer) {
		this(buffer.readBlockPos());
	}
	
	public void encode(final PacketBuffer buffer) {
		buffer.writeBlockPos(this.storePos);
	}
	
	public void handle(final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			final ServerPlayerEntity serverPlayer = ctx.get().getSender();
			final ServerWorld serverWorld = serverPlayer.getLevel();
			
			if (!serverWorld.isLoaded(this.storePos)) return;
			
			final TileEntity tileEntity = serverWorld.getBlockEntity(this.storePos);
			
			if (!(tileEntity instanceof StoreTileEntity)) return;
			
			final INamedContainerProvider containerProvider = new INamedContainerProvider() {
				@Override
				public ITextComponent getDisplayName() {
					return StringTextComponent.EMPTY;
				}
				
				@Nullable
				@Override
				public Container createMenu(final int windowID, final PlayerInventory playerInventory, final PlayerEntity playerEntity) {
					return new StoreStorageContainer(windowID, playerInventory, (StoreTileEntity) tileEntity);
				}
			};
			
			NetworkHooks.openGui(serverPlayer, containerProvider, tileEntity.getBlockPos());
		});
		ctx.get().setPacketHandled(true);
	}
}
