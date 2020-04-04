package p455w0rd.danknull.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;
import p455w0rd.danknull.api.DankNullItemModes.ItemExtractionMode;
import p455w0rd.danknull.api.DankNullItemModes.ItemPlacementMode;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.container.ContainerDankNullDock;
import p455w0rd.danknull.init.ModConfig;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModGlobals.DankNullTier;
import p455w0rd.danknull.init.ModNetworking;
import p455w0rd.danknull.integration.Chisel;
import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.inventory.slot.SlotDankNull;
import p455w0rd.danknull.network.PacketChangeMode;
import p455w0rdslib.LibGlobals.Mods;
import p455w0rdslib.client.gui.GuiModular;
import p455w0rdslib.integration.Thaumcraft;
import p455w0rdslib.util.GuiUtils;
import p455w0rdslib.util.MathUtils;
import p455w0rdslib.util.ReadableNumberConverter;
import p455w0rdslib.util.RenderUtils;
import yalter.mousetweaks.api.MouseTweaksIgnore;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

/**
 * @author p455w0rd
 */
@MouseTweaksIgnore
public class GuiDankNull extends GuiModular {

    private Slot theSlot;
	private Slot returningStackDestSlot;
	private long returningStackTime;
	private ItemStack returningStack = ItemStack.EMPTY;
	private int touchUpX;
	private int touchUpY;
	protected int xSize = 210;
	private final DankNullTier tier;

	public GuiDankNull(final ContainerDankNull c) {
		super(c);
		tier = c.getHandler().getTier();
		setWidth(210);
		setHeight(tier.getGuiHeight());
		setBackgroundTexture(tier.getGuiBackground());
	}

	public IDankNullHandler getDankNullHandler() {
		return ((ContainerDankNull) inventorySlots).getHandler();
	}

	@Override
	public void initGui() {
		super.initGui();
		ModGlobals.GUI_DANKNULL_ISOPEN = true;
		xSize = 201;
		if (Minecraft.getMinecraft().player.capabilities.isCreativeMode && tier.isCreative()) {
			buttonList.clear();
			buttonList.add(new GuiButton(0, getX() + xSize / 2 - 25, getY() - 20, 50, 20, (getDankNullHandler().isLocked() ? "Unl" : "L") + "ock"));
		}
	}

	@Override
	protected void actionPerformed(final GuiButton btn) {
		final IDankNullHandler dankNullHandler = getDankNullHandler();
		if (btn.id == 0) {
			final String lock = I18n.translateToLocal("dn.lock.desc");
			final String unlock = I18n.translateToLocal("dn.unlock.desc");
			boolean isLocked = false;
			if (btn.displayString.equals(lock)) {
				btn.displayString = unlock;
				dankNullHandler.setLocked(true);
				isLocked = true;
			}
			else {
				btn.displayString = lock;
				dankNullHandler.setLocked(false);
			}
			ModNetworking.getInstance().sendToServer(new PacketChangeMode(isLocked ? PacketChangeMode.ChangeType.LOCK : PacketChangeMode.ChangeType.UNLOCK));
		}
	}

	public boolean isTile() {
		return inventorySlots instanceof ContainerDankNullDock;
	}

	@Override
	public void onGuiClosed() {
		ModGlobals.GUI_DANKNULL_ISOPEN = false;
		super.onGuiClosed();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
		final IDankNullHandler dankNullHandler = getDankNullHandler();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		final int fontColor = 16777215;
		final int yOffset = tier.getNumRows() * 20 + 18 + tier.getNumRows() - 1;
		final String name = "/d" + (Options.callItDevNull ? "ev" : "ank") + "/null";
		mc.fontRenderer.drawString(name, 7, 6, tier.getHexColor(true), true);
		mc.fontRenderer.drawString(I18n.translateToLocal("container.inventory"), 7, yOffset, fontColor);
		if (dankNullHandler.getSelected() > -1) {
			mc.fontRenderer.drawString("=" + I18n.translateToLocal("dn.selected.desc"), xSize - 64, 6, fontColor);
		}
		GlStateManager.enableBlend();
		GlStateManager.enableLighting();
	}

