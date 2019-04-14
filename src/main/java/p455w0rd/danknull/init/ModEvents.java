package p455w0rd.danknull.init;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.gui.GuiDankNull;
import p455w0rd.danknull.init.ModIntegration.Mods;
import p455w0rd.danknull.integration.NEI;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.network.*;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.EasyMappings;
import p455w0rdslib.util.ItemUtils;

/**
 * @author p455w0rd
 *
 */
@EventBusSubscriber(modid = ModGlobals.MODID)
public class ModEvents {

	@SubscribeEvent
	public static void onRecipeRegistryReady(final RegistryEvent.Register<IRecipe> event) {
		event.getRegistry().registerAll(ModRecipes.getInstance().getArray());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onTextureStitch(final TextureStitchEvent event) {
		//event.getMap().registerSprite(DankTextures.DOCK_SPRITE);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onModelRegistryReady(final ModelRegistryEvent event) {

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderOverlayEvent(final RenderGameOverlayEvent e) {
		if (ModGlobals.GUI_DANKNULL_ISOPEN && (e.getType() == RenderGameOverlayEvent.ElementType.HOTBAR || e.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS || e.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE || e.getType() == RenderGameOverlayEvent.ElementType.FOOD || e.getType() == RenderGameOverlayEvent.ElementType.HEALTH || e.getType() == RenderGameOverlayEvent.ElementType.ARMOR)) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onItemPickUp(final EntityItemPickupEvent e) {
		final EntityPlayer player = e.getEntityPlayer();
		final ItemStack entityStack = e.getItem().getItem();
		if (entityStack.isEmpty() || player == null) {
			return;
		}
		final ItemStack dankNull = DankNullUtils.getDankNullForStack(player, entityStack).getRight();
		if (!dankNull.isEmpty()) {
			final InventoryDankNull inventory = DankNullUtils.getNewDankNullInventory(dankNull);
			if (inventory != null && DankNullUtils.addFilteredStackToDankNull(inventory, entityStack)) {
				entityStack.setCount(0);
				player.getEntityWorld().playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, player.getSoundCategory(), 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
				return;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onKeyInput(final KeyInputEvent event) {
		final EntityPlayer player = EasyMappings.player();
		ItemStack dankNullItem = ItemStack.EMPTY;

		dankNullItem = DankNullUtils.getFirstDankNull(player);
		final InventoryDankNull inventory = DankNullUtils.getInventoryFromHeld(player);
		if (dankNullItem.isEmpty() || !DankNullUtils.isDankNull(dankNullItem)) {
			return;
		}
		if (ModKeyBindings.getOpenDankNullKeyBind().isPressed()) {
			ModNetworking.getInstance().sendToServer(new PacketOpenDankGui());
		}
		final int currentIndex = DankNullUtils.getSelectedStackIndex(inventory);
		final int totalSize = DankNullUtils.getItemCount(inventory);
		if (currentIndex == -1 || totalSize <= 1) {
			return;
		}
		if (ModKeyBindings.getNextItemKeyBind().isPressed()) {
			DankNullUtils.setNextSelectedStack(inventory, player);
		}
		else if (ModKeyBindings.getPreviousItemKeyBind().isPressed()) {
			DankNullUtils.setPreviousSelectedStack(inventory, player);
		}
	}

	@SubscribeEvent
	public static void tickEvent(final TickEvent.PlayerTickEvent e) {
		if (e.side == Side.CLIENT) {
			if (ModGlobals.TIME >= 360.1F) {
				ModGlobals.TIME = 0.0F;
			}
			ModGlobals.TIME += 0.75F;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onMouseEventCustom(final MouseInputEvent event) {
		//handle Ctrl/Alt+Clicking slots to cycle extraction mode
		if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0) {
			final Minecraft mc = Minecraft.getMinecraft();
			final EntityPlayer player = mc.player;
			if (player == null) {
				return;
			}
			boolean shouldCancel = false;
			final Pair<Integer, ItemStack> dankNull = DankNullUtils.getDankNullFromCurrentScreen(player);
			if (mc.currentScreen instanceof GuiDankNull) {
				final GuiDankNull dankNullGui = (GuiDankNull) mc.currentScreen;
				final int width = dankNullGui.width;
				final int height = dankNullGui.height;
				final int mouseX = Mouse.getEventX() * width / mc.displayWidth;
				final int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
				final Slot hoveredSlot = dankNullGui.getSlotAtPos(mouseX, mouseY);
				if (hoveredSlot != null && hoveredSlot.getHasStack() && Mouse.isButtonDown(0)) {
					if (GuiScreen.isAltKeyDown() && !GuiScreen.isCtrlKeyDown()) {
						if (!ItemUtils.areItemsEqual(DankNullUtils.getSelectedStack(dankNullGui.getDankNullInventory()), hoveredSlot.getStack())) {
							int count = 0;
							for (final Slot slotHovered : dankNullGui.inventorySlots.inventorySlots) {
								count++;
								if (slotHovered.equals(hoveredSlot)) {
									final int index = count - 1 - 36;
									DankNullUtils.setSelectedStackIndex(dankNullGui.getDankNullInventory(), index);
									ModNetworking.getInstance().sendToServer(new PacketSetSelectedItem(index));
									event.setCanceled(true);
									return;
								}
							}
						}
					}
					if (GuiScreen.isCtrlKeyDown() && !GuiScreen.isAltKeyDown()) {
						DankNullUtils.cycleExtractionMode(dankNullGui.getDankNull(), hoveredSlot.getStack());
						ModNetworking.getInstance().sendToServer(new PacketSyncDankNull(dankNull.getLeft(), dankNull.getRight().getTagCompound()));
						shouldCancel = true;
					}
					else if (Keyboard.isKeyDown(Keyboard.KEY_O) && !GuiScreen.isAltKeyDown() && !GuiScreen.isCtrlKeyDown()) {
						if (DankNullUtils.isOreDictBlacklistEnabled() && !DankNullUtils.isItemOreDictBlacklisted(hoveredSlot.getStack()) || DankNullUtils.isOreDictWhitelistEnabled() && DankNullUtils.isItemOreDictWhitelisted(hoveredSlot.getStack()) || !DankNullUtils.isOreDictBlacklistEnabled() && !DankNullUtils.isOreDictWhitelistEnabled()) {
							DankNullUtils.cycleOreDictModeForStack(dankNullGui.getDankNull(), hoveredSlot.getStack());
							ModNetworking.getInstance().sendToServer(new PacketSyncDankNull(dankNull.getLeft(), dankNull.getRight().getTagCompound()));
							shouldCancel = true;
						}
					}
					else if (Keyboard.isKeyDown(Keyboard.KEY_P) && !GuiScreen.isAltKeyDown() && !GuiScreen.isCtrlKeyDown()) {
						DankNullUtils.cyclePlacementMode(dankNullGui.getDankNull(), hoveredSlot.getStack());
						ModNetworking.getInstance().sendToServer(new PacketSyncDankNull(dankNull.getLeft(), dankNull.getRight().getTagCompound()));
						shouldCancel = true;
					}
				}
				if (shouldCancel) {
					event.setCanceled(true);
				}
			}
			/*
						else if (mc.currentScreen instanceof GuiDankNullDock) {
							GuiDankNullDock dankNullGui = (GuiDankNullDock) mc.currentScreen;
							int width = dankNullGui.width;
							int height = dankNullGui.height;
							int mouseX = Mouse.getEventX() * width / mc.displayWidth;
							int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
							Slot hoveredSlot = dankNullGui.getSlotAtPos(mouseX, mouseY);
							if (hoveredSlot != null && hoveredSlot.getHasStack() && Mouse.isButtonDown(0)) {
								if (GuiScreen.isCtrlKeyDown() && !GuiScreen.isAltKeyDown()) {
									DankNullUtils.cycleExtractionMode(dankNullGui.getDankNull(), hoveredSlot.getStack());
									ModNetworking.getInstance().sendToServer(new PacketSyncDankNullDock(dankNullGui.getDock(), dankNullGui.getDock().getDankNull()));
									event.setCanceled(true);
								}
								else if (GuiScreen.isAltKeyDown() && !GuiScreen.isCtrlKeyDown()) {
									if (!ItemUtils.areItemsEqual(DankNullUtils.getSelectedStack(dankNullGui.getDankNullInventory()), hoveredSlot.getStack())) {
										int count = 0;
										for (Slot slotHovered : dankNullGui.inventorySlots.inventorySlots) {
											count++;
											if (slotHovered.equals(hoveredSlot)) {
												int index = (count - 1) - 36;
												DankNullUtils.setSelectedStackIndex(dankNullGui.getDankNullInventory(), index);
												ModNetworking.getInstance().sendToServer(new PacketSetSelectedItemDock(index, dankNullGui.getDock().getPos()));
												event.setCanceled(true);
											}
										}
									}
								}
								else if (!Options.disableOreDictMode && Keyboard.isKeyDown(Keyboard.KEY_O) && !GuiScreen.isAltKeyDown() && !GuiScreen.isCtrlKeyDown()) {
									if ((DankNullUtils.isOreDictBlacklistEnabled() && !DankNullUtils.isItemOreDictBlacklisted(hoveredSlot.getStack())) || (DankNullUtils.isOreDictWhitelistEnabled() && DankNullUtils.isItemOreDictWhitelisted(hoveredSlot.getStack())) || !DankNullUtils.isOreDictBlacklistEnabled() && !DankNullUtils.isOreDictWhitelistEnabled()) {
										DankNullUtils.cycleOreDictModeForStack(dankNullGui.getDankNull(), hoveredSlot.getStack());
										ModNetworking.getInstance().sendToServer(new PacketSyncDankNullDock(dankNullGui.getDock(), dankNullGui.getDock().getDankNull()));
										event.setCanceled(true);
									}
								}
								else if (Keyboard.isKeyDown(Keyboard.KEY_P) && !GuiScreen.isAltKeyDown() && !GuiScreen.isCtrlKeyDown()) {
									DankNullUtils.cyclePlacementMode(dankNullGui.getDankNull(), hoveredSlot.getStack());
									ModNetworking.getInstance().sendToServer(new PacketSyncDankNullDock(dankNullGui.getDock(), dankNullGui.getDock().getDankNull()));
									event.setCanceled(true);
								}
							}
						}
			*/
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onMouseEvent(final MouseEvent event) {
		final EntityPlayer player = EasyMappings.player();
		ItemStack dankNullItem = ItemStack.EMPTY;

		dankNullItem = DankNullUtils.getFirstDankNull(player);
		final InventoryDankNull inventory = DankNullUtils.getInventoryFromHeld(player);
		if (dankNullItem.isEmpty() || !DankNullUtils.isDankNull(dankNullItem)) {
			return;
		}

		final Minecraft mc = Minecraft.getMinecraft();
		final World world = mc.world;
		if (event.isButtonstate() && event.getButton() == 2 && event.getDwheel() == 0) {
			final RayTraceResult target = mc.objectMouseOver;
			if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
				final IBlockState state = world.getBlockState(target.getBlockPos());

				if (state.getBlock().isAir(state, world, target.getBlockPos())) {
					return;
				}
				final ItemStack stackToSelect = state.getBlock().getPickBlock(state, target, world, target.getBlockPos(), player);
				if (!stackToSelect.isEmpty() && (DankNullUtils.isFiltered(inventory, stackToSelect) || DankNullUtils.isFilteredOreDict(inventory, stackToSelect))) {
					final int newIndex = DankNullUtils.getIndexForStack(inventory, stackToSelect);
					DankNullUtils.setSelectedStackIndex(inventory, newIndex);
					ModNetworking.getInstance().sendToServer(new PacketSetSelectedItem(newIndex));
					event.setCanceled(true);
				}
			}
		}

		if (event.getDwheel() == 0) {
			final int currentIndex = DankNullUtils.getSelectedStackIndex(inventory);
			final int totalSize = DankNullUtils.getItemCount(inventory);
			if (currentIndex == -1 || totalSize <= 1) {
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
			final int currentIndex = DankNullUtils.getSelectedStackIndex(inventory);
			final int totalSize = DankNullUtils.getItemCount(inventory);
			if (currentIndex == -1 || totalSize <= 1) {
				return;
			}
			final int scrollForward = event.getDwheel();
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
	public static void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
		final EntityPlayer player = event.getEntityPlayer();
		final World world = player.getEntityWorld();
		if (world.isRemote) {
			return;
		}
		final BlockPos pos = event.getPos();
		final EnumHand hand = event.getHand();
		TileDankNullDock dankDock = null;
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileDankNullDock) {
			dankDock = (TileDankNullDock) world.getTileEntity(pos);
		}
		if (dankDock != null) {
			if (player.getServer().isBlockProtected(world, pos, player)) {
				return;
			}
			if (player.getHeldItem(hand).isEmpty()) {
				if (player.isSneaking()) {
					if (!dankDock.getDankNull().isEmpty()) {
						player.setHeldItem(hand, dankDock.getDankNull().copy());
						DankNullUtils.emptyDankNullDock(dankDock);
						//if (!world.isRemote) {
						ModNetworking.getInstance().sendToAll(new PacketEmptyDock(dankDock.getPos()));
						//}
						dankDock.markDirty();
					}
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onPostRenderOverlay(final RenderGameOverlayEvent.Post e) {
		if (e.getType() == ElementType.HOTBAR) {
			final Minecraft mc = Minecraft.getMinecraft();
			DankNullUtils.renderHUD(mc, new ScaledResolution(mc));
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onWorldLoaded(final WorldEvent.Load e) {
		if (Mods.NEI.isLoaded() && FMLCommonHandler.instance().getSide().isClient()) {
			NEI.init();
		}
	}

	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
		if (event.player != null && event.player instanceof EntityPlayerMP) {
			DankNullUtils.sendConfigsToClient((EntityPlayerMP) event.player);
		}
	}

	@SubscribeEvent
	public static void onConfigChange(final ConfigChangedEvent.OnConfigChangedEvent e) {
		if (e.getModID().equals(ModGlobals.MODID)) {
			ModConfig.init();
		}
	}

}
