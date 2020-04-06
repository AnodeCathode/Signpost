package gollorum.signpost.util;

import java.util.Objects;
import java.util.function.Function;

import gollorum.signpost.Signpost;
import gollorum.signpost.blocks.tiles.BigPostPostTile;
import gollorum.signpost.blocks.tiles.PostPostTile;
import gollorum.signpost.management.ClientConfigStorage;
import gollorum.signpost.network.NetworkUtil;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class MyBlockPos{
	
	private static final String VERSION = "1";
	
	public int x, y, z;
	public DimensionType dim;
	
	public MyBlockPos(World world, int x, int y, int z) {
		this(x, y, z, dim(world));
	}

	public MyBlockPos(double x, double y, double z, DimensionType dim){
		this((int)x, (int)y, (int)z, dim);
	}

	public MyBlockPos(World world, BlockPos pos){
		this(pos, dim(world));
	}

	public MyBlockPos(BlockPos pos, DimensionType dim){
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		this.dim = dim;
	}
	
	public MyBlockPos(int x, int y, int z, DimensionType dim){
		this.x = x;
		this.y = y;
		this.z = z;
		this.dim = dim;
	}

	public MyBlockPos(MyBlockPos pos) {
		this(pos.x, pos.y, pos.z, pos.dim);
	}
	
	public MyBlockPos(Entity entity){
		this((int)Math.floor(entity.posX), (int)Math.floor(entity.posY), (int)Math.floor(entity.posZ), dim(entity.world));
	}

	private static DimensionType dim(World world){
		if(world==null){
			return null;
		}else
			return world.getDimension().getType();
	}

	public World getWorld(){
		return Signpost.getServerInstance().getWorld(dim);
	}
	
	public static enum Connection{VALID, WORLD, DIST}
	
	public Connection canConnectTo(BaseInfo inf){
		if(inf==null){
			return Connection.VALID;
		}
		if(ClientConfigStorage.INSTANCE.deactivateTeleportation()){
			return Connection.VALID;
		}
		if(!checkInterdimensional(inf.teleportPosition)){
			return Connection.WORLD;
		}
		if(ClientConfigStorage.INSTANCE.getMaxDist()>-1&&distance(inf.teleportPosition)>ClientConfigStorage.INSTANCE.getMaxDist()){
			return Connection.DIST;
		}
		return Connection.VALID;
	}
	
	public boolean checkInterdimensional(MyBlockPos pos){
		boolean config = ClientConfigStorage.INSTANCE.interdimensional();
		return config || sameDim(pos);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tC){
		int[] arr = {x, y, z, dim.getId()};
		tC.setIntArray("Position", arr);
		tC.setString("Version", VERSION);
		return tC;
	}
	
	public static MyBlockPos readFromNBT(NBTTagCompound tC){
		int[] arr = tC.getIntArray("Position");
		return new MyBlockPos(arr[0], arr[1], arr[2], DimensionType.getById(arr[3]));
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeString(VERSION);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeInt(dim.getId());
	}
	
	public static MyBlockPos decode(PacketBuffer buffer) {
		String savedVersion = buffer.readString(NetworkUtil.MAX_STRING_LENGTH);
		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();
		DimensionType dim = DimensionType.getById(buffer.readInt());
		if(!savedVersion.equals(VERSION)) buffer.readString(NetworkUtil.MAX_STRING_LENGTH);
	    return new MyBlockPos(x, y, z, dim); 
	}
	
	public boolean sameDim(MyBlockPos other){
		return this.dim == other.dim;
	}

	public MyBlockPos update(MyBlockPos newPos){
		x = newPos.x;
		y = newPos.y;
		z = newPos.z;
		dim = newPos.dim;
		return this;
	}
	
	public double distance(MyBlockPos other){
		int dx = this.x-other.x;
		int dy = this.y-other.y;
		int dz = this.z-other.z;
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}

	public BlockPos toBlockPos(){
		return new BlockPos(x, y, z);
	}
	
	public TileEntity getTile(){
		World world = getWorld();
		if(world!=null){
			TileEntity tile = world.getTileEntity(this.toBlockPos());
			if(tile instanceof PostPostTile){
				((PostPostTile) tile).getBases();
			}else if(tile instanceof PostPostTile){
				((BigPostPostTile) tile).getBases();
			}
			return tile;
		}else{
			return null;
		}
	}

	public MyBlockPos withX(int x) {
		return new MyBlockPos(x, y, z, dim);
	}
	
	public MyBlockPos withX(Function<Integer, Integer> xMap) {
		return this.withX(xMap.apply(x));
	}

	public MyBlockPos withY(int y) {
		return new MyBlockPos(x, y, z, dim);
	}
	
	public MyBlockPos withY(Function<Integer, Integer> yMap) {
		return this.withY(yMap.apply(y));
	}

	public MyBlockPos withZ(int z) {
		return new MyBlockPos(x, y, z, dim);
	}
	
	public MyBlockPos withZ(Function<Integer, Integer> zMap) {
		return this.withZ(zMap.apply(z));
	}
    
	public MyBlockPos getBelow() {
		return this.withY(y -> y - 1);
	}

	public MyBlockPos front(EnumFacing facing, int i) {
		return this
				.withX(x -> x + facing.getXOffset() * i)
				.withY(y -> y + facing.getYOffset() * i)
				.withZ(z -> z + facing.getZOffset() * i);
	}

	public BiomeContainer getBiome() {
		World world = getWorld();
		if (world != null) {
			return new BiomeContainer(world.getBiome(toBlockPos()));
		} else {
			return null;
		}
	}

	@Override
	public String toString(){
		return x+"|"+y+"|"+z+" in "+dim;
	}

	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof MyBlockPos)){
			return false;
		}
		MyBlockPos other = (MyBlockPos) obj;
		return other.x == this.x
			&& other.y == this.y
			&& other.z == this.z
			&& sameDim(other);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y, z, dim.getId());
	}
}