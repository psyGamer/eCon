package dev.psygamer.econ.block;

import dev.psygamer.econ.block.tileentity.StoreTileEntity;

import com.google.common.collect.Lists;

import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class StoreBlock extends HorizontalBlock {
	
	public static final DirectionProperty FACING = HorizontalBlock.FACING;
	
	private static final VoxelShape SHAPE_NORTH = Stream.of(
			Block.box(2.5, 1, 3.5, 13.5, 12, 14.5),
			Block.box(1, 0, 1, 15, 1, 15),
			Block.box(1.5, 1, 1.25, 14.5, 3, 2.25)
	).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape SHAPE_EAST = Stream.of(
			Block.box(1.5, 1, 2.5, 12.5, 12, 13.5),
			Block.box(1, 0, 1, 15, 1, 15),
			Block.box(13.730969883127822, 0.9043291419087276, 1.5, 14.730969883127822, 2.9043291419087276, 14.5)
	).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape SHAPE_SOUTH = Stream.of(
			Block.box(2.5, 1, 1.5, 13.5, 12, 12.5),
			Block.box(1, 0, 1, 15, 1, 15),
			Block.box(1.5, 0.9043291419087276, 13.730969883127822, 14.5, 2.9043291419087276, 14.730969883127822)
	).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape SHAPE_WEST = Stream.of(
			Block.box(3.5, 1, 2.5, 14.5, 12, 13.5),
			Block.box(1, 0, 1, 15, 1, 15),
			Block.box(1.2690301168721785, 0.9043291419087276, 1.5, 2.2690301168721785, 2.9043291419087276, 14.5)
	).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
	
	public StoreBlock() {
		super(
				AbstractBlock.Properties.of(Material.METAL)
						.noOcclusion()
						.strength(1.2f)
						.sound(SoundType.WOOD)
						.harvestTool(ToolType.AXE)
		);
		
		this.registerDefaultState(
				this.stateDefinition.any().setValue(FACING, Direction.NORTH)
		);
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(final BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public void setPlacedBy(final World world, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack heldItem) {
		if (!world.isClientSide() && world.isLoaded(pos)) {
			final TileEntity tileEntity = world.getBlockEntity(pos);
			
			if (tileEntity instanceof StoreTileEntity) {
				((StoreTileEntity) tileEntity).setOwner(placer.getUUID());
			}
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(final BlockState oldBlockState, final World world, final BlockPos pos, final BlockState newBlockState, final boolean p_196243_5_) {
		if (oldBlockState.hasTileEntity() && !oldBlockState.is(newBlockState.getBlock())) {
			final TileEntity tileEntity = world.getBlockEntity(pos);
			
			if (tileEntity instanceof StoreTileEntity) {
				final StoreTileEntity storeTileEntity = (StoreTileEntity) tileEntity;
				for (int i = 1 ; i < storeTileEntity.getSlots() ; i++) {
					Block.popResource(world, pos, storeTileEntity.getStackInSlot(i));
				}
			}
			
			world.removeBlockEntity(pos);
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public List<ItemStack> getDrops(final BlockState p_220076_1_, final LootContext.Builder p_220076_2_) {
		return Lists.newArrayList(new ItemStack(asItem()));
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public ActionResultType use(final BlockState state, final World world, final BlockPos pos, final PlayerEntity playerEntity, final Hand hand, final BlockRayTraceResult hit) {
		if (!world.isClientSide()) {
			final TileEntity tileEntity = world.getBlockEntity(pos);
			
			if (tileEntity instanceof StoreTileEntity) {
				final StoreTileEntity storeTileEntity = (StoreTileEntity) tileEntity;
				
				if (storeTileEntity.getOwner() == null)
					storeTileEntity.setOwner(playerEntity.getUUID());
//				playerEntity.openMenu(containerProvider);
				NetworkHooks.openGui((ServerPlayerEntity) playerEntity, storeTileEntity, pos);
				
				return ActionResultType.CONSUME;
			}
		}
		
		return ActionResultType.SUCCESS;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(final BlockState state, final IBlockReader reader, final BlockPos pos, final ISelectionContext context) {
		switch (state.getValue(FACING)) {
			case SOUTH:
				return SHAPE_SOUTH;
			case WEST:
				return SHAPE_WEST;
			case EAST:
				return SHAPE_EAST;
			default:
				return SHAPE_NORTH;
		}
	}
	
	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return new StoreTileEntity();
	}
	
	@Override
	protected void createBlockStateDefinition(final StateContainer.Builder<Block, BlockState> stateBuilder) {
		stateBuilder.add(FACING);
	}
}
