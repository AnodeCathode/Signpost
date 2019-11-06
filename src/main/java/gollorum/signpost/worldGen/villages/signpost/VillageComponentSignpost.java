package gollorum.signpost.worldGen.villages.signpost;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import gollorum.signpost.Signpost;
import gollorum.signpost.util.MyBlockPos;
import gollorum.signpost.util.code.MinecraftDependent;
import gollorum.signpost.worldGen.villages.GenerateStructureHelper;
import gollorum.signpost.worldGen.villages.VillageLibrary;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.VillagePieces;
import net.minecraft.world.gen.feature.structure.VillagePieces.Start;
import net.minecraft.world.gen.feature.structure.VillagePieces.Village;

@MinecraftDependent
public class VillageComponentSignpost extends Village{
	
	private boolean built = false;
	private Start start;
	private EnumFacing facing;
	
	public VillageComponentSignpost(){
		super();
	}
		
	public VillageComponentSignpost(Start start, int type, MutableBoundingBox boundingBox, EnumFacing facing){
		super(start, type);
		this.boundingBox = boundingBox;
		this.start = start;
		this.facing = facing;
	}
	
	@Nullable
	public static Village buildComponent(VillagePieces.Start startPiece, List<StructurePiece> pieces, Random random, int x, int y, int z, EnumFacing facing, int type) {
		MutableBoundingBox boundingBox = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 3, 3, 3, facing);
		if (canVillageGoDeeper(boundingBox) && findIntersecting(pieces, boundingBox) == null) {
			return new VillageComponentSignpost(startPiece, type, boundingBox, facing.getOpposite());
		}
		return null;
	}

	@Override
	public boolean addComponentParts(IWorld iworld, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos) {
		if(built || start==null){
			return true;
		}else{
			built = true;
		}
		int x = (this.boundingBox.minX + this.boundingBox.maxX)/2;
		int z = (this.boundingBox.minZ + this.boundingBox.maxZ)/2;
		BlockPos postPos;
		World world = iworld.getWorld();
		try{
			postPos = GenerateStructureHelper.getInstance().getTopSolidOrLiquidBlock(world, new BlockPos(x, 0, z));
		}catch(Exception e) {
			postPos = new BlockPos(x, this.boundingBox.maxY, z);
		}
		world.setBlockState(postPos, Signpost.proxy.blockHandler.post_oak.getDefaultState());
		world.setBlockState(postPos.add(0, 1, 0), Signpost.proxy.blockHandler.post_oak.getDefaultState());
		if (world.getBlockState(postPos.add(0, -1, 0)).getMaterial().isLiquid()) {
			IBlockState block = this.getBiomeSpecificBlockState(Blocks.OAK_PLANKS.getDefaultState());
			world.setBlockState(postPos.add(0, -1, 0), block);
			world.setBlockState(postPos.add(-1, -1, -1), block);
			world.setBlockState(postPos.add(-1, -1, 0), block);
			world.setBlockState(postPos.add(-1, -1, 1), block);
			world.setBlockState(postPos.add(0, -1, -1), block);
			world.setBlockState(postPos.add(0, -1, 1), block);
			world.setBlockState(postPos.add(1, -1, -1), block);
			world.setBlockState(postPos.add(1, -1, 0), block);
			world.setBlockState(postPos.add(1, -1, 1), block);
		}
		MutableBoundingBox villageBox = start.getBoundingBox();
		MyBlockPos villagePos = new MyBlockPos(world, villageBox.minX, 0, villageBox.minZ);
		MyBlockPos blockPos = new MyBlockPos(world, postPos.add(0, 1, 0));
		VillageLibrary.getInstance().putSignpost(villagePos, blockPos, optimalRot(facing));
		return true;
	}

	private double optimalRot(EnumFacing facing) {
		switch(facing){
			case NORTH:
				return 0;
			case EAST:
				return 1.5*Math.PI;
			case SOUTH:
				return Math.PI;
			case WEST:
				return 0.5*Math.PI;
			default:
				return 0;
		}
	}
}
