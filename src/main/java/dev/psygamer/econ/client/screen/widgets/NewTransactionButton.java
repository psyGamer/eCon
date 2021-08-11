package dev.psygamer.econ.client.screen.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class NewTransactionButton extends Button {
	
	private final float scale;
	
	public NewTransactionButton(final int x, final int y, final int width, final int height, final float scale, final ITextComponent content, final IPressable onPress) {
		super(x, y, width, height, content, onPress);
		
		this.scale = 1;
	}
	
	public NewTransactionButton(final int x, final int y, final int width, final int height, final float scale, final ITextComponent content, final IPressable onPress, final ITooltip onToolTip) {
		super(x, y, width, height, content, onPress, onToolTip);
		
		this.scale = scale;
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		final Minecraft minecraft = Minecraft.getInstance();
		final FontRenderer fontrenderer = minecraft.font;
		
		minecraft.getTextureManager().bind(WIDGETS_LOCATION);

//		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		
		final int yImage = this.getYImage(this.isHovered());
		final int foregroundColor = getFGColor();
		
		this.blit(matrix, this.x, this.y, 0, 46 + yImage * 20, this.width / 2, this.height);
		this.blit(matrix, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + yImage * 20, this.width / 2, this.height);
		this.renderBg(matrix, minecraft, mouseX, mouseY);
		
		matrix.pushPose();
		matrix.scale(this.scale, this.scale, this.scale);
		
		drawCenteredString(matrix, fontrenderer, this.getMessage(), (int) ((this.x + this.width / 2) / this.scale), (int) ((this.y + (this.height - 8) / 2) / this.scale), foregroundColor | MathHelper.ceil(this.alpha * 255.0F) << 24);
		
		matrix.popPose();
	}
}
