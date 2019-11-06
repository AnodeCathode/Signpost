package gollorum.signpost.network.messages;

import gollorum.signpost.util.BaseInfo;
import net.minecraft.network.PacketBuffer;

public class BaseUpdateServerMessage extends Message<BaseUpdateServerMessage> {

	public BaseInfo wayStone;
	public boolean destroyed;

	public BaseUpdateServerMessage(){}
	
	public BaseUpdateServerMessage(BaseInfo wayStone, boolean destroyed){
		this.wayStone = wayStone;
		this.destroyed = destroyed;
	}
	
	@Override
	protected void encode(PacketBuffer buffer) {
		wayStone.encode(buffer);
		buffer.writeBoolean(destroyed);
		
	}

	@Override
	protected void decode(PacketBuffer buffer) {
		wayStone = BaseInfo.decode(buffer);
		destroyed = buffer.readBoolean();
	}

}
