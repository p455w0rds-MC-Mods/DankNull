package p455w0rd.danknull.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModIntegration.Mods;
import p455w0rd.danknull.init.ModNetworking;
import p455w0rd.danknull.integration.Chisel;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.inventory.slot.SlotDankNull;
import p455w0rd.danknull.network.PacketSyncDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rd.danknull.util.DankNullUtils.SlotExtractionMode;
import p455w0rdslib.client.gui.GuiModular;
import p455w0rdslib.util.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

//import p455w0rd.danknull.client.render.DankNullRenderItem;
//import yalter.mousetweaks.api.MouseTweaksIgnore;

/**
 * @author p455w0rd
 */
//@MouseTweaksIgnore
@SuppressWarnings("Duplicates")
public class GuiDankNull extends GuiModular {

    //	private DankNullRenderItem pRenderItem;
    private final List<SlotDankNull> slots = new LinkedList<SlotDankNull>();
    private Slot theSlot;
    private Slot returningStackDestSlot;
    private long returningStackTime;
    private ItemStack returningStack = ItemStack.EMPTY;
    private int touchUpX;
    private int touchUpY;
    private int numRows = 0;
    protected int xSize = 210;
    protected int ySize = 140;
    private EntityPlayer player;
    private InventoryDankNull inventory;

    public GuiDankNull(InventoryDankNull inventory, EntityPlayer player) {
        super(new ContainerDankNull(player, inventory));
        this.inventory = inventory;
        this.player = player;
        ItemStack stack = inventory.getDankNull();
//		pRenderItem = new DankNullRenderItem(Minecraft.getMinecraft().renderEngine, Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager(), Minecraft.getMinecraft().getItemColors(), getDankNull(), false);
        numRows = stack.getItemDamage();
        if (DankNullUtils.isCreativeDankNull(stack)) {
            numRows--;
        }
        setWidth(210);
        setHeight(140 + (numRows * 20 + numRows + 1));
        setBackgroundTexture(new ResourceLocation(ModGlobals.MODID, "textures/gui/danknullscreen" + (numRows + (DankNullUtils.isCreativeDankNull(getDankNull()) ? 1 : 0)) + ".png"));
    }

    @Override
    public void initGui() {
        super.initGui();
        ModGlobals.GUI_DANKNULL_ISOPEN = true;
        xSize = 201;
        if (Minecraft.getMinecraft().player.capabilities.isCreativeMode && DankNullUtils.isCreativeDankNull(getDankNull())) {
            buttonList.clear();
            buttonList.add(new GuiButton(0, getX() + (xSize / 2) - 25, getY() - 20, 50, 20, (DankNullUtils.isCreativeDankNullLocked(getDankNull()) ? "Unl" : "L") + "ock"));
        }
    }

    @Override
    protected void actionPerformed(final GuiButton btn) {
        if (btn.id == 0) {
            String lock = I18n.format("dn.lock.desc");
            String unlock = I18n.format("dn.unlock.desc");
            if (btn.displayString.equals(lock)) {
                btn.displayString = unlock;
                DankNullUtils.setLocked(getDankNull(), true);
            }
            else {
                btn.displayString = lock;
                DankNullUtils.setLocked(getDankNull(), false);
            }
            ModNetworking.getInstance().sendToServer(new PacketSyncDankNull(DankNullUtils.getSyncableDankNull(mc.player)));
        }
    }

    @Override
    public void onGuiClosed() {
        ModGlobals.GUI_DANKNULL_ISOPEN = false;
        super.onGuiClosed();
    }

    public ItemStack getDankNull() {
        return inventory.getDankNull();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        int fontColor = 16777215;
        int yOffset = 101 - ((20 * numRows) + numRows);
        String name = "/d" + (Options.callItDevNull ? "ev" : "ank") + "/null";
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
        if (inventory == null) {
            inventory = DankNullUtils.getNewDankNullInventory(getDankNull());
        }
        return inventory;
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
            renderItemOverlayIntoGUI(RenderUtils.getFontRenderer(), itemstack, i, j);
        }
        itemRender.zLevel = 0.0F;
        zLevel = 0.0F;
    }

    private void renderItemOverlayIntoGUI(FontRenderer fontRenderer, @Nonnull ItemStack is, int par4, int par5) {
        if (!is.isEmpty()) {
            float scaleFactor = 0.5F;
            float inverseScaleFactor = 1.0F / scaleFactor;
            int offset = -1;
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

            int amount = inventory.getSizeForSlot(DankNullUtils.getIndexForStack(inventory, is));
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
        return ReadableNumberConverter.INSTANCE.toSlimReadableForm(originalSize);
    }

    @Override
    public void updateScreen() {
        if ((!EasyMappings.player().isEntityAlive()) || (EasyMappings.player().isDead)) {
            EasyMappings.player().closeScreen();
        }

        inventory.loadInventory(inventory.getDNTag());
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
        ((ContainerDankNull) inventorySlots).sync();
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

            Block selectedBlock = Block.getBlockFromItem(stack.getItem());
            boolean isSelectedStackABlock = selectedBlock != null && selectedBlock != Blocks.AIR;
            if (extractMode != null) {
                list.add(1, DankNullUtils.translate("dn.extract_mode.desc") + ": " + extractMode.getTooltip());
            }

            list.add(2, TextFormatting.GRAY + "" + TextFormatting.ITALIC + "  " + DankNullUtils.translate("dn.ctrl_click_change.desc"));
            if (isSelectedStackABlock) {
                list.add(2, TextFormatting.GRAY + "" + TextFormatting.ITALIC + "  " + DankNullUtils.translate("dn.p_click_toggle.desc"));
            }
            if (DankNullUtils.getSelectedStackIndex(getDankNullInventory()) != s.getSlotIndex()) {
                list.add(3, TextFormatting.GRAY + "" + TextFormatting.ITALIC + "  " + DankNullUtils.translate("dn.alt_click_set.desc"));
            }
            if (placementMode != null && isSelectedStackABlock) {
                list.add(1, DankNullUtils.translate("dn.placement_mode.desc") + ": " + placementMode.getTooltip().replace(DankNullUtils.translate("dn.extract.desc").toLowerCase(Locale.ENGLISH), DankNullUtils.translate("dn.place.desc").toLowerCase(Locale.ENGLISH)).replace(DankNullUtils.translate("dn.extract.desc"), DankNullUtils.translate("dn.place.desc")));
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
