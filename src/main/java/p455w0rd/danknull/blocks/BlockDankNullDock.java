package p455w0rd.danknull.blocks;

import javax.annotation.Nullable;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock.ExtractionMode;
import p455w0rd.danknull.client.render.DankTextures;
import p455w0rd.danknull.client.render.TESRDankNullDock;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class BlockDankNullDock extends BlockContainerBase {

	public static final String NAME = "danknull_dock";

	public BlockDankNullDock() {
		super(Material.IRON, NAME, 10.0F, 6000000.0F);
		GameRegistry.registerTileEntity(TileDankNullDock.class, ModGlobals.MODID + ":" + NAME);
		setLightOpacity(255);
		useNeighborBrightness = true;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return isEmpty(worldIn, pos) ? new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 3 * 0.0625D, 1.0D) : new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 12 * 0.0625D, 1.0D);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return isEmpty(source, pos) ? new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 3 * 0.0625D, 1.0D) : new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 12 * 0.0625D, 1.0D);
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDankNullDock();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new TESRDankNullDock());
		ModelRegistryHelper.setParticleTexture(this, DankTextures.DOCK_SPRITE);
		ClientRegistry.bindTileEntitySpecialRenderer(TileDankNullDock.class, new TESRDankNullDock());
	}

	private TileDankNullDock getTE(IBlockAccess worldIn, BlockPos pos) {
		if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileDankNullDock) {
			return (TileDankNullDock) worldIn.getTileEntity(pos);
		}
		return null;
	}

	private boolean isEmpty(IBlockAccess world, BlockPos pos) {
		return getTE(world, pos) != null && getTE(world, pos).getStack().isEmpty();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (getTE(world, pos) != null) {
			if (!player.getHeldItem(hand).isEmpty() && getTE(world, pos).getStack().isEmpty() && player.getHeldItem(hand).getItem() == ModItems.DANK_NULL) {
				getTE(world, pos).setStack(player.getHeldItem(hand));
				getTE(world, pos).setSelectedStack(DankNullUtils.getSelectedStack(DankNullUtils.getInventoryFromHeld(player)));
				player.setHeldItem(hand, ItemStack.EMPTY);
				return true;
			}
			else if (player.getHeldItem(hand).isEmpty()) {
				if (!player.isSneaking()) {
					if (getTE(world, pos).getExtractionMode() == ExtractionMode.NORMAL) {
						getTE(world, pos).setExtractionMode(ExtractionMode.SELECTED);
						if (world.isRemote) {
							player.sendMessage(new TextComponentString("Extraction mode: Only Selected Item Extracted"));
						}
					}
					else {
						getTE(world, pos).setExtractionMode(ExtractionMode.NORMAL);
						if (world.isRemote) {
							player.sendMessage(new TextComponentString("Extraction mode: All Items Extracted"));
						}
					}
				}
				else {
					if (!getTE(world, pos).getStack().isEmpty()) {
						player.setHeldItem(hand, getTE(world, pos).getStack());
						getTE(world, pos).setStack(ItemStack.EMPTY);
						getTE(world, pos).setSelectedStack(ItemStack.EMPTY);
						getTE(world, pos).resetInventory();
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.025F);
		ItemStack itemstack = new ItemStack(this);
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		te.writeToNBT(nbttagcompound);
		itemstack.setTagInfo("BlockEntityTag", nbttagcompound);
		spawnAsEntity(worldIn, pos, itemstack);
	}

}