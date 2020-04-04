package p455w0rd.danknull.init;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import p455w0rd.danknull.blocks.BlockDankNullDock;
import p455w0rdslib.api.client.IModelHolder;

/**
 * @author p455w0rd
 */
public class ModBlocks {

    public static final BlockDankNullDock DANKNULL_DOCK = new BlockDankNullDock();
    private static final Block[] BLOCK_ARRAY = new Block[]{
            DANKNULL_DOCK
    };

    public static void registerModels() {
        for (final Block block : getBlocks()) {
            if (block instanceof IModelHolder) {
                ((IModelHolder) block).initModel();
            }
        }
    }

    public static Block[] getBlocks() {
        return BLOCK_ARRAY;
    }

    public static void register(final RegistryEvent.Register<Block> e) {
        e.getRegistry().registerAll(getBlocks());
    }

}
