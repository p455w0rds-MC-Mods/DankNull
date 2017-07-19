package p455w0rd.danknull.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.base.Throwables;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author p455w0rd
 *
 */
public class PacketConfigSync implements IMessage {

	public Map<String, Object> values;

	public PacketConfigSync() {
	}

	public PacketConfigSync(Map<String, Object> valuesIn) {
		values = valuesIn;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fromBytes(ByteBuf buf) {
		short len = buf.readShort();
		byte[] compressedBody = new byte[len];

		for (short i = 0; i < len; i++) {
			compressedBody[i] = buf.readByte();
		}

		try {
			ObjectInputStream obj = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(compressedBody)));
			values = (Map<String, Object>) obj.readObject();
			obj.close();
		}
		catch (Exception e) {
			Throwables.propagate(e);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteArrayOutputStream obj = new ByteArrayOutputStream();

		try {
			GZIPOutputStream gzip = new GZIPOutputStream(obj);
			ObjectOutputStream objStream = new ObjectOutputStream(gzip);
			objStream.writeObject(values);
			objStream.close();
		}
		catch (IOException e) {
			Throwables.propagate(e);
		}
		buf.writeShort(obj.size());
		buf.writeBytes(obj.toByteArray());
	}

	public static class Handler implements IMessageHandler<PacketConfigSync, IMessage> {
		@Override
		public IMessage onMessage(final PacketConfigSync message, final MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(new Runnable() {
				@Override
				public void run() {
					handle(message, ctx);
				}
			});
			return null;
		}

		private void handle(PacketConfigSync message, MessageContext ctx) {
			if (ctx.getClientHandler() != null) {
				//Options.TEMP_REGULATOR_RADIUS = (Integer) message.values.get("TempRegulatorBlockRadius");
			}
		}
	}
}
