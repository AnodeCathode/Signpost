package gollorum.signpost.network.messages;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class RequestTextureMessage extends Message<RequestTextureMessage> {

	private int x,y,z;
	public EnumHand hand;
	public EnumFacing facing;
	public float hitX, hitY, hitZ;

	public RequestTextureMessage(){}

	public RequestTextureMessage(int x, int y, int z, EnumHand hand, float hitX, float hitY, float hitZ){
		this.x = x;
		this.y = y;
		this.z = z;
		this.hand = hand;
		
		//TODO: Whatever
		this.facing = facing;
		this.hitX = hitX;
		this.hitY = hitY;
		this.hitZ = hitZ;
	}
	
	@Override
	public void decode(PacketBuffer buffer) {
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		hand = buffer.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		facing = EnumFacing.values()[buffer.readByte()];
		hitX = buffer.readFloat();
		hitY = buffer.readFloat();
		hitZ = buffer.readFloat();
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeBoolean(hand == EnumHand.MAIN_HAND);
		buffer.writeByte(facing.getIndex());
		buffer.writeFloat(hitX);
		buffer.writeFloat(hitY);
		buffer.writeFloat(hitZ);
	}

	public BlockPos toBlockPos() {
		return new BlockPos(x, y, z);
	}

}
