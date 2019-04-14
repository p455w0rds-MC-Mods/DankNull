package p455w0rd.danknull.client.render;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.ReadableNumberConverter;

/**
 * @author p455w0rd
 *
 */
public class DankNullRenderItem extends RenderItem {

	private boolean useLg = false;
	ItemStack itemStack = ItemStack.EMPTY, dankNull = ItemStack.EMPTY;

	public DankNullRenderItem(final TextureManager textureManager, final ModelManager modelManager, final ItemColors colors, @Nonnull final ItemStack dankNull, final boolean useLg) {
		super(textureManager, modelManager, colors);
		this.useLg = useLg;
		this.dankNull = dankNull;
	}

	@Override
	public void renderItemOverlayIntoGUI(final FontRenderer fontRenderer, @Nonnull final ItemStack is, final int par4, final int par5, final String par6Str) {
		if (!is.isEmpty() && !dankNull.isEmpty()) {
			float scaleFactor = useLg ? 1.0F : 0.5F;
			float inverseScaleFactor = 1.0F / scaleFactor;
			int offset = useLg ? 0 : -1;
			String stackSize = "";

			final boolean unicodeFlag = fontRenderer.getUnicodeFlag();
			fontRenderer.setUnicodeFlag(false);
			if (is.getItem().showDurabilityBar(is)) {
				final double health = is.getItem().getDurabilityForDisplay(is);
				final int j = (int) Math.round(13.0D - health * 13.0D);
				final int i = (int) Math.round(255.0D - health * 255.0D);

				GlStateManager.disableDepth();
				GlStateManager.disableTexture2D();

				final Tessellator tessellator = Tessellator.getInstance();
				final BufferBuilder vertexbuffer = tessellator.getBuffer();
				draw(vertexbuffer, par4 + 2, par5 + 13, 13, 2, 0, 0, 0, 255);
				draw(vertexbuffer, par4 + 2, par5 + 13, 12, 1, (255 - i) / 4, 64, 0, 255);
				draw(vertexbuffer, par4 + 2, par5 + 13, j, 1, 255 - i, i, 0, 255);

				GlStateManager.enableTexture2D();

				GlStateManager.enableDepth();
			}
			final InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(dankNull);
			final int amount = inv.getStackInSlot(DankNullUtils.getIndexForStack(inv, is)).getCount();//inv.getSizeForSlot(DankNullUtils.getIndexForStack(inv, is));
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
			final int X = (int) ((par4 + offset + 16.0F - fontRenderer.getStringWidth(stackSize) * scaleFactor) * inverseScaleFactor);
			final int Y = (int) ((par5 + offset + 16.0F - 7.0F * scaleFactor) * inverseScaleFactor);
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

	private void draw(final BufferBuilder renderer, final int x, final int y, final int width, final int height, final int red, final int green, final int blue, final int alpha) {
		renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		renderer.pos(x + 0, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos(x + 0, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos(x + width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos(x + width, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
		Tessellator.getInstance().draw();
	}

	private String getToBeRenderedStackSize(final long originalSize) {
		if (!useLg) {
			return ReadableNumberConverter.INSTANCE.toSlimReadableForm(originalSize);
		}
		return ReadableNumberConverter.INSTANCE.toWideReadableForm(originalSize);
	}

	public ItemStack getStack() {
		return itemStack == null ? ItemStack.EMPTY : itemStack;
	}

	public void setStack(@Nonnull final ItemStack stack, final boolean regularSlotStack) {
		itemStack = stack;
		useLg = regularSlotStack;
	}
}