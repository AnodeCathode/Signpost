package gollorum.signpost.network.handlers;

import java.util.Map.Entry;
import java.util.function.Supplier;

import gollorum.signpost.blocks.tiles.SuperPostPostTile;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.messages.BaseUpdateClientMessage;
import gollorum.signpost.util.BaseInfo;
import gollorum.signpost.util.BigBaseInfo;
import gollorum.signpost.util.DoubleBaseInfo;
import gollorum.signpost.util.MyBlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class BaseUpdateClientHandler extends Handler<BaseUpdateClientHandler, BaseUpdateClientMessage> {

	@Override
	public void handle(BaseUpdateClientMessage message, Supplier<Context> contextSupplier) {
		for(Entry<MyBlockPos, DoubleBaseInfo> now: PostHandler.getPosts().entrySet()){
			TileEntity tile = Minecraft.getInstance().world.getTileEntity(now.getKey().toBlockPos());
			if(tile instanceof SuperPostPostTile){
				((SuperPostPostTile)tile).isWaystone=false;
			}
		}
		for(Entry<MyBlockPos, BigBaseInfo> now: PostHandler.getBigPosts().entrySet()){
			TileEntity tile = Minecraft.getInstance().world.getTileEntity(now.getKey().toBlockPos());
			if(tile instanceof SuperPostPostTile){
				((SuperPostPostTile)tile).isWaystone=false;
			}
		}
		PostHandler.setNativeWaystones(message.waystones);
		for(BaseInfo now: PostHandler.getNativeWaystones()){
			TileEntity tile = Minecraft.getInstance().world.getTileEntity(now.blockPosition.toBlockPos());
			if(tile instanceof SuperPostPostTile){
				((SuperPostPostTile)tile).isWaystone=true;
			}
		}
		for(Entry<MyBlockPos, DoubleBaseInfo> now: PostHandler.getPosts().entrySet()){
			BaseInfo base = now.getValue().sign1.base;
			if(base!=null &&!(base.teleportPosition==null && base.owner==null)){
				now.getValue().sign1.base = PostHandler.getAllWaystones().getByPos(base.blockPosition);
			}
			base = now.getValue().sign2.base;
			if(base!=null &&!(base.teleportPosition==null && base.owner==null)){
				now.getValue().sign2.base = PostHandler.getAllWaystones().getByPos(base.blockPosition);
			}
		}
		for(Entry<MyBlockPos, BigBaseInfo> now: PostHandler.getBigPosts().entrySet()){
			BaseInfo base = now.getValue().sign.base;
			if(base!=null &&!(base.teleportPosition==null && base.owner==null)){
				now.getValue().sign.base = PostHandler.getAllWaystones().getByPos(base.blockPosition);
			}
			TileEntity tile = now.getKey().getTile();
		}
	}

}
