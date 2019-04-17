package p455w0rd.danknull.blocks;

import static net.minecraft.util.EnumHand.MAIN_HAND;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.render.DankTextures;
import p455w0rd.danknull.client.render.TESRDankNullDock;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModNetworking;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.network.PacketSetDankNullInDock;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class BlockDankNullDock extends BlockContainerBase {

	public static final String NAME = "danknull_dock";

	public BlockDankNullDock() {
		super(Material.IRON, NAME, 10.0F, 6000000.0F);
		GameRegistry.registerTileEntity(TileDankNullDock.class, new ResourceLocation(ModGlobals.MODID, NAME));
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
		ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new TESRDankNullDock());
		setParticleTexture(this);
		ClientRegistry.bindTileEntitySpecialRenderer(TileDankNullDock.class, new TESRDankNullDock());
	}

	@SideOnly(Side.CLIENT)
	public static void setParticleTexture(final Block block) {
		final ModelResourceLocation modelLoc = new ModelResourceLocation(block.getRegistryName(), "particle");
		ModelRegistryHelper.register(modelLoc, new BuiltInModel(ItemCameraTransforms.DEFAULT, ItemOverrideList.NONE) {
			@Override
			public TextureAtlasSprite getParticleTexture() {
				return DankTextures.DANKNULL_DOCK_SPRITE;
			}
		});
		ModelLoader.setCustomStateMapper(block, blockIn -> Maps.toMap(blockIn.getBlockState().getValidStates(), input -> modelLoc));
	}

	private TileDankNullDock getTE(final IBlockAccess worldIn, final BlockPos pos) {
		if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileDankNullDock) {
			return (TileDankNullDock) worldIn.getTileEntity(pos);
		}
		return null;
	}

	private boolean isEmpty(final IBlockAccess world, final BlockPos pos) {
		return getTE(world, pos) != null && getTE(world, pos).getDankNull().isEmpty();
	}

	@Override
	public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
		if (world.isRemote) {
			return false;
		}
		if (player.getServer().isBlockProtected(world, pos, player)) {
			return false;
		}
		final TileDankNullDock dankDock = getTE(world, pos);
		if (dankDock != null) {
			final PlayerSlot slot = PlayerSlot.getHand(player, hand);
			final ItemStack stack = slot.getStackInSlot(player);

			if (DankNullUtils.isDankNull(stack)) {
				//dankDock.setDankNull(dankNull);
				dankDock.setInventory(DankNullUtils.getNewDankNullInventory(stack));
				//if (!player.capabilities.isCreativeMode || (player.capabilities.isCreativeMode && !player.isSneaking())) {
				player.setHeldItem(hand, ItemStack.EMPTY);
				//}
				//if (!world.isRemote) {
				ModNetworking.getInstance().sendToDimension(new PacketSetDankNullInDock(dankDock, stack), world.provider.getDimension());
				//}
				//dankDock.markDirty();
				return true;
			}
			else //if (player.getHeldItem(hand).isEmpty()) {
			if (!player.isSneaking() && hand == MAIN_HAND) {
				if (!dankDock.getDankNull().isEmpty()) {
					//ModGuiHandler.launchGui(GUIType.DANKNULL_TE, player, world, pos.getX(), pos.getY(), pos.getZ());
					//return true;
				}
				//}
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
			stack.setTagInfo("BlockEntityTag", nbttagcompound);
		}
		return stack;
	}

}