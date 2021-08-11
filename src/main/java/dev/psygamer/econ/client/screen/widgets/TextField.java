package dev.psygamer.econ.client.screen.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class TextField extends TextFieldWidget {
	
	@SubscribeEvent
	public static void onKeyPressed(final InputEvent.KeyInputEvent event) {
		if (currentlyFocused != null && event.getKey() == GLFW.GLFW_KEY_TAB) {
			currentlyFocused.completeSuggestion();
		}
	}
	
	private static TextField currentlyFocused;
	
	private int currentFrame;
	private int maxTextLength;
	private int highlightedPos;
	
	private boolean isValid;
	private String suggestion;
	
	private final FontRenderer font;
	
	private final float offsetRight, offsetTop;
	
	private static final int TEXT_COLOR = 14737632; // #E0E0E0
	private static final int TEXT_COLOR_INVALID = 16733525;// #FF5555
	private static final int TEXT_COLOR_SUGGESTION = 8421504;// #808080
	
	public TextField(final FontRenderer fontRenderer, final int x, final int y, final int width, final int height, final float offsetRight, final float offsetTop) {
		super(fontRenderer, x, y, width, height, StringTextComponent.EMPTY);
		
		this.font = fontRenderer;
		this.offsetRight = offsetRight;
		this.offsetTop = offsetTop;
	}
	
	private void completeSuggestion() {
		if (this.suggestion != null && this.suggestion.toLowerCase().startsWith(getValue().toLowerCase())) {
			setValue(this.suggestion);
		}
	}
	
	@Override
	public void tick() {
		this.currentFrame++;
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		renderBg(matrix, Minecraft.getInstance(), mouseX, mouseY);
		
		final int
				highlightedFrom = Math.min(getCursorPosition(), this.highlightedPos),
				highlightedTo = Math.max(getCursorPosition(), this.highlightedPos);
		
		final float
				xPos = this.x + this.offsetRight,
				yPos = this.y + this.offsetTop;
		
		final boolean
				shouldDrawCursor = this.isFocused() && this.currentFrame / 6 % 2 == 0,
				shouldDrawVerticalCursor = getCursorPosition() < getValue().length() || getValue().length() >= this.maxTextLength;
		
		if (this.suggestion != null && isFocused()) {
			try {
				this.font.drawShadow(matrix, this.suggestion.substring(getValue().length()),
						xPos + this.font.width(getValue()), yPos,
						
						TEXT_COLOR_SUGGESTION
				);
			} catch (final StringIndexOutOfBoundsException ex) {
				ex.printStackTrace();
			}
		}
		
		this.font.drawShadow(matrix, getValue(),
				xPos, yPos,
				
				this.isValid ? TEXT_COLOR : TEXT_COLOR_INVALID
		);
		
		if (highlightedFrom != highlightedTo) {
			renderHighlight(
					(int) Math.ceil(xPos + this.font.width(getValue().substring(0, highlightedTo))), (int) yPos + this.font.lineHeight + 1,
					(int) Math.floor(xPos + this.font.width(getValue().substring(0, highlightedFrom))), (int) yPos - 1
			);
		}
		
		if (shouldDrawCursor) {
			if (shouldDrawVerticalCursor) {
				AbstractGui.fill(matrix,
						(int) xPos + this.font.width(getValue().substring(0, getCursorPosition())), (int) yPos - 1,
						(int) xPos + this.font.width(getValue().substring(0, getCursorPosition())) + 1, (int) yPos + this.font.lineHeight + 1,
						
						TEXT_COLOR + 0xFF000000
				);
			} else {
				this.font.drawShadow(matrix, "_",
						xPos + this.font.width(getValue()), yPos,
						
						TEXT_COLOR
				);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void renderHighlight(int topRightX, int topRightY, int bottomLeftX, int bottomLeftY) {
		if (topRightX < bottomLeftX) {
			final int i = topRightX;
			topRightX = bottomLeftX;
			bottomLeftX = i;
		}
		
		if (topRightY < bottomLeftY) {
			final int j = topRightY;
			topRightY = bottomLeftY;
			bottomLeftY = j;
		}
		
		if (bottomLeftX > this.x + this.width) {
			bottomLeftX = this.x + this.width;
		}
		
		if (topRightX > this.x + this.width) {
			topRightX = this.x + this.width;
		}
		
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder bufferbuilder = tessellator.getBuilder();
		RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.vertex(topRightX, bottomLeftY, 0.0D).endVertex();
		bufferbuilder.vertex(bottomLeftX, bottomLeftY, 0.0D).endVertex();
		bufferbuilder.vertex(bottomLeftX, topRightY, 0.0D).endVertex();
		bufferbuilder.vertex(topRightX, topRightY, 0.0D).endVertex();
		tessellator.end();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}
	
	public boolean isValid() {
		return this.isValid;
	}
	
	public void setValid(final boolean valid) {
		this.isValid = valid;
	}
	
	@Override
	public void setSuggestion(final String suggestion) {
		this.suggestion = suggestion;
	}
	
	@Override
	public void renderButton(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		// Disable rendering of default TextFieldWidget
	}
	
	@Override
	protected void onFocusedChanged(final boolean reset) {
		if (reset) {
			this.currentFrame = 0;
		}
	}
	
	@Override
	public void setFocus(final boolean focus) {
		if (focus)
			currentlyFocused = this;
		
		super.setFocus(focus);
	}
	
	@Override
	public void setHighlightPos(final int highlightPos) {
		this.highlightedPos = highlightPos;
		
		super.setHighlightPos(highlightPos);
	}
	
	@Override
	public void setMaxLength(final int maxLength) {
		this.maxTextLength = maxLength;
		
		super.setMaxLength(maxLength);
	}
}
