package gollorum.signpost.network.messages;

import java.util.Collection;
import java.util.HashSet;

import gollorum.signpost.network.NetworkUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class SendAllWaystoneNamesMessage extends Message<SendAllWaystoneNamesMessage> {

	public Collection<String> waystones;

	public SendAllWaystoneNamesMessage(Collection<String> waystones) {
		this.waystones = waystones;
	}

	public SendAllWaystoneNamesMessage() {
	}

	@Override
	public void decode(PacketBuffer buf) {
		int count = buf.readInt();
		waystones = new HashSet<String>(count);
		for (int i = 0; i < count; i++) {
			String name = buf.readString(NetworkUtil.MAX_STRING_LENGTH);
			waystones.add(name);
		}
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeInt(waystones.size());
		for (String name : waystones) {
			buf.writeString(name);
		}
	}

}
