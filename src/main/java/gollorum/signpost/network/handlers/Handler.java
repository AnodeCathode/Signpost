package gollorum.signpost.network.handlers;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import gollorum.signpost.network.messages.Message;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public abstract class Handler<H extends Handler<H, M>, M extends Message<M>> {

	public static <H extends Handler<H, M>, M extends Message<M>> BiConsumer<M, Supplier<Context>> handle(Class<H> h) {
		return (message, contextSupplier) -> {
			try {
				h.newInstance().handle(message, contextSupplier);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Message type "+h+" must provide a parameterless constructor");
			}
		};
	}
	
	public abstract void handle(M message, Supplier<Context> contextSupplier);
	
}
