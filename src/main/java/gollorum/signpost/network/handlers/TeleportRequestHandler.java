package gollorum.signpost.network.handlers;

import java.util.function.Supplier;

import gollorum.signpost.management.ClientConfigStorage;
import gollorum.signpost.management.ConfigHandler;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.NetworkHandler;
import gollorum.signpost.network.messages.TeleportRequestMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class TeleportRequestHandler extends Handler<TeleportRequestHandler, TeleportRequestMessage> {

	@Override
	public void handle(TeleportRequestMessage message, Supplier<Context> contextSupplier) {
		EntityPlayerMP player = contextSupplier.get().getSender();
		if(contextSupplier.get().getDirection().getReceptionSide().equals(LogicalSide.SERVER)){
			PostHandler.confirm(player);
		}else{
			if(ClientConfigStorage.INSTANCE.skipTeleportConfirm()){
				NetworkHandler.sendTo(player, message);
			}else{
				String out;
				if(ClientConfigStorage.INSTANCE.getCost()!=null){
					out = I18n.format("signpost.confirmTeleport")
						.replaceAll("<Waystone>", message.waystoneName)
						.replaceAll("<amount>", Integer.toString(message.stackSize))
						.replaceAll("<itemName>", ConfigHandler.costName());
				}else{
					out = I18n.format("signpost.confirmTeleportNoCost")
						.replaceAll("<Waystone>", message.waystoneName);
				}
				Minecraft.getInstance().player.sendMessage(new TextComponentString(out));
			}
		}
	}

	public String getReplacement(String replace){
		String ret = I18n.format(replace);
		if(!ret.equals("")){
			return ret;
		}
		return replace;
	}
}
