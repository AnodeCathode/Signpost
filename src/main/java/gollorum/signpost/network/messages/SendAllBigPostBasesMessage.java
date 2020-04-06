package gollorum.signpost.network.messages;

import java.util.HashMap;
import java.util.Map.Entry;

import gollorum.signpost.blocks.tiles.BigPostPostTile;
import gollorum.signpost.blocks.tiles.SuperPostPostTile;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.NetworkUtil;
import gollorum.signpost.util.BaseInfo;
import gollorum.signpost.util.BigBaseInfo;
import gollorum.signpost.util.MyBlockPos;
import gollorum.signpost.util.Sign;
import gollorum.signpost.util.Sign.OverlayType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class SendAllBigPostBasesMessage extends Message<SendAllBigPostBasesMessage> {

	public class BigStringInt{
		public String string;
		public int datInt;
		public boolean bool;
		public OverlayType overlay;
		public boolean bool2;
		public String[] strings;
		public ResourceLocation paint;
		public ResourceLocation postPaint;
		
		public byte paintObjectIndex;
		
		public BigStringInt(String string, int datInt, boolean bool, OverlayType overlay, boolean bool2, String[] strings, ResourceLocation paint, ResourceLocation postPaint,  byte paintObjectIndex) {
			this.string = string;
			this.datInt = datInt;
			this.bool = bool;
			this.overlay = overlay;
			this.bool2 = bool2;
			this.strings = strings;
			this.paint = paint;
			this.postPaint = postPaint;
			this.paintObjectIndex = paintObjectIndex;
		}
	}
	
	public HashMap<MyBlockPos, BigStringInt> bigPosts = new HashMap<MyBlockPos, BigStringInt>();

	public HashMap<MyBlockPos, BigBaseInfo> toPostMap(){
		HashMap<MyBlockPos, BigBaseInfo> postMap = new HashMap<MyBlockPos, BigBaseInfo>();
		for(Entry<MyBlockPos, BigStringInt> now: bigPosts.entrySet()){
			BaseInfo base = PostHandler.getForceWSbyName(now.getValue().string);
			postMap.put(now.getKey(), new BigBaseInfo(new Sign(base,
															   now.getValue().datInt,
															   now.getValue().bool,
															   now.getValue().overlay,
															   now.getValue().bool2,
															   now.getValue().paint),
														now.getValue().strings,
														now.getValue().postPaint));
		}
		return postMap;
	}
	
	public SendAllBigPostBasesMessage(){}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(PostHandler.getBigPosts().size());
		for(Entry<MyBlockPos, BigBaseInfo> now: PostHandler.getBigPosts().entrySet()){
			now.getKey().encode(buffer);
			buffer.writeString(""+now.getValue().sign.base);
			buffer.writeInt(now.getValue().sign.rotation);
			buffer.writeBoolean(now.getValue().sign.flip);
			buffer.writeString(""+now.getValue().sign.overlay);
			buffer.writeBoolean(now.getValue().sign.point);
			buffer.writeInt(now.getValue().description.length);
			for(String now2: now.getValue().description){
				buffer.writeString(now2);
			}
			buffer.writeString(SuperPostPostTile.locToString(now.getValue().sign.paint));
			buffer.writeString(SuperPostPostTile.locToString(now.getValue().postPaint));
			BigPostPostTile tile = (BigPostPostTile) now.getKey().getTile();
			if(tile!=null){
				if(now.getValue().equals(tile.getPaintObject())){
					buffer.writeByte(1);
				}else if(now.getValue().sign.equals(tile.getPaintObject())){
					buffer.writeByte(2);
				}else{
					buffer.writeByte(0);
				}
			}else{
				buffer.writeByte(0);
			}
		}
	}
	
	@Override
	public void decode(PacketBuffer buffer) {
		int c = buffer.readInt();
		for(int i = 0; i<c; i++){
			bigPosts.put(
				MyBlockPos.decode(buffer), 
				new BigStringInt(buffer.readString(NetworkUtil.MAX_STRING_LENGTH),
					buffer.readInt(),
					buffer.readBoolean(),
					OverlayType.get(buffer.readString(NetworkUtil.MAX_STRING_LENGTH)),
					buffer.readBoolean(),
					readDescription(buffer),
					SuperPostPostTile.stringToLoc(buffer.readString(NetworkUtil.MAX_STRING_LENGTH)),
					SuperPostPostTile.stringToLoc(buffer.readString(NetworkUtil.MAX_STRING_LENGTH)),
					buffer.readByte()));
		}
	}

	private String[] readDescription(PacketBuffer buffer){
		String[] ret = new String[buffer.readInt()];
		for(int i=0; i<ret.length; i++){
			ret[i] = buffer.readString(NetworkUtil.MAX_STRING_LENGTH);
		}
		return ret;
	}
	
}
