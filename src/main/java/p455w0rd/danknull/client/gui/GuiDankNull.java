package p455w0rd.danknull.client.gui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import p455w0rd.danknull.client.render.DankNullRenderItem;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.inventory.slot.SlotDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.client.gui.GuiModular;
import p455w0rdslib.util.EasyMappings;
import p455w0rdslib.util.GuiUtils;
import p455w0rdslib.util.ItemUtils;
import p455w0rdslib.util.MCPrivateUtils;
import p455w0rdslib.util.MathUtils;
import p455w0rdslib.util.RenderUtils;

/**
 * @author p455w0rd
 *
 */
public class GuiDankNull extends GuiModular {

	private DankNullRenderItem pRenderItem;
	private final List<SlotDankNull> slots = new LinkedList<SlotDankNull>();
	private Slot theSlot;
	private Slot returningStackDestSlot;
	private long returningStackTime;
	private ItemStack returningStack = ItemStack.EMPTY;
	private ItemStack draggedStack = ItemStack.EMPTY;
	//private ItemStack dankNull;
	private int dragSplittingRemnant;
	private boolean isRightMouseClick;
	private int touchUpX;
	private int touchUpY;
	private int numRows = 0;
	protected int xSize = 210;
	protected int ySize = 140;
	EntityPlayer player;

	public GuiDankNull(Container container, EntityPlayer player) {
		super(container);
		this.player = player;
		ItemStack dankNull = DankNullUtils.getDankNull(player);
		pRenderItem = new DankNullRenderItem(Minecraft.getMinecraft().renderEngine, Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager(), Minecraft.getMinecraft().getItemColors(), dankNull, false, (ContainerDankNull) container);
		numRows = dankNull.getItemDamage();
		setWidth(210);
		setHeight(140 + (numRows * 20 + numRows + 1));
		setBackgroundTexture(new ResourceLocation(ModGlobals.MODID, "textures/gui/danknullscreen" + numRows + ".png"));
	}

	@Override
	public void initGui() {
		super.initGui();
		ModGlobals.GUI_DANKNULL_ISOPEN = true;
		xSize = 201;
	}

