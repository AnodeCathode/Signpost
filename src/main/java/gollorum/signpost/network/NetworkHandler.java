package gollorum.signpost.network;

import gollorum.signpost.Signpost;
import gollorum.signpost.network.handlers.BaseUpdateClientHandler;
import gollorum.signpost.network.handlers.BaseUpdateServerHandler;
import gollorum.signpost.network.handlers.ChatHandler;
import gollorum.signpost.network.handlers.Handler;
import gollorum.signpost.network.handlers.InitPlayerResponseHandler;
import gollorum.signpost.network.handlers.OpenGuiHandler;
import gollorum.signpost.network.handlers.RequestTextureHandler;
import gollorum.signpost.network.handlers.SendAllBigPostBasesHandler;
import gollorum.signpost.network.handlers.SendAllPostBasesHandler;
import gollorum.signpost.network.handlers.SendAllWaystoneNamesHandler;
import gollorum.signpost.network.handlers.SendBigPostBasesHandler;
import gollorum.signpost.network.handlers.SendDiscoveredToServerHandler;
import gollorum.signpost.network.handlers.SendPostBasesHandler;
import gollorum.signpost.network.handlers.TeleportMeHandler;
import gollorum.signpost.network.handlers.TeleportRequestHandler;
import gollorum.signpost.network.messages.BaseUpdateClientMessage;
import gollorum.signpost.network.messages.BaseUpdateServerMessage;
import gollorum.signpost.network.messages.ChatMessage;
import gollorum.signpost.network.messages.InitPlayerResponseMessage;
import gollorum.signpost.network.messages.Message;
import gollorum.signpost.network.messages.OpenGuiMessage;
import gollorum.signpost.network.messages.RequestTextureMessage;
import gollorum.signpost.network.messages.SendAllBigPostBasesMessage;
import gollorum.signpost.network.messages.SendAllPostBasesMessage;
import gollorum.signpost.network.messages.SendAllWaystoneNamesMessage;
import gollorum.signpost.network.messages.SendBigPostBasesMessage;
import gollorum.signpost.network.messages.SendDiscoveredToServerMessage;
import gollorum.signpost.network.messages.SendPostBasesMessage;
import gollorum.signpost.network.messages.TeleportMeMessage;
import gollorum.signpost.network.messages.TeleportRequestMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {

	private static final String PROTOCOL_VERSION = Integer.toString(1);
	public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(Signpost.MODID, "main_channel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
	
	private static int i = 0;
	
	public static void register(){
		register(BaseUpdateServerHandler.class, BaseUpdateServerMessage.class);
		register(BaseUpdateClientHandler.class, BaseUpdateClientMessage.class);
		register(SendDiscoveredToServerHandler.class, SendDiscoveredToServerMessage.class);
		register(InitPlayerResponseHandler.class, InitPlayerResponseMessage.class);
		register(SendPostBasesHandler.class, SendPostBasesMessage.class);
		register(SendAllPostBasesHandler.class, SendAllPostBasesMessage.class);
		register(SendBigPostBasesHandler.class, SendBigPostBasesMessage.class);
		register(SendAllBigPostBasesHandler.class, SendAllBigPostBasesMessage.class);
		register(TeleportMeHandler.class, TeleportMeMessage.class);
		register(ChatHandler.class, ChatMessage.class);
		register(OpenGuiHandler.class, OpenGuiMessage.class);
		register(TeleportRequestHandler.class, TeleportRequestMessage.class);
		register(RequestTextureHandler.class, RequestTextureMessage.class);
		register(SendAllWaystoneNamesHandler.class, SendAllWaystoneNamesMessage.class);
	}
	
	private static <M extends Message<M>, H extends Handler<H, M>> void register(Class<H> handler, Class<M> message) {
		HANDLER.registerMessage(i++, message, Message.encode(), Message.decode(message), Handler.handle(handler));
	}
	
	public static <T extends Message<T>> void sendToAll(T message) {
		HANDLER.send(PacketDistributor.ALL.noArg(), message);
	}
	
	public static <T extends Message<T>> void sendTo(EntityPlayerMP player, T message) {
		HANDLER.send(PacketDistributor.PLAYER.with(() -> player), message);
	}
	
	public static <T extends Message<T>> void sendToServer(T message) {
		HANDLER.send(PacketDistributor.SERVER.noArg(), message);
	}
}