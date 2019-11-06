package gollorum.signpost.network.handlers;

import java.util.function.Supplier;

import gollorum.signpost.blocks.tiles.PostPostTile;
import gollorum.signpost.blocks.tiles.SuperPostPostTile;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.NetworkHandler;
import gollorum.signpost.network.messages.SendPostBasesMessage;
import gollorum.signpost.util.DoubleBaseInfo;
import gollorum.signpost.util.Sign.OverlayType;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SendPostBasesHandler extends Handler<SendPostBasesHandler, SendPostBasesMessage>{

	@Override
	public void handle(SendPostBasesMessage message, Supplier<Context> contextSupplier) {
		TileEntity tile = message.pos.getTile();
		DoubleBaseInfo bases;
		if(tile instanceof PostPostTile){
			PostPostTile postTile = (PostPostTile) tile;
			postTile.isWaystone();
			bases = postTile.getBases();
		}else{
			bases = PostHandler.getPosts().get(message.pos);
			if(bases==null){
				bases = new DoubleBaseInfo(null, null);
				PostHandler.getPosts().put(message.pos, bases);
			}
		}
		bases.sign1.rotation = message.base1rot;
		bases.sign2.rotation = message.base2rot;
		bases.sign1.flip = message.flip1;
		bases.sign2.flip = message.flip2;
		bases.sign1.base = PostHandler.getForceWSbyName(message.base1);
		bases.sign2.base = PostHandler.getForceWSbyName(message.base2);
		
		bases.sign1.overlay = OverlayType.get(message.overlay1);
		bases.sign2.overlay = OverlayType.get(message.overlay2);
		
		bases.sign1.point = message.point1;
		bases.sign2.point = message.point2;
		bases.sign1.paint = SuperPostPostTile.stringToLoc(message.paint1);
		bases.sign2.paint = SuperPostPostTile.stringToLoc(message.paint2);
		
		bases.postPaint = SuperPostPostTile.stringToLoc(message.postPaint);

		switch(message.paintObjectIndex){
		case 1:
			bases.paintObject = bases;
			bases.awaitingPaint = true;
			break;
		case 2:
			bases.paintObject = bases.sign1;
			bases.awaitingPaint = true;
			break;
		case 3:
			bases.paintObject = bases.sign2;
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
