package p455w0rd.danknull.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.client.render.DankTextures;
import p455w0rd.danknull.entity.EntityPFakePlayer;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.EasyMappings;
import p455w0rdslib.util.ItemUtils;

/**
 * @author p455w0rd
 *
 */
public class ModEvents {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new ModEvents());
	}

	@SideOnly(Side.SERVER)
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e) {
		Map<String, Object> configs = new HashMap<String, Object>();
		//ModNetworking.INSTANCE.sendTo(new PacketConfigSync(configs), (EntityPlayerMP) e.player);
		EntityPFakePlayer.getFakePlayerForParent(e.player);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent event) {
		event.getMap().registerSprite(DankTextures.DOCK_SPRITE);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderOverlayEvent(RenderGameOverlayEvent e) {
		if ((ModGlobals.GUI_DANKNULL_ISOPEN) && ((e.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) || (e.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) || (e.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) || (e.getType() == RenderGameOverlayEvent.ElementType.FOOD) || (e.getType() == RenderGameOverlayEvent.ElementType.HEALTH) || (e.getType() == RenderGameOverlayEvent.ElementType.ARMOR))) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onCrafted(ItemCraftedEvent e) {
		if (e.crafting.getItem() instanceof ItemDankNull) {
			for (int i = 0; i < e.craftMatrix.getSizeInventory(); ++i) {
				if (!e.craftMatrix.getStackInSlot(i).isEmpty() && e.craftMatrix.getStackInSlot(i).getItem() instanceof ItemDankNull) {
					NBTTagCompound oldCompound = e.craftMatrix.getStackInSlot(i).getTagCompound();
					e.crafting.setTagCompound(oldCompound);
					break;
				}
				ItemUtils.setItem(e.crafting, e.crafting.getItem());
			}
		}
	}

	@SubscribeEvent
	public void onItemPickUp(EntityItemPickupEvent e) {
		EntityPlayer player = e.getEntityPlayer();
		ItemStack entityStack = e.getItem().getItem();
		if ((entityStack == null) || (player == null)) {
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

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onMouseEvent(MouseEvent event) {
		EntityPlayer player = EasyMappings.player();
		ItemStack dankNullItem = null;

		dankNullItem = DankNullUtils.getDankNull(player);
		InventoryDankNull inventory = DankNullUtils.getInventoryFromHeld(player);
		if (dankNullItem == null || !DankNullUtils.isDankNull(dankNullItem)) {
			return;
		}

		if ((event.getDwheel() == 0) && (event.isButtonstate())) {
			int currentIndex = DankNullUtils.getSelectedStackIndex(inventory);
			int totalSize = DankNullUtils.getItemCount(inventory);
			if ((currentIndex == -1) || (totalSize <= 1)) {
				return;
			}
			if (event.getButton() == 3) {
				DankNullUtils.setNextSelectedStack(inventory, player);
				event.setCanceled(true);
			}
			else if (event.getButton() == 4) {
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

}
