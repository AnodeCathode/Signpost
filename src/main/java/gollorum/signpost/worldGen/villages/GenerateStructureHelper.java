package gollorum.signpost.worldGen.villages;

import gollorum.signpost.util.MyBlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class GenerateStructureHelper {

	private static final GenerateStructureHelper INSTANCE = new GenerateStructureHelper();
	
	public static GenerateStructureHelper getInstance(){
		return INSTANCE;
	}
	
	private GenerateStructureHelper(){}
	
	public BlockPos getTopSolidOrLiquidBlock(IWorld world, BlockPos pos){
		BlockPos ret = pos;
		IBlockState state = world.getBlockState(ret);
		while(state.getMaterial().isLiquid() || state.getMaterial().isSolid()){
			ret = ret.add(0, 1, 0);
			state = world.getBlockState(ret);
		}
		return ret;
	}
    
	public MyBlockPos getTopSolidOrLiquidBlock(MyBlockPos pos) {
		BlockPos blockPos = getTopSolidOrLiquidBlock(pos.getWorld(), pos.toBlockPos());
		return pos.fromNewPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}

}