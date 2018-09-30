package p455w0rd.danknull.network;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Shadows_of_Fire
 *
 */
public class VanillaPacketDispatcher {

	public static void dispatchTEToNearbyPlayers(TileEntity tile) {
		World world = tile.getWorld();
		if (world != null) {
			List<EntityPlayer> players = world.playerEntities;
			if (world.playerEntities.size() > 0) {
				for (Object player : players) {
					if (player instanceof EntityPlayerMP) {
						EntityPlayerMP mp = (EntityPlayerMP) player;
						if (pointDistancePlane(mp.posX, mp.posZ, tile.getPos().getX() + 0.5, tile.getPos().getZ() + 0.5) < 64) {
							((EntityPlayerMP) player).connection.sendPacket(tile.getUpdatePacket());
						}
					}
				}
			}
		}
	}

	public static void dispatchTEToNearbyPlayers(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null) {
			dispatchTEToNearbyPlayers(tile);
		}
	}

	public static float pointDistancePlane(double x1, double y1, double x2, double y2) {
		return (float) Math.hypot(x1 - x2, y1 - y2);
	}

}
