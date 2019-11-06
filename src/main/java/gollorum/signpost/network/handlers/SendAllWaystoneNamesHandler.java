package gollorum.signpost.network.handlers;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Supplier;

import gollorum.signpost.network.messages.SendAllWaystoneNamesMessage;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SendAllWaystoneNamesHandler extends Handler<SendAllWaystoneNamesHandler, SendAllWaystoneNamesMessage>{
	
	public static Collection<String> cachedWaystoneNames = new HashSet<String>();

	@Override
	public void handle(SendAllWaystoneNamesMessage message, Supplier<Context> contextSupplier) {
		cachedWaystoneNames = message.waystones;
	}

}
