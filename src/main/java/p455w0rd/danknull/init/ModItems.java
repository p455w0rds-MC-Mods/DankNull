package p455w0rd.danknull.init;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import p455w0rd.danknull.init.ModGlobals.DankNullTier;
import p455w0rd.danknull.items.ItemBlockDankNullDock;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.items.ItemDankNullPanel;
import p455w0rdslib.api.client.IModelHolder;
import p455w0rdslib.api.client.ItemRenderingRegistry;

/**
 * @author p455w0rd
 */
public class ModItems {

    public static final ItemDankNull REDSTONE_DANKNULL = new ItemDankNull(DankNullTier.REDSTONE);
    public static final ItemDankNull LAPIS_DANKNULL = new ItemDankNull(DankNullTier.LAPIS);
    public static final ItemDankNull IRON_DANKNULL = new ItemDankNull(DankNullTier.IRON);
    public static final ItemDankNull GOLD_DANKNULL = new ItemDankNull(DankNullTier.GOLD);
    public static final ItemDankNull DIAMOND_DANKNULL = new ItemDankNull(DankNullTier.DIAMOND);
    public static final ItemDankNull EMERALD_DANKNULL = new ItemDankNull(DankNullTier.EMERALD);
    public static final ItemDankNull CREATIVE_DANKNULL = new ItemDankNull(DankNullTier.CREATIVE);

    public static final ItemDankNullPanel REDSTONE_PANEL = new ItemDankNullPanel(DankNullTier.REDSTONE);
    public static final ItemDankNullPanel LAPIS_PANEL = new ItemDankNullPanel(DankNullTier.LAPIS);
    public static final ItemDankNullPanel IRON_PANEL = new ItemDankNullPanel(DankNullTier.IRON);
    public static final ItemDankNullPanel GOLD_PANEL = new ItemDankNullPanel(DankNullTier.GOLD);
    public static final ItemDankNullPanel DIAMOND_PANEL = new ItemDankNullPanel(DankNullTier.DIAMOND);
    public static final ItemDankNullPanel EMERALD_PANEL = new ItemDankNullPanel(DankNullTier.EMERALD);

    public static final ItemBlockDankNullDock DANK_NULL_DOCK_ITEM = new ItemBlockDankNullDock();

    private static final Item[] ITEM_ARRAY = new Item[]{
            //@formatter:off
            REDSTONE_DANKNULL, LAPIS_DANKNULL, IRON_DANKNULL, GOLD_DANKNULL, DIAMOND_DANKNULL, EMERALD_DANKNULL, CREATIVE_DANKNULL,
            REDSTONE_PANEL, LAPIS_PANEL, IRON_PANEL, GOLD_PANEL, DIAMOND_PANEL, EMERALD_PANEL,
            DANK_NULL_DOCK_ITEM
            //@formatter:on
    };

    public static Item[] getItems() {
        return ITEM_ARRAY;
    }

    public static void register(final RegistryEvent.Register<Item> e) {
        e.getRegistry().registerAll(getItems());
    }

    public static void registerCustomRenderedItems() {
        for (final Item item : ITEM_ARRAY) {
            if (item instanceof IModelHolder) {
                ItemRenderingRegistry.registerCustomRenderingItem((IModelHolder) item);
            }
        }
    }

}