	@Override
	public void onGuiClosed() {
		ModGlobals.GUI_DANKNULL_ISOPEN = false;
		super.onGuiClosed();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		int fontColor = 16777215;
		int yOffset = 101 - ((20 * numRows) + numRows);
		ItemStack dankNull = DankNullUtils.getDankNull(player);
		String name = "/dank/null";
		/*
		if (dankNull != null) {
			name = I18n.format(DankNullUtils.getDankNull(player).getDisplayName(), new Object[0]).trim();
			if (name != null && name.substring(0, 4).equals("/dank")) {
				name = name.substring(0, 10);
			}
			//name = I18n.format(((ItemDankNull) DankNullUtils.getDankNull(player).getItem()).getUnlocalizedNameInefficiently(dankNull) + "_0.name", new Object[0]).trim();
		}
		*/
		mc.fontRenderer.drawString(name, 7, 6, DankNullUtils.getColor(dankNull.getItemDamage(), true), true);

		mc.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 7, ySize - yOffset, fontColor);
		if (DankNullUtils.getItemCount(getDankNullInventory()) > 0) {
			mc.fontRenderer.drawString("=Selected", xSize - 64, 6, fontColor);
		}
		GlStateManager.enableBlend();
		GlStateManager.enableLighting();
	}

	protected List<SlotDankNull> getSlots() {
		return slots;
	}

	public void drawSelectionBox(int x) {
		int selectedBoxColor = -1140916224;
		drawGradientRect(x - 75, 4, x - 66, 5, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 75, 4, x - 74, 14, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 75, 13, x - 66, 14, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 66, 4, x - 65, 14, selectedBoxColor, selectedBoxColor);
	}

	public void drawSelectionBox(int x, int y) {
		int selectedBoxColor = -1140916224;
		drawGradientRect(x - 1, y - 1, x + 16, y, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 1, y - 1, x, y + 17, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x + 16, y - 1, x + 17, y + 17, selectedBoxColor, selectedBoxColor);
		drawGradientRect(x - 1, y + 16, x + 18, y + 17, selectedBoxColor, selectedBoxColor);
	}

	private InventoryDankNull getDankNullInventory() {
		return ((ContainerDankNull) inventorySlots).getDankNullInventory();
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
			buttonList.get(i).drawButton(mc, mouseX, mouseY);
		}
		for (int j2 = 0; j2 < labelList.size(); j2++) {
			labelList.get(j).drawLabel(mc, mouseX, mouseY);
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

			if ((DankNullUtils.getItemCount(getDankNullInventory()) > 0) && (DankNullUtils.getSelectedStackIndex(getDankNullInventory()) == i1 - 36)) {
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
		if (inventoryplayer.getItemStack().isEmpty() && (theSlot != null) && (theSlot.getHasStack())) {
			ItemStack itemstack1 = (theSlot instanceof SlotDankNull) ? ((SlotDankNull) theSlot).getStack() : theSlot.getStack();
			renderToolTip(itemstack1, mouseX, mouseY);
		}
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

	private void drawStack(ItemStack stack, int x, int y, String altText) {
		//GlStateManager.translate(0.0F, 0.0F, 32.0F);
		MCPrivateUtils.setGuiZLevel(this, 200.0F);
		MCPrivateUtils.setGuiScreenRendererZLevel(this, 200.0F);
		FontRenderer font = null;
		if (stack != null) {
			font = stack.getItem().getFontRenderer(stack);
		}
		if (font == null) {
			font = RenderUtils.getFontRenderer();
		}
		MCPrivateUtils.getGuiScreenRenderItem(this).renderItemAndEffectIntoGUI(stack, x, y);
		MCPrivateUtils.getGuiScreenRenderItem(this).renderItemOverlayIntoGUI(font, stack, x, y, altText);
		MCPrivateUtils.setGuiScreenRendererZLevel(this, 0.0F);
		MCPrivateUtils.setGuiZLevel(this, 0.0F);
		//GlStateManager.translate(0.0F, 0.0F, -32.0F);
	}

	@Override
	public void handleMouseInput() throws IOException {
		int mouseX = Mouse.getEventX() * width / mc.displayWidth;
		int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
		int button = Mouse.getEventButton();

		if (Mouse.getEventButtonState()) {
			//if (GuiScreen.isCtrlKeyDown()) {
			if (GuiScreen.isAltKeyDown()) {
				if (button == 0) {
					Slot slot = getSlotAtPos(mouseX, mouseY);
					if (slot != null && !slot.getStack().isEmpty()) {
						if (!ItemUtils.areItemsEqual(DankNullUtils.getSelectedStack(getDankNullInventory()), slot.getStack())) {
							int count = 0;
							for (Slot slotHovered : inventorySlots.inventorySlots) {
								count++;
								if (slotHovered.equals(slot)) {
									DankNullUtils.setSelectedStackIndex(getDankNullInventory(), (count - 1) - 36);
									return;
								}
							}
						}
					}
				}
				//}
			}
		}
		super.handleMouseInput();
	}

	private boolean isMouseHovering(Slot slot, int x, int y) {
		return isPointInRegion(slot.xPos, slot.yPos, 16, 16, x, y);
	}

	public Pair<Integer, Integer> getPosFromSlot(Slot slot) {
		return Pair.of(slot.xPos, slot.yPos);
	}

	public Slot getSlotByIndex(int index) {
		List<Slot> slots = ((ContainerDankNull) inventorySlots).inventorySlots;
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

	private void drawDankNullSlot(Slot slotIn) {
		GuiContainer gui = this;
		int i = EasyMappings.slotPosX(slotIn);
		int j = EasyMappings.slotPosY(slotIn);
		ItemStack itemstack = slotIn.getStack();
		boolean flag = false;
		boolean flag1 = (slotIn == MCPrivateUtils.getGuiClickedSlot(gui)) && (MCPrivateUtils.getGuiDraggedStack(gui) != null) && (!MCPrivateUtils.getGuiIsRightMouseClick(gui));
		ItemStack itemstack1 = EasyMappings.player().inventory.getItemStack();
		String s = null;
		if ((slotIn == MCPrivateUtils.getGuiClickedSlot(gui)) && (MCPrivateUtils.getGuiDraggedStack(gui) != null) && (MCPrivateUtils.getGuiIsRightMouseClick(gui)) && (!itemstack.isEmpty())) {
			itemstack = itemstack.copy();
			itemstack.setCount(itemstack.getCount() / 2);
		}
		else if ((MCPrivateUtils.getGuiDragSplitting(gui)) && (MCPrivateUtils.getGuiDragSplittingSlots(gui).contains(slotIn)) && (!itemstack1.isEmpty())) {
			if (MCPrivateUtils.getGuiDragSplittingSlots(gui).size() == 1) {
				return;
			}
			MCPrivateUtils.getGuiDragSplittingSlots(gui).remove(slotIn);
			GuiUtils.updateDragSplitting(gui);
		}
		MCPrivateUtils.setGuiZLevel(gui, 100.0F);
		MCPrivateUtils.setGuiScreenRendererZLevel(gui, 100.0F);
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
		MCPrivateUtils.setGuiScreenRendererZLevel(gui, 0.0F);
		MCPrivateUtils.setGuiZLevel(gui, 0.0F);
	}

	@Override
	public void updateScreen() {
		if ((!EasyMappings.player().isEntityAlive()) || (EasyMappings.player().isDead)) {
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
	protected void renderToolTip(ItemStack stack, int x, int y) {
		List<String> list = stack.getTooltip(EasyMappings.player(), mc.gameSettings.advancedItemTooltips);

		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i, stack.getRarity().rarityColor + list.get(i));
			}
			else {
				list.set(i, TextFormatting.GRAY + list.get(i));
			}
		}
		Slot s = getSlotAtPos(x, y);
		if (s != null && s instanceof SlotDankNull && s.getHasStack() && s.getStack().getCount() > 1000) {
			list.add(1, TextFormatting.GRAY + "" + TextFormatting.ITALIC + "Count: " + s.getStack().getCount());
		}

		net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
		GuiUtils.drawToolTipWithBorderColor(this, list, x, y, DankNullUtils.getColor(DankNullUtils.getDankNull(player).getItemDamage(), true), DankNullUtils.getColor(DankNullUtils.getDankNull(player).getItemDamage(), false));
		net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
	}

}
