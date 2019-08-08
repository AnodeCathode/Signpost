package gollorum.signpost.forge;

import net.minecraft.block.material.Material;

public enum MaterialType {
	
	WOOD(Material.wood),
	ROCK(Material.rock);
	
	private Material target;
	
	private MaterialType(Material target) {
		this.target = target;
	}
	
	public Material GetTarget() {
		return target;
	}

}
