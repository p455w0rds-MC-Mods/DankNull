package p455w0rd.danknull.integration.jei;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mezz.jei.network.IPacketId;
import mezz.jei.network.PacketIdServer;
import mezz.jei.network.packets.PacketJei;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import p455w0rd.danknull.integration.JEI.VanillaRecipeTransferHandlerServer;

/**
 * @author p455w0rd
 *
 */
public class PacketVanllaRecipeTransfer extends PacketJei {

	public final Map<Integer, ItemStack> recipeMap;
	/*
	public final List<Integer> craftingSlots;
	public final List<Integer> inventorySlots;
	*/
	private final boolean maxTransfer;

	/*
	public PacketVanllaRecipeTransfer(Map<Integer, ItemStack> recipeMap, List<Integer> craftingSlots, List<Integer> inventorySlots, boolean maxTransfer) {
		this.recipeMap = recipeMap;
		this.craftingSlots = craftingSlots;
		this.inventorySlots = inventorySlots;
		this.maxTransfer = maxTransfer;
	}
	*/
	public PacketVanllaRecipeTransfer(Map<Integer, ItemStack> recipeMap, boolean maxTransfer) {
		this.recipeMap = recipeMap;
		this.maxTransfer = maxTransfer;
	}

	@Override
	public IPacketId getPacketId() {
		return PacketIdServer.RECIPE_TRANSFER;
	}

	@Override
	public void writePacketData(PacketBuffer buf) {
		buf.writeVarInt(recipeMap.size());
		for (Map.Entry<Integer, ItemStack> recipeMapEntry : recipeMap.entrySet()) {
			buf.writeVarInt(recipeMapEntry.getKey());
			ByteBufUtils.writeItemStack(buf, recipeMapEntry.getValue() == null ? ItemStack.EMPTY : recipeMapEntry.getValue());
		}
		/*
				buf.writeVarInt(craftingSlots.size());
				for (Integer craftingSlot : craftingSlots) {
					buf.writeVarInt(craftingSlot);
				}
		
				buf.writeVarInt(inventorySlots.size());
				for (Integer inventorySlot : inventorySlots) {
					buf.writeVarInt(inventorySlot);
				}
		*/
		buf.writeBoolean(maxTransfer);
	}

	public static void readPacketData(PacketBuffer buf, EntityPlayer player) throws IOException {
		int recipeMapSize = buf.readVarInt();
		Map<Integer, ItemStack> recipeMap = new HashMap<>(recipeMapSize);
		for (int i = 0; i < recipeMapSize; i++) {
			int slotIndex = buf.readVarInt();
			ItemStack recipeItem = ByteBufUtils.readItemStack(buf);
			recipeMap.put(slotIndex, recipeItem);
		}
		/*
				int craftingSlotsSize = buf.readVarInt();
				List<Integer> craftingSlots = new ArrayList<>(craftingSlotsSize);
				for (int i = 0; i < craftingSlotsSize; i++) {
					int slotIndex = buf.readVarInt();
					craftingSlots.add(slotIndex);
				}
		
				int inventorySlotsSize = buf.readVarInt();
				List<Integer> inventorySlots = new ArrayList<>(inventorySlotsSize);
				for (int i = 0; i < inventorySlotsSize; i++) {
					int slotIndex = buf.readVarInt();
					inventorySlots.add(slotIndex);
				}
				*/
		boolean maxTransfer = buf.readBoolean();
		/*
				VanillaRecipeTransferHandlerServer.setItems(player, recipeMap, craftingSlots, inventorySlots, maxTransfer);
				*/
		VanillaRecipeTransferHandlerServer.setItems(player, recipeMap, maxTransfer);
	}

}