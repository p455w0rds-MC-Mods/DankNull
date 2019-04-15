package p455w0rd.danknull.inventory;

import codechicken.lib.util.ItemNBTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 */
public class InventoryDankNull implements IInventory {

    public static final String INVENTORY_NAME = "danknull-inventory";
    public static final String TAG_SLOT = "Slot";
    public static final String TAG_COUNT = "RealCount";

    private final NonNullList<ItemStack> itemStacks;
    private final int[] stackSizes;
    //	private ItemStack dankNull = ItemStack.EMPTY;
    private EntityPlayer player;
    private PlayerSlot dankNullSlot = null;
    private String itemUUID = "";
    private ItemStack unsafeStack = null;
    //private Map<ItemStack, SlotExtractionMode> extractionModes = Maps.<ItemStack, SlotExtractionMode>newHashMap();

    public InventoryDankNull(final PlayerSlot dankNullSlot, EntityPlayer player) {
        this.dankNullSlot = dankNullSlot;
        this.player = player;

        itemUUID = ItemNBTUtils.getString(getDankNull(), "UUID");
        itemStacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        stackSizes = new int[getSizeInventory()];

        loadInventory(getDNTag());
    }

    //This is for those few situations where you just dont have access to the player.
    public InventoryDankNull(ItemStack stack) {
        this.unsafeStack = stack;

        itemUUID = ItemNBTUtils.getString(getDankNull(), "UUID");
        itemStacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        stackSizes = new int[getSizeInventory()];

        loadInventory(getDNTag());
    }

    @Override
    public int getSizeInventory() {
        ItemStack dankNull = getDankNull();
        int numRows = dankNull.getItemDamage() + 1;
        if (DankNullUtils.isCreativeDankNull(dankNull)) {
            numRows--;
        }
        return numRows * 9;
    }

    @Override
    public ItemStack getStackInSlot(final int index) {
        if (DankNullUtils.isCreativeDankNull(getDankNull()) && index < getSizeInventory() && !itemStacks.get(index).isEmpty()) {
            final ItemStack tmp = itemStacks.get(index).copy();
            tmp.setCount(Integer.MAX_VALUE);
            return tmp;
        }
        return index < getSizeInventory() ? itemStacks.get(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(final int index, final int amount) {

        if (!getStackInSlot(index).isEmpty()) {
            if (DankNullUtils.isCreativeDankNull(getDankNull())) {
                final ItemStack tmp = getStackInSlot(index).copy();
                tmp.setCount(Integer.MAX_VALUE);
                return tmp;
            }
            if (getStackInSlot(index).getCount() <= amount) {
                final ItemStack itemstack = getStackInSlot(index);
                itemStacks.set(index, ItemStack.EMPTY);
                setSizeForSlot(index, 0);
                markDirty();
                return itemstack;
            }
            final ItemStack itemstack1 = getStackInSlot(index).splitStack(amount);
            setSizeForSlot(index, getSizeForSlot(index) - amount);
            if (getStackInSlot(index).getCount() == 0) {
                itemStacks.set(index, ItemStack.EMPTY);
            }
            markDirty();
            return itemstack1;
        }
        else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(final int index, final ItemStack itemStack) {
        setInventorySlotContents(index, itemStack, null);
    }

    public void setInventorySlotContents(final int index, final ItemStack itemStack, final TileDankNullDock te) {
        itemStacks.set(index, itemStack);
        markDirty();
    }

    @Override
    public boolean isUsableByPlayer(final EntityPlayer var1) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(final int i, final ItemStack stack) {
        return !(stack.getItem() instanceof ItemDankNull);
    }

    @Override
    public String getName() {
        return INVENTORY_NAME;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(getName());
    }

    @Override
    public ItemStack removeStackFromSlot(final int index) {
        final ItemStack stack = getStackInSlot(index);
        if (!stack.isEmpty()) {
            itemStacks.set(index, ItemStack.EMPTY);
        }
        markDirty();
        return stack;
    }

    @Override
    public int getInventoryStackLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void openInventory(final EntityPlayer player) {
    }

    @Override
    public void closeInventory(final EntityPlayer player) {
    }

    @Override
    public int getField(final int id) {
        return 0;
    }

    @Override
    public void setField(final int id, final int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
    }

    public NonNullList<ItemStack> getStacks() {
        return itemStacks;
    }

    public int getSizeForSlot(final int index) {
        return index >= 0 ? DankNullUtils.isCreativeDankNull(getDankNull()) ? Integer.MAX_VALUE : stackSizes[index] : 0;
    }

    public void setSizeForSlot(final int index, int size) {
        if (DankNullUtils.isCreativeDankNull(getDankNull())) {
            size = Integer.MAX_VALUE;
        }
        stackSizes[index] = size < 0 ? 0 : size;
    }

    public long getMaxStackSize() {
        return DankNullUtils.getDankNullMaxStackSize(getDankNull());
    }

    @Override
    public boolean isEmpty() {
        for (int x = 0; x < getSizeInventory(); x++) {
            if (!getStackInSlot(x).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public ItemStack getDankNull() {
        if (unsafeStack != null) {
            return unsafeStack;
        }

        return dankNullSlot.getStackInSlot(player);
    }

    public NBTTagCompound getDNTag() {
        ItemStack dn = getDankNull();
        if (!dn.hasTagCompound()) {
            dn.setTagCompound(new NBTTagCompound()); //This should not be needed but just in case...
        }
        return dn.getTagCompound();
    }

    @Override
    public void markDirty() {
        if (!isValid()) {
            return;
        }

        ItemStack stack = getDankNull();
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound()); //Really not needed but better safe than sorry
        }

        saveInventory(stack.getTagCompound());
        if (dankNullSlot != null){
            dankNullSlot.setStackInSlot(player, stack);
        }
    }

    public NBTTagCompound saveInventory(NBTTagCompound compound) {
        final NBTTagList nbtTL = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++) {
            if (!getStackInSlot(i).isEmpty()) {
                final NBTTagCompound nbtTC = new NBTTagCompound();
                nbtTC.setInteger(TAG_SLOT, i);
                nbtTC.setInteger(TAG_COUNT, getStackInSlot(i).getCount() <= DankNullUtils.getDankNullMaxStackSize(this) ? getStackInSlot(i).getCount() : DankNullUtils.getDankNullMaxStackSize(this));
                getStackInSlot(i).writeToNBT(nbtTC);
                nbtTL.appendTag(nbtTC);
            }
        }

        compound.setTag(getName(), nbtTL);
        return compound;
    }

    public void loadInventory(NBTTagCompound compound) {
        final NBTTagList nbtTL = compound.getTagList(getName(), Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < nbtTL.tagCount(); i++) {
            final NBTTagCompound nbtTC = nbtTL.getCompoundTagAt(i);
            final int slot = nbtTC.getInteger(TAG_SLOT);
            final ItemStack stack = new ItemStack(nbtTC);
            if (nbtTC.hasKey(TAG_COUNT)) {
                stack.setCount(nbtTC.getInteger(TAG_COUNT));
                setSizeForSlot(slot, nbtTC.getInteger(TAG_COUNT));
            }
            itemStacks.set(slot, stack);
        }
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public boolean isValid() {
        if (itemUUID.isEmpty()) {
            return false;
        }

        ItemStack stack = getDankNull();
        String itemUUID = ItemNBTUtils.getString(getDankNull(), "UUID");
        return stack.getItem() instanceof ItemDankNull && itemUUID.equals(this.itemUUID);
        //This instanceof probably isn't needed. I mean the chances of some random item somehow having a UUID field that matches yours...
    }
}