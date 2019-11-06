package gollorum.signpost.network.handlers;

import java.util.function.Supplier;

import gollorum.signpost.event.UpdateWaystoneEvent;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.NetworkHandler;
import gollorum.signpost.network.messages.BaseUpdateClientMessage;
import gollorum.signpost.network.messages.BaseUpdateServerMessage;
import gollorum.signpost.util.BaseInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;

public class BaseUpdateServerHandler extends Handler<BaseUpdateServerHandler, BaseUpdateServerMessage> {

	@Override
	public void handle(BaseUpdateServerMessage message, Supplier<Context> contextSupplier) {
		Context ctx = contextSupplier.get();
		if (message.destroyed) {
		} else {
			PostHandler.addDiscovered(ctx.getSender().getUniqueID(), message.wayStone);
		}
		BaseInfo waystone = PostHandler.getAllWaystones().getByPos(message.wayStone.blockPosition);
		waystone.setAll(message.wayStone);
		NetworkHandler.HANDLER.send(PacketDistributor.ALL.noArg(), new BaseUpdateClientMessage());
		MinecraftForge.EVENT_BUS.post(new UpdateWaystoneEvent(UpdateWaystoneEvent.WaystoneEventType.NAMECHANGED, ctx.getSender().world, waystone.teleportPosition.x, waystone.teleportPosition.y, waystone.teleportPosition.z, waystone.getName()));
	}

}
