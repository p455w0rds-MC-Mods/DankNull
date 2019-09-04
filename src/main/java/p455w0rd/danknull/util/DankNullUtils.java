package p455w0rd.danknull.util;

import java.util.Locale;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.init.*;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals.DankNullTier;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;
import p455w0rd.danknull.items.*;
import p455w0rd.danknull.network.PacketConfigSync;
import p455w0rd.danknull.recipes.RecipeDankNullUpgrade;
import p455w0rdslib.util.*;

/**
 * @author p455w0rd
 */
public class DankNullUtils {

	/*public static PlayerSlot getDankNullSlot(final EntityPlayer player) {
		final InventoryPlayer playerInv = player.inventory;
		final ItemStack mainHand = player.getHeldItemMainhand();
		final ItemStack offHand = player.getHeldItemOffhand();
	
		if (mainHand.getItem() instanceof ItemDankNull) {
			return new PlayerSlot(playerInv.currentItem, MAIN);
		}
		else if (offHand.getItem() instanceof ItemDankNull) {
			return new PlayerSlot(0, OFF_HAND);
		}
	
		for (int i = 0; i < playerInv.mainInventory.size(); i++) {
			final ItemStack stack = playerInv.mainInventory.get(i);
			if (stack.getItem() instanceof ItemDankNull) {
				return new PlayerSlot(i, MAIN);
			}
		}
		return null;
	}*/

	/*public static ItemStack findDankNull(final EntityPlayer player, final String uuid) {
		final List<PlayerSlot> dankNulls = getAllDankNulls(player);
		for (final PlayerSlot slot : dankNulls) {
			final ItemStack itemStack = slot.getStackInSlot(player);
			if (itemStack.hasCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null)) {
				final IDankNullHandler dankNullHandler = itemStack.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
				if (dankNullHandler.getUUID().equalsIgnoreCase(uuid)) {
					return itemStack;
				}
			}
		}
		return null;
	}*/

	/*public static ItemStack getDockedDankNull(final ItemStack dankNullDock) {
		if (dankNullDock.hasTagCompound() && dankNullDock.getTagCompound().hasKey(NBT.BLOCKENTITYTAG, Constants.NBT.TAG_COMPOUND)) {
			final NBTTagCompound nbt = dankNullDock.getTagCompound().getCompoundTag(NBT.BLOCKENTITYTAG);
			if (!nbt.hasNoTags()) {
				return new ItemStack(nbt.getCompoundTag(NBT.DOCKEDSTACK));
			}
		}
		return ItemStack.EMPTY;
	}
	
	public static void setDockedDankNull(final ItemStack dankNullDock, final ItemStack newDankNull) {
		if (!dankNullDock.hasTagCompound()) {
			dankNullDock.setTagCompound(new NBTTagCompound());
		}
		if (dankNullDock.getTagCompound().hasKey(NBT.BLOCKENTITYTAG, Constants.NBT.TAG_COMPOUND)) {
			final NBTTagCompound nbt = dankNullDock.getTagCompound().getCompoundTag(NBT.BLOCKENTITYTAG);
			nbt.setTag(NBT.DOCKEDSTACK, newDankNull.serializeNBT());
		}
		else {
			final NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag(NBT.DOCKEDSTACK, newDankNull.serializeNBT());
			dankNullDock.getTagCompound().setTag(NBT.BLOCKENTITYTAG, nbt);
		}
	}
	
	public static ItemStack getDockedDankNull(final TileEntity dankDock) {
		if (isDankNullDock(dankDock)) {
			return ((TileDankNullDock) dankDock).getDankNull();
		}
		return ItemStack.EMPTY;
	}*/

	/*public static List<PlayerSlot> getDankNullsForPlayer(final EntityPlayer player) {
		final InventoryPlayer playerInv = player.inventory;
		final List<PlayerSlot> dankNullList = Lists.newArrayList();
		for (int i = 0; i < playerInv.mainInventory.size(); i++) {
			if (isDankNull(playerInv.mainInventory.get(i))) {
				dankNullList.add(new PlayerSlot(i, MAIN));
			}
		}
		for (int i = 0; i < playerInv.offHandInventory.size(); i++) {
			if (isDankNull(playerInv.offHandInventory.get(i))) {
				dankNullList.add(new PlayerSlot(i, MAIN));
			}
		}
		return dankNullList;
	}*/

