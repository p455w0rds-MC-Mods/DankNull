package p455w0rd.danknull.items;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import p455w0rd.danknull.api.IModelHolder;
import p455w0rd.danknull.client.render.DankNullRenderer;
import p455w0rd.danknull.entity.EntityPFakePlayer;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModGuiHandler;
import p455w0rd.danknull.init.ModGuiHandler.GUIType;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class ItemDankNull extends Item implements IModelHolder {

	public static String INV_NAME = "danknull-inventory";
	InventoryDankNull inventory = null;

	public ItemDankNull() {
		setRegistryName("dank_null");
		setUnlocalizedName("dank_null");
		//GameRegistry.register(this);
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
		ModelResourceLocation loc = new ModelResourceLocation(getRegistryName(), "inventory");
		for (int i = 0; i < 6; i++) {
			ModelLoader.setCustomModelResourceLocation(this, i, loc);
		}
		ModelRegistryHelper.register(loc, DankNullRenderer.getInstance());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		return I18n.translateToLocal(getUnlocalizedNameInefficiently(stack) + "_" + getDamage(stack) + ".name").trim();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (playerIn.isSneaking() && hand == EnumHand.MAIN_HAND) {
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

				EntityPFakePlayer fakePlayer = EntityPFakePlayer.getFakePlayerForParent(playerIn);
				if (fakePlayer.interactionManager.getGameType() != GameType.NOT_SET) {
					fakePlayer.interactionManager.setGameType(GameType.NOT_SET);
				}
				fakePlayer.setHeldItem(EnumHand.MAIN_HAND, selectedStack);
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

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos posIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return EnumActionResult.SUCCESS;
		}
		if (player.isSneaking()) {
			ModGuiHandler.launchGui(GUIType.DANKNULL, player, worldIn, (int) player.posX, (int) player.posY, (int) player.posZ);
			return EnumActionResult.SUCCESS;
		}
		ItemStack stack = player.getHeldItem(hand);
		InventoryDankNull inventory = new InventoryDankNull(stack);
		ItemStack selectedStack = DankNullUtils.getSelectedStack(inventory);
		IBlockState state = worldIn.getBlockState(posIn);
		Block block = state.getBlock();
		BlockPos pos = posIn;

		if (selectedStack.isEmpty() || (!(selectedStack.getItem() instanceof ItemBlock) && !(selectedStack.getItem() instanceof ItemBlockSpecial))) { //TODO I do have an idea
			//return EnumActionResult.PASS;
		}

		if ((block.isReplaceable(worldIn, posIn)) && (block == Blocks.SNOW_LAYER)) {
			facing = EnumFacing.UP;
		}
		else if (block.isReplaceable(worldIn, posIn)) {
			pos = posIn.offset(facing);
		}
		if ((DankNullUtils.getSelectedStackSize(inventory) > 0) && (player.canPlayerEdit(posIn, facing, stack))) {
			pos = posIn.offset(facing);
			//EnumActionResult result = selectedStack.copy().onItemUse(player, worldIn, pos.up(), hand, facing, hitX, hitY, hitZ);
			Block selectedBlock = Block.getBlockFromItem(selectedStack.getItem());
			int meta = selectedStack.getMetadata();
			if (selectedBlock instanceof BlockStairs || selectedBlock instanceof BlockBanner) {
				IBlockState newState = selectedBlock.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, player);
				EnumActionResult result = DankNullUtils.placeBlock(newState, worldIn, pos);
				//EnumActionResult result = EnumActionResult.SUCCESS;
				//selectedBlock.onBlockPlacedBy(worldIn, pos, newState, player, selectedStack);
				if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntityBanner) {
					if (facing == EnumFacing.UP) {
						int i = MathHelper.floor((player.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5D) & 15;
						worldIn.setBlockState(pos, Blocks.STANDING_BANNER.getDefaultState().withProperty(BlockStandingSign.ROTATION, Integer.valueOf(i)), 3);
					}
					else {
						worldIn.setBlockState(pos, Blocks.WALL_BANNER.getDefaultState().withProperty(BlockWallSign.FACING, facing), 3);
					}
					((TileEntityBanner) worldIn.getTileEntity(pos)).setItemValues(selectedStack, false);
				}
				if (result != EnumActionResult.FAIL) {
					SoundType soundType = block.getSoundType(newState, worldIn, pos, player);
					worldIn.playSound((EntityPlayer) null, player.getPosition(), soundType.getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 0.5F * ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7F + 2F));
				}
				if (!player.capabilities.isCreativeMode) {
					DankNullUtils.decrSelectedStackSize(inventory, 1);
				}
				if (FMLCommonHandler.instance().getSide().isServer()) {
					//for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
					//ModNetworking.INSTANCE.sendTo(new PacketSyncDankNull(DankNullUtils.getInventory(stack).serializeNBT()), player);
					//}
				}
				return result;
			}
			else if (selectedStack.getItem() instanceof ItemBucket) {
				//TODO soon!
			}
			else if ((selectedStack.getItem() instanceof ItemSnowball) || (selectedStack.getItem() instanceof ItemEnderPearl) || (selectedStack.getItem() instanceof ItemEgg)) {
				//TODO soon!
			}
			else {
				EntityPFakePlayer fakePlayer = EntityPFakePlayer.getFakePlayerForParent(player);
				if (fakePlayer.interactionManager.getGameType() != GameType.NOT_SET) {
					fakePlayer.interactionManager.setGameType(GameType.NOT_SET);
				}
				fakePlayer.setHeldItem(EnumHand.MAIN_HAND, selectedStack.copy());
				fakePlayer.setItemInHand(selectedStack.copy());
				fakePlayer.posX = player.posX;
				fakePlayer.posY = player.posY;
				fakePlayer.posZ = player.posZ;
				fakePlayer.setRotationYawHead(player.rotationYawHead);
				fakePlayer.rotationYaw = player.rotationYaw;
				fakePlayer.rotationPitch = player.rotationPitch;
				fakePlayer.eyeHeight = player.eyeHeight;
				EnumActionResult result = selectedStack.copy().onItemUse(fakePlayer, worldIn, posIn, hand, facing, hitX, hitY, hitZ);
				if (result == EnumActionResult.PASS) {
					selectedStack.copy().getItem().onItemRightClick(worldIn, fakePlayer, hand).getType();
				}
				else if (result == EnumActionResult.SUCCESS && !player.capabilities.isCreativeMode) {
					DankNullUtils.decrSelectedStackSize(inventory, 1);
				}
				return result;
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return ModGlobals.Rarities.getRarityFromMeta(stack.getItemDamage());
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

}
