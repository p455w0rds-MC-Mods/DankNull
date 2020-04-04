package p455w0rd.danknull.integration.jei;

import mezz.jei.network.IPacketId;
import mezz.jei.network.PacketIdServer;
import mezz.jei.network.packets.PacketJei;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.Map;

/**
 * @author p455w0rd
 */
public class PacketVanllaRecipeTransfer extends PacketJei {

    public final Map<Integer, ItemStack> recipeMap;
    private final boolean maxTransfer;

    public PacketVanllaRecipeTransfer(final Map<Integer, ItemStack> recipeMap, final boolean maxTransfer) {
        this.recipeMap = recipeMap;
        this.maxTransfer = maxTransfer;
    }

    @Override
    public IPacketId getPacketId() {
        return PacketIdServer.RECIPE_TRANSFER;
    }

    @Override
    public void writePacketData(final PacketBuffer buf) {
        buf.writeVarInt(recipeMap.size());
        for (final Map.Entry<Integer, ItemStack> recipeMapEntry : recipeMap.entrySet()) {
            buf.writeVarInt(recipeMapEntry.getKey());
            ByteBufUtils.writeItemStack(buf, recipeMapEntry.getValue() == null ? ItemStack.EMPTY : recipeMapEntry.getValue());
        }
        buf.writeBoolean(maxTransfer);
    }
}