package p455w0rd.danknull.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * Created by brandon3055 on 7/06/2016.
 * Used to store a reference to a specific slot in a players inventory.
 * The slot field corresponds to the index of the item within the sub inventory for the given category.
 */
public class PlayerSlot {

    private final int slot;
    private final EnumInvCategory category;

    public PlayerSlot(final int slot, final EnumInvCategory category) {
        this.slot = slot;
        this.category = category;
    }

    public static PlayerSlot fromBuff(final ByteBuf buf) {
        final EnumInvCategory category = EnumInvCategory.fromIndex(buf.readByte());
        final int slot = buf.readByte();
        return new PlayerSlot(slot, category);
    }

    public static PlayerSlot getHand(final EntityPlayer player, final EnumHand hand) {
        if (hand == EnumHand.MAIN_HAND) {
            return new PlayerSlot(player.inventory.currentItem, EnumInvCategory.MAIN);
        } else {
            return new PlayerSlot(0, EnumInvCategory.OFF_HAND);
        }
    }

    public void toBuff(final ByteBuf buf) {
        buf.writeByte(category.getIndex());
        buf.writeByte(slot);
    }

    public int getSlotIndex() {
        return slot;
    }

    public int getCatIndex() {
        return category.getIndex();
    }

    @Override
    public String toString() {
        return category.getIndex() + ":" + slot;
    }

    public ItemStack getStackInSlot(final EntityPlayer player) {
        if (category == EnumInvCategory.ARMOR) {
            return player.inventory.armorInventory.get(slot);
        } else if (category == EnumInvCategory.MAIN) {
            return player.inventory.mainInventory.get(slot);
        } else if (category == EnumInvCategory.OFF_HAND) {
            return player.inventory.offHandInventory.get(slot);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public enum EnumInvCategory {
        MAIN(0),
        ARMOR(1),
        OFF_HAND(2);
        private static EnumInvCategory[] indexMap = new EnumInvCategory[3];

        static {
            indexMap[0] = MAIN;
            indexMap[1] = ARMOR;
            indexMap[2] = OFF_HAND;
        }

        private int index;

        EnumInvCategory(final int index) {
            this.index = index;
        }

        public static EnumInvCategory fromIndex(final int index) {
            if (index > 2 || index < 0) {
                return indexMap[0];
            }
            return indexMap[index];
        }

        public int getIndex() {
            return index;
        }
    }
}
