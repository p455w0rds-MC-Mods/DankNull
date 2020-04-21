package p455w0rd.danknull.inventory.cap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.inventory.DankNullHandler;

/**
 * @author BrockWS
 */
public class DankNullCapabilityProvider implements ICapabilityProvider {

    public static final String DANK_NULL_CAP_TAG = "DankNullCap";

    private final ItemStack stack;
    private final IDankNullHandler dankNullHandler;
    private boolean needsInitialNBT = false; // When a stack is copied the NBT isn't applied until after capabilities are initialized

    public DankNullCapabilityProvider(final ModGlobals.DankNullTier tier, final ItemStack stack) {
        this.stack = stack;
        dankNullHandler = new DankNullHandler(tier) {
            @Override
            protected void onDataChanged() {
                super.onDataChanged();

                if(!stack.hasTagCompound()) {
                    stack.setTagCompound(new NBTTagCompound());
                }

                stack.getTagCompound().setTag(DANK_NULL_CAP_TAG, CapabilityDankNull.DANK_NULL_CAPABILITY.writeNBT(this, null));
            }
        };
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(DANK_NULL_CAP_TAG)) {
            CapabilityDankNull.DANK_NULL_CAPABILITY.readNBT(dankNullHandler, null, stack.getTagCompound().getCompoundTag(DANK_NULL_CAP_TAG));
        } else {
            needsInitialNBT = true;
        }
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
        if (needsInitialNBT && stack.hasTagCompound() && stack.getTagCompound().hasKey(DANK_NULL_CAP_TAG)) {
            needsInitialNBT = false;
            CapabilityDankNull.DANK_NULL_CAPABILITY.readNBT(dankNullHandler, null, stack.getTagCompound().getCompoundTag(DANK_NULL_CAP_TAG));
        }
        return capability == CapabilityDankNull.DANK_NULL_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
        if (hasCapability(capability, facing)) {
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(dankNullHandler);
            } else if (capability == CapabilityDankNull.DANK_NULL_CAPABILITY) {
                return CapabilityDankNull.DANK_NULL_CAPABILITY.cast(dankNullHandler);
            }
        }
        return null;
    }

}
