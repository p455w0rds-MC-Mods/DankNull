package p455w0rd.danknull.util;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

/**
 * @author p455w0rd
 *
 */
public class TargetPos extends TargetPoint {

	/**
	 * @param dimension
	 * @param pos
	 * @param range
	 */
	public TargetPos(int dimension, BlockPos pos, double range) {
		super(dimension, pos.getX(), pos.getY(), pos.getZ(), range);
	}

}
