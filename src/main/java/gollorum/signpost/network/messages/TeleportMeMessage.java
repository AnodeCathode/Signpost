package gollorum.signpost.network.messages;

import gollorum.signpost.util.BaseInfo;
import net.minecraft.network.PacketBuffer;

public class TeleportMeMessage extends Message<TeleportMeMessage> {

	public BaseInfo base;
	
	public TeleportMeMessage(){}
	
	public TeleportMeMessage(BaseInfo base) {
		this.base = base;
	}

	@Override
	public void decode(PacketBuffer buf) {
		base = BaseInfo.decode(buf);
	}

	@Override
	public void encode(PacketBuffer buf) {
		base.encode(buf);
	}
	
}
