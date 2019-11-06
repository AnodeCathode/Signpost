package gollorum.signpost.network.messages;

import gollorum.signpost.blocks.tiles.PostPostTile;
import gollorum.signpost.blocks.tiles.SuperPostPostTile;
import gollorum.signpost.network.NetworkUtil;
import gollorum.signpost.util.DoubleBaseInfo;
import gollorum.signpost.util.MyBlockPos;
import net.minecraft.network.PacketBuffer;

public class SendPostBasesMessage extends Message<SendPostBasesMessage> {

	public MyBlockPos pos;
	
	public String base1;
	public String base2;
	
	public int base1rot;
	public int base2rot;

	public boolean flip1;
	public boolean flip2;

	public String overlay1;
	public String overlay2;

	public boolean point1;
	public boolean point2;

	public String paint1;
	public String paint2;

	public String postPaint;
	
	public byte paintObjectIndex;

	public SendPostBasesMessage(){}
	
	public SendPostBasesMessage(PostPostTile tile, DoubleBaseInfo base) {
		tile.markDirty();
		this.pos = tile.toPos();
		this.base1 = ""+base.sign1.base;
		this.base2 = ""+base.sign2.base;
		base1rot = base.sign1.rotation;
		base2rot = base.sign2.rotation;
		flip1 = base.sign1.flip;
		flip2 = base.sign2.flip;
		overlay1 = ""+base.sign1.overlay;
		overlay2 = ""+base.sign2.overlay;
		point1 = base.sign1.point;
		point2 = base.sign2.point;
		paint1 = SuperPostPostTile.locToString(base.sign1.paint);
		paint2 = SuperPostPostTile.locToString(base.sign2.paint);
		postPaint = SuperPostPostTile.locToString(base.postPaint);
		
		if(base.equals(tile.getPaintObject())){
			paintObjectIndex = 1;
		}else if(base.sign1.equals(tile.getPaintObject())){
			paintObjectIndex = 2;
		}else if(base.sign2.equals(tile.getPaintObject())){
			paintObjectIndex = 3;
		}else{
			paintObjectIndex = 0;
		}
	}

	@Override
	public void encode(PacketBuffer buf) {
		pos.encode(buf);
		buf.writeString(base1);
		buf.writeString(base2);
		buf.writeInt(base1rot);
		buf.writeInt(base2rot);
		buf.writeBoolean(flip1);
		buf.writeBoolean(flip2);
		buf.writeString(overlay1);
		buf.writeString(overlay2);
		buf.writeBoolean(point1);
		buf.writeBoolean(point2);
		buf.writeString(""+paint1);
		buf.writeString(""+paint2);
		buf.writeString(""+postPaint);
		buf.writeByte(paintObjectIndex);
	}

	@Override
	public void decode(PacketBuffer buf) {
		pos = MyBlockPos.decode(buf);
		base1 = buf.readString(NetworkUtil.MAX_STRING_LENGTH);
		base2 = buf.readString(NetworkUtil.MAX_STRING_LENGTH);
		base1rot = buf.readInt();
		base2rot = buf.readInt();
		flip1 = buf.readBoolean();
		flip2 = buf.readBoolean();
		overlay1 = buf.readString(NetworkUtil.MAX_STRING_LENGTH);
		overlay2 = buf.readString(NetworkUtil.MAX_STRING_LENGTH);
		point1 = buf.readBoolean();
		point2 = buf.readBoolean();
		paint1 = buf.readString(NetworkUtil.MAX_STRING_LENGTH);
		paint2 = buf.readString(NetworkUtil.MAX_STRING_LENGTH);
		postPaint = buf.readString(NetworkUtil.MAX_STRING_LENGTH);
		paintObjectIndex = buf.readByte();
	}

}
