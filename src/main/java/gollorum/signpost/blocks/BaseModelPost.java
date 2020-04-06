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
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BaseModelPost extends BlockContainer {

	public static enum ModelType implements IStringSerializable{
		MODEL1(0, "model0"),
		MODEL2(1, "model1"),
		MODEL3(2, "model2"),
		MODEL4(3, "model3"),
		MODEL5(4, "model4");
		
		private int ID;
		private String name;
		
		private ModelType(int ID, String name){
			this.ID = ID;
			this.name = name;
		}
		
		@Override
		public String getName(){
			return name;
		}
		
		@Override
		public String toString(){
			return getName();
		}
		
		public int getID(){
			return ID;
		}
		
		private static ModelType getByID(int ID){
			for(ModelType now: ModelType.values()){
				if(ID == now.ID){
					return now;
				}
			}
			return ModelType.MODEL1;
		}
	}

    public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
	public final ModelType type;
	
	public BaseModelPost(int typ) {
		super(Properties.from(Blocks.STONE));
		//super(Properties.create(Material.ROCK).hardnessAndResistance(2, 100000));
		//this.setHarvestLevel("pickaxe", 1);
//		setCreativeTab(CreativeTabs.TRANSPORTATION);
//		this.setTranslationKey("SignpostBase");
		this.setRegistryName(Signpost.MODID+":blockbasemodel"+typ);
		type = ModelType.values()[typ];
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.SOUTH));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FACING);
	}
	
	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext context){
		return this.getDefaultState().with(FACING, context.getPlayer().getHorizontalFacing().getOpposite());
	} 
	 
	public IBlockState getStateForFacing(EnumFacing facing) {
		return this.getDefaultState().with(FACING, facing);
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
					if (!ClientConfigStorage.INSTANCE.deactivateTeleportation()) {
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

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new BasePostTile();
	}

	public static BasePostTile getWaystoneRootTile(IWorld world, BlockPos pos) {
		TileEntity ret = world.getTileEntity(pos);
		if (ret instanceof BasePostTile) {
			return (BasePostTile) ret;
		} else {
			return null;
		}
	}

	public static void placeServer(IWorld world, MyBlockPos blockPos, EntityPlayerMP player) {
		MyBlockPos telePos = new MyBlockPos(player);
		BasePostTile tile = getWaystoneRootTile(world, blockPos.toBlockPos());
		String name = BasePost.generateName();
		UUID owner = player.getUniqueID();
		BaseInfo ws;
		if((ws = tile.getBaseInfo())==null){
			ws = new BaseInfo(name, blockPos, telePos, owner);
			PostHandler.addWaystone(ws);
		}else{
			ws.setAll(new BaseInfo(name, blockPos, telePos, owner));
		}
		PostHandler.addDiscovered(player.getUniqueID(), ws);
		NetworkHandler.sendToAll(new BaseUpdateClientMessage());
		MinecraftForge.EVENT_BUS.post(new UpdateWaystoneEvent(UpdateWaystoneEvent.WaystoneEventType.PLACED, world.getWorld(), blockPos.x, blockPos.y, blockPos.z, name));
		NetworkHandler.sendTo(player, new OpenGuiMessage(Signpost.GuiBaseID, blockPos.x, blockPos.y, blockPos.z));
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	public static BaseModelPost[] createAll() {
		BaseModelPost[] ret = new BaseModelPost[ModelType.values().length];
		for(int i=0; i<ModelType.values().length; i++){
			ret[i] = new BaseModelPost(i);
		}
		return ret;
	}

}
