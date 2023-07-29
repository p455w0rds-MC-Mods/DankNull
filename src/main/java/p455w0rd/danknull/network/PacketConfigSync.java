package p455w0rd.danknull.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import p455w0rd.danknull.init.ModConfig;
import p455w0rd.danknull.init.ModConfig.Options;

import java.util.HashMap;
import java.util.Map;

/**
 * @author p455w0rd
 */
public class PacketConfigSync implements IMessage {

    public Map<String, Object> values = new HashMap<>();
    public PacketConfigSync() {}

    @Override
    public void fromBytes(final ByteBuf buf) {
        final PacketBuffer packetBuf = new PacketBuffer(buf);
        values.put(ModConfig.NAME_CREATIVE_BLACKLIST, packetBuf.readString(Short.MAX_VALUE));
        values.put(ModConfig.NAME_CREATIVE_WHITELIST, packetBuf.readString(Short.MAX_VALUE));
        values.put(ModConfig.NAME_OREDICT_BLACKLIST, packetBuf.readString(Short.MAX_VALUE));
        values.put(ModConfig.NAME_OREDICT_WHITELIST, packetBuf.readString(Short.MAX_VALUE));
        values.put(ModConfig.NAME_DISABLE_OREDICT, buf.readBoolean());
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        final PacketBuffer packetBuf = new PacketBuffer(buf);
        packetBuf.writeString(Options.creativeBlacklist);
        packetBuf.writeString(Options.creativeWhitelist);
        packetBuf.writeString(Options.oreBlacklist);
        packetBuf.writeString(Options.oreWhitelist);
        buf.writeBoolean(Options.disableOreDictMode);
    }

    public static class Handler implements IMessageHandler<PacketConfigSync, IMessage> {
        @Override
        public IMessage onMessage(final PacketConfigSync message, final MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(final PacketConfigSync message, final MessageContext ctx) {
            if (ctx.getClientHandler() != null) {
                Options.creativeBlacklist = (String) message.values.getOrDefault(ModConfig.NAME_CREATIVE_BLACKLIST, "");
                Options.creativeWhitelist = (String) message.values.getOrDefault(ModConfig.NAME_CREATIVE_WHITELIST, "");
                Options.oreBlacklist = (String) message.values.getOrDefault(ModConfig.NAME_OREDICT_BLACKLIST, "");
                Options.oreWhitelist = (String) message.values.getOrDefault(ModConfig.NAME_OREDICT_WHITELIST, "");
                Options.disableOreDictMode = (Boolean) message.values.getOrDefault(ModConfig.NAME_DISABLE_OREDICT, false);
            }
        }
    }
}
