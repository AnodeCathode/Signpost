package gollorum.signpost.blocks.tiles;

import gollorum.signpost.blocks.WaystoneContainer;
import gollorum.signpost.event.UpdateWaystoneEvent;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.NetworkHandler;
import gollorum.signpost.network.messages.BaseUpdateClientMessage;
import gollorum.signpost.network.messages.BaseUpdateServerMessage;
import gollorum.signpost.util.BaseInfo;
import gollorum.signpost.util.MyBlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;

public class BasePostTile extends TileEntity implements WaystoneContainer {

	public boolean isCanceled = false;
	
    private static final TileEntityType<BasePostTile> TYPE = TileEntityType.register("baseposttile", TileEntityType.Builder.create(BasePostTile::new));

	public BasePostTile() { super(TYPE); }
	
	public BaseInfo getBaseInfo(){
		return PostHandler.getNativeWaystones().getByPos(toPos());
	}

	public MyBlockPos toPos(){
		if(getWorld()==null||getWorld().isRemote){
			return new MyBlockPos(pos, dim());
		}else{
			return new MyBlockPos(pos, dim());
		}
	}

	public DimensionType dim(){
		if(getWorld() == null){
			return null;
		}else
			return getWorld().getDimension().getType();
	}
	
	public void onBlockDestroy(MyBlockPos pos) {
		isCanceled = true;
//		BaseInfo base = PostHandler.allWaystones.getByPos(pos);
		BaseInfo base = getBaseInfo();
		if(PostHandler.getNativeWaystones().remove(base)){
			MinecraftForge.EVENT_BUS.post(new UpdateWaystoneEvent(UpdateWaystoneEvent.WaystoneEventType.DESTROYED, getWorld(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), base==null?"":base.getName()));
			NetworkHandler.sendToAll(new BaseUpdateClientMessage());
		}
	}

	@Override
	public void setName(String name) {
		BaseInfo ws = getBaseInfo();
		ws.setName(name);
		NetworkHandler.sendToServer(new BaseUpdateServerMessage(ws, false));
	}

	@Override
	public String getName() {
		BaseInfo ws = getBaseInfo();
		return ws == null ? "null" : getBaseInfo().toString();
	}

	@Override
	public String toString() {
		return getName();
	}

}