    private void drawSelectionBox(final int x) {
		final int selectedBoxColor = getDankNullHandler().getTier().ordinal() == 0 ? 0xFFFFFF00 : -1140916224;
		drawGradientRect(x - 75, 4, x - 66, 5, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 75, 4, x - 74, 14, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 75, 13, x - 66, 14, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 66, 4, x - 65, 14, selectedBoxColor, selectedBoxColor);
	}

	private void drawSelectionBox(final int x, final int y) {
		final int selectedBoxColor = getDankNullHandler().getTier().ordinal() == 0 ? 0xFFFFFF00 : -1140916224;
		drawGradientRect(x - 1, y - 1, x + 16, y, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 1, y - 1, x, y + 17, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x + 16, y - 1, x + 17, y + 17, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 1, y + 16, x + 17, y + 17, selectedBoxColor, selectedBoxColor);
	}

	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
		final IDankNullHandler dankNullHandler = getDankNullHandler();
		drawDefaultBackground();
		final int i = guiLeft;
		final int j = guiTop;
		drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		for (int i2 = 0; i2 < buttonList.size(); i2++) {
			buttonList.get(i2).drawButton(mc, mouseX, mouseY, 0);
		}
		for (int j2 = 0; j2 < labelList.size(); j2++) {
			labelList.get(j2).drawLabel(mc, mouseX, mouseY);
		}
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate(i, j, 0.0F);

