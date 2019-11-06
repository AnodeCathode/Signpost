package gollorum.signpost.network.messages;

import gollorum.signpost.management.PostHandler;
import gollorum.signpost.util.BaseInfo;
import gollorum.signpost.util.StonedHashSet;
import net.minecraft.network.PacketBuffer;

public class BaseUpdateClientMessage extends Message<BaseUpdateClientMessage> {

	public StonedHashSet waystones = new StonedHashSet();
	
	public BaseUpdateClientMessage(){
		waystones = PostHandler.getNativeWaystones();
	}
	
	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(waystones.size());
		for(BaseInfo now: waystones){
			now.encode(buffer);
		}
	}

	@Override
	public void decode(PacketBuffer buffer) {
		waystones = new StonedHashSet();
		int c = buffer.readInt();
		for(int i = 0; i<c; i++){
			waystones.add(BaseInfo.decode(buffer));
		}
	}

}
