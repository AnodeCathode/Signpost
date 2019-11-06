package gollorum.signpost.network.messages;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.network.PacketBuffer;

public abstract class Message<T extends Message<T>> {

	public static <T extends Message<T>> BiConsumer<T, PacketBuffer> encode() {
		return T::encode;
	}

	public static <T extends Message<T>> Function<PacketBuffer, T> decode(Class<T> t) {
		return buffer -> {
			try {
				T ret = t.newInstance();
				ret.decode(buffer);
				return ret;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Message type "+t+" must provide a parameterless constructor");
			}
		};
	}
	
	protected abstract void encode(PacketBuffer buffer);

	protected abstract void decode(PacketBuffer buffer);

}
