package p455w0rd.danknull.items;

import static p455w0rd.danknull.inventory.PlayerSlot.EnumInvCategory.MAIN;
import static p455w0rd.danknull.inventory.PlayerSlot.EnumInvCategory.OFF_HAND;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import p455w0rd.danknull.api.DankNullItemModes.ItemPlacementMode;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.client.render.DankNullRenderer;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals.DankNullTier;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rd.danknull.init.ModGuiHandler;
import p455w0rd.danknull.init.ModGuiHandler.GUIType;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.inventory.PlayerSlot.EnumInvCategory;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;
import p455w0rd.danknull.inventory.cap.DankNullCapabilityProvider;
import p455w0rdslib.api.client.*;
import p455w0rdslib.util.ItemNBTUtils;
import p455w0rdslib.util.TextUtils;

/**
 * @author p455w0rd
 *
 */
@SuppressWarnings({
		"unchecked", "deprecation"
})
public class ItemDankNull extends Item implements IModelHolder {

	private final DankNullTier tier;
	@SideOnly(Side.CLIENT)
	private ItemLayerWrapper wrappedModel;

	public ItemDankNull(final DankNullTier tier) {
		this.tier = tier;
		setRegistryName(tier.getDankNullRegistryName());
		setUnlocalizedName(tier.getUnlocalizedNameForDankNull());
		setMaxStackSize(1);
		setMaxDamage(0);
	}

