package p455w0rd.danknull.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.gui.GuiDankNull;
import p455w0rd.danknull.container.ContainerDankNullDock;
import p455w0rd.danknull.container.ContainerDankNullItem;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.items.ItemDankNull;

import static p455w0rd.danknull.inventory.PlayerSlot.EnumInvCategory.MAIN;
import static p455w0rd.danknull.inventory.PlayerSlot.EnumInvCategory.OFF_HAND;

/**
 * @author p455w0rd
 */
public class ModGuiHandler implements IGuiHandler {

    public static void init() {
        DankNull.LOGGER.info("Registering GUI Handler");
        NetworkRegistry.INSTANCE.registerGuiHandler(ModGlobals.MODID, new ModGuiHandler());
    }

    public static void launchGui(final GUIType type, final EntityPlayer player, final World world, final BlockPos pos) {
        if (!world.isRemote) {
            player.openGui(DankNull.INSTANCE, type.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    private static PlayerSlot getDankNullSlot(final EntityPlayer player) {
        final InventoryPlayer playerInv = player.inventory;
        final ItemStack mainHand = player.getHeldItemMainhand();
        final ItemStack offHand = player.getHeldItemOffhand();

        if (mainHand.getItem() instanceof ItemDankNull) {
            return new PlayerSlot(playerInv.currentItem, MAIN);
        } else if (offHand.getItem() instanceof ItemDankNull) {
            return new PlayerSlot(0, OFF_HAND);
        }

        for (int i = 0; i < playerInv.mainInventory.size(); i++) {
            final ItemStack stack = playerInv.mainInventory.get(i);
            if (stack.getItem() instanceof ItemDankNull) {
                return new PlayerSlot(i, MAIN);
            }
        }
        return null;
    }

    @Override
    public Object getServerGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        switch (GUIType.VALUES[id]) {
            case DANKNULL:
                final PlayerSlot dankNull = getDankNullSlot(player);
                if (dankNull == null) {
                    return null;
                }
                return new ContainerDankNullItem(player, dankNull);
            case DANKNULL_TE:
                final TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
                if (te instanceof TileDankNullDock) {
                    final TileDankNullDock dankDock = (TileDankNullDock) te;
                    if (!dankDock.getDankNull().isEmpty()) {
                        return new ContainerDankNullDock(player, dankDock);
                    }
                }
            default:
                break;
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        switch (GUIType.VALUES[id]) {
            case DANKNULL_TE:
                final TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
                if (te instanceof TileDankNullDock) {
                    final TileDankNullDock dankDock = (TileDankNullDock) te;
                    if (!dankDock.getDankNull().isEmpty()) {
                        return new GuiDankNull(new ContainerDankNullDock(player, dankDock));
                    }
                }
            case DANKNULL:
                final PlayerSlot dankNull = getDankNullSlot(player);
                if (dankNull == null) {
                    return null;
                }
                return new GuiDankNull(new ContainerDankNullItem(player, dankNull));
            default:
                break;
        }
        return null;
    }

    public enum GUIType {

        DANKNULL, DANKNULL_TE;

        public static final GUIType[] VALUES = values();

    }

}