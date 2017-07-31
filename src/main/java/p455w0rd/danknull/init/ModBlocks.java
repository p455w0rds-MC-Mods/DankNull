package p455w0rd.danknull.init;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.NonNullList;
import p455w0rd.danknull.api.IModelHolder;
import p455w0rd.danknull.blocks.BlockDankNullDock;

/**
 * @author p455w0rd
 *
 */
public class ModBlocks {

	private static final NonNullList<Block> BLOCK_LIST = NonNullList.<Block>create();

	public static final BlockDankNullDock DANKNULL_DOCK = new BlockDankNullDock();

	public static void init() {
		BLOCK_LIST.addAll(Arrays.asList(DANKNULL_DOCK));
		ModItems.getList().add(new ItemBlock(DANKNULL_DOCK).setRegistryName(DANKNULL_DOCK.getRegistryName()));
	}

	public static void preInitModels() {
		for (Block block : BLOCK_LIST) {
			if (block instanceof IModelHolder) {
				((IModelHolder) block).initModel();
			}
		}
	}

	public static NonNullList<Block> getList() {
		return BLOCK_LIST;
	}

}