	/*public static PlayerSlot getDankNullForStack(final EntityPlayer player, final ItemStack stack) {
		final List<PlayerSlot> dankNulls = getAllDankNulls(player);

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
	}*/

	/*public static boolean isDankDock(final Object obj) {
		return obj != null && obj instanceof TileDankNullDock || obj instanceof BlockDankNullDock || obj instanceof ItemBlockDankNullDock;
	}
	
	public static boolean isDankNull(final ItemStack stack) {
		return !stack.isEmpty() && stack.hasCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
	}
	
	public static boolean isCreativeDankNull(final ItemStack stack) {
		return isDankNull(stack) && stack.getItem() == ModItems.CREATIVE_DANKNULL;
	}
	
	public static boolean isDankNullPanel(final ItemStack stack) {
		return stack.getItem() instanceof ItemDankNullPanel;
	}
	
	public static boolean isDankNullDock(final ItemStack stack) {
		return stack.getItem() instanceof ItemBlockDankNullDock;
	}*/

	/*public static boolean isDankNullDock(final TileEntity tile) {
		return tile instanceof TileDankNullDock;
	}*/

	/*public static boolean isDankNullDockEmpty(final ItemStack dankDock) {
		return isDankNullDock(dankDock) && getDockedDankNull(dankDock).isEmpty();
	}
	
	public static int getMeta(final ItemStack stack) {
		if (isDankNull(stack)) {
			return ((ItemDankNull) stack.getItem()).getTier().ordinal();
		}
		else if (isDankNullPanel(stack)) {
			return ((ItemDankNullPanel) stack.getItem()).getTier().ordinal();
		}
		else if (isDankNullDock(stack)) {
			return !isDankNullDockEmpty(stack) ? ((ItemDankNull) getDockedDankNull(stack).getItem()).getTier().ordinal() : -1;
		}
		return -1;
	}*/

	/*public static IDankNullHandler getHandlerFromHeld(final EntityPlayer player) {
		if (player == null) {
			return null;
		}
		if (isDankNull(player.getHeldItemMainhand())) {
			return player.getHeldItemMainhand().getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
		}
		else if (isDankNull(player.getHeldItemOffhand())) {
			return player.getHeldItemOffhand().getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
		}
		else {
			return null;
		}
	}*/

	/*public static boolean isOreDictBlacklistEnabled() {
		return !Options.getOreBlacklist().isEmpty() && !isOreDictWhitelistEnabled();
	}

	public static boolean isOreDictWhitelistEnabled() {
		return !Options.getOreWhitelist().isEmpty();
	}

	public static boolean isItemOreDictBlacklisted(final ItemStack stack) {
		if (isOreDictBlacklistEnabled()) {
			for (final int id : OreDictionary.getOreIDs(stack)) {
				if (Options.getOreBlacklist().contains(OreDictionary.getOreName(id))) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isItemOreDictWhitelisted(final ItemStack stack) {
		if (isOreDictWhitelistEnabled()) {
			for (final int id : OreDictionary.getOreIDs(stack)) {
				if (Options.getOreWhitelist().contains(OreDictionary.getOreName(id))) {
					return true;
				}
			}
		}
		return false;
	}*/

	/*public static boolean canStackBeAdded(final ItemStack dankNull, final ItemStack stack) {
		if (isDankNull(dankNull)) {
			if (DankNullUtils.isCreativeDankNull(dankNull)) {
				NonNullList<ItemStack> whiteList = null;
				try {
					whiteList = Options.getCreativeWhitelistedItems();
				}
				catch (final Exception e) {
				}
				if (whiteList != null && !whiteList.isEmpty()) {
					for (final ItemStack whiteListedStack : whiteList) {
						if (ItemUtils.areItemStacksEqualIgnoreSize(stack, whiteListedStack)) {
							return true;
						}
					}
					return false;
				}
				NonNullList<ItemStack> blackList = null;
				try {
					blackList = Options.getCreativeBlacklistedItems();
				}
				catch (final Exception e) {
				}
				if (blackList != null && !blackList.isEmpty()) {
					for (final ItemStack blackListedStack : blackList) {
						if (ItemUtils.areItemStacksEqualIgnoreSize(stack, blackListedStack)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}*/

