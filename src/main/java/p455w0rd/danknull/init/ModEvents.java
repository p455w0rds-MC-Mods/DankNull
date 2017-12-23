package p455w0rd.danknull.init;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.gui.GuiDankNull;
import p455w0rd.danknull.entity.EntityPFakePlayer;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.network.PacketSyncDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.EasyMappings;
import p455w0rdslib.util.ItemUtils;

/**
 * @author p455w0rd
 *
 */
public class ModEvents {

	private static final ModEvents INSTANCE = new ModEvents();

	public static void init() {
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	public static ModEvents getInstance() {
		return INSTANCE;
	}

	@SideOnly(Side.SERVER)
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e) {
		//Map<String, Object> configs = new HashMap<String, Object>();
		//ModNetworking.INSTANCE.sendTo(new PacketConfigSync(configs), (EntityPlayerMP) e.player);
		if (e.player instanceof EntityPlayerMP) {
			EntityPFakePlayer.getFakePlayerForParent((EntityPlayerMP) e.player);
		}
	}

	@SubscribeEvent
	public void onRecipeRegistryReady(RegistryEvent.Register<IRecipe> event) {
		event.getRegistry().registerAll(ModRecipes.getInstance().getArray());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent event) {
		//event.getMap().registerSprite(DankTextures.DOCK_SPRITE);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onModelRegistryReady(ModelRegistryEvent event) {

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderOverlayEvent(RenderGameOverlayEvent e) {
		if ((ModGlobals.GUI_DANKNULL_ISOPEN) && ((e.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) || (e.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) || (e.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) || (e.getType() == RenderGameOverlayEvent.ElementType.FOOD) || (e.getType() == RenderGameOverlayEvent.ElementType.HEALTH) || (e.getType() == RenderGameOverlayEvent.ElementType.ARMOR))) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onItemPickUp(EntityItemPickupEvent e) {
		EntityPlayer player = e.getEntityPlayer();
		ItemStack entityStack = e.getItem().getItem();
		if ((entityStack.isEmpty()) || (player == null)) {
			return;
		}
		ItemStack dankNull = DankNullUtils.getDankNullForStack(player, entityStack);
		if (!dankNull.isEmpty()) {
			InventoryDankNull inventory = DankNullUtils.getInventoryFromStack(dankNull);
			if (inventory != null && (DankNullUtils.addFilteredStackToDankNull(inventory, entityStack))) {
				entityStack.setCount(0);
				return;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(KeyInputEvent event) {
		EntityPlayer player = EasyMappings.player();
		ItemStack dankNullItem = ItemStack.EMPTY;

		dankNullItem = DankNullUtils.getDankNull(player);
		InventoryDankNull inventory = DankNullUtils.getInventoryFromHeld(player);
		if (dankNullItem.isEmpty() || !DankNullUtils.isDankNull(dankNullItem)) {
			return;
		}

		int currentIndex = DankNullUtils.getSelectedStackIndex(inventory);
		int totalSize = DankNullUtils.getItemCount(inventory);
		if ((currentIndex == -1) || (totalSize <= 1)) {
			return;
		}
		if (ModKeyBindings.getNextItemKeyBind().isPressed()) {
			DankNullUtils.setNextSelectedStack(inventory, player);
		}
		else if (ModKeyBindings.getPreviousItemKeyBind().isPressed()) {
			DankNullUtils.setPreviousSelectedStack(inventory, player);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onMouseEventCustom(MouseInputEvent event) {
		//handle Ctrl/Alt+Clicking slots to cycle extraction mode
		if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.currentScreen instanceof GuiDankNull) {
				GuiDankNull dankNullGui = (GuiDankNull) mc.currentScreen;
				int width = dankNullGui.width;
				int height = dankNullGui.height;
				int mouseX = Mouse.getEventX() * width / mc.displayWidth;
				int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
				Slot hoveredSlot = dankNullGui.getSlotAtPos(mouseX, mouseY);
				if (hoveredSlot != null && hoveredSlot.getHasStack()) {
					if (GuiScreen.isCtrlKeyDown()) {
						DankNullUtils.cycleExtractionMode(dankNullGui.getDankNull(), hoveredSlot.getStack());
						ModNetworking.getInstance().sendToServer(new PacketSyncDankNull(dankNullGui.getDankNullInventory().getDankNull()));
						event.setCanceled(true);
					}
					else if (GuiScreen.isAltKeyDown()) {
						if (!ItemUtils.areItemsEqual(DankNullUtils.getSelectedStack(dankNullGui.getDankNullInventory()), hoveredSlot.getStack())) {
							int count = 0;
							for (Slot slotHovered : dankNullGui.inventorySlots.inventorySlots) {
								count++;
								if (slotHovered.equals(hoveredSlot)) {
									DankNullUtils.setSelectedStackIndex(dankNullGui.getDankNullInventory(), (count - 1) - 36);
									event.setCanceled(true);
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onMouseEvent(MouseEvent event) {
		EntityPlayer player = EasyMappings.player();
		ItemStack dankNullItem = ItemStack.EMPTY;

		dankNullItem = DankNullUtils.getDankNull(player);
		InventoryDankNull inventory = DankNullUtils.getInventoryFromHeld(player);
		if (dankNullItem.isEmpty() || !DankNullUtils.isDankNull(dankNullItem)) {
			return;
		}

		if ((event.getDwheel() == 0)) {
			int currentIndex = DankNullUtils.getSelectedStackIndex(inventory);
			int totalSize = DankNullUtils.getItemCount(inventory);
			if ((currentIndex == -1) || (totalSize <= 1)) {
				return;
			}
			if (ModKeyBindings.getNextItemKeyBind().isPressed()) {
				DankNullUtils.setNextSelectedStack(inventory, player);
				event.setCanceled(true);
			}
			else if (ModKeyBindings.getPreviousItemKeyBind().isPressed()) {
				DankNullUtils.setPreviousSelectedStack(inventory, player);
				event.setCanceled(true);
			}
		}
		else if (player.isSneaking()) {
			int currentIndex = DankNullUtils.getSelectedStackIndex(inventory);
			int totalSize = DankNullUtils.getItemCount(inventory);
			if ((currentIndex == -1) || (totalSize <= 1)) {
				return;
			}
			int scrollForward = event.getDwheel();
			if (scrollForward < 0) {
				DankNullUtils.setNextSelectedStack(inventory, player);
				event.setCanceled(true);
			}
			else if (scrollForward > 0) {
				DankNullUtils.setPreviousSelectedStack(inventory, player);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		World world = player.getEntityWorld();
		BlockPos pos = event.getPos();
		EnumHand hand = event.getHand();
		TileDankNullDock te = null;
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileDankNullDock) {
			te = (TileDankNullDock) world.getTileEntity(pos);
		}
		if (te != null) {
			if (player.getHeldItem(hand).isEmpty()) {
				if (player.isSneaking()) {
					if (!te.getStack().isEmpty()) {
						player.setHeldItem(hand, te.getStack());
						te.setStack(ItemStack.EMPTY);
						te.setSelectedStack(ItemStack.EMPTY);
						te.resetInventory();
					}
				}
			}
		}
	}

	private int remainingHighlightTicks;
	private String highlightItemName = "";

	public void setSelectedMessage(String msg) {
		highlightItemName = msg;
		remainingHighlightTicks = 160;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientTick(PlayerTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.player != null) {

			if (highlightItemName.isEmpty()) {
				remainingHighlightTicks = 0;
			}
			else if (!highlightItemName.isEmpty()) {
				if (remainingHighlightTicks > 0) {
					--remainingHighlightTicks;
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPostRenderOverlay(RenderGameOverlayEvent.Post e) {
		if (e.getType() == ElementType.VIGNETTE) {
			renderSelectedItem();
		}
	}

	@SideOnly(Side.CLIENT)
	public void renderSelectedItem() {
		/*
		Minecraft mc = Minecraft.getMinecraft();
		mc.mcProfiler.startSection("dankNullSelectedItem");
		ScaledResolution scaledRes = new ScaledResolution(mc);
		if (remainingHighlightTicks > 0 && !highlightItemName.isEmpty()) {
			int i = (scaledRes.getScaledWidth() - mc.fontRenderer.getStringWidth(highlightItemName)) / 2;
			int j = scaledRes.getScaledHeight() - 75;
			if (!mc.playerController.shouldDrawHUD()) {
				j += 14 * 3;
			}
			int k = (int) (remainingHighlightTicks * 256.0F / 10.0F);
			if (k > 255) {
				k = 255;
			}
			if (k > 0) {
				GlStateManager.pushMatrix();
				mc.fontRenderer.drawStringWithShadow(highlightItemName, i, j, 16777215 + (k << 24));
				GlStateManager.popMatrix();
			}
		}
		mc.mcProfiler.endSection();
		*/
		Minecraft mc = Minecraft.getMinecraft();
		DankNullUtils.renderHUD(mc, new ScaledResolution(mc));
	}

}
