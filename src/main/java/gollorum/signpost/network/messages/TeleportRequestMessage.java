package gollorum.signpost.network.messages;

import gollorum.signpost.network.NetworkUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class TeleportRequestMessage extends Message<TeleportRequestMessage> {

	public int stackSize;
	public String waystoneName;
	
	public TeleportRequestMessage(){}
	
	public TeleportRequestMessage(int stackSize, String waystoneName) {
		this.stackSize = stackSize;
		this.waystoneName = waystoneName;
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeInt(stackSize);
		buf.writeString(waystoneName);
	}

	@Override
	public void decode(PacketBuffer buf) {
		stackSize = buf.readInt();
		waystoneName = buf.readString(NetworkUtil.MAX_STRING_LENGTH);
	}

}
