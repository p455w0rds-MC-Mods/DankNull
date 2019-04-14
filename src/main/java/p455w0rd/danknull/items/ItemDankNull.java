package p455w0rd.danknull.items;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.*;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import p455w0rd.danknull.api.IModelHolder;
import p455w0rd.danknull.client.render.DankNullRenderer;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModGuiHandler;
import p455w0rd.danknull.init.ModGuiHandler.GUIType;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rd.danknull.util.DankNullUtils.SlotExtractionMode;

/**
 * @author p455w0rd
 *
 */
@SuppressWarnings({
		"unchecked", "deprecation"
})
public class ItemDankNull extends Item implements IModelHolder {

	public static String INV_NAME = "danknull-inventory";
	InventoryDankNull inventory = null;

	public ItemDankNull() {
		setRegistryName("dank_null");
		setUnlocalizedName("dank_null");
		ForgeRegistries.ITEMS.register(this);
		setMaxStackSize(1);
		setMaxDamage(0);
	}

	@Override
	public ICapabilityProvider initCapabilities(final ItemStack stack, final NBTTagCompound nbt) {
		return new ICapabilityProvider() {
			@Override
			public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
				return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
			}

			@Override
			public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
				return hasCapability(capability, facing) ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new InvWrapper(DankNullUtils.getNewDankNullInventory(stack))) : null;
			}

		};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(final ItemStack stack, @Nullable final World worldIn, final List<String> tooltip, final ITooltipFlag flagIn) {
		int numSlots = DankNullUtils.getSlotCount(stack);
		String numPerSlot = "0";
		if (DankNullUtils.isCreativeDankNull(stack)) {
			numSlots -= 9;
			numPerSlot = I18n.translateToLocal("dn.infinity");
		}
		else {
			final DecimalFormat df = new DecimalFormat("###,###.###", new DecimalFormatSymbols(Locale.US));
			numPerSlot = I18n.translateToLocal("dn.upto") + " " + df.format(Double.valueOf(DankNullUtils.getDankNullMaxStackSize(stack)));
		}
		tooltip.add(I18n.translateToLocalFormatted("dn.numslots", numSlots));
		tooltip.add(I18n.translateToLocalFormatted("dn.num_per_slot", numPerSlot));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(final ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for (int i = 0; i <= 6; i++) {
			final ResourceLocation regName = new ResourceLocation(getRegistryName().getResourceDomain(), getRegistryName().getResourcePath() + "_" + i);
			final ModelResourceLocation location = new ModelResourceLocation(regName, "inventory");
			ModelLoader.setCustomModelResourceLocation(this, i, location);
			ModelRegistryHelper.register(location, new DankNullRenderer(() -> new ModelResourceLocation(regName, "inventory")));
		}
	}

	@Override
	public String getItemStackDisplayName(final ItemStack stack) {
		String name = I18n.translateToLocal(getUnlocalizedNameInefficiently(stack) + "_" + getDamage(stack) + ".name").trim();
		if (Options.callItDevNull) {
			name = name.replace("/dank/", "/dev/");
		}
		return name;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking() && (Options.ignoreEdgeDetection || getBlockUnderPlayer(player) != Blocks.AIR)) {
			ModGuiHandler.launchGui(GUIType.DANKNULL, player, world);
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		}
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab)) {
			for (int i = 0; i <= 6; i++) {
				subItems.add(new ItemStack(this, 1, i));
			}
		}
	}

	@Override
	public boolean getHasSubtypes() {
		return true;
	}

	@Override
	public boolean isDamaged(final ItemStack stack) {
		return false;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	@Override
	public boolean showDurabilityBar(final ItemStack stack) {
		return false;
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	private IBlockState getBlockUnderPlayer(final EntityPlayer player) {
		final int blockX = MathHelper.floor(player.posX);
		final int blockY = MathHelper.floor(player.getEntityBoundingBox().minY - 0.5);
		final int blockZ = MathHelper.floor(player.posZ);
		return player.getEntityWorld().getBlockState(new BlockPos(blockX, blockY, blockZ));
	}

	public RayTraceResult rayTrace(final EntityPlayer player, final double blockReachDistance, final float partialTicks) {
		final Vec3d vec3d = player.getPositionEyes(partialTicks);
		final Vec3d vec3d1 = player.getLook(partialTicks);
		final Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
		return player.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
	}

	@Override
	public EnumActionResult onItemUse(final EntityPlayer player, final World world, final BlockPos posIn, final EnumHand hand, EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
		final ItemStack stack = player.getHeldItem(hand);
		final InventoryDankNull inventory = new InventoryDankNull(stack);
		final ItemStack selectedStack = DankNullUtils.getSelectedStack(inventory);
		final Block selectedBlock = Block.getBlockFromItem(selectedStack.getItem());
		final boolean isSelectedStackABlock = selectedBlock != null && selectedBlock != Blocks.AIR;
		final Block blockUnderPlayer = getBlockUnderPlayer(player).getBlock();
		if (player.isSneaking() && (Options.ignoreEdgeDetection || blockUnderPlayer != Blocks.AIR && isSelectedStackABlock && blockUnderPlayer != selectedBlock)) {
			ModGuiHandler.launchGui(GUIType.DANKNULL, player, world);
			return EnumActionResult.SUCCESS;
		}
		final SlotExtractionMode placementMode = DankNullUtils.getPlacementModeForStack(stack, selectedStack);
		if (placementMode != null) {
			if (placementMode != SlotExtractionMode.KEEP_NONE) {
				final int count = DankNullUtils.getSelectedStackSize(inventory);
				final int amountToKeep = placementMode.getNumberToKeep();
				if (count <= amountToKeep && !player.capabilities.isCreativeMode) {
					return EnumActionResult.FAIL;
				}
			}
		}
		final IBlockState state = world.getBlockState(posIn);
		final Block block = state.getBlock();
		BlockPos pos = posIn;

		if (selectedStack.isEmpty() || !(selectedStack.getItem() instanceof ItemBlock) && !(selectedStack.getItem() instanceof ItemBlockSpecial)) { //TODO I do have an idea
			//return EnumActionResult.PASS;
		}
		if (!block.isReplaceable(world, posIn) && block == Blocks.SNOW_LAYER) {
			facing = EnumFacing.UP;
		}
		else if (!block.isReplaceable(world, posIn) && selectedBlock != null && !selectedBlock.isFullBlock(selectedBlock.getStateFromMeta(selectedStack.getMetadata()))) {
			pos = pos.offset(facing);
		}
		if (DankNullUtils.getSelectedStackSize(inventory) > 0 && player.canPlayerEdit(posIn, facing, stack)) {
			final int meta = selectedStack.getMetadata();
			if (selectedBlock instanceof BlockStairs || selectedBlock instanceof BlockBanner) {
				final IBlockState newState = selectedBlock.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, player);
				final EnumActionResult result = DankNullUtils.placeBlock(newState, world, pos);
				if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityBanner) {
					if (facing == EnumFacing.UP) {
						final int i = MathHelper.floor((player.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5D) & 15;
						world.setBlockState(pos, Blocks.STANDING_BANNER.getDefaultState().withProperty(BlockStandingSign.ROTATION, Integer.valueOf(i)), 3);
					}
					else {
						world.setBlockState(pos, Blocks.WALL_BANNER.getDefaultState().withProperty(BlockWallSign.FACING, facing), 3);
					}
					((TileEntityBanner) world.getTileEntity(pos)).setItemValues(selectedStack, false);
				}
				if (result != EnumActionResult.FAIL) {
					final SoundType soundType = block.getSoundType(newState, world, pos, player);
					world.playSound((EntityPlayer) null, player.getPosition(), soundType.getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 2F));
				}
				if (!player.capabilities.isCreativeMode) {
					DankNullUtils.decrSelectedStackSize(inventory, 1);
				}
				return EnumActionResult.SUCCESS;
			}
			else if (selectedStack.getItem() instanceof ItemBucket || selectedStack.getItem() instanceof UniversalBucket) {
				//TODO soon!
			}
			else if (selectedStack.getItem() instanceof ItemSnowball || selectedStack.getItem() instanceof ItemEnderPearl || selectedStack.getItem() instanceof ItemEgg) {
				//TODO soon!
			}
			else {
				final EnumActionResult result = placeItemIntoWorld(selectedStack.copy(), player, world, pos, facing, hitX, hitY, hitZ, hand);

				if (result == EnumActionResult.SUCCESS && !player.capabilities.isCreativeMode && !DankNullUtils.isCreativeDankNull(stack)) {
					DankNullUtils.decrSelectedStackSize(inventory, 1);
				}
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.SUCCESS;
	}

	public EnumActionResult placeItemIntoWorld(@Nonnull final ItemStack itemstack, @Nonnull final EntityPlayer player, @Nonnull final World world, @Nonnull final BlockPos pos, @Nonnull final EnumFacing facing, final float hitX, final float hitY, final float hitZ, @Nonnull final EnumHand hand) {
		return placeItemIntoWorld(itemstack, player, world, pos, facing, hitX, hitY, hitZ, hand, false);
	}

	public EnumActionResult placeItemIntoWorld(@Nonnull final ItemStack itemstack, @Nonnull final EntityPlayer player, @Nonnull final World world, @Nonnull BlockPos pos, @Nonnull final EnumFacing facing, final float hitX, final float hitY, final float hitZ, @Nonnull final EnumHand hand, final boolean skipSlab) {
		if (itemstack.getItem() instanceof ItemBlock) {
			final Block block = Block.getBlockFromItem(itemstack.getItem());
			ItemSlab slab = null;
			if (itemstack.getItem() instanceof ItemSlab && !skipSlab) {
				slab = (ItemSlab) itemstack.getItem();
			}

			if (slab != null) {
				return placeSlab(player, world, pos.offset(facing.getOpposite()), hand, facing, hitX, hitY, hitZ, itemstack, slab);
			}
			if (!block.isReplaceable(world, pos)) {
				pos = pos.offset(facing);
			}
			if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && world.mayPlace(block, pos, false, facing, (Entity) null)) {
				IBlockState iblockstate1 = block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, itemstack.getItemDamage(), player, hand);

				if (placeBlockAt(itemstack, player, world, pos, facing, hitX, hitY, hitZ, iblockstate1, block)) {
					iblockstate1 = world.getBlockState(pos);
					final SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, world, pos, player);
					//world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					world.playSound((EntityPlayer) null, player.getPosition(), soundtype.getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 2F));
				}
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}

	public boolean placeBlockAt(final ItemStack stack, final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final IBlockState newState, final Block block) {
		final ItemStack tmpStack = stack.copy();
		tmpStack.setCount(1);
		if (tmpStack.getItem() instanceof ItemBlock) {
			final ItemBlock blockItem = (ItemBlock) tmpStack.getItem();
			blockItem.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
			return true;
		}
		world.setBlockState(pos, newState, 3);
		//IBlockState state = world.getBlockState(pos);
		//if (state.getBlock() == block) {
		//world.setBlockState(pos, newState, 3);
		//Block worldBlock = state.getBlock();
		//ItemBlock.setTileEntityNBT(world, player, pos, tmpStack);
		//block.onBlockPlacedBy(world, pos, newState, player, tmpStack);
		world.notifyNeighborsOfStateChange(pos, block, true);
		if (player instanceof EntityPlayerMP) {
			CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, tmpStack);
		}
		return true;
	}

	public EnumActionResult placeSlab(final EntityPlayer player, final World worldIn, final BlockPos pos, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final ItemStack itemstack, final ItemSlab slab) {
		final BlockSlab singleSlab = ObfuscationReflectionHelper.getPrivateValue(ItemSlab.class, slab, "singleSlab");
		//BlockSlab doubleSlab = ReflectionHelper.getPrivateValue(ItemSlab.class, slab, "doubleSlab");
		if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack)) {
			final Comparable<?> comparable = singleSlab.getTypeForItem(itemstack);
			final IBlockState iblockstate = worldIn.getBlockState(pos);//block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, itemstack.getItemDamage(), player);
			final ItemStack blockAsStack = new ItemStack(Item.getItemFromBlock(iblockstate.getBlock()), 1, iblockstate.getBlock().getMetaFromState(iblockstate));
			if (iblockstate.getBlock() == singleSlab && ((BlockSlab) iblockstate.getBlock()).getTypeForItem(blockAsStack) == comparable) {
				final IProperty<?> iproperty = singleSlab.getVariantProperty();
				final Comparable<?> comparable1 = iblockstate.getValue(iproperty);
				final BlockSlab.EnumBlockHalf blockslab$enumblockhalf = iblockstate.getValue(BlockSlab.HALF);
				if ((facing == EnumFacing.UP && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.BOTTOM || facing == EnumFacing.DOWN && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.TOP) && comparable1 == comparable) {
					final IBlockState iblockstate1 = makeState(iproperty, comparable1, slab);
					final AxisAlignedBB axisalignedbb = iblockstate1.getCollisionBoundingBox(worldIn, pos);
					if (axisalignedbb != Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb.offset(pos)) && worldIn.setBlockState(pos, iblockstate1, 3)) {
						final SoundType soundtype = singleSlab.getSoundType(iblockstate1, worldIn, pos, player);
						worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
						if (player instanceof EntityPlayerMP) {
							CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, itemstack);
						}
					}
					return tryPlace(player, itemstack, worldIn, pos, comparable, slab) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
				}
				return tryPlace(player, itemstack, worldIn, pos.offset(facing), comparable, slab) ? EnumActionResult.SUCCESS : placeItemIntoWorld(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, hand, true);
			}
			return tryPlace(player, itemstack, worldIn, pos.offset(facing), comparable, slab) ? EnumActionResult.SUCCESS : placeItemIntoWorld(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, hand, true);
		}
		//else {
		return EnumActionResult.FAIL;
		//}
	}

	private boolean tryPlace(final EntityPlayer player, final ItemStack stack, final World worldIn, final BlockPos pos, final Object itemSlabType, final ItemSlab slab) {
		final BlockSlab singleSlab = ReflectionHelper.getPrivateValue(ItemSlab.class, slab, "singleSlab");
		final BlockSlab doubleSlab = ReflectionHelper.getPrivateValue(ItemSlab.class, slab, "doubleSlab");
		final IBlockState iblockstate = worldIn.getBlockState(pos);
		if (iblockstate.getBlock() == singleSlab) {
			final Comparable<?> comparable = singleSlab.getTypeForItem(stack);//slab.getBlock().getDefaultState().getValue(singleSlab.getVariantProperty());
			final ItemStack blockAsStack = new ItemStack(Item.getItemFromBlock(iblockstate.getBlock()), 1, iblockstate.getBlock().getMetaFromState(iblockstate));
			if (comparable == itemSlabType && ((BlockSlab) iblockstate.getBlock()).getTypeForItem(blockAsStack) == comparable) {
				final IBlockState iblockstate1 = makeState(singleSlab.getVariantProperty(), comparable, slab);
				final AxisAlignedBB axisalignedbb = iblockstate1.getCollisionBoundingBox(worldIn, pos);
				if (axisalignedbb != Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb.offset(pos)) && worldIn.setBlockState(pos, iblockstate1, 3)) {
					final SoundType soundtype = doubleSlab.getSoundType(iblockstate1, worldIn, pos, player);
					worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				}
				return true;
			}
		}
		return false;
	}

	protected <T extends Comparable<T>> IBlockState makeState(final IProperty<T> p_185055_1_, final Comparable<?> p_185055_2_, final ItemSlab slab) {
		final BlockSlab doubleSlab = ReflectionHelper.getPrivateValue(ItemSlab.class, slab, "doubleSlab");
		return doubleSlab.getDefaultState().withProperty(p_185055_1_, (T) p_185055_2_);
	}

	@Override
	public EnumRarity getRarity(final ItemStack stack) {
		return ModGlobals.Rarities.getRarityFromMeta(stack.getItemDamage());
	}

	@Override
	public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack, final boolean slotChanged) {
		return !oldStack.isItemEqual(newStack) || slotChanged;
	}

}
