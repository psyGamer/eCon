package dev.psygamer.econ.gui.widgets;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
	
	private static final int TEXT_COLOR = Color.parseColor("#E0E0E0").getValue();
	private static final int TEXT_COLOR_INVALID_ = Color.parseColor("#FF5555").getValue();
	private static final int TEXT_COLOR_SUGGESTION = Color.parseColor("#808080").getValue();
	
	public TextField(final FontRenderer fontRenderer, final int x, final int y, final int width, final int height) {
		super(fontRenderer, x, y, width, height, StringTextComponent.EMPTY);
		
		this.font = fontRenderer;
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
		
		final int highlightedFrom = Math.min(getCursorPosition(), this.highlightedPos);
		final int highlightedTo = Math.max(getCursorPosition(), this.highlightedPos);
		
		final boolean shouldDrawCursor = this.isFocused() && this.currentFrame / 6 % 2 == 0;
		final boolean shouldDrawVerticalCursor = getCursorPosition() < getValue().length() || getValue().length() >= this.maxTextLength;
		
		if (this.suggestion != null && isFocused()) {
			try {
				this.font.drawShadow(matrix, this.suggestion.substring(getValue().length()),
						this.x + this.font.width(getValue()) + 5, this.y + this.height - this.font.lineHeight - 3,
						
						TEXT_COLOR_SUGGESTION
				);
			} catch (final StringIndexOutOfBoundsException ex) {
				ex.printStackTrace();
			}
		}
		
		this.font.drawShadow(matrix, getValue(),
				this.x + 5, this.y + this.height - this.font.lineHeight - 3,
				
				this.isValid ? TEXT_COLOR : TEXT_COLOR_INVALID_
		);
		
		renderHighlight(
				this.x + 5 + this.font.width(getValue().substring(0, highlightedTo)), this.y + this.height - 13,
				this.x + 5 + this.font.width(getValue().substring(0, highlightedFrom)), this.y + this.height - 2
		);
		
		if (shouldDrawCursor) {
			if (shouldDrawVerticalCursor) {
				AbstractGui.fill(matrix,
						this.x + 5 + this.font.width(getValue().substring(0, getCursorPosition())), this.y + this.height - 2,
						this.x + 5 + this.font.width(getValue().substring(0, getCursorPosition())) + 1, this.y + this.height - 13,
						
						TEXT_COLOR + 0xFF000000
				);
			} else {
				this.font.drawShadow(matrix, "_",
						this.x + 5 + this.font.width(getValue()), this.y + this.height - this.font.lineHeight - 3,
						
						TEXT_COLOR
				);
			}
		}
	}
	
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
		bufferbuilder.vertex((double) topRightX, (double) bottomLeftY, 0.0D).endVertex();
		bufferbuilder.vertex((double) bottomLeftX, (double) bottomLeftY, 0.0D).endVertex();
		bufferbuilder.vertex((double) bottomLeftX, (double) topRightY, 0.0D).endVertex();
		bufferbuilder.vertex((double) topRightX, (double) topRightY, 0.0D).endVertex();
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
