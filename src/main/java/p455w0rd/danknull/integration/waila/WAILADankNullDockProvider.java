package p455w0rd.danknull.integration.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.integration.WAILA;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class WAILADankNullDockProvider implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		ItemStack stack = new ItemStack(ModBlocks.DANKNULL_DOCK);
		TileEntity tile = accessor.getTileEntity();
		if (tile != null && tile instanceof TileDankNullDock) {
			TileDankNullDock te = (TileDankNullDock) tile;
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			te.writeToNBT(nbttagcompound);
			stack.setTagInfo("BlockEntityTag", nbttagcompound);
		}
		return stack;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		EntityPlayer player = accessor.getPlayer();
		TileDankNullDock dankDock = (TileDankNullDock) accessor.getTileEntity();
		currenttip.add(WAILA.toolTipEnclose);
		String msg = dankDock.getStack().isEmpty() ? "Right-click with /dank/null" : "Right-click with empty hand to open GUI";
		currenttip.add(msg);
		InventoryDankNull dankDockInventory = dankDock.getInventory();
		if (dankDockInventory != null) {
			ItemStack dockedDankNull = dankDockInventory.getDankNull();
			if (!dockedDankNull.isEmpty()) {
				currenttip.add(" ");
				currenttip.add(ModGlobals.Rarities.getRarityFromMeta(dockedDankNull.getItemDamage()).rarityColor + "" + dockedDankNull.getDisplayName() + "" + TextFormatting.GRAY + " Docked");
				ItemStack selectedStack = DankNullUtils.getSelectedStack(dankDock.getInventory());
				if (!selectedStack.isEmpty()) {
					currenttip.add(selectedStack.getDisplayName() + " Selected");
				}
			}
		}

		currenttip.add(WAILA.toolTipEnclose);
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		return null;
	}
}