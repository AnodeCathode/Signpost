package gollorum.signpost.network.handlers;

import java.util.function.Supplier;

import gollorum.signpost.network.messages.ChatMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ChatHandler extends Handler<ChatHandler, ChatMessage> {

	@Override
	public void handle(ChatMessage message, Supplier<Context> contextSupplier) {
		String out = I18n.format(message.message);
		for(int i=0; i<message.keyword.length; i++){
			out = out.replaceAll(message.keyword[i], getReplacement(message.replacement[i]));
		}
		Minecraft.getInstance().player.sendMessage(new TextComponentString(out));
	}

	public String getReplacement(String replace){
		String ret = I18n.format(replace);
		if(!ret.equals("")){
			return ret;
		}
		return replace;
	}

}
