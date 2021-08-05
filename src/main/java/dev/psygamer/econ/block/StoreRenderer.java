package dev.psygamer.econ.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class StoreRenderer extends TileEntityRenderer<StoreTileEntity> {
	
	private final Minecraft minecraft = Minecraft.getInstance();
	private final ItemRenderer itemRenderer = this.minecraft.getItemRenderer();
	
	private final float rotationSpeed = 0.03f;
	
	private float rotation = 0.0f;
	private float yTranslation = 0.0f;
	
	public StoreRenderer(final TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public void render(final StoreTileEntity tileEntity, final float partialTicks,
					   final MatrixStack matrix, final IRenderTypeBuffer renderBuffer,
					   final int combinedLight, final int combinedOverlay
	) {
		if (tileEntity.getItem() == ItemStack.EMPTY || tileEntity.getItem().getItem() == Items.AIR) {
			return;
		}
		
		final Direction facingDirection = tileEntity.getLevel().getBlockState(tileEntity.getBlockPos()).getValue(StoreBlock.FACING);
		
		final int lightLevel = getLightLevel(tileEntity.getLevel(), tileEntity.getBlockPos());
		final Vector2f unitOffset = getUnitOffset(facingDirection);
		
		this.rotation = (this.rotation + (this.rotationSpeed * partialTicks)) % 360;
		this.yTranslation = (float) (Math.sin(Math.toRadians(this.rotation * 110)) * 0.05 + 0.05);
		
		renderItem(
				matrix, tileEntity.getItem(), renderBuffer,
				
				new Vector3f(0.5f + unitOffset.x / 20f, 0.3f + this.yTranslation, 0.5f + unitOffset.y / 20f),
				Vector3f.YP.rotation(this.rotation), 1.0f,
				
				partialTicks, combinedLight, lightLevel
		);
		
		renderPriceLabel(matrix, renderBuffer, 99999,
				facingDirection, lightLevel, 0x2B303D
		);
		renderStockLabel(matrix, renderBuffer, 64,
				facingDirection, lightLevel, 0xDCDCDC
		);
	}
	
	private void renderItem(final MatrixStack matrix, final ItemStack itemStack, final IRenderTypeBuffer renderBuffer,
							final Vector3f translation, final Quaternion rotation, final float scale,
							final float partialTicks, final int combinedLight, final int lightLevel
	) {
		matrix.pushPose();
		matrix.scale(scale, scale, scale);
		matrix.translate(translation.x(), translation.y(), translation.z());
//		matrix.translate((1 - scale) / 2f, (1 - scale) / 2f, (1 - scale) / 2f);
		matrix.mulPose(rotation);
		
		this.itemRenderer.render(
				itemStack, ItemCameraTransforms.TransformType.GROUND, true,
				matrix, renderBuffer, lightLevel, combinedLight, this.itemRenderer.getModel(
						itemStack, null, null
				)
		);
		
		matrix.popPose();
	}
	
	private void renderPriceLabel(final MatrixStack matrixStack, final IRenderTypeBuffer renderBuffer, final int price,
								  final Direction facingDirection, final int lightLevel, final int color
	) {
		final FontRenderer font = this.minecraft.font;
		
		final float scale = 13f / 16f;
		
		final Vector3f translation;
		final Vector3f rotation;
		
		switch (facingDirection) {
			case WEST:
				translation = new Vector3f(-1, 1, 2.5f);
				rotation = new Vector3f(-22.5f, 270, 0);
				break;
			case SOUTH:
				translation = new Vector3f(-1.5f, 1, 14.75f);
				rotation = new Vector3f(22.5f, 180, 0);
				break;
			case EAST:
				translation = new Vector3f(-14.5f, 1, 14.5f);
				rotation = new Vector3f(0, 90, 22.5f);
				break;
			default:
				translation = new Vector3f(-14.5f, 1, 1.25f);
				rotation = new Vector3f(-22.5f, 0, 0);
				break;
		}
		
		matrixStack.pushPose();
		
		matrixStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
		matrixStack.mulPose(
				Vector3f.ZP.rotationDegrees(180f)
		);
		matrixStack.translate(0, -font.lineHeight, 0);
		
		matrixStack.translate(translation.x(), translation.y(), translation.z());
		matrixStack.mulPose(
				Vector3f.YP.rotationDegrees(rotation.y())
		);
		
		// cos(22.5°) * h = 0.9238795325112867 * h
		
		matrixStack.translate(0.5f, font.lineHeight - 2.11f - 0.9238795325112867f * 2.3f, 2.3f / 2f);
		
		
		matrixStack.mulPose(
				Vector3f.XP.rotationDegrees(rotation.x())
		);
		matrixStack.mulPose(
				Vector3f.ZP.rotationDegrees(rotation.z())
		);
//		matrixStack.translate(0, -5, 0);

//		matrixStack.scale(scale, scale, scale);
		matrixStack.scale(2 / 7f, 2 / 7f, 2 / 7f);
		
		font.drawInBatch(new StringTextComponent(price + "\u20AC"), 0, 0, color,
				false, matrixStack.last().pose(), renderBuffer,
				false, 0x00000000, lightLevel
		);
		
		matrixStack.popPose();
	}
	
	private void renderStockLabel(final MatrixStack matrixStack, final IRenderTypeBuffer renderBuffer, final int stockLeft,
								  final Direction facingDirection, final int lightLevel, final int color
	) {
		final FontRenderer font = this.minecraft.font;
		
		final float scale = 13f / 16f;
		
		final Vector3f translation;
		final Vector3f rotation;
		
		switch (facingDirection) {
			case WEST:
				translation = new Vector3f(-1, 1, 2.5f);
				rotation = new Vector3f(-22.5f, 270, 0);
				break;
			case SOUTH:
				translation = new Vector3f(-1.5f, 1, 14.75f);
				rotation = new Vector3f(22.5f, 180, 0);
				break;
			case EAST:
				translation = new Vector3f(-14.5f, 1, 14.5f);
				rotation = new Vector3f(0, 90, 22.5f);
				break;
			default:
				translation = new Vector3f(-14.5f, 1, 1.25f);
				rotation = new Vector3f(-22.5f, 0, 0);
				break;
		}
		
		matrixStack.pushPose();
		
		matrixStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
		matrixStack.mulPose(
				Vector3f.ZP.rotationDegrees(180f)
		);
		matrixStack.translate(0, -font.lineHeight, 0);
		
		matrixStack.translate(translation.x(), translation.y(), translation.z());
		matrixStack.mulPose(
				Vector3f.YP.rotationDegrees(rotation.y())
		);
		
		// cos(22.5°) * h = 0.9238795325112867 * h
		matrixStack.translate(9, -1, 3.5);
		matrixStack.translate(-2.1, font.lineHeight - 2 - 0.9238795325112867f * 2.1f, 2.1f / 2f);
		
		matrixStack.mulPose(
				Vector3f.XP.rotationDegrees(rotation.x())
		);
		matrixStack.mulPose(
				Vector3f.ZP.rotationDegrees(rotation.z())
		);
//		matrixStack.translate(0, -5, 0);

//		matrixStack.scale(scale, scale, scale);
		matrixStack.scale(1 / 5f, 1 / 5f, 1 / 5f);
		
		font.drawInBatch(new StringTextComponent(String.valueOf(stockLeft)), 0, 0, color,
				false, matrixStack.last().pose(), renderBuffer,
				false, 0x00000000, lightLevel
		);
		
		matrixStack.popPose();
	}
	
	private int getLightLevel(final World world, final BlockPos pos) {
		return LightTexture.pack(
				world.getBrightness(LightType.BLOCK, pos),
				world.getBrightness(LightType.SKY, pos)
		);
	}
	
	private Vector2f getUnitOffset(final Direction facingDirection) {
		switch (facingDirection) {
			case WEST:
				return Vector2f.UNIT_X;
			case SOUTH:
				return Vector2f.UNIT_Y;
			case EAST:
				return Vector2f.NEG_UNIT_X;
			default:
				return Vector2f.NEG_UNIT_Y;
		}
	}
}
