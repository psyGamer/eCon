package dev.psygamer.econ.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.psygamer.econ.gui.BankAccountScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class PageButtons extends Widget {
	
	public static final int guiWidth = 116;
	public static final int guiHeight = 20;
	
	private static final int margin = 4;
	
	private final BankAccountScreen parent;
	
	private final Button nextPage;
	private final Button prevPage;
	
	public PageButtons(final int x, final int y, final BankAccountScreen parent) {
		super(x, y, 0, 0, StringTextComponent.EMPTY);
		
		this.parent = parent;
		
		this.prevPage = new Button(x, y, guiWidth / 2 - margin / 2, guiHeight, new StringTextComponent("\u00AB"), button -> {
			PageButtons.this.parent.setCurrentPage(PageButtons.this.parent.getCurrentPage() - 1);
		});
		this.nextPage = new Button(x + guiWidth / 2 + margin / 2, y, guiWidth / 2 - margin / 2, guiHeight, new StringTextComponent("\u00BB"), button -> {
			PageButtons.this.parent.setCurrentPage(PageButtons.this.parent.getCurrentPage() + 1);
		});

//		this.visible = false;
	}
	
	@Override
	public void render(final MatrixStack matrix, final int mouseX, final int mouseY, final float partialTicks) {
		super.render(matrix, mouseX, mouseY, partialTicks);

//		this.nextPage.x = mouseX;
//		this.nextPage.y = mouseY;
//		this.prevPage.x = mouseX + guiWidth / 2;
//		this.prevPage.y = mouseY;
		
		this.nextPage.active = this.parent.getCurrentPage() != this.parent.getMaxPages();
		this.prevPage.active = this.parent.getCurrentPage() != 0;
		
		this.nextPage.render(matrix, mouseX, mouseY, partialTicks);
		this.prevPage.render(matrix, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public boolean mouseClicked(final double mouseX, final double mouseY, final int widgetId) {
		this.nextPage.mouseClicked(mouseX, mouseY, widgetId);
		this.prevPage.mouseClicked(mouseX, mouseY, widgetId);
		
		return super.mouseClicked(mouseX, mouseY, widgetId);
	}
}
