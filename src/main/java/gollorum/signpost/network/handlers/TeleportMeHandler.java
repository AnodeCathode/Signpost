package gollorum.signpost.network.handlers;

import java.util.function.Supplier;

import gollorum.signpost.management.ClientConfigStorage;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.NetworkHandler;
import gollorum.signpost.network.messages.ChatMessage;
import gollorum.signpost.network.messages.TeleportMeMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class TeleportMeHandler extends Handler<TeleportMeHandler, TeleportMeMessage> {

	@Override
	public void handle(TeleportMeMessage message, Supplier<Context> contextSupplier) {
		if(ClientConfigStorage.INSTANCE.deactivateTeleportation()) return;
		Context ctx = contextSupplier.get();
		if(PostHandler.canTeleport(ctx.getSender(), message.base)){
			World world = message.base.teleportPosition.getWorld();
			EntityPlayerMP player = ctx.getSender();
			if(world == null){
				NetworkHandler.sendTo(player, new ChatMessage("signpost.errorWorld", "<world>", message.base.teleportPosition.world));
			}else{
				if(!player.world.equals(world)){
					player.setWorld(world);
				}
				if(!(player.dimension==message.base.teleportPosition.dim)){
					player.changeDimension(message.base.teleportPosition.dim);
					Dimension d;
					player.changeDimension(d.getType(), null);
				}
				ctx.getSender().setPositionAndUpdate(message.base.teleportPosition.x, message.base.teleportPosition.y, message.base.teleportPosition.z);
			}
		}
	}

}
