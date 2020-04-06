package gollorum.signpost.blocks;

import java.util.UUID;

import gollorum.signpost.Signpost;
import gollorum.signpost.blocks.tiles.BasePostTile;
import gollorum.signpost.event.UpdateWaystoneEvent;
import gollorum.signpost.management.ClientConfigStorage;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.NetworkHandler;
import gollorum.signpost.network.messages.BaseUpdateClientMessage;
import gollorum.signpost.network.messages.ChatMessage;
import gollorum.signpost.network.messages.OpenGuiMessage;
import gollorum.signpost.util.BaseInfo;
import gollorum.signpost.util.MyBlockPos;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.Block.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BasePost extends BlockContainer {

	public BasePost() {
		super(Properties.from(Blocks.STONE));
		this.setRegistryName(Signpost.MODID+":blockbase");
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (ClientConfigStorage.INSTANCE.deactivateTeleportation()) {
			return false;
		}
		if (!worldIn.isRemote) {
			BaseInfo ws = getWaystoneRootTile(worldIn, pos).getBaseInfo();
			if(ws==null){
				ws = new BaseInfo(BasePost.generateName(), new MyBlockPos(pos, player.dimension), player.getUniqueID());
				PostHandler.addWaystone(ws);
			}
			if (!player.isSneaking()) {
				if(!PostHandler.doesPlayerKnowNativeWaystone((EntityPlayerMP) player, ws)){
					if (!ClientConfigStorage.INSTANCE.deactivateTeleportation()||ClientConfigStorage.INSTANCE.isDisableDiscovery()) {
						NetworkHandler.sendTo((EntityPlayerMP) player, new ChatMessage("signpost.discovered", "<Waystone>", ws.getName()));
					}
					PostHandler.addDiscovered(player.getUniqueID(), ws);
				}
			} else {
				if (!ClientConfigStorage.INSTANCE.deactivateTeleportation()
						&& ClientConfigStorage.INSTANCE.getSecurityLevelWaystone().canUse((EntityPlayerMP) player, ""+ws.owner)) {
					NetworkHandler.sendTo((EntityPlayerMP) player, new OpenGuiMessage(Signpost.GuiBaseID, pos.getX(), pos.getY(), pos.getZ()));
				}
			}
		}
		return true;
	}

	public static BasePostTile getWaystoneRootTile(IWorld world, BlockPos pos) {
		TileEntity ret = world.getTileEntity(pos);
		if (ret instanceof BasePostTile) {
			return (BasePostTile) ret;
		} else {
			return null;
		}
	}

	public static String generateName() {
		int i = 1;
		String ret;
		do {
			ret = "Waystone " + (i++);
		} while (PostHandler.getAllWaystones().nameTaken(ret));
		return ret;
	}

	public static void placeServer(IWorld world, MyBlockPos pos, EntityPlayerMP player) {
		BasePostTile tile = getWaystoneRootTile(world, pos.toBlockPos());
		String name = generateName();
		UUID owner = player.getUniqueID();
		BaseInfo ws;
		if((ws = tile.getBaseInfo())==null){
			if(owner==null){
				System.out.println("bp ps t null");
			}
			ws = new BaseInfo(name, pos, owner);
			PostHandler.addWaystone(ws);
		}else{
			if(owner==null){
				System.out.println("bp ps f null");
			}
			ws.setAll(new BaseInfo(name, pos, owner));
		}
		PostHandler.addDiscovered(player.getUniqueID(), ws);
		NetworkHandler.sendToAll(new BaseUpdateClientMessage());
		MinecraftForge.EVENT_BUS.post(new UpdateWaystoneEvent(UpdateWaystoneEvent.WaystoneEventType.PLACED, world.getWorld(), pos.x, pos.y, pos.z, name));
		NetworkHandler.sendTo(player, new OpenGuiMessage(Signpost.GuiBaseID, pos.x, pos.y, pos.z));
	}

	public static void placeClient(final IWorld world, final MyBlockPos pos, final EntityPlayer player) {
		BasePostTile tile = getWaystoneRootTile(world, pos.toBlockPos());
		if (tile != null && tile.getBaseInfo() == null) {
			BaseInfo ws = PostHandler.getAllWaystones().getByPos(pos);
			if (ws == null) {
				UUID owner = player != null ? player.getUniqueID() : null;
				PostHandler.getAllWaystones().add(new BaseInfo("", pos, owner));
			}
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return true;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}

	public static void generate(World world, MyBlockPos blockPos, MyBlockPos telePos, String name) {
		BasePostTile tile = getWaystoneRootTile(world, blockPos.toBlockPos());
		UUID owner = null;
		BaseInfo ws;
		if ((ws = tile.getBaseInfo()) == null) {
			ws = new BaseInfo(name, blockPos, telePos, owner);
			PostHandler.addWaystone(ws);
		} else {
			ws.setAll(new BaseInfo(name, blockPos, telePos, owner));
		}
		NetworkHandler.sendToAll(new BaseUpdateClientMessage());
		MinecraftForge.EVENT_BUS.post(new UpdateWaystoneEvent(UpdateWaystoneEvent.WaystoneEventType.PLACED, world, blockPos.x, blockPos.y, blockPos.z, name));
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader reader) {
		return new BasePostTile();
	}

}
