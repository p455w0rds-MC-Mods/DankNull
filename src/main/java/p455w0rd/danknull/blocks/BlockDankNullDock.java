package p455w0rd.danknull.blocks;

import static net.minecraft.util.EnumHand.MAIN_HAND;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.render.TESRDankNullDock;
import p455w0rd.danknull.init.*;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rd.danknull.init.ModGuiHandler.GUIType;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.network.PacketSetDankNullInDock;
import p455w0rdslib.api.client.IModelHolder;

/**
 * @author p455w0rd
 *
 */
public class BlockDankNullDock extends BlockContainer implements IModelHolder {

	public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(ModGlobals.MODID, "danknull_dock");

	public BlockDankNullDock() {
		super(Material.IRON);
		setUnlocalizedName(REGISTRY_NAME.getResourcePath());
		setRegistryName(REGISTRY_NAME);
		setResistance(6000000.0F);
		setHardness(10.0F);
		GameRegistry.registerTileEntity(TileDankNullDock.class, REGISTRY_NAME);
		setLightOpacity(255);
		useNeighborBrightness = true;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(final IBlockState blockState, final IBlockAccess worldIn, final BlockPos pos) {
		return isEmpty(worldIn, pos) ? new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 3 * 0.0625D, 1.0D) : new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 12 * 0.0625D, 1.0D);
	}

	@Override
	public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
		return isEmpty(source, pos) ? new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 3 * 0.0625D, 1.0D) : new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 12 * 0.0625D, 1.0D);
	}

	@Override
	public boolean isPassable(final IBlockAccess worldIn, final BlockPos pos) {
		return true;
	}

	@Override
	public boolean doesSideBlockRendering(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing face) {
		return false;
	}

	@Override
	public boolean isFullCube(final IBlockState state) {
		return false;
	}

	@Override
	public boolean canConnectRedstone(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing side) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(final World worldIn, final int meta) {
		return new TileDankNullDock();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileDankNullDock.class, new TESRDankNullDock());
	}

	private TileDankNullDock getTE(final IBlockAccess worldIn, final BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileDankNullDock) {
			return (TileDankNullDock) te;
		}
		return null;
	}

	private boolean isEmpty(final IBlockAccess world, final BlockPos pos) {
        TileDankNullDock te = getTE(world, pos);
		return te != null && te.getDankNull().isEmpty();
	}

	@Override
	public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
		if (world.isRemote) {
			return true;
		}
		if (player.getServer().isBlockProtected(world, pos, player)) {
			return false;
		}
		final TileDankNullDock dankDock = getTE(world, pos);
		if (dankDock != null) {
			final PlayerSlot slot = PlayerSlot.getHand(player, hand);
			final ItemStack stack = slot.getStackInSlot(player);
			if (dankDock.getDankNull().isEmpty()) {
				if (ItemDankNull.isDankNull(stack)) {
					dankDock.setDankNull(stack);
					player.setHeldItem(hand, ItemStack.EMPTY);
					ModNetworking.getInstance().sendToAllTracking(new PacketSetDankNullInDock(dankDock, stack), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
					return true;
				}
			}
			if (!player.isSneaking() && hand == MAIN_HAND) {
				if (!dankDock.getDankNull().isEmpty()) {
					ModGuiHandler.launchGui(GUIType.DANKNULL_TE, player, world, pos, null);
					return true;
				}
			}

		}
		return false;
	}

	@Override
	public ItemStack getPickBlock(final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player) {
		return getItemBlockWithNBT(world.getTileEntity(pos));
	}

	@Override
	public void harvestBlock(final World worldIn, final EntityPlayer player, final BlockPos pos, final IBlockState state, @Nullable final TileEntity te, @Nullable ItemStack stack) {
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.025F);
		stack = getItemBlockWithNBT(te);
		spawnAsEntity(worldIn, pos, stack);
	}

	private ItemStack getItemBlockWithNBT(@Nullable final TileEntity te) {
		final ItemStack stack = new ItemStack(this);
		final NBTTagCompound nbttagcompound = new NBTTagCompound();
		if (te != null) {
			te.writeToNBT(nbttagcompound);
			stack.setTagInfo(NBT.BLOCKENTITYTAG, nbttagcompound);
		}
		return stack;
	}

	@Override
	public EnumBlockRenderType getRenderType(final IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(final IBlockState blockState, final IBlockAccess blockAccess, final BlockPos pos, final EnumFacing side) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSideSolid(final IBlockState base_state, final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockNormalCube(final IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isOpaqueCube(final IBlockState blockState) {
		return false;
	}

	public static ItemStack getDockedDankNull(final TileEntity dankDock) {
		if (isDankNullDock(dankDock)) {
			return ((TileDankNullDock) dankDock).getDankNull();
		}
		return ItemStack.EMPTY;
	}

	public static boolean isDankNullDock(final TileEntity tile) {
		return tile.hasCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
	}

}