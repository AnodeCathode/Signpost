package gollorum.signpost.network.messages;

import gollorum.signpost.network.NetworkUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.PacketBuffer;

public class ChatMessage extends Message<ChatMessage> implements com.mojang.brigadier.Message {

	public String message;
	public String[] keyword, replacement;

	public ChatMessage(){}
	
	public ChatMessage(String message, String keyword, String replacement){
		this.message = message;
		this.keyword = new String[1];
		this.keyword[0] = keyword;
		this.replacement = new String[1];
		this.replacement[0] = replacement;
	}
	
	public ChatMessage(String message, String[] keyword, String[] replacement){
		this.message = message;
		this.keyword = keyword;
		this.replacement = replacement;
	}
	
	public ChatMessage(String message) {
		this(message, new String[0], new String[0]);
	}
	
	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeString(message);
		buffer.writeInt(keyword.length);
		for(int i=0; i<keyword.length; i++){
			buffer.writeString(getKeyword(i));
			buffer.writeString(getReplacement(i));
		}
	}

	@Override
	public void decode(PacketBuffer buffer) {
		message = buffer.readString(NetworkUtil.MAX_STRING_LENGTH);
		keyword = new String[buffer.readInt()];
		replacement = new String[keyword.length];
		for(int i=0; i<keyword.length; i++){
			keyword[i] = buffer.readString(NetworkUtil.MAX_STRING_LENGTH);
			replacement[i] = buffer.readString(NetworkUtil.MAX_STRING_LENGTH);
		}
	}
	
	private String getKeyword(int i){
		String ret = keyword[i];
		return ""+ret;
	}
	
	private String getReplacement(int i){
		String ret = replacement[i];
		return ""+ret;
	}

	@Override
	public String getString() {
		String out = I18n.format(message);
		for(int i=0; i<keyword.length; i++){
			out = out.replaceAll(keyword[i], getReplacement(replacement[i]));
		}
		return out;
	}

	public String getReplacement(String replace){
		String ret = I18n.format(replace);
		if(!ret.equals("")){
			return ret;
		}
		return replace;
	}

}