	public static List<PlayerSlot> getDankNullsForPlayer(final EntityPlayer player) {
		final InventoryPlayer playerInv = player.inventory;
		final List<PlayerSlot> dankNullList = Lists.newArrayList();
		for (int i = 0; i < playerInv.mainInventory.size(); i++) {
			if (isDankNull(playerInv.mainInventory.get(i))) {
				dankNullList.add(new PlayerSlot(i, MAIN));
			}
		}
		for (int i = 0; i < playerInv.offHandInventory.size(); i++) {
			if (isDankNull(playerInv.offHandInventory.get(i))) {
				dankNullList.add(new PlayerSlot(i, OFF_HAND));
			}
		}
		return dankNullList;
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

	public static boolean isDankNull(final ItemStack stack) {
		return !stack.isEmpty() && stack.hasCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
	}

	public DankNullTier getTier() {
		return tier;
	}

	@Override
	public ICapabilityProvider initCapabilities(final ItemStack stack, final NBTTagCompound nbt) {
		return new DankNullCapabilityProvider(tier, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(final ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, getModelResource(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemLayerWrapper getWrappedModel() {
		return wrappedModel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setWrappedModel(final ItemLayerWrapper wrappedModel) {
		this.wrappedModel = wrappedModel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldUseInternalTEISR() {
		return true;
	}

	@Override
	public ICustomItemRenderer getRenderer() {
		return DankNullRenderer.getRendererForItem(this);
	}

	@Override
	public String getItemStackDisplayName(final ItemStack stack) {
		String name = TextUtils.translate(getUnlocalizedNameInefficiently(stack) + ".name").trim();
		if (Options.callItDevNull) {
			name = name.replace("/dank/", "/dev/");
		}
		return name;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		if (ItemNBTUtils.getString(stack, NBT.UUID).isEmpty() && !world.isRemote) {
			ItemNBTUtils.setString(stack, NBT.UUID, UUID.randomUUID().toString());
		}
		if (player.isSneaking() && getBlockUnderPlayer(player) != Blocks.AIR && !world.isRemote) {
			ModGuiHandler.launchGui(GUIType.DANKNULL, player, world, player.getPosition(), new PlayerSlot(player.inventory.currentItem, EnumInvCategory.MAIN));
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
		final PlayerSlot playerSlot = new PlayerSlot(player.inventory.currentItem, hand == EnumHand.MAIN_HAND ? MAIN : OFF_HAND);
		final IDankNullHandler dankNullHandler = stack.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);

		final ItemStack selectedStack = dankNullHandler.getSelected() > -1 ? dankNullHandler.getFullStackInSlot(dankNullHandler.getSelected()) : ItemStack.EMPTY;
		final Block selectedBlock = Block.getBlockFromItem(selectedStack.getItem());
		final boolean isSelectedStackABlock = selectedBlock != null && selectedBlock != Blocks.AIR;
		final Block blockUnderPlayer = getBlockUnderPlayer(player).getBlock();
		if (player.isSneaking() && blockUnderPlayer != Blocks.AIR && isSelectedStackABlock && blockUnderPlayer != selectedBlock) {
			ModGuiHandler.launchGui(GUIType.DANKNULL, player, world, player.getPosition(), playerSlot);
			return EnumActionResult.SUCCESS;
		}
		final ItemPlacementMode placementMode = dankNullHandler.getPlacementMode(selectedStack);
		if (placementMode == ItemPlacementMode.KEEP_ALL && !player.capabilities.isCreativeMode) {
			return EnumActionResult.FAIL;
		}
		if (placementMode != ItemPlacementMode.KEEP_NONE) {
			final int count = selectedStack.getCount();
			final int amountToKeep = placementMode.getNumberToKeep();
			if (count <= amountToKeep && !player.capabilities.isCreativeMode) {
				return EnumActionResult.FAIL;
			}
		}
		final IBlockState state = world.getBlockState(posIn);
		final Block block = state.getBlock();
		BlockPos pos = posIn;
		if (isBucket(selectedStack)) {
			if (tryUseBucket(world, player, selectedStack) == EnumActionResult.SUCCESS) {
				dankNullHandler.extractItem(dankNullHandler.getSelected(), 1, false);
				return EnumActionResult.SUCCESS;
			}
		}
		if (selectedStack.isEmpty() || !(selectedStack.getItem() instanceof ItemBlock) && !(selectedStack.getItem() instanceof ItemBlockSpecial)) { //TODO I do have an idea
			//return EnumActionResult.PASS;
		}
		if (!block.isReplaceable(world, posIn) && block == Blocks.SNOW_LAYER) {
			facing = EnumFacing.UP;
		}
		else if (!block.isReplaceable(world, posIn) && selectedBlock != null && !selectedBlock.isFullBlock(selectedBlock.getStateFromMeta(selectedStack.getMetadata()))) {
			pos = pos.offset(facing);
		}
		if (selectedStack.getCount() > 0 && player.canPlayerEdit(posIn, facing, stack)) {
			final int meta = selectedStack.getMetadata();
			if (selectedBlock instanceof BlockStairs || selectedBlock instanceof BlockBanner) {
				final IBlockState newState = selectedBlock.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, player);
				final EnumActionResult result = placeBlock(newState, world, pos);
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
					dankNullHandler.extractItem(dankNullHandler.getSelected(), 1, false);
				}
				return EnumActionResult.SUCCESS;
			}
			else if (selectedStack.getItem() instanceof ItemSnowball || selectedStack.getItem() instanceof ItemEnderPearl || selectedStack.getItem() instanceof ItemEgg) {
				//TODO soon!
			}
			else {
				final EnumActionResult result = placeItemIntoWorld(selectedStack.copy(), player, world, pos, facing, hitX, hitY, hitZ, hand);

				if (result == EnumActionResult.SUCCESS && !player.capabilities.isCreativeMode && !dankNullHandler.getTier().isCreative()) {
					dankNullHandler.extractItem(dankNullHandler.getSelected(), 1, false);
				}
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.SUCCESS;
	}

	private boolean isBucket(final ItemStack stack) {
		return stack.getItem() == Items.WATER_BUCKET || stack.getItem() == Items.LAVA_BUCKET || stack.getItem() instanceof UniversalBucket;
	}

	@Nonnull
	public EnumActionResult tryUseBucket(@Nonnull final World world, @Nonnull final EntityPlayer player, final ItemStack bucket) {
		if (!isBucket(bucket)) {
			return EnumActionResult.FAIL;
		}
		final FluidStack fluidStack = getFluid(bucket);
		if (fluidStack == null) {
			return EnumActionResult.PASS;
		}
		final RayTraceResult mop = this.rayTrace(world, player, false);
		if (mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK) {
			return EnumActionResult.PASS;
		}
		final BlockPos clickPos = mop.getBlockPos();
		if (FluidUtil.tryPlaceFluid(player, world, clickPos.offset(mop.sideHit), bucket.copy(), FluidUtil.getFluidContained(bucket)).success) {
			ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.BUCKET));
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	private FluidStack getFluid(final ItemStack stack) {
		if (isBucket(stack)) {
			return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).getTankProperties()[0].getContents();
		}
		return null;
	}

	public static EnumActionResult placeBlock(@Nonnull final IBlockState state, final World world, final BlockPos pos) {
		return world.setBlockState(pos, state, 2) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
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
		world.notifyNeighborsOfStateChange(pos, block, true);
		if (player instanceof EntityPlayerMP) {
			CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, tmpStack);
		}
		return true;
	}

	public EnumActionResult placeSlab(final EntityPlayer player, final World worldIn, final BlockPos pos, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final ItemStack itemstack, final ItemSlab slab) {
		final BlockSlab singleSlab = slab.singleSlab;
		if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack)) {
			final Comparable<?> comparable = singleSlab.getTypeForItem(itemstack);
			final IBlockState iblockstate = worldIn.getBlockState(pos);
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
		return EnumActionResult.FAIL;
	}

	private boolean tryPlace(final EntityPlayer player, final ItemStack stack, final World worldIn, final BlockPos pos, final Object itemSlabType, final ItemSlab slab) {
		final BlockSlab singleSlab = slab.singleSlab;
		final BlockSlab doubleSlab = slab.doubleSlab;
		final IBlockState iblockstate = worldIn.getBlockState(pos);
		if (iblockstate.getBlock() == singleSlab) {
			final Comparable<?> comparable = singleSlab.getTypeForItem(stack);
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
		final BlockSlab doubleSlab = slab.doubleSlab;
		return doubleSlab.getDefaultState().withProperty(p_185055_1_, (T) p_185055_2_);
	}

	@Override
	public IRarity getForgeRarity(final ItemStack stack) {
		return tier.getRarity();
	}

	@Override
	public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack, final boolean slotChanged) {
		return !oldStack.isItemEqual(newStack) || slotChanged;
	}

}
