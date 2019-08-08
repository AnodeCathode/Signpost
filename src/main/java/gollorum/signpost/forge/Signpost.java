package gollorum.signpost.forge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import gollorum.signpost.core.SignpostCore;

@Mod(modid = SignpostCore.MODID, version = SignpostCore.VERSION, name = SignpostCore.NAME)
public class Signpost {

	@Instance
	public static Signpost instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		SignpostCore.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		SignpostCore.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		SignpostCore.postInit();
	}
	
}
