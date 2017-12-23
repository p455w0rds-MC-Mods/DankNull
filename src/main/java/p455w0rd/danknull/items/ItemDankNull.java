package p455w0rd.danknull.items;

import javax.annotation.Nonnull;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import p455w0rd.danknull.api.IModelHolder;
import p455w0rd.danknull.client.render.DankNullRenderer;
import p455w0rd.danknull.client.render.PModelRegistryHelper;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModGuiHandler;
import p455w0rd.danknull.init.ModGuiHandler.GUIType;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
@SuppressWarnings({
		"unchecked",
		"deprecation"
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
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new ICapabilityProvider() {

			@Override
			public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
				return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
			}

			@Override
			public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
				return hasCapability(capability, facing) ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new InvWrapper(DankNullUtils.getNewDankNullInventory(stack))) : null;
			}

		};

	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for (int i = 0; i < 6; i++) {
			PModelRegistryHelper.registerMetaRenderer(this, DankNullRenderer.getInstance(), i);
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return I18n.translateToLocal(getUnlocalizedNameInefficiently(stack) + "_" + getDamage(stack) + ".name").trim();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (playerIn.isSneaking()) {
			/*
			}
			for (int i = 0; i < 5; ++i) {
				RayTraceResult rayTrace = playerIn.rayTrace(i, 1.0F);
				if (rayTrace != null) {
					BlockPos pos = rayTrace.getBlockPos();
					EnumFacing side = rayTrace.sideHit;
					float hitX = (float) rayTrace.hitVec.xCoord;
					float hitY = (float) rayTrace.hitVec.yCoord;
					float hitZ = (float) rayTrace.hitVec.zCoord;
					IBlockState state = worldIn.getBlockState(pos);
					Block block = state.getBlock();
					boolean flag = block.onBlockActivated(worldIn, pos, state, playerIn, hand, itemStackIn, side, hitX, hitY, hitZ);
					if (rayTrace.getBlockPos() != null && (flag || block != Blocks.AIR || block instanceof ITileEntityProvider || (!flag && block.hasTileEntity(state)))) {
					if (rayTrace.getBlockPos() != null && (block != Blocks.AIR || block instanceof ITileEntityProvider || block.hasTileEntity(state))) {
						return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
					}
				}
			}
			*/
			ModGuiHandler.launchGui(GUIType.DANKNULL, playerIn, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		}
		else {
			if (!worldIn.isRemote) {
				InventoryDankNull inventory = new InventoryDankNull(stack);
				ItemStack selectedStack = DankNullUtils.getSelectedStack(inventory);

				if ((selectedStack.getItem() instanceof ItemSnowball) || (selectedStack.getItem() instanceof ItemEnderPearl) || (selectedStack.getItem() instanceof ItemEgg)) {
					//TODO soon!
					return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
				}
				/*
								EntityPFakePlayer fakePlayer = EntityPFakePlayer.getFakePlayerForParent((EntityPlayerMP) playerIn);
								if (fakePlayer.interactionManager.getGameType() != GameType.NOT_SET) {
									fakePlayer.interactionManager.setGameType(GameType.NOT_SET);
								}
								fakePlayer.setHeldItem(hand, selectedStack);
								fakePlayer.setItemInHand(selectedStack);
								fakePlayer.posX = playerIn.posX;
								fakePlayer.posY = playerIn.posY;
								fakePlayer.posZ = playerIn.posZ;
								fakePlayer.setRotationYawHead(playerIn.rotationYawHead);
								fakePlayer.rotationYaw = playerIn.rotationYaw;
								fakePlayer.rotationPitch = playerIn.rotationPitch;
								fakePlayer.eyeHeight = playerIn.eyeHeight;
				
								if (selectedStack.copy().getItem().onItemRightClick(worldIn, fakePlayer, hand).getType() == EnumActionResult.SUCCESS) {
									DankNullUtils.decrSelectedStackSize(inventory, 0);
								}
								return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
								*/
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab)) {
			for (int i = 0; i < 6; i++) {
				subItems.add(new ItemStack(this, 1, i));
			}
		}
	}

	@Override
	public boolean getHasSubtypes() {
		return true;
	}

	@Override
	public boolean isDamaged(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	private Block getBlockUnderPlayer(EntityPlayer player) {
		int blockX = MathHelper.floor(player.posX);
		int blockY = MathHelper.floor(player.getEntityBoundingBox().minY - 1);
		int blockZ = MathHelper.floor(player.posZ);
		return player.getEntityWorld().getBlockState(new BlockPos(blockX, blockY, blockZ)).getBlock();
	}

	public RayTraceResult rayTrace(EntityPlayer player, double blockReachDistance, float partialTicks) {
		Vec3d vec3d = player.getPositionEyes(partialTicks);
		Vec3d vec3d1 = player.getLook(partialTicks);
		Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
		return player.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos posIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return EnumActionResult.SUCCESS;
		}
		/*
		RayTraceResult ray = rayTrace(player, 100, 1.0F);
		Block hitBlock = null;
		if (ray != null) {
			hitBlock = worldIn.getBlockState(ray.getBlockPos()).getBlock();
		}
		*/
		Block blockUnderPlayer = getBlockUnderPlayer(player);
		if (player.isSneaking() && blockUnderPlayer != Blocks.AIR) {
			ModGuiHandler.launchGui(GUIType.DANKNULL, player, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			return EnumActionResult.SUCCESS;
		}
		ItemStack stack = player.getHeldItem(hand);
		InventoryDankNull inventory = new InventoryDankNull(stack);
		ItemStack selectedStack = DankNullUtils.getSelectedStack(inventory);
		IBlockState state = world.getBlockState(posIn);
		Block block = state.getBlock();
		BlockPos pos = posIn;

		if (selectedStack.isEmpty() || (!(selectedStack.getItem() instanceof ItemBlock) && !(selectedStack.getItem() instanceof ItemBlockSpecial))) { //TODO I do have an idea
			//return EnumActionResult.PASS;
		}

		if ((block.isReplaceable(world, posIn)) && (block == Blocks.SNOW_LAYER)) {
			facing = EnumFacing.UP;
		}
		else if (block.isReplaceable(world, posIn)) {
			pos = posIn.offset(facing);
		}
		if ((DankNullUtils.getSelectedStackSize(inventory) > 0) && (player.canPlayerEdit(posIn, facing, stack))) {
			//pos = posIn.offset(facing);
			//EnumActionResult result = selectedStack.copy().onItemUse(player, worldIn, pos.up(), hand, facing, hitX, hitY, hitZ);
			Block selectedBlock = Block.getBlockFromItem(selectedStack.getItem());
			int meta = selectedStack.getMetadata();
			if (selectedBlock instanceof BlockStairs || selectedBlock instanceof BlockBanner) {
				IBlockState newState = selectedBlock.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, player);
				EnumActionResult result = DankNullUtils.placeBlock(newState, world, pos);
				//EnumActionResult result = EnumActionResult.SUCCESS;
				//selectedBlock.onBlockPlacedBy(worldIn, pos, newState, player, selectedStack);
				if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityBanner) {
					if (facing == EnumFacing.UP) {
						int i = MathHelper.floor((player.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5D) & 15;
						world.setBlockState(pos, Blocks.STANDING_BANNER.getDefaultState().withProperty(BlockStandingSign.ROTATION, Integer.valueOf(i)), 3);
					}
					else {
						world.setBlockState(pos, Blocks.WALL_BANNER.getDefaultState().withProperty(BlockWallSign.FACING, facing), 3);
					}
					((TileEntityBanner) world.getTileEntity(pos)).setItemValues(selectedStack, false);
				}
				if (result != EnumActionResult.FAIL) {
					SoundType soundType = block.getSoundType(newState, world, pos, player);
					world.playSound((EntityPlayer) null, player.getPosition(), soundType.getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 2F));
				}
				if (!player.capabilities.isCreativeMode) {
					DankNullUtils.decrSelectedStackSize(inventory, 1);
				}
				if (FMLCommonHandler.instance().getSide().isServer()) {
					//for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
					//ModNetworking.INSTANCE.sendTo(new PacketSyncDankNull(DankNullUtils.getInventory(stack).serializeNBT()), player);
					//}
				}
				return EnumActionResult.PASS;
			}
			else if (selectedStack.getItem() instanceof ItemBucket || selectedStack.getItem() instanceof UniversalBucket) {
				//TODO soon!
			}
			else if ((selectedStack.getItem() instanceof ItemSnowball) || (selectedStack.getItem() instanceof ItemEnderPearl) || (selectedStack.getItem() instanceof ItemEgg)) {
				//TODO soon!
			}
			else {/*
					EntityPFakePlayer fakePlayer = EntityPFakePlayer.getFakePlayerForParent((EntityPlayerMP) player);
					if (fakePlayer.interactionManager.getGameType() != GameType.NOT_SET) {
					fakePlayer.interactionManager.setGameType(GameType.NOT_SET);
					}
					fakePlayer.setHeldItem(hand, selectedStack.copy());
					fakePlayer.setItemInHand(selectedStack.copy());
					fakePlayer.posX = player.posX;
					fakePlayer.posY = player.posY;
					fakePlayer.posZ = player.posZ;
					fakePlayer.setRotationYawHead(player.rotationYawHead);
					fakePlayer.rotationYaw = player.rotationYaw;
					fakePlayer.rotationPitch = player.rotationPitch;
					fakePlayer.eyeHeight = player.eyeHeight;
					*/
				//EnumActionResult result = selectedStack.copy().onItemUse(fakePlayer, worldIn, posIn, hand, facing, hitX, hitY, hitZ);
				//EnumActionResult result = selectedStack.copy().onItemUse(player, worldIn, posIn, hand, facing, hitX, hitY, hitZ);
				//if (result == EnumActionResult.PASS) {
				//result=selectedStack.copy().getItem().onItemRightClick(worldIn, fakePlayer, hand).getType();
				EnumActionResult result = placeItemIntoWorld(selectedStack.copy(), player, world, pos, facing, hitX, hitY, hitZ, hand);
				//selectedStack.copy().getItem().onItemRightClick(worldIn, player, hand).getType();
				//}
				/*else*/ if (result == EnumActionResult.SUCCESS && !player.capabilities.isCreativeMode) {
					DankNullUtils.decrSelectedStackSize(inventory, 1);
				}
				return EnumActionResult.PASS;
			}
		}
		return EnumActionResult.PASS;
	}

	public EnumActionResult placeItemIntoWorld(@Nonnull ItemStack itemstack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, @Nonnull EnumHand hand) {
		return placeItemIntoWorld(itemstack, player, world, pos, facing, hitX, hitY, hitZ, hand, false);
	}

	public EnumActionResult placeItemIntoWorld(@Nonnull ItemStack itemstack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, @Nonnull EnumHand hand, boolean skipSlab) {
		if (itemstack.getItem() instanceof ItemBlock) {
			Block block = Block.getBlockFromItem(itemstack.getItem());
			ItemSlab slab = null;
			if (itemstack.getItem() instanceof ItemSlab && !skipSlab) {
				slab = (ItemSlab) itemstack.getItem();
			}

			if (slab != null) {
				return placeSlab(player, world, pos, hand, facing, hitX, hitY, hitZ, itemstack, slab);
			}
			if (!block.isReplaceable(world, pos)) {
				pos = pos.offset(facing);
			}
			if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && world.mayPlace(block, pos, false, facing, (Entity) null)) {
				IBlockState iblockstate1 = block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, itemstack.getMetadata(), player, hand);

				if (placeBlockAt(itemstack, player, world, pos, facing, hitX, hitY, hitZ, iblockstate1, block)) {
					iblockstate1 = world.getBlockState(pos);
					SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, world, pos, player);
					world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					//itemstack.shrink(1);
				}
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
		/*
		if (itemstack.getItem() instanceof ItemBlock && world.getBlockState(pos).getBlock().canPlaceBlockAt(world, pos)) {
			Block block = Block.getBlockFromItem(itemstack.getItem());
		
			IBlockState state = block.getStateFromMeta(itemstack.getItemDamage());
			block.onBlockAdded(world, pos, state);
			//block.onBlockPlacedBy(world, pos, state, player, itemstack);
			//world.setBlockState(pos, state, 3);
			player.addStat(StatList.getObjectUseStats(itemstack.getItem()));
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
		*/
	}

	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState, Block block) {
		if (!world.setBlockState(pos, newState, 11)) {
			return false;
		}

		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == block) {
			ItemBlock.setTileEntityNBT(world, player, pos, stack);
			block.onBlockPlacedBy(world, pos, state, player, stack);

			if (player instanceof EntityPlayerMP) {
				CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
			}
		}

		return true;
	}

	public EnumActionResult placeSlab(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, ItemStack itemstack, ItemSlab slab) {
		BlockSlab singleSlab = ReflectionHelper.getPrivateValue(ItemSlab.class, slab, "singleSlab");
		BlockSlab doubleSlab = ReflectionHelper.getPrivateValue(ItemSlab.class, slab, "doubleSlab");
		if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack)) {
			Comparable<?> comparable = singleSlab.getTypeForItem(itemstack);
			IBlockState iblockstate = worldIn.getBlockState(pos);//block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, itemstack.getItemDamage(), player);
			if (iblockstate.getBlock() == singleSlab) {
				IProperty<?> iproperty = singleSlab.getVariantProperty();
				Comparable<?> comparable1 = iblockstate.getValue(iproperty);
				BlockSlab.EnumBlockHalf blockslab$enumblockhalf = iblockstate.getValue(BlockSlab.HALF);
				if ((facing == EnumFacing.UP && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.BOTTOM || facing == EnumFacing.DOWN && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.TOP) && comparable1 == comparable) {
					IBlockState iblockstate1 = makeState(iproperty, comparable1, slab);
					AxisAlignedBB axisalignedbb = iblockstate1.getCollisionBoundingBox(worldIn, pos);
					if (axisalignedbb != Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb.offset(pos)) && worldIn.setBlockState(pos, iblockstate1, 11)) {
						SoundType soundtype = doubleSlab.getSoundType(iblockstate1, worldIn, pos, player);
						worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
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
		else {
			return EnumActionResult.FAIL;
		}
	}

	private boolean tryPlace(EntityPlayer player, ItemStack stack, World worldIn, BlockPos pos, Object itemSlabType, ItemSlab slab) {
		BlockSlab singleSlab = ReflectionHelper.getPrivateValue(ItemSlab.class, slab, "singleSlab");
		BlockSlab doubleSlab = ReflectionHelper.getPrivateValue(ItemSlab.class, slab, "doubleSlab");
		IBlockState iblockstate = worldIn.getBlockState(pos);
		if (iblockstate.getBlock() == singleSlab) {
			Comparable<?> comparable = slab.getBlock().getDefaultState().getValue(singleSlab.getVariantProperty());
			if (comparable == itemSlabType) {
				IBlockState iblockstate1 = makeState(singleSlab.getVariantProperty(), comparable, slab);
				AxisAlignedBB axisalignedbb = iblockstate1.getCollisionBoundingBox(worldIn, pos);
				if (axisalignedbb != Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb.offset(pos)) && worldIn.setBlockState(pos, iblockstate1, 11)) {
					SoundType soundtype = doubleSlab.getSoundType(iblockstate1, worldIn, pos, player);
					worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				}
				return true;
			}
		}
		return false;
	}

	protected <T extends Comparable<T>> IBlockState makeState(IProperty<T> p_185055_1_, Comparable<?> p_185055_2_, ItemSlab slab) {
		BlockSlab doubleSlab = ReflectionHelper.getPrivateValue(ItemSlab.class, slab, "doubleSlab");
		return doubleSlab.getDefaultState().withProperty(p_185055_1_, (T) p_185055_2_);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return ModGlobals.Rarities.getRarityFromMeta(stack.getItemDamage());
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !oldStack.isItemEqual(newStack) || slotChanged;
	}

}
