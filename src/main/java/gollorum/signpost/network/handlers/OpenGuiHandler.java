package gollorum.signpost.network.handlers;

import java.util.function.Supplier;

import gollorum.signpost.Signpost;
import gollorum.signpost.network.messages.OpenGuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class OpenGuiHandler extends Handler<OpenGuiHandler, OpenGuiMessage> {

	@Override
	public void handle(OpenGuiMessage message, Supplier<Context> contextSupplier) {
		Minecraft.getInstance().player.openGui(Signpost.instance, message.guiID, Signpost.proxy.getWorld(contextSupplier.get()), message.x, message.y, message.z);
	}

}