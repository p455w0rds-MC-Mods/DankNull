package p455w0rd.danknull.client.render;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rdslib.util.ReadableNumberConverter;

/**
 * @author p455w0rd
 *
 */
public class DankNullRenderItem extends RenderItem {

	private boolean useLg = false;
	ItemStack itemStack = ItemStack.EMPTY, dankNull = ItemStack.EMPTY;
	ContainerDankNull container;

	public DankNullRenderItem(TextureManager textureManager, ModelManager modelManager, ItemColors colors, @Nonnull ItemStack dankNull, boolean useLg, ContainerDankNull container) {
		super(textureManager, modelManager, colors);
		this.useLg = useLg;
		this.dankNull = dankNull;
		this.container = container;
	}

	@Override
	public void renderItemOverlayIntoGUI(FontRenderer fontRenderer, @Nonnull ItemStack is, int par4, int par5, String par6Str) {
		if (!is.isEmpty() && !dankNull.isEmpty()) {
			float scaleFactor = useLg ? 1.0F : 0.5F;
			float inverseScaleFactor = 1.0F / scaleFactor;
			int offset = useLg ? 0 : -1;
			String stackSize = "";

			boolean unicodeFlag = fontRenderer.getUnicodeFlag();
			fontRenderer.setUnicodeFlag(false);
			if (is.getItem().showDurabilityBar(is)) {
				double health = is.getItem().getDurabilityForDisplay(is);
				int j = (int) Math.round(13.0D - health * 13.0D);
				int i = (int) Math.round(255.0D - health * 255.0D);

				GlStateManager.disableDepth();
				GlStateManager.disableTexture2D();

				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder vertexbuffer = tessellator.getBuffer();
				draw(vertexbuffer, par4 + 2, par5 + 13, 13, 2, 0, 0, 0, 255);
				draw(vertexbuffer, par4 + 2, par5 + 13, 12, 1, (255 - i) / 4, 64, 0, 255);
				draw(vertexbuffer, par4 + 2, par5 + 13, j, 1, 255 - i, i, 0, 255);

				GlStateManager.enableTexture2D();

				GlStateManager.enableDepth();
			}
			int amount = 0;
			amount = is.getCount();
			if (container != null) {
				//amount = container.getDankNullInventory().getSizeForSlot(DankNullUtils.getIndexForStack(container.getDankNullInventory(), is));
			}
			if (amount < 0 || amount > 127) {
				//amount = (byte) amount & (0xff);
			}
			if (amount != 0) {
				scaleFactor = 0.5F;
				inverseScaleFactor = 1.0F / scaleFactor;
				offset = -1;
				stackSize = getToBeRenderedStackSize(amount);
			}
			GlStateManager.disableLighting();
			GlStateManager.disableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.disableDepth();
			GlStateManager.pushMatrix();
			GlStateManager.scale(scaleFactor, scaleFactor, scaleFactor);
			int X = (int) ((par4 + offset + 16.0F - fontRenderer.getStringWidth(stackSize) * scaleFactor) * inverseScaleFactor);
			int Y = (int) ((par5 + offset + 16.0F - 7.0F * scaleFactor) * inverseScaleFactor);
			if (amount > 1L) {
				fontRenderer.drawStringWithShadow(stackSize, X, Y, 16777215);
			}
			GlStateManager.popMatrix();
			GlStateManager.enableDepth();
			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.enableLighting();

			fontRenderer.setUnicodeFlag(unicodeFlag);
		}
	}

	private void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		renderer.pos(x + 0, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos(x + 0, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos(x + width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos(x + width, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
		Tessellator.getInstance().draw();
	}

	private String getToBeRenderedStackSize(long originalSize) {
		if (useLg) {
			return ReadableNumberConverter.INSTANCE.toSlimReadableForm(originalSize);
		}
		return ReadableNumberConverter.INSTANCE.toWideReadableForm(originalSize);
	}

	public ItemStack getStack() {
		return itemStack;
	}

	public void setStack(@Nonnull ItemStack stack, boolean regularSlotStack) {
		itemStack = stack;
		useLg = regularSlotStack;
	}
}
