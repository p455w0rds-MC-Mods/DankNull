package p455w0rd.danknull.init;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.gui.GuiDankNull;
import p455w0rd.danknull.client.render.HUDRenderer;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;
import p455w0rd.danknull.inventory.slot.SlotDankNull;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.network.PacketChangeMode;
import p455w0rd.danknull.network.PacketEmptyDock;
import p455w0rd.danknull.network.PacketOpenGui;
import p455w0rdslib.LibGlobals.Mods;
import p455w0rdslib.util.ItemUtils;

import java.util.List;

/**
 * @author p455w0rd
 */
@EventBusSubscriber(modid = ModGlobals.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void onRecipeRegistryReady(final RegistryEvent.Register<IRecipe> event) {
        ModRecipes.register(event);
    }

    @SubscribeEvent
    public static void onBlockRegistryReady(final RegistryEvent.Register<Block> event) {
        ModBlocks.register(event);
    }

    @SubscribeEvent
    public static void onItemRegistryReady(final RegistryEvent.Register<Item> event) {
        ModItems.register(event);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onModelRegister(final ModelRegistryEvent event) {
        ModBlocks.registerModels();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderOverlayEvent(final RenderGameOverlayEvent event) {
        if (ModGlobals.GUI_DANKNULL_ISOPEN && (//@formatter:off
                event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR ||
                        event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS ||
                        event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE ||
                        event.getType() == RenderGameOverlayEvent.ElementType.FOOD ||
                        event.getType() == RenderGameOverlayEvent.ElementType.HEALTH ||
                        event.getType() == RenderGameOverlayEvent.ElementType.ARMOR)//@formatter:on
        ) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemPickUp(final EntityItemPickupEvent event) {
        final EntityPlayer player = event.getEntityPlayer();
        final ItemStack entityStack = event.getItem().getItem();
        if (entityStack.isEmpty() || !(player instanceof EntityPlayerMP)) {
            return;
        }
        // Demagnetize integration
        if (event.getItem().getEntityData().hasKey("PreventRemoteMovement")) {
            return;
        }
        final PlayerSlot dankNull = getDankNullForStack(player, entityStack);
        if (dankNull != null) {
            final IDankNullHandler dankNullHandler = dankNull.getStackInSlot(player).getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
            final ItemStack leftover = dankNullHandler.insertItem(0, entityStack, false);
            if (entityStack.getCount() != leftover.getCount()) {
                entityStack.setCount(leftover.getCount());
                if (leftover.isEmpty()) { // Only play if its empty to prevent duplicate playback
                    player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, player.getSoundCategory(), 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                }
            }
        }
    }

    private static PlayerSlot getDankNullForStack(final EntityPlayer player, final ItemStack stack) {
        final List<PlayerSlot> dankNulls = ItemDankNull.getDankNullsForPlayer(player);
        for (final PlayerSlot slot : dankNulls) {
            final ItemStack itemStack = slot.getStackInSlot(player);
            if (itemStack.hasCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null)) {
                final IDankNullHandler dankNullHandler = itemStack.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
                if (dankNullHandler.containsItemStack(stack)) {
                    return slot;
                }
                if (dankNullHandler.isOreDictFiltered(stack)) {
                    return slot;
                }
            }
        }
        return null;
    }

    @SubscribeEvent
    public static void updateMappings(final RegistryEvent.MissingMappings<Item> event) {
        for (final Mapping<Item> mapping : event.getAllMappings()) {
            final String modId = mapping.key.getNamespace();
            final String regPath = mapping.key.getPath();
            if (modId.equals(ModGlobals.MODID)) {
                if (regPath.equals("dank_null")) {
                    mapping.remap(ModItems.REDSTONE_DANKNULL);
                } else if (regPath.equals("dank_null_panel")) {
                    mapping.remap(ModItems.REDSTONE_PANEL);
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onKeyInput(final KeyInputEvent event) {
        if (ModKeyBindings.isAnyModKeybindPressed()) {
            if (ModKeyBindings.getToggleHUDKeyBind().isPressed()) {
                HUDRenderer.toggleHUD();
            }
            final EntityPlayer player = Minecraft.getMinecraft().player;
            final List<PlayerSlot> dankNulls = ItemDankNull.getDankNullsForPlayer(player);
            //Only check keybinds if player has DankNulls
            if (!dankNulls.isEmpty()) {
                if (ModKeyBindings.getOpenDankNullKeyBind().isPressed()) {
                    ModNetworking.getInstance().sendToServer(new PacketOpenGui(dankNulls.get(0)));
                }
                if (ModKeyBindings.getNextItemKeyBind().isPressed() || ModKeyBindings.getPreviousItemKeyBind().isPressed()) {
                    final Pair<EnumHand, IDankNullHandler> dankNull = getHandlerFromHeld(player);
                    if (dankNull != null) {
                        dankNull.getRight().cycleSelected(ModKeyBindings.getNextItemKeyBind().isKeyDown());
                        ModNetworking.getInstance().sendToServer(new PacketChangeMode(PacketChangeMode.ChangeType.SELECTED, dankNull.getRight().getSelected(), false, dankNull.getLeft()));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void tickEvent(final TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT) {
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
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen instanceof GuiDankNull && Mouse.getEventButtonState() && Mouse.getEventButton() == 0) {
            final EntityPlayer player = mc.player;
            if (player == null) {
                return;
            }
            boolean shouldCancel = false;
            final GuiDankNull dankNullGui = (GuiDankNull) mc.currentScreen;
            final IDankNullHandler dankNullHandler = dankNullGui.getDankNullHandler();
            final int width = dankNullGui.width;
            final int height = dankNullGui.height;
            final int mouseX = Mouse.getEventX() * width / mc.displayWidth;
            final int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
            final Slot hoveredSlot = dankNullGui.getSlotAtPos(mouseX, mouseY);
            if (hoveredSlot instanceof SlotDankNull && hoveredSlot.getHasStack() && Mouse.isButtonDown(0)) {
                IMessage syncPacket = null;
                if (GuiScreen.isCtrlKeyDown() && !GuiScreen.isAltKeyDown()) {
                    dankNullHandler.cycleExtractionMode(hoveredSlot.getStack(), true);
                    syncPacket = new PacketChangeMode(dankNullHandler.getExtractionMode(hoveredSlot.getStack()), hoveredSlot.getSlotIndex());
                    shouldCancel = true;
                } else if (GuiScreen.isAltKeyDown() && !GuiScreen.isCtrlKeyDown()) {
                    if (dankNullHandler.getSelected() < 0 || !ItemUtils.areItemStacksEqualIgnoreSize(dankNullHandler.getFullStackInSlot(dankNullHandler.getSelected()), hoveredSlot.getStack())) {
                        int count = 0;
                        for (final Slot slotHovered : dankNullGui.inventorySlots.inventorySlots) {
                            count++;
                            if (slotHovered.equals(hoveredSlot)) {
                                final int index = count - 1 - 36;
                                dankNullHandler.setSelected(index);
                                syncPacket = new PacketChangeMode(PacketChangeMode.ChangeType.SELECTED, hoveredSlot.getSlotIndex());
                                shouldCancel = true;
                            }
                        }
                    }
                } else if (Keyboard.isKeyDown(Keyboard.KEY_O) && !GuiScreen.isAltKeyDown() && !GuiScreen.isCtrlKeyDown()) {
                    if (ModConfig.isOreDictBlacklistEnabled() && !ModConfig.isItemOreDictBlacklisted(hoveredSlot.getStack()) || ModConfig.isOreDictWhitelistEnabled() && ModConfig.isItemOreDictWhitelisted(hoveredSlot.getStack()) || !ModConfig.isOreDictBlacklistEnabled() && !ModConfig.isOreDictWhitelistEnabled()) {
                        dankNullHandler.setOre(hoveredSlot.getStack(), !dankNullHandler.isOre(hoveredSlot.getStack()));
                        syncPacket = new PacketChangeMode(dankNullHandler.isOre(hoveredSlot.getStack()) ? PacketChangeMode.ChangeType.ORE_ON : PacketChangeMode.ChangeType.ORE_OFF, hoveredSlot.getSlotIndex());
                        shouldCancel = true;
                    }
                } else if (Keyboard.isKeyDown(Keyboard.KEY_P) && !GuiScreen.isAltKeyDown() && !GuiScreen.isCtrlKeyDown()) {
                    dankNullHandler.cyclePlacementMode(hoveredSlot.getStack(), true);
                    syncPacket = new PacketChangeMode(dankNullHandler.getPlacementMode(hoveredSlot.getStack()), hoveredSlot.getSlotIndex());
                    shouldCancel = true;
                }
                if (syncPacket != null) {
                    ModNetworking.getInstance().sendToServer(syncPacket);
                }
                if (shouldCancel) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMouseEvent(final MouseEvent event) {
        final EntityPlayer player = Minecraft.getMinecraft().player;

        final Minecraft mc = Minecraft.getMinecraft();
        final World world = mc.world;
        if (event.isButtonstate() && event.getButton() == 2 && event.getDwheel() == 0) {
            final Pair<EnumHand, IDankNullHandler> dankNull = getHandlerFromHeld(player);
            if (dankNull == null) {
                return;
            }
            final IDankNullHandler dankNullHandler = dankNull.getRight();
            final RayTraceResult target = mc.objectMouseOver;
            if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
                final IBlockState state = world.getBlockState(target.getBlockPos());
                if (world.isAirBlock(target.getBlockPos())) {
                    return;
                }
                final ItemStack stackToSelect = state.getBlock().getPickBlock(state, target, world, target.getBlockPos(), player);
                if (!stackToSelect.isEmpty() && (dankNullHandler.containsItemStack(stackToSelect) || dankNullHandler.isOre(stackToSelect))) {
                    final int newIndex = dankNullHandler.findItemStack(stackToSelect);
                    dankNullHandler.setSelected(newIndex);
                    ModNetworking.getInstance().sendToServer(new PacketChangeMode(PacketChangeMode.ChangeType.SELECTED, newIndex, false, dankNull.getLeft()));
                    event.setCanceled(true);
                }
            }
        }
        if (ModKeyBindings.isAnyModKeybindPressed() && event.getDwheel() == 0) {
            final Pair<EnumHand, IDankNullHandler> dankNull = getHandlerFromHeld(player);
            if (dankNull == null) {
                return;
            }
            final IDankNullHandler dankNullHandler = dankNull.getRight();
            final int currentIndex = dankNullHandler.getSelected();
            final int totalSize = dankNullHandler.stackCount();
            if (currentIndex == -1 || totalSize <= 1) {
                return;
            }
            if (ModKeyBindings.getNextItemKeyBind().isPressed()) {
                dankNullHandler.cycleSelected(true);
                ModNetworking.getInstance().sendToServer(new PacketChangeMode(PacketChangeMode.ChangeType.SELECTED, dankNullHandler.getSelected(), false, dankNull.getLeft()));
                event.setCanceled(true);
            } else if (ModKeyBindings.getPreviousItemKeyBind().isPressed()) {
                dankNullHandler.cycleSelected(false);
                ModNetworking.getInstance().sendToServer(new PacketChangeMode(PacketChangeMode.ChangeType.SELECTED, dankNullHandler.getSelected(), false, dankNull.getLeft()));
                event.setCanceled(true);
            }
        } else if (event.getDwheel() != 0 && player.isSneaking()) {
            // i do this multiple times to avoid constantly firing DankNullUtils#getInventoryFromHeld any time the mouse is used
            final Pair<EnumHand, IDankNullHandler> dankNull = getHandlerFromHeld(player);
            if (dankNull == null) {
                return;
            }
            final IDankNullHandler dankNullHandler = dankNull.getRight();
            final int currentIndex = dankNullHandler.getSelected();
            final int totalSize = dankNullHandler.stackCount();
            if (currentIndex == -1 || totalSize <= 1) {
                return;
            }
            final int scrollForward = event.getDwheel();
            if (scrollForward < 0) {
                dankNullHandler.cycleSelected(true);
                ModNetworking.getInstance().sendToServer(new PacketChangeMode(PacketChangeMode.ChangeType.SELECTED, dankNullHandler.getSelected(), false, dankNull.getLeft()));
                event.setCanceled(true);
            } else if (scrollForward > 0) {
                dankNullHandler.cycleSelected(false);
                ModNetworking.getInstance().sendToServer(new PacketChangeMode(PacketChangeMode.ChangeType.SELECTED, dankNullHandler.getSelected(), false, dankNull.getLeft()));
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
                        dankDock.removeDankNull();
                        ModNetworking.getInstance().sendToAll(new PacketEmptyDock(dankDock.getPos()));
                        dankDock.markDirty();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onPostRenderOverlay(final RenderGameOverlayEvent.Post event) {
        if (event.getType() == ElementType.HOTBAR) {
            final Minecraft mc = Minecraft.getMinecraft();
            HUDRenderer.renderHUD(mc, new ScaledResolution(mc));
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onWorldLoaded(final WorldEvent.Load event) {
        if (Mods.NEI.isLoaded() && FMLCommonHandler.instance().getSide().isClient()) {
            //NEI.init();
        }
    }

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            ModConfig.sendConfigsToClient((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public static void onConfigChange(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ModGlobals.MODID)) {
            ModConfig.sync();
        }
    }

    private static Pair<EnumHand, IDankNullHandler> getHandlerFromHeld(final EntityPlayer player) {
        if (player == null) {
            return null;
        }
        if (ItemDankNull.isDankNull(player.getHeldItemMainhand())) {
            return Pair.of(EnumHand.MAIN_HAND, player.getHeldItemMainhand().getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null));
        } else if (ItemDankNull.isDankNull(player.getHeldItemOffhand())) {
            return Pair.of(EnumHand.OFF_HAND, player.getHeldItemOffhand().getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null));
        } else {
            return null;
        }
    }

}
