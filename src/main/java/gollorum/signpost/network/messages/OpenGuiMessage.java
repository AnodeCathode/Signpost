package gollorum.signpost.network.messages;

import net.minecraft.network.PacketBuffer;

public class OpenGuiMessage extends Message<OpenGuiMessage> {

	public int guiID;
	public int x, y, z;
	
	public OpenGuiMessage(){}
	
	public OpenGuiMessage(int guiID, int x, int y, int z){
		this.guiID = guiID;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(guiID);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		guiID = buffer.readInt();
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
	}

}