	// for rendering
	public static ItemStack getStackInDankNullSlot(final ItemStack dankNull, final int slot) {
		final NBTTagList itemList = getNBTItemList(dankNull);
		if (!itemList.hasNoTags() && itemList.tagCount() > slot) {
			return new ItemStack(itemList.getCompoundTagAt(slot));
		}
		return ItemStack.EMPTY;
	}

	/*@Deprecated
	public static ItemStack getStackInDankNullSlotWithSize(final ItemStack dankNull, final int slot) {
		final NBTTagList itemList = getNBTItemList(dankNull);
		if (!itemList.hasNoTags() && itemList.tagCount() > slot) {
			final NBTTagCompound nbt = itemList.getCompoundTagAt(slot);
			final ItemStack ret = new ItemStack(nbt);
			if (NBTUtils.hasInt(nbt, NBT.REALCOUNT)) {
				ret.setCount(NBTUtils.getInt(nbt, NBT.REALCOUNT));
			}
			return ret;
		}
		return ItemStack.EMPTY;
	}*/

	/*public static int getSizeInventory(final ItemStack dankNull) {
		return !getNBTItemList(dankNull).hasNoTags() ? getNBTItemList(dankNull).tagCount() : 0;
	}*/

	public static NBTTagList getNBTItemList(final ItemStack dankNull) {
		if (dankNull.hasTagCompound()) {
			final NBTTagCompound nbt = dankNull.getTagCompound();
			if (NBTUtils.hasNBTTagList(nbt, NBT.DANKNULL_INVENTORY)) {
				return NBTUtils.getNBTTagList(nbt, NBT.DANKNULL_INVENTORY);
			}
		}
		return new NBTTagList();
	}

	public static DankNullTier getTier(final ItemStack dankNull) {
		int meta = -1;
		if (ItemDankNull.isDankNull(dankNull)) {
			meta = ((ItemDankNull) dankNull.getItem()).getTier().ordinal();
		}
		else if (ItemDankNullPanel.isDankNullPanel(dankNull)) {
			meta = ((ItemDankNullPanel) dankNull.getItem()).getTier().ordinal();
		}
		else if (ItemBlockDankNullDock.isDankNullDock(dankNull)) {
			final ItemStack dockedDank = ItemBlockDankNullDock.getDockedDankNull(dankNull);
			final boolean isEmpty = ItemBlockDankNullDock.isDankNullDock(dankNull) && dockedDank.isEmpty();
			meta = !isEmpty ? ((ItemDankNull) dockedDank.getItem()).getTier().ordinal() : -1;
		}
		return meta == -1 ? DankNullTier.NONE : DankNullTier.VALUES[meta];
	}

	/*public static int getSlotCount(final ItemStack stack) {
		return (getMeta(stack) + 1) * 9;
	}*/

	public static EnumActionResult placeBlock(@Nonnull final IBlockState state, final World world, final BlockPos pos) {
		return world.setBlockState(pos, state, 2) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
	}

	public static IRecipe addDankNullUpgradeRecipe(final String recipeName, final Object... params) {
		final ShapedPrimer primer = CraftingHelper.parseShaped(params);
		final IRecipe recipe = new RecipeDankNullUpgrade(primer.input).setRegistryName(new ResourceLocation(ModGlobals.MODID, recipeName));
		return recipe;
	}

