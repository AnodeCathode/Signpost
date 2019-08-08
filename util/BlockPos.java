package gollorum.signpost.core.util;

public class BlockPos {
	
	private int x, y, z;
	
	public BlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlockPos(BlockPos toCopy) {
		this(toCopy.x, toCopy.y, toCopy.z);
	}
	
	public BlockPos(Vector3 vector) {
		this(Math.round(vector.getX()), Math.round(vector.getY()), Math.round(vector.getZ()));
	}

	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public void setZ(int z) { this.z = z; }

	public int getX() { return this.x; }
	public int getY() { return this.y; }
	public int getZ() { return this.z; }
	
	public BlockPos add(BlockPos other) {
		return new BlockPos(x + other.x, y + other.y, z + other.z);
	}
	
	public BlockPos substract(BlockPos other) {
		return new BlockPos(x - other.x, y - other.y, z - other.z);
	}
}
