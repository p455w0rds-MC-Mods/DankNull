package p455w0rd.danknull.init;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.blocks.BlockDankNullDock;

/**
 * @author p455w0rd
 *
 */
public class ModBlocks {

	private static final NonNullList<Block> BLOCK_LIST = NonNullList.<Block>create();

	public static BlockDankNullDock DANKNULL_DOCK;

	public static void init() {
		BLOCK_LIST.addAll(Arrays.asList(DANKNULL_DOCK = new BlockDankNullDock()));
		for (Block block : BLOCK_LIST) {
			ItemBlock itemblock = new ItemBlock(block);
			itemblock.setRegistryName(block.getRegistryName());
		}
	}

	@SideOnly(Side.CLIENT)
	public static void preInitModels() {
		DANKNULL_DOCK.initModel();
	}

	public static NonNullList<Block> getList() {
		return BLOCK_LIST;
	}

}