	@SideOnly(Side.CLIENT)
	public static void renderHUD(final Minecraft mc, final ScaledResolution scaledRes) {
		if (!Options.showHUD || !mc.playerController.shouldDrawHUD() && !mc.player.capabilities.isCreativeMode) {
			return;
		}
		ItemStack currentItem = mc.player.inventory.getCurrentItem();
		if (currentItem.isEmpty() || !ItemDankNull.isDankNull(currentItem)) {
			currentItem = mc.player.getHeldItemOffhand();
		}
		if (!currentItem.isEmpty() && ItemDankNull.isDankNull(currentItem)) {
			final IDankNullHandler dankNullHandler = currentItem.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
			if (dankNullHandler.getSelected() < 0) {
				return;
			}
			final ItemStack selectedStack = dankNullHandler.getStackInSlot(dankNullHandler.getSelected());
			if (!selectedStack.isEmpty()) {
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(ModGlobals.MODID, "textures/gui/danknullscreen0.png"));
				GlStateManager.enableBlend();
				GlStateManager.enableAlpha();
				GuiUtils.drawTexturedModalRect(scaledRes.getScaledWidth() - 106, scaledRes.getScaledHeight() - 45, 0, 210, 106, 45, 0);
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				mc.fontRenderer.drawStringWithShadow(currentItem.getDisplayName(), scaledRes.getScaledWidth() * 2 - 212 + 55, scaledRes.getScaledHeight() * 2 - 83, dankNullHandler.getTier().getHexColor(true));
				String selectedStackName = selectedStack.getDisplayName();
				final int itemNameWidth = mc.fontRenderer.getStringWidth(selectedStackName);
				if (itemNameWidth >= 88 && selectedStackName.length() >= 14) {
					selectedStackName = selectedStackName.substring(0, 14).trim() + "...";
				}
				final ItemPlacementMode placementMode = dankNullHandler.getPlacementMode(selectedStack);
				mc.fontRenderer.drawStringWithShadow(TextUtils.translate("dn.selected_item.desc") + ": " + selectedStackName, scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 72, 16777215);
				mc.fontRenderer.drawStringWithShadow(TextUtils.translate("dn.count.desc") + ": " + (getTier(currentItem) == DankNullTier.CREATIVE ? "Infinite" : selectedStack.getCount()), scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 61, 16777215);
				mc.fontRenderer.drawStringWithShadow(TextUtils.translate("dn.place.desc") + ": " + placementMode.getTooltip().replace(TextUtils.translate("dn.extract.desc").toLowerCase(Locale.ENGLISH), TextUtils.translate("dn.place.desc").toLowerCase(Locale.ENGLISH)).replace(TextUtils.translate("dn.extract.desc"), TextUtils.translate("dn.place.desc")), scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 50, 16777215);
				mc.fontRenderer.drawStringWithShadow(TextUtils.translate("dn.extract.desc") + ": " + dankNullHandler.getExtractionMode(selectedStack).getTooltip(), scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 40, 16777215);

				final String keyBind = ModKeyBindings.getOpenDankNullKeyBind().getDisplayName();
				mc.fontRenderer.drawStringWithShadow(keyBind.equalsIgnoreCase("none") ? TextUtils.translate("dn.no_open_keybind.desc") : TextUtils.translate("dn.open_with.desc") + " " + keyBind, scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 29, 16777215);
				String oreDictMode = TextUtils.translate("dn.ore_dictionary.desc") + ": " + (dankNullHandler.isOre(selectedStack) ? TextUtils.translate("dn.enabled.desc") : TextUtils.translate("dn.disabled.desc"));
				if (!isItemOreDicted(selectedStack)) {
					oreDictMode = TextUtils.translate("dn.not_oredicted.desc");
				}
				mc.fontRenderer.drawStringWithShadow(oreDictMode, scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 18, 16777215);
				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.popMatrix();
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				RenderUtils.getRenderItem().renderItemAndEffectIntoGUI(currentItem, scaledRes.getScaledWidth() - 106 + 5, scaledRes.getScaledHeight() - 20);
				GlStateManager.popMatrix();
				//String oreNames = "";
				if (isItemOreDicted(selectedStack)) {
					for (int i = 0; i < OreDictionary.getOreIDs(selectedStack).length; i++) {
						//oreNames += OreDictionary.getOreName(OreDictionary.getOreIDs(selectedStack)[i]) + " ";
					}
				}
			}
		}
	}

	public static void toggleHUD() {
		Options.showHUD = !Options.showHUD;
		ModConfig.CONFIG.save();
	}

	public static boolean isItemOreDicted(final ItemStack stack) {
		return OreDictionary.getOreIDs(stack).length > 0;
	}

	public static ItemStack convertToOreDictedStack(final ItemStack toBeConverted, final ItemStack toConvertTo) {
		if (!isItemOreDicted(toBeConverted) || !isItemOreDicted(toConvertTo) || Options.disableOreDictMode) {
			return ItemStack.EMPTY;
		}
		final ItemStack newStack = toConvertTo.copy();
		newStack.setCount(toBeConverted.getCount());
		return newStack;
	}

	@SideOnly(Side.SERVER)
	public static void sendConfigsToClient(final EntityPlayerMP player) {
		final WeakHashMapSerializable<String, Object> map = new WeakHashMapSerializable<>();
		map.put(ModConfig.CONST_CREATIVE_BLACKLIST, Options.creativeBlacklist);
		map.put(ModConfig.CONST_CREATIVE_WHITELIST, Options.creativeWhitelist);
		map.put(ModConfig.CONST_OREDICT_BLACKLIST, Options.oreBlacklist);
		map.put(ModConfig.CONST_OREDICT_WHITELIST, Options.oreWhitelist);
		map.put(ModConfig.CONST_DISABLE_OREDICT, Options.disableOreDictMode);
		ModNetworking.getInstance().sendTo(new PacketConfigSync(map), player);
	}

	public static enum ItemExtractionMode {

			KEEP_ALL(Integer.MAX_VALUE, TextUtils.translate("dn.not_extract.desc")),
			KEEP_1(1, TextUtils.translate("dn.extract_all_but.desc") + " 1"),
			KEEP_16(16, TextUtils.translate("dn.extract_all_but.desc") + " 16"),
			KEEP_64(64, TextUtils.translate("dn.extract_all_but.desc") + " 64"),
			KEEP_NONE(0, TextUtils.translate("dn.extract_all.desc"));

		int number = 0;
		String msg;

		ItemExtractionMode(final int numberToKeep, final String message) {
			number = numberToKeep;
			msg = message;
		}

		public int getNumberToKeep() {
			return number;
		}

		public String getMessage() {
			return TextUtils.translate("dn.will.desc") + " " + msg + " " + TextUtils.translate("dn.from_slot.desc");
		}

		public String getTooltip() {
			if (toString().equals("KEEP_ALL")) {
				return TextUtils.translate("dn.do.desc") + " " + msg;
			}
			return msg.substring(0, 1).toUpperCase() + msg.substring(1);
		}

	}

	public static enum ItemPlacementMode {

			KEEP_ALL(Integer.MAX_VALUE, TextUtils.translate("dn.not_extract.desc")),
			KEEP_1(1, TextUtils.translate("dn.extract_all_but.desc") + " 1"),
			KEEP_16(16, TextUtils.translate("dn.extract_all_but.desc") + " 16"),
			KEEP_64(64, TextUtils.translate("dn.extract_all_but.desc") + " 64"),
			KEEP_NONE(0, TextUtils.translate("dn.extract_all.desc"));

		int number = 0;
		String msg;

		ItemPlacementMode(final int numberToKeep, final String message) {
			number = numberToKeep;
			msg = message;
		}

		public int getNumberToKeep() {
			return number;
		}

		public String getMessage() {
			return TextUtils.translate("dn.will.desc") + " " + msg + " " + TextUtils.translate("dn.from_slot.desc");
		}

		public String getTooltip() {
			if (toString().equals("KEEP_ALL")) {
				return TextUtils.translate("dn.do.desc") + " " + msg;
			}
			return msg.substring(0, 1).toUpperCase() + msg.substring(1);
		}

	}

}