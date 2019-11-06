package gollorum.signpost.network.messages;

import gollorum.signpost.blocks.tiles.BigPostPostTile;
import gollorum.signpost.blocks.tiles.SuperPostPostTile;
import gollorum.signpost.network.NetworkUtil;
import gollorum.signpost.util.BigBaseInfo;
import gollorum.signpost.util.MyBlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class SendBigPostBasesMessage extends Message<SendBigPostBasesMessage> {

	public MyBlockPos pos;
	public String base;
	public int baserot;
	public boolean flip;
	public String overlay;
	public boolean point;
	public String[] description;
	public ResourceLocation paint;
	public ResourceLocation postPaint;
	
	public byte paintObjectIndex;

	public SendBigPostBasesMessage(){}
	
	public SendBigPostBasesMessage(BigPostPostTile tile, BigBaseInfo base) {
		tile.markDirty();
		this.pos = tile.toPos();
		this.base = ""+base.sign.base;
		baserot = base.sign.rotation;
		flip = base.sign.flip;
		overlay = ""+base.sign.overlay;
		point = base.sign.point;
		description = base.description;
		paint = base.sign.paint;
		postPaint = base.postPaint;
		
		if(base.equals(tile.getPaintObject())){
			paintObjectIndex = 1;
		}else if(base.sign.equals(tile.getPaintObject())){
			paintObjectIndex = 2;
		}else{
			paintObjectIndex = 0;
		}
	}

	@Override
	public void encode(PacketBuffer buf) {
		pos.encode(buf);
		buf.writeString(base);
		buf.writeInt(baserot);
		buf.writeBoolean(flip);
		buf.writeString(overlay);
		buf.writeBoolean(point);
		buf.writeInt(description.length);
		for(String now: description){
			buf.writeString(now);
		}
		buf.writeString(SuperPostPostTile.locToString(paint));
		buf.writeString(SuperPostPostTile.locToString(postPaint));
		buf.writeByte(paintObjectIndex);
	}

	@Override
	public void decode(PacketBuffer buf) {
		pos = MyBlockPos.decode(buf);
		base = buf.readString(NetworkUtil.MAX_STRING_LENGTH);
		baserot = buf.readInt();
		flip = buf.readBoolean();
		overlay = buf.readString(NetworkUtil.MAX_STRING_LENGTH);
		point = buf.readBoolean();
		description = new String[buf.readInt()];
		for(int i=0; i<description.length; i++){
			description[i] = buf.readString(NetworkUtil.MAX_STRING_LENGTH);
		}
		paint = SuperPostPostTile.stringToLoc(buf.readString(NetworkUtil.MAX_STRING_LENGTH));
		postPaint = SuperPostPostTile.stringToLoc(buf.readString(NetworkUtil.MAX_STRING_LENGTH));
		paintObjectIndex = buf.readByte();
	}

}
