package gollorum.signpost.network.messages;

import gollorum.signpost.network.NetworkUtil;
import net.minecraft.network.PacketBuffer;

public class SendDiscoveredToServerMessage extends Message<SendDiscoveredToServerMessage> {

	public String waystone;
	
	public SendDiscoveredToServerMessage(){}
	
	public SendDiscoveredToServerMessage(String waystone){
		this.waystone = waystone;
	}
	
	@Override
	public void decode(PacketBuffer buf) {
		waystone = buf.readString(NetworkUtil.MAX_STRING_LENGTH);
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeString(waystone);
	}

}
