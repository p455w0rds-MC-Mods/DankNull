package p455w0rd.danknull.network;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;

/**
 * @author BrockWS
 */
public class PacketUpdateSlot implements IMessage {

    private int slot;
    private ItemStack stack;

    public PacketUpdateSlot() {}

    public PacketUpdateSlot(int slot, ItemStack stack) {
        this.slot = slot;
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.slot = buf.readInt();
        this.stack = ByteBufUtils.readItemStack(buf);
        this.stack.setCount(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.slot);
        ByteBufUtils.writeItemStack(buf, this.stack);
        buf.writeInt(this.stack.getCount());
    }

    public static class Handler implements IMessageHandler<PacketUpdateSlot, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateSlot message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                Minecraft.getMinecraft().player.openContainer.putStackInSlot(message.slot, message.stack);
            });
            return null;
        }
    }
}
