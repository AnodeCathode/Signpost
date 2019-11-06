package gollorum.signpost.network.handlers;

import java.util.function.Supplier;

import gollorum.signpost.blocks.tiles.BigPostPostTile;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.NetworkHandler;
import gollorum.signpost.network.messages.SendBigPostBasesMessage;
import gollorum.signpost.util.BigBaseInfo;
import gollorum.signpost.util.Sign;
import gollorum.signpost.util.Sign.OverlayType;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SendBigPostBasesHandler extends Handler<SendBigPostBasesHandler, SendBigPostBasesMessage>{

	@Override
	public void handle(SendBigPostBasesMessage message, Supplier<Context> contextSupplier) {
		TileEntity tile = message.pos.getTile();
		BigBaseInfo bases;
		if(tile instanceof BigPostPostTile){
			BigPostPostTile postTile = (BigPostPostTile) tile;
			postTile.isWaystone();
			bases = postTile.getBases();
		}else{
			bases = PostHandler.getBigPosts().get(message.pos);
			if(bases==null){
				bases = new BigBaseInfo(new Sign(null), null);
				PostHandler.getBigPosts().put(message.pos, bases);
			}
		}
		bases.sign.rotation = message.baserot;
		bases.sign.flip = message.flip;
		bases.sign.base = PostHandler.getForceWSbyName(message.base);
		bases.sign.overlay = OverlayType.get(message.overlay);
		bases.sign.point = message.point;
		bases.description = message.description;
		bases.sign.paint = message.paint;
		bases.postPaint = message.postPaint;
		
		switch(message.paintObjectIndex){
		case 1:
			bases.paintObject = bases;
			bases.awaitingPaint = true;
			break;
		case 2:
			bases.paintObject = bases.sign;
			bases.awaitingPaint = true;
			break;
		default:
			bases.paintObject = null;
			bases.awaitingPaint = false;
			break;
		}
		Context ctx = contextSupplier.get();
		if(ctx.getDirection().getReceptionSide().equals(LogicalSide.SERVER)){
			ctx.getSender().world.getTileEntity(message.pos.toBlockPos()).markDirty();
			NetworkHandler.sendToAll(message);
		}
	}

}
