package p455w0rd.danknull.integration;

import static mcjty.theoneprobe.api.TextStyleClass.*;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.api.IProbeConfig.ConfigMode;
import mcjty.theoneprobe.apiimpl.providers.DefaultProbeInfoProvider;
import mcjty.theoneprobe.apiimpl.styles.ItemStyle;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import mcjty.theoneprobe.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.items.ItemHandlerHelper;
import p455w0rd.danknull.api.DankNullItemModes.ItemExtractionMode;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.init.*;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rdslib.LibGlobals.Mods;
import p455w0rdslib.util.TextUtils;

/**
 * @author p455w0rd
 *
 */
public class TOP {

	private static boolean registered;

	public static void init() {
		if (registered) {
			return;
		}
		ModLogger.info(Mods.TOP.getName() + " Integation: Enabled");
		registered = true;
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "p455w0rd.danknull.integration.TOP$GetTheOneProbe");
	}

	public static class GetTheOneProbe implements com.google.common.base.Function<ITheOneProbe, Void> {

		@Nullable
		@Override
		public Void apply(final ITheOneProbe theOneProbe) {
			theOneProbe.registerProvider(new DankNullProbeInfoProvider());
			return null;
		}
	}

	public static class DankNullProbeInfoProvider implements IProbeInfoProvider {

		static final IProbeInfoProvider DEFAULT_PROVIDER = new DefaultProbeInfoProvider();

		@Override
		public String getID() {
			return TheOneProbe.MODID + ":default";
		}

		@Override
		public void addProbeInfo(final ProbeMode mode, final IProbeInfo probeInfo, final EntityPlayer player, final World world, final IBlockState blockState, final IProbeHitData data) {
			final Block block = blockState.getBlock();
			final BlockPos pos = data.getPos();
			final TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileDankNullDock) {
				final TileDankNullDock dankDock = (TileDankNullDock) tile;
				final ItemStack stack = new ItemStack(ModBlocks.DANKNULL_DOCK);
				if (tile != null && tile instanceof TileDankNullDock) {
					final TileDankNullDock te = (TileDankNullDock) tile;
					stack.setTagInfo(NBT.BLOCKENTITYTAG, te.writeToNBT(new NBTTagCompound()));
					final String dankNull = "/d" + (Options.callItDevNull ? "ev" : "ank") + "/null";
					final String msg = TextUtils.translate("dn.right_click_with.desc") + (te.getDankNull().isEmpty() ? " " + dankNull : " " + TextUtils.translate("dn.empty_hand_open.desc"));
					final ItemStack dockedDankNull = te.getDankNull().isEmpty() ? ItemStack.EMPTY : te.getDankNull();
					final IProbeInfo topTip = probeInfo.horizontal().item(stack).vertical().itemLabel(stack);
					if (!dockedDankNull.isEmpty()) {
						final IDankNullHandler dankNullHandler = dankDock.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
						final String dockedMsg = ModGlobals.Rarities.getRarityFromMeta(ItemDankNull.getTier(dockedDankNull).ordinal()).getColor() + "" + dockedDankNull.getDisplayName() + "" + TextFormatting.WHITE + " " + TextUtils.translate("dn.docked.desc");
						topTip.text(dockedMsg);
						if (dankNullHandler.getSelected() < 0) {
							return;
						}
						final ItemStack selectedStack = dankNullHandler.getFullStackInSlot(dankNullHandler.getSelected());
						if (!selectedStack.isEmpty()) {
							topTip.horizontal(new LayoutStyle().alignment(ElementAlignment.ALIGN_TOPLEFT).borderColor(0xFFFF0000).spacing(-1)).item(selectedStack);
							topTip.text(TextUtils.translate("dn.extract_mode.desc") + ": " + dankNullHandler.getExtractionMode(selectedStack).getTooltip());
						}
					}
					else {
						topTip.text(msg);
					}
					topTip.text(TextStyleClass.MODNAME.toString() + Tools.getModName(blockState.getBlock()));
				}
				final IProbeConfig config = Config.getRealConfig();
				final boolean showHarvestLevel = Tools.show(mode, config.getShowHarvestLevel());
				final boolean showHarvested = Tools.show(mode, config.getShowCanBeHarvested());
				if (showHarvested && showHarvestLevel) {
					HarvestInfoTools.showHarvestInfo(probeInfo, world, pos, block, blockState, player);
				}
				if (Tools.show(mode, config.getShowRedstone())) {
					final int power = world.getRedstonePower(data.getPos(), data.getSideHit().getOpposite());
					if (power > 0) {
						probeInfo.horizontal().item(new ItemStack(Items.REDSTONE), probeInfo.defaultItemStyle().width(14).height(14)).text(LABEL + "Power: " + INFO + power);
					}
				}
				DockInfoTools.showDockInfo(mode, probeInfo, world, pos, config);
			}
			else {
				DEFAULT_PROVIDER.addProbeInfo(mode, probeInfo, player, world, blockState, data);;
			}
		}

	}

	public static class HarvestInfoTools {

		private static final ResourceLocation ICONS = new ResourceLocation(TheOneProbe.MODID, "textures/gui/icons.png");
		private static String[] harvestLevels = new String[] {
				"stone", "iron", "diamond", "obsidian", "cobalt"
		};

		private static final HashMap<String, ItemStack> testTools = new HashMap<>();
		static {
			testTools.put("shovel", new ItemStack(Items.WOODEN_SHOVEL));
			testTools.put("axe", new ItemStack(Items.WOODEN_AXE));
			testTools.put("pickaxe", new ItemStack(Items.WOODEN_PICKAXE));
		}

		static void showHarvestLevel(final IProbeInfo probeInfo, final IBlockState blockState, final Block block) {
			final String harvestTool = block.getHarvestTool(blockState);
			if (harvestTool != null) {
				final int harvestLevel = block.getHarvestLevel(blockState);
				String harvestName;
				if (harvestLevel >= harvestLevels.length) {
					harvestName = Integer.toString(harvestLevel);
				}
				else if (harvestLevel < 0) {
					harvestName = Integer.toString(harvestLevel);
				}
				else {
					harvestName = harvestLevels[harvestLevel];
				}
				probeInfo.text(LABEL + "Tool: " + INFO + harvestTool + " (level " + harvestName + ")");
			}
		}

		static void showCanBeHarvested(final IProbeInfo probeInfo, final World world, final BlockPos pos, final Block block, final EntityPlayer player) {
			final boolean harvestable = block.canHarvestBlock(world, pos, player) && world.getBlockState(pos).getBlockHardness(world, pos) >= 0;
			if (harvestable) {
				probeInfo.text(OK + "Harvestable");
			}
			else {
				probeInfo.text(WARNING + "Not harvestable");
			}
		}

		static void showHarvestInfo(final IProbeInfo probeInfo, final World world, final BlockPos pos, final Block block, final IBlockState blockState, final EntityPlayer player) {
			final boolean harvestable = block.canHarvestBlock(world, pos, player) && world.getBlockState(pos).getBlockHardness(world, pos) >= 0;
			String harvestTool = block.getHarvestTool(blockState);
			String harvestName = null;
			if (harvestTool == null) {
				final float blockHardness = blockState.getBlockHardness(world, pos);
				if (blockHardness > 0f) {
					for (final Map.Entry<String, ItemStack> testToolEntry : testTools.entrySet()) {
						final ItemStack testTool = testToolEntry.getValue();
						if (testTool != null && testTool.getItem() instanceof ItemTool) {
							final ItemTool toolItem = (ItemTool) testTool.getItem();
							if (testTool.getDestroySpeed(blockState) >= toolItem.toolMaterial.getEfficiency()) {
								harvestTool = testToolEntry.getKey();
								break;
							}
						}
					}
				}
			}

			if (harvestTool != null) {
				final int harvestLevel = block.getHarvestLevel(blockState);
				if (harvestLevel >= 0 && harvestLevel < harvestLevels.length) {
					harvestName = harvestLevels[harvestLevel];
				}
				harvestTool = StringUtils.capitalize(harvestTool);
			}
			final boolean v = Config.harvestStyleVanilla;
			final int offs = v ? 16 : 0;
			final int dim = v ? 13 : 16;
			final ILayoutStyle alignment = probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER);
			final IIconStyle iconStyle = probeInfo.defaultIconStyle().width(v ? 18 : 20).height(v ? 14 : 16).textureWidth(32).textureHeight(32);
			final IProbeInfo horizontal = probeInfo.horizontal(alignment);
			if (harvestable) {
				horizontal.icon(ICONS, 0, offs, dim, dim, iconStyle).text(OK + (harvestTool != null ? harvestTool : "No tool"));
			}
			else {
				if (harvestName == null || harvestName.isEmpty()) {
					horizontal.icon(ICONS, 16, offs, dim, dim, iconStyle).text(WARNING + (harvestTool != null ? harvestTool : "No tool"));
				}
				else {
					horizontal.icon(ICONS, 16, offs, dim, dim, iconStyle).text(WARNING + (harvestTool != null ? harvestTool : "No tool") + " (level " + harvestName + ")");
				}
			}
		}
	}

	public static class DockInfoTools {

		static void showDockInfo(final ProbeMode mode, final IProbeInfo probeInfo, final World world, final BlockPos pos, final IProbeConfig config) {
			if (world.getTileEntity(pos) instanceof TileDankNullDock && !((TileDankNullDock) world.getTileEntity(pos)).getDankNull().isEmpty()) {
				final List<ItemStack> stacks = getDockContents(world, pos);
				if (!stacks.isEmpty()) {
					final boolean showDetailed = Tools.show(mode, ConfigMode.EXTENDED);// && stacks.size() <= Config.showItemDetailThresshold;
					showDockContents(((TileDankNullDock) world.getTileEntity(pos)).getDankNull(), probeInfo, stacks, showDetailed);
				}
			}
		}

		private static void addItemStack(final List<ItemStack> stacks, final Set<Item> foundItems, @Nonnull final ItemStack stack) {
			if (stack.isEmpty()) {
				return;
			}
			if (foundItems != null && foundItems.contains(stack.getItem())) {
				for (final ItemStack s : stacks) {
					if (ItemHandlerHelper.canItemStacksStack(s, stack)) {
						s.grow(stack.getCount());
						return;
					}
				}
			}
			// If we come here we need to append a new stack
			stacks.add(stack.copy());
			if (foundItems != null) {
				foundItems.add(stack.getItem());
			}
		}

		private static void showDockContents(final ItemStack dankNull, final IProbeInfo probeInfo, final List<ItemStack> stacks, final boolean detailed) {
			IProbeInfo vertical = null;
			IProbeInfo horizontal = null;
			int idx = 0;
			vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(Config.chestContentsBorderColor).spacing(0));
			if (detailed) {
				for (final ItemStack stackInSlot : stacks) {
					horizontal = vertical.horizontal(new LayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER));
					if (ItemDankNull.isDankNull(dankNull)) {
						final IDankNullHandler dankNullHandler = dankNull.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
						final ItemStack tmpStack = stackInSlot.copy();
						int extractable = stackInSlot.getCount() - dankNullHandler.getExtractionMode(stackInSlot).getNumberToKeep();
						if (dankNullHandler.getExtractionMode(stackInSlot) == ItemExtractionMode.KEEP_ALL) {
							extractable = 0;
						}
						horizontal.item(tmpStack, new ItemStyle().width(16).height(16)).text(INFO + stackInSlot.getDisplayName() + " (" + extractable + " extractable)");
					}
					else {
						horizontal.item(stackInSlot, new ItemStyle().width(16).height(16)).text(INFO + stackInSlot.getDisplayName());
					}
				}
			}
			else {
				for (final ItemStack stackInSlot : stacks) {
					if (idx % 10 == 0) {
						horizontal = vertical.horizontal(new LayoutStyle().spacing(0));
					}
					horizontal.item(stackInSlot);
					idx++;
				}
			}
		}

		private static List<ItemStack> getDockContents(final World world, final BlockPos pos) {
			final List<ItemStack> stacks = new ArrayList<>();
			final TileEntity te = world.getTileEntity(pos);
			final Set<Item> foundItems = Config.compactEqualStacks ? new HashSet<>() : null;
			if (te instanceof TileDankNullDock && te.hasCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null)) {
				final IDankNullHandler handler = te.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
				for (int i = 0; i < handler.getSlots(); i++) {
					addItemStack(stacks, foundItems, handler.getFullStackInSlot(i));
				}
			}
			return stacks;
		}
	}

}
