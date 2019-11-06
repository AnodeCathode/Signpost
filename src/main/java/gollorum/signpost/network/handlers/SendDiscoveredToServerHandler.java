package gollorum.signpost.network.handlers;

import java.util.function.Supplier;

import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.messages.SendDiscoveredToServerMessage;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SendDiscoveredToServerHandler extends Handler<SendDiscoveredToServerHandler, SendDiscoveredToServerMessage> {

	@Override
	public void handle(SendDiscoveredToServerMessage message, Supplier<Context> contextSupplier) {
		PostHandler.addDiscovered(contextSupplier.get().getSender().getUniqueID(), PostHandler.getWSbyName(message.waystone));
	}

}
