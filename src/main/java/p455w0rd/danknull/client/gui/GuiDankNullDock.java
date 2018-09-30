package p455w0rd.danknull.client.gui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.render.DankNullRenderItem;
import p455w0rd.danknull.container.ContainerDankNullDock;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModIntegration.Mods;
import p455w0rd.danknull.integration.Chisel;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.inventory.slot.SlotDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rd.danknull.util.DankNullUtils.SlotExtractionMode;
import p455w0rdslib.client.gui.GuiModular;
import p455w0rdslib.util.EasyMappings;
import p455w0rdslib.util.GuiUtils;
import p455w0rdslib.util.MathUtils;
import p455w0rdslib.util.RenderUtils;

/**
 * @author p455w0rd
 *
 */
public class GuiDankNullDock extends GuiModular {

	private DankNullRenderItem pRenderItem;
	private final List<SlotDankNull> slots = new LinkedList<SlotDankNull>();
	private Slot theSlot;
	private Slot returningStackDestSlot;
	private long returningStackTime;
	private ItemStack returningStack = ItemStack.EMPTY;
	//private ItemStack draggedStack = ItemStack.EMPTY;
	//private ItemStack dankNull;
	//private int dragSplittingRemnant;
	//private boolean isRightMouseClick;
	private int touchUpX;
	private int touchUpY;
	private int numRows = 0;
	protected int xSize = 210;
	protected int ySize = 140;
	EntityPlayer player;
	TileDankNullDock dock;

	public GuiDankNullDock(Container container, EntityPlayer player, TileDankNullDock te) {
		super(container);
		this.player = player;
		if (te != null) {
			dock = te;
		}
		//te.getDankNull();
		pRenderItem = new DankNullRenderItem(Minecraft.getMinecraft().renderEngine, Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager(), Minecraft.getMinecraft().getItemColors(), te.getDankNull(), false);
		numRows = te.getDankNull().getItemDamage();
		if (DankNullUtils.isCreativeDankNull(te.getDankNull())) {
			numRows--;
		}
		setWidth(210);
		setHeight(140 + (numRows * 20 + numRows + 1));
		setBackgroundTexture(new ResourceLocation(ModGlobals.MODID, "textures/gui/danknullscreen" + (numRows + (DankNullUtils.isCreativeDankNull(te.getDankNull()) ? 1 : 0)) + ".png"));
	}

	@Override
	public void initGui() {
		super.initGui();
		ModGlobals.GUI_DANKNULL_ISOPEN = true;
		xSize = 201;
	}

	@Override
	protected void actionPerformed(final GuiButton btn) {

	}

	@Override
	public void onGuiClosed() {
		ModGlobals.GUI_DANKNULL_ISOPEN = false;
		super.onGuiClosed();
	}

	public TileDankNullDock getDock() {
		return (TileDankNullDock) mc.world.getTileEntity(dock.getPos());
	}

	public ItemStack getDankNull() {
		return getDock().getDankNull();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		int fontColor = 16777215;
		int yOffset = 101 - ((20 * numRows) + numRows);
		String name = "/d" + (Options.callItDevNull ? "ev" : "ank") + "/null";
		/*
		if (dankNull != null) {
			name = I18n.format(DankNullUtils.getDankNull(player).getDisplayName(), new Object[0]).trim();
			if (name != null && name.substring(0, 4).equals("/dank")) {
				name = name.substring(0, 10);
			}
			//name = I18n.format(((ItemDankNull) DankNullUtils.getDankNull(player).getItem()).getUnlocalizedNameInefficiently(dankNull) + "_0.name", new Object[0]).trim();
		}
		*/
		mc.fontRenderer.drawString(name, 7, 6, DankNullUtils.getColor(getDankNull().getItemDamage(), true), true);

		mc.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 7, ySize - yOffset, fontColor);
		if (DankNullUtils.getItemCount(getDankNullInventory()) > 0) {
			mc.fontRenderer.drawString("=" + DankNullUtils.translate("dn.selected.desc"), xSize - 64, 6, fontColor);
		}
		GlStateManager.enableBlend();
		GlStateManager.enableLighting();
	}

	protected List<SlotDankNull> getSlots() {
		return slots;
	}

	public void drawSelectionBox(int x) {
		int selectedBoxColor = getDankNull().getItemDamage() == 0 ? 0xFFFFFF00 : -1140916224;
		drawGradientRect(x - 75, 4, x - 66, 5, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 75, 4, x - 74, 14, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 75, 13, x - 66, 14, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 66, 4, x - 65, 14, selectedBoxColor, selectedBoxColor);
	}

	public void drawSelectionBox(int x, int y) {
		int selectedBoxColor = getDankNull().getItemDamage() == 0 ? 0xFFFFFF00 : -1140916224;
		drawGradientRect(x - 1, y - 1, x + 16, y, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 1, y - 1, x, y + 17, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x + 16, y - 1, x + 17, y + 17, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 1, y + 16, x + 17, y + 17, selectedBoxColor, selectedBoxColor);
	}

	public InventoryDankNull getDankNullInventory() {
		return getDock().getInventory();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		drawDefaultBackground();
		int i = guiLeft;
		int j = guiTop;
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
		if (DankNullUtils.getItemCount(getDankNullInventory()) > 0) {
			drawSelectionBox(xSize);
		}
		theSlot = null;
		int k = 240;
		int l = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k, l);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		for (int i1 = 0; i1 < inventorySlots.inventorySlots.size(); i1++) {
			Slot slot = inventorySlots.inventorySlots.get(i1);
			int j1 = EasyMappings.slotPosX(slot);
			int k1 = EasyMappings.slotPosY(slot);
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

			if (!getDankNullInventory().isEmpty() && (DankNullUtils.getSelectedStackIndex(getDankNullInventory()) == i1 - 36)) {

				GlStateManager.disableLighting();
				int index = DankNullUtils.getSelectedStackIndex(getDankNullInventory());
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
					for (int i3 = i1 - 35; i3 < numRows * 9; i3++) {
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

		InventoryPlayer inventoryplayer = EasyMappings.player().inventory;
		ItemStack itemstack = draggedStack.isEmpty() ? inventoryplayer.getItemStack() : draggedStack;
		if (!itemstack.isEmpty()) {
			int j2 = 8;
			int k2 = draggedStack.isEmpty() ? 8 : 16;
			String s = null;
			if ((!draggedStack.isEmpty()) && (isRightMouseClick)) {
				itemstack = itemstack.copy();
				itemstack.setCount(MathUtils.ceil(itemstack.getCount() / 2.0F));
			}
			else if ((dragSplitting) && (dragSplittingSlots.size() > 1)) {
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

			int l2 = EasyMappings.slotPosX(returningStackDestSlot) - touchUpX;
			int i3 = EasyMappings.slotPosY(returningStackDestSlot) - touchUpY;
			int l1 = touchUpX + (int) (l2 * f);
			int i2 = touchUpY + (int) (i3 * f);
			drawStack(returningStack, l1, i2, (String) null);
		}
		GlStateManager.popMatrix();
		if (inventoryplayer.getItemStack().isEmpty() && (theSlot != null)) {// && (theSlot.getHasStack())) {
			ItemStack itemstack1 = (theSlot instanceof SlotDankNull) ? ((SlotDankNull) theSlot).getStack() : theSlot.getStack();
			renderToolTip(itemstack1, mouseX, mouseY);
		}
	}

	private void drawStack(ItemStack stack, int x, int y, String altText) {
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

	private boolean isMouseHovering(Slot slot, int x, int y) {
		return isPointInRegion(slot.xPos, slot.yPos, 16, 16, x, y);
	}

	public Pair<Integer, Integer> getPosFromSlot(Slot slot) {
		return Pair.of(slot.xPos, slot.yPos);
	}

	public Slot getSlotByIndex(int index) {
		List<Slot> slots = ((ContainerDankNullDock) inventorySlots).inventorySlots;
		return slots.get(index + 36);
	}

	public Slot getSlotAtPos(int x, int y) {
		List<Slot> slots = inventorySlots.inventorySlots;
		for (int i = 0; i < slots.size(); i++) {
			if (slots.get(i) instanceof SlotDankNull) {
				if (isMouseHovering(slots.get(i), x, y)) {
					return slots.get(i);
				}
			}
		}
		return null;
	}

	public void updateDragSplitting() {
		ItemStack itemstack = EasyMappings.player().inventory.getItemStack();

		if (itemstack != null && dragSplitting) {
			dragSplittingRemnant = itemstack.getCount();

			for (Slot slot : dragSplittingSlots) {
				ItemStack itemstack1 = itemstack.copy();
				int i = slot.getStack() == null ? 0 : slot.getStack().getCount();
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

	private void drawDankNullSlot(Slot slotIn) {

		GuiContainer gui = this;
		int i = EasyMappings.slotPosX(slotIn);
		int j = EasyMappings.slotPosY(slotIn);
		ItemStack itemstack = slotIn.getStack();
		boolean flag = false;
		boolean flag1 = (slotIn == clickedSlot) && (draggedStack != null) && (!isRightMouseClick);
		ItemStack itemstack1 = EasyMappings.player().inventory.getItemStack();
		String s = null;
		if ((slotIn == clickedSlot) && (draggedStack != null) && (isRightMouseClick) && (!itemstack.isEmpty())) {
			itemstack = itemstack.copy();
			itemstack.setCount(itemstack.getCount() / 2);
		}
		else if ((dragSplitting) && (dragSplittingSlots.contains(slotIn)) && (!itemstack1.isEmpty())) {
			if (dragSplittingSlots.size() == 1) {
				return;
			}
			dragSplittingSlots.remove(slotIn);
			//updateDragSplitting();
		}
		zLevel = 100.0F;
		itemRender.zLevel = 100.0F;
		if (itemstack.isEmpty()) {
			TextureAtlasSprite textureatlassprite = slotIn.getBackgroundSprite();
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
			Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(itemstack, i, j);
			pRenderItem.renderItemOverlayIntoGUI(RenderUtils.getFontRenderer(), itemstack, i, j, s);
		}
		itemRender.zLevel = 0.0F;
		zLevel = 0.0F;
	}

	@Override
	public void updateScreen() {
		if (getDankNull().isEmpty() || (!EasyMappings.player().isEntityAlive()) || (EasyMappings.player().isDead)) {
			EasyMappings.player().closeScreen();
		}
	}

	@Override
	protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
		int i = guiLeft;
		int j = guiTop;
		pointX -= i;
		pointY -= j;
		return (pointX >= rectX - 1) && (pointX < rectX + rectWidth + 1) && (pointY >= rectY - 1) && (pointY < rectY + rectHeight + 1);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
			mc.player.closeScreen();
		}

		//this.checkHotbarKeys(keyCode);

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
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		((ContainerDankNullDock) inventorySlots).detectAndSendChanges();
		//((ContainerDankNullDock) inventorySlots).sync();
		//((ContainerDankNullDock) inventorySlots).getTileEntity().markDirty();
	}

	@Override
	protected void renderToolTip(ItemStack stack, int x, int y) {
		List<String> list = stack.isEmpty() ? Lists.newArrayList() : stack.getTooltip(EasyMappings.player(), mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);

		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i, stack.getRarity().rarityColor + list.get(i));
			}
			else {
				list.set(i, TextFormatting.GRAY + list.get(i));
			}
		}
		Slot s = getSlotAtPos(x, y);
		if (s != null && s instanceof SlotDankNull && s.getHasStack()) {
			boolean showOreDictMessage = ((DankNullUtils.isOreDictBlacklistEnabled() && !DankNullUtils.isItemOreDictBlacklisted(s.getStack())) || (DankNullUtils.isOreDictWhitelistEnabled() && DankNullUtils.isItemOreDictWhitelisted(s.getStack())) || !DankNullUtils.isOreDictBlacklistEnabled() && !DankNullUtils.isOreDictWhitelistEnabled());
			SlotExtractionMode extractMode = DankNullUtils.getExtractionModeForStack(getDankNull(), s.getStack());
			SlotExtractionMode placementMode = DankNullUtils.getPlacementModeForStack(getDankNull(), s.getStack());

			if (extractMode != null) {
				list.add(1, DankNullUtils.translate("dn.extract_mode.desc") + ": " + extractMode.getTooltip());
			}

			list.add(2, TextFormatting.GRAY + "" + TextFormatting.ITALIC + "  " + DankNullUtils.translate("dn.ctrl_click_change.desc"));
			if (DankNullUtils.getSelectedStackIndex(getDankNullInventory()) != s.getSlotIndex()) {
				list.add(3, TextFormatting.GRAY + "" + TextFormatting.ITALIC + "  " + DankNullUtils.translate("dn.alt_click_set.desc"));
			}
			if (placementMode != null) {
				list.add(1, "Placement Mode: " + placementMode.getTooltip().replace("extract", "place").replace("Extract", "Place"));
			}
			if (showOreDictMessage) {
				String oreDictMode = DankNullUtils.getOreDictModeForStack(getDankNull(), s.getStack()) ? DankNullUtils.translate("dn.enabled.desc") : DankNullUtils.translate("dn.disabled.desc");
				boolean oreDicted = DankNullUtils.isItemOreDicted(s.getStack());
				if (oreDicted) {
					list.add(2, DankNullUtils.translate("dn.ore_dictionary.desc") + ": " + oreDictMode);
				}
				if (oreDicted) {
					list.add(4, TextFormatting.GRAY + "" + TextFormatting.ITALIC + "  " + DankNullUtils.translate("dn.o_click_toggle.desc"));
				}
			}
			if (Mods.CHISEL.isLoaded()) {
				int lineToRemove = -1;
				if (Chisel.isBlockChiseled(stack)) {
					String name = Chisel.getVariantName(stack);
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).contains(name)) {
							lineToRemove = i;
							break;
						}
					}
					if (lineToRemove > -1) {
						list.remove(lineToRemove);
						list.add(1, TextFormatting.RESET + I18n.format("dn.chisel_varient.desc") + ": " + TextFormatting.GRAY + "" + TextFormatting.ITALIC + "" + name);
					}
				}
			}
			if (s.getStack().getCount() > 1000) {
				list.add(1, TextFormatting.GRAY + "" + TextFormatting.ITALIC + DankNullUtils.translate("dn.count.desc") + ": " + (DankNullUtils.isCreativeDankNull(getDankNull()) ? DankNullUtils.translate("dn.infinite.desc") : s.getStack().getCount()));
			}
		}

		net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
		GuiUtils.drawToolTipWithBorderColor(this, list, x, y, DankNullUtils.getColor(getDankNull().getItemDamage(), true), DankNullUtils.getColor(getDankNull().getItemDamage(), false));
		net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
	}

}
