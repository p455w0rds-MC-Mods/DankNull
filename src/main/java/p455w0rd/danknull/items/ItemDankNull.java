package p455w0rd.danknull.items;

import static p455w0rd.danknull.inventory.PlayerSlot.EnumInvCategory.MAIN;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.client.render.DankNullRenderer;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals.DankNullTier;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rd.danknull.init.ModGuiHandler;
import p455w0rd.danknull.init.ModGuiHandler.GUIType;
import p455w0rd.danknull.integration.PwLib;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.inventory.PlayerSlot.EnumInvCategory;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rd.danknull.util.DankNullUtils.ItemPlacementMode;
import p455w0rdslib.api.client.*;
import p455w0rdslib.integration.Albedo;
import p455w0rdslib.util.*;

/**
 * @author p455w0rd
 *
 */
@SuppressWarnings({
		"unchecked", "deprecation"
})
public class ItemDankNull extends Item implements IModelHolder/*, IBlockLightEmitter*/ {

	InventoryDankNull inventory = null;
	DankNullTier tier;

	@SideOnly(Side.CLIENT)
	ItemLayerWrapper wrappedModel;

	public ItemDankNull(final DankNullTier tier) {
		this.tier = tier;
		setRegistryName(tier.getDankNullRegistryName());
		setUnlocalizedName(tier.getUnlocalizedNameForDankNull());
		setMaxStackSize(1);
		setMaxDamage(0);
	}

	public DankNullTier getTier() {
		return tier;
	}

	@Override
	public ICapabilityProvider initCapabilities(final ItemStack stack, final NBTTagCompound nbt) {
		return new ICapabilityProvider() {
			@Override
			public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
				return CapabilityUtils.isItemHandler(capability) || Albedo.albedoCapCheck(capability) || PwLib.checkCap(capability);
			}

			@Override
			public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
				if (hasCapability(capability, facing)) {
					if (Albedo.albedoCapCheck(capability)) {
						return p455w0rd.danknull.integration.Albedo.getStackCapability(stack);
					}
					else if (PwLib.checkCap(capability)) {
						return PwLib.getStackCapability(stack);
					}
					else if (CapabilityUtils.isItemHandler(capability)) {
						return CapabilityUtils.getWrappedItemHandler(new InventoryDankNull(stack));
					}
				}
				return null;
			}

		};
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
		final PlayerSlot playerSlot = new PlayerSlot(player.inventory.currentItem, MAIN);
		final InventoryDankNull inventory = new InventoryDankNull(playerSlot, player);

		final ItemStack selectedStack = DankNullUtils.getSelectedStack(inventory);
		final Block selectedBlock = Block.getBlockFromItem(selectedStack.getItem());
		final boolean isSelectedStackABlock = selectedBlock != null && selectedBlock != Blocks.AIR;
		final Block blockUnderPlayer = getBlockUnderPlayer(player).getBlock();
		if (player.isSneaking() && blockUnderPlayer != Blocks.AIR && isSelectedStackABlock && blockUnderPlayer != selectedBlock) {
			ModGuiHandler.launchGui(GUIType.DANKNULL, player, world, player.getPosition(), playerSlot);
			return EnumActionResult.SUCCESS;
		}
		final ItemPlacementMode placementMode = DankNullUtils.getPlacementModeForStack(stack, selectedStack);
		if (placementMode != null) {
			if (placementMode == ItemPlacementMode.KEEP_ALL && !player.capabilities.isCreativeMode) {
				return EnumActionResult.FAIL;
			}
			if (placementMode != ItemPlacementMode.KEEP_NONE) {
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

	/*private static int brightness = 0;
	private static boolean brightnessDir = false;
	private static int step = 0;
	private static boolean initLight = false;
	
	@Override
	public void emitLight(final List<Light> lights, final Entity e) {
		if (e == null || !Options.enabledColoredLightShaderSupport) {
			return;
		}
		if (!initLight) {
			step = e.getEntityWorld().rand.nextInt(4);
			initLight = true;
		}
		ItemStack lightStack = ItemStack.EMPTY;
		if (e instanceof EntityPlayer) {
			for (final ItemStack stack : ((EntityPlayer) e).getHeldEquipment()) {
				if (DankNullUtils.isDankNull(stack)) {
					lightStack = stack.copy();
					break;
				}
				else if (stack.getItem() instanceof ItemBlockDankNullDock) {
					lightStack = DankNullUtils.getDockedDankNull(stack);
				}
			}
		}
		else if (e instanceof EntityItem) {
			if (DankNullUtils.isDankNull(((EntityItem) e).getItem())) {
				lightStack = ((EntityItem) e).getItem();
			}
			else if (((EntityItem) e).getItem().getItem() instanceof ItemBlockDankNullDock) {
				lightStack = DankNullUtils.getDockedDankNull(((EntityItem) e).getItem());
			}
		}
		if (!lightStack.isEmpty() && lightStack.hasEffect()) {
			if (brightnessDir) {
				brightness++;
				if (brightness > DankNullUtils.STEPS_MOST[step]) {
					brightnessDir = !brightnessDir;
					step++;
					if (step > 4) {
						step = 0;
					}
				}
			}
			else {
				brightness--;
				if (brightness < DankNullUtils.STEPS_LEAST[step]) {
					brightnessDir = !brightnessDir;
					step++;
					if (step > 4) {
						step = 0;
					}
				}
			}
	
			final Vec3i c = RenderUtils.hexToRGB(DankNullUtils.getTier(lightStack).getHexColor(false));
			lights.add(Light.builder().pos(e).color(c.getX(), c.getY(), c.getZ(), (float) (brightness * 0.001)).radius(2.5f).intensity(5).build());
		}
		else {
			brightnessDir = false;
			brightness = 0;
			step = 0;
		}
	}*/

}
