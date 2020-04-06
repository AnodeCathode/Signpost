package gollorum.signpost.network.handlers;

import java.util.function.Supplier;

import gollorum.signpost.network.messages.ChatMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ChatHandler extends Handler<ChatHandler, ChatMessage> {

	@Override
	public void handle(ChatMessage message, Supplier<Context> contextSupplier) {
		Minecraft.getInstance().player.sendMessage(new TextComponentString(message.getString()));
	}

}