		GlStateManager.enableRescaleNormal();
		if (dankNullHandler.getSelected() > -1) {
			drawSelectionBox(xSize);
		}
		theSlot = null;
		final int k = 240;
		final int l = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k, l);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		for (int i1 = 0; i1 < inventorySlots.inventorySlots.size(); i1++) {
			final Slot slot = inventorySlots.inventorySlots.get(i1);
            final int j1 = slot.xPos;
            final int k1 = slot.yPos;
			if (slot instanceof SlotDankNull) {
				drawDankNullSlot(slot);
			}
			else {
				GuiUtils.drawSlot(this, slot);
			}
			if (isMouseHovering(slot, mouseX, mouseY)) {
				theSlot = slot;
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.disableBlend();
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, 2457);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
				GlStateManager.enableBlend();
			}
			if (dankNullHandler.getSelected() == i1 - 36) {
				GlStateManager.disableLighting();
				final int index = dankNullHandler.getSelected();
				if (index != -1) {
					if (getSlotByIndex(index) != null && getSlotByIndex(index).getHasStack()) {
						drawSelectionBox(j1, k1);
					}
					else {
						for (int i3 = index; i3 >= 0; i3--) {
							if (getSlotByIndex(i3).getHasStack()) {
								drawSelectionBox(getSlotByIndex(i3).xPos, getSlotByIndex(i3).yPos);
								break;
							}
						}
					}
				}
				else {
					for (int i3 = i1 - 35; i3 < tier.getNumRowsMultiplier() * 9; i3++) {
						if (getSlotByIndex(i3).getHasStack()) {
							drawSelectionBox(getSlotByIndex(i3).xPos, getSlotByIndex(i3).yPos);
							break;
						}
					}
				}
				GlStateManager.enableLighting();
			}
		}
		drawGuiContainerForegroundLayer(mouseX, mouseY);
		final InventoryPlayer inventoryplayer = Minecraft.getMinecraft().player.inventory;
		ItemStack itemstack = draggedStack.isEmpty() ? inventoryplayer.getItemStack() : draggedStack;
		if (!itemstack.isEmpty()) {
			final int j2 = 8;
			final int k2 = draggedStack.isEmpty() ? 8 : 16;
			String s = null;
			if (!draggedStack.isEmpty() && isRightMouseClick) {
				itemstack = itemstack.copy();
				itemstack.setCount(MathUtils.ceil(itemstack.getCount() / 2.0F));
			}
			else if (dragSplitting && dragSplittingSlots.size() > 1) {
				itemstack = itemstack.copy();
				itemstack.setCount(dragSplittingRemnant);
				if (itemstack.getCount() == 0) {
					s = "" + TextFormatting.YELLOW + "0";
				}
			}
			drawStack(itemstack, mouseX - i - j2, mouseY - j - k2, s);
		}
		if (!returningStack.isEmpty()) {
			float f = (Minecraft.getSystemTime() - returningStackTime) / 100.0F;
			if (f >= 1.0F) {
				f = 1.0F;
				returningStack = ItemStack.EMPTY;
			}
            final int l2 = returningStackDestSlot.xPos - touchUpX;
            final int i3 = returningStackDestSlot.yPos - touchUpY;
			final int l1 = touchUpX + (int) (l2 * f);
			final int i2 = touchUpY + (int) (i3 * f);
			drawStack(returningStack, l1, i2, null);
		}
		GlStateManager.popMatrix();
		if (inventoryplayer.getItemStack().isEmpty() && theSlot != null) {
			final ItemStack itemstack1 = theSlot.getStack();
			renderToolTip(itemstack1, mouseX, mouseY);
		}
	}

	private void drawStack(final ItemStack stack, final int x, final int y, final String altText) {
		zLevel = 200.0F;
		itemRender.zLevel = 200.0F;
		FontRenderer font = null;
		if (stack != null) {
			font = stack.getItem().getFontRenderer(stack);
		}
		if (font == null) {
			font = RenderUtils.getFontRenderer();
		}
		itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		itemRender.renderItemOverlayIntoGUI(font, stack, x, y, altText);
		itemRender.zLevel = 0.0F;
		zLevel = 0.0F;
	}

	private boolean isMouseHovering(final Slot slot, final int x, final int y) {
		return isPointInRegion(slot.xPos, slot.yPos, 16, 16, x, y);
	}

    public Slot getSlotByIndex(final int index) {
		final List<Slot> slots = inventorySlots.inventorySlots;
		return slots.get(index + 36);
	}

	public Slot getSlotAtPos(final int x, final int y) {
		final List<Slot> slots = inventorySlots.inventorySlots;
		for (int i = 0; i < slots.size(); i++) {
			if (isMouseHovering(slots.get(i), x, y)) {
				return slots.get(i);
			}
		}
		return null;
	}

	public void updateDragSplitting() {
		final ItemStack itemstack = Minecraft.getMinecraft().player.inventory.getItemStack();
		if (itemstack != null && dragSplitting) {
			dragSplittingRemnant = itemstack.getCount();
			for (final Slot slot : dragSplittingSlots) {
				final ItemStack itemstack1 = itemstack.copy();
				final int i = slot.getStack() == null ? 0 : slot.getStack().getCount();
				Container.computeStackSize(dragSplittingSlots, dragSplittingLimit, itemstack1, i);
				if (itemstack1.getCount() > itemstack1.getMaxStackSize()) {
					itemstack1.setCount(itemstack1.getMaxStackSize());
				}
				if (itemstack1.getCount() > slot.getItemStackLimit(itemstack1)) {
					itemstack1.setCount(slot.getItemStackLimit(itemstack1));
				}
				dragSplittingRemnant = dragSplittingRemnant - (itemstack1.getCount() - i);
			}
		}
	}

	private void drawDankNullSlot(final Slot slotIn) {
		final GuiContainer gui = this;
        final int i = slotIn.xPos;
        final int j = slotIn.yPos;
		ItemStack itemstack = slotIn.getStack();
		final boolean flag = false;
		boolean flag1 = slotIn == clickedSlot && draggedStack != null && !isRightMouseClick;
		final ItemStack itemstack1 = Minecraft.getMinecraft().player.inventory.getItemStack();
		if (slotIn == clickedSlot && draggedStack != null && isRightMouseClick && !itemstack.isEmpty()) {
			itemstack = itemstack.copy();
			itemstack.setCount(itemstack.getCount() / 2);
		}
		else if (dragSplitting && dragSplittingSlots.contains(slotIn) && !itemstack1.isEmpty()) {
			if (dragSplittingSlots.size() == 1) {
				return;
			}
			dragSplittingSlots.remove(slotIn);
		}
		zLevel = 100.0F;
		itemRender.zLevel = 100.0F;
		if (itemstack.isEmpty()) {
			final TextureAtlasSprite textureatlassprite = slotIn.getBackgroundSprite();
			if (textureatlassprite != null) {
				GlStateManager.disableLighting();
				GuiUtils.bindTexture(slotIn.getBackgroundLocation());
				GuiUtils.drawTexturedModalRect(gui, i, j, textureatlassprite, 16, 16);
				GlStateManager.enableLighting();
				flag1 = true;
			}
		}
		if (!flag1) {
			if (flag) {
				Gui.drawRect(i, j, i + 16, j + 16, -2130706433);
			}
			GlStateManager.enableDepth();
			final ItemStack tempStack = itemstack.copy();
			tempStack.setCount(1);
			Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(tempStack, i, j);
			renderItemOverlayIntoGUI(RenderUtils.getFontRenderer(), itemstack, i, j);
		}
		itemRender.zLevel = 0.0F;
		zLevel = 0.0F;
	}

	private void renderItemOverlayIntoGUI(final FontRenderer fontRenderer, @Nonnull final ItemStack is, final int par4, final int par5) {
		if (!is.isEmpty()) {
			float scaleFactor = 0.5F;
			float inverseScaleFactor = 1.0F / scaleFactor;
			int offset = -1;
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
			final int amount = is.getCount();
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
		return ReadableNumberConverter.INSTANCE.toWideReadableForm(originalSize);
	}

	@Override
	public void updateScreen() {
		if (!Minecraft.getMinecraft().player.isEntityAlive() || Minecraft.getMinecraft().player.isDead) {
			Minecraft.getMinecraft().player.closeScreen();
		}
	}

	@Override
	protected boolean isPointInRegion(final int rectX, final int rectY, final int rectWidth, final int rectHeight, int pointX, int pointY) {
		final int i = guiLeft;
		final int j = guiTop;
		pointX -= i;
		pointY -= j;
		return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
	}

	@Override
	protected void keyTyped(final char typedChar, final int keyCode) {
		if (keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
			mc.player.closeScreen();
		}
		if (theSlot != null && theSlot.getHasStack()) {
			if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(keyCode)) {
				handleMouseClick(theSlot, theSlot.slotNumber, 0, ClickType.CLONE);
			}
			else if (mc.gameSettings.keyBindDrop.isActiveAndMatches(keyCode)) {
				handleMouseClick(theSlot, theSlot.slotNumber, isCtrlKeyDown() && !(theSlot instanceof SlotDankNull) ? 1 : 0, ClickType.THROW);
			}
		}
	}

	@Override
	protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
		super.mouseReleased(mouseX, mouseY, state);
		inventorySlots.detectAndSendChanges();
	}

	@Override
	protected void renderToolTip(final ItemStack stack, final int x, final int y) {
		final List<String> list = stack.isEmpty() ? Lists.newArrayList() : stack.getTooltip(Minecraft.getMinecraft().player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i, stack.getItem().getRarity(stack).color + list.get(i));
			}
			else {
				list.set(i, TextFormatting.GRAY + list.get(i));
			}
		}
		final Slot s = getSlotAtPos(x, y);
		final IDankNullHandler dankNullHandler = getDankNullHandler();
		if (s instanceof SlotDankNull && s.getHasStack()) {
			final boolean showOreDictMessage = ModConfig.isOreDictBlacklistEnabled() && !ModConfig.isItemOreDictBlacklisted(s.getStack()) || ModConfig.isOreDictWhitelistEnabled() && ModConfig.isItemOreDictWhitelisted(s.getStack()) || !ModConfig.isOreDictBlacklistEnabled() && !ModConfig.isOreDictWhitelistEnabled();
			final ItemExtractionMode extractMode = dankNullHandler.getExtractionMode(s.getStack());
			final ItemPlacementMode placementMode = dankNullHandler.getPlacementMode(s.getStack());
			final Block selectedBlock = Block.getBlockFromItem(stack.getItem());
			final boolean isSelectedStackABlock = selectedBlock != null && selectedBlock != Blocks.AIR;
			if (extractMode != null) {
				list.add(1, I18n.translateToLocal("dn.extract_mode.desc") + ": " + extractMode.getTooltip());
			}

			list.add(2, TextFormatting.GRAY + "" + TextFormatting.ITALIC + "  " + I18n.translateToLocal("dn.ctrl_click_change.desc"));
			if (isSelectedStackABlock) {
				list.add(2, TextFormatting.GRAY + "" + TextFormatting.ITALIC + "  " + I18n.translateToLocal("dn.p_click_toggle.desc"));
			}
			if (dankNullHandler.getSelected() != s.getSlotIndex()) {
				list.add(3, TextFormatting.GRAY + "" + TextFormatting.ITALIC + "  " + I18n.translateToLocal("dn.alt_click_set.desc"));
			}
			if (placementMode != null && isSelectedStackABlock) {
				list.add(1, I18n.translateToLocal("dn.placement_mode.desc") + ": " + placementMode.getTooltip().replace(I18n.translateToLocal("dn.extract.desc").toLowerCase(Locale.ENGLISH), I18n.translateToLocal("dn.place.desc").toLowerCase(Locale.ENGLISH)).replace(I18n.translateToLocal("dn.extract.desc"), I18n.translateToLocal("dn.place.desc")));
			}
			if (showOreDictMessage) {
				final String oreDictMode = dankNullHandler.isOre(s.getStack()) ? I18n.translateToLocal("dn.enabled.desc") : I18n.translateToLocal("dn.disabled.desc");
				final boolean oreDicted = OreDictionary.getOreIDs(s.getStack()).length > 0;
				int lineOffset = 0;
				if (oreDicted) {
					list.add(2, I18n.translateToLocal("dn.ore_dictionary.desc") + ": " + oreDictMode);
					final List<String> oreNames = DankNullHandler.getOreNames(stack);
					if (oreNames.size() > 0 && GuiScreen.isShiftKeyDown()) {
						list.add(3, "" + TextFormatting.YELLOW + TextFormatting.UNDERLINE + TextFormatting.BOLD + " Enabled OreDict Conversions: ");
					}
					if (GuiScreen.isShiftKeyDown()) {
						for (int i = 0; i < oreNames.size(); i++) {
							lineOffset = 5 + i;
							list.add(4 + i, "" + TextFormatting.GRAY + TextFormatting.ITALIC + "   - " + oreNames.get(i));
						}
					}
				}
				if (oreDicted) {
					list.add(GuiScreen.isShiftKeyDown() ? lineOffset + 1 : 4, TextFormatting.GRAY + "" + TextFormatting.ITALIC + "  " + I18n.translateToLocal("dn.o_click_toggle.desc"));
					/*final List<String> oreNames = DankNullHandler.getOreNames(stack);
					for (int i = 0; i < oreNames.size(); i++) {
						list.add(5 + i, TextFormatting.ITALIC + " - " + oreNames.get(i));
					}*/
				}

			}
			if (Mods.CHISEL.isLoaded()) {
				int lineToRemove = -1;
				if (Chisel.isBlockChiseled(stack)) {
					final String name = Chisel.getVariantName(stack);
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).contains(name)) {
							lineToRemove = i;
							break;
						}
					}
					if (lineToRemove > -1) {
						list.remove(lineToRemove);
						list.add(1, TextFormatting.RESET + I18n.translateToLocal("dn.chisel_varient.desc") + ": " + TextFormatting.GRAY + "" + TextFormatting.ITALIC + "" + name);
					}
				}
			}
			if (s.getStack().getCount() > 1000) {
				list.add(1, TextFormatting.GRAY + "" + TextFormatting.ITALIC + I18n.translateToLocal("dn.count.desc") + ": " + (getDankNullHandler().getTier().isCreative() ? I18n.translateToLocal("dn.infinite.desc") : dankNullHandler.getFullStackInSlot(s.getSlotIndex()).getCount()));
			}
		}
		net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
		GuiUtils.drawToolTipWithBorderColor(this, list, x, y, tier.getHexColor(true), tier.getHexColor(false));
		if (isShiftKeyDown() && Mods.THAUMCRAFT.isLoaded() && s != null && s.getHasStack()) {
			int i = 0;
			for (final String str : list) {
				final int j = RenderUtils.getFontRenderer().getStringWidth(str);
				if (j > i) {
					i = j;
				}
			}
			int l1 = x + 12;
			int i2 = y - 12;
			int k = 8;
			if (list.size() > 1) {
				k += 2 + (list.size() - 1) * 10;
			}
			if (l1 + i > width) {
				l1 -= 28 + i;
			}
			if (i2 + k + 8 > height) {
				i2 = height - k - 8;
			}
			Thaumcraft.renderAspectsOnTooltip(this, list, stack, l1 - 12, i2 + 12);
		}
		net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
	}

}
