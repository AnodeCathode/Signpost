package gollorum.signpost;

import java.io.File;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gollorum.signpost.commands.ConfirmTeleportCommand;
import gollorum.signpost.commands.DiscoverWaystone;
import gollorum.signpost.commands.GetSignpostCount;
import gollorum.signpost.commands.GetWaystoneCount;
import gollorum.signpost.commands.ListAllWaystones;
import gollorum.signpost.commands.SetSignpostCount;
import gollorum.signpost.commands.SetWaystoneCount;
import gollorum.signpost.gui.SignGuiHandler;
import gollorum.signpost.management.ConfigHandler;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.management.PostHandler.TeleportInformation;
import gollorum.signpost.util.BigBaseInfo;
import gollorum.signpost.util.DoubleBaseInfo;
import gollorum.signpost.util.MyBlockPos;
import gollorum.signpost.util.StonedHashSet;
import gollorum.signpost.util.collections.Lurchpaerchensauna;
import gollorum.signpost.worldGen.villages.NameLibrary;
import gollorum.signpost.worldGen.villages.VillageLibrary;
import net.minecraft.command.Commands;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.NetworkRegistry;

@Mod(Signpost.MODID)
@EventBusSubscriber(bus = Bus.MOD)
public class Signpost {
	
	public static Signpost instance;
	public static final String MODID = "signpost";

	public static final int GuiBaseID = 0;
	public static final int GuiPostID = 1;
	public static final int GuiBigPostID = 2;
	public static final int GuiPostBrushID = 3;
	public static final int GuiPostRotationID = 4;

	public static File configFile;
	public static File configFolder;
	
	public static NBTTagCompound saveFile;
	public static final Logger LOG = LogManager.getLogger(MODID); 
	
	public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	
	private static MinecraftServer dedicatedServerInstance;
	public static MinecraftServer getServerInstance() { return dedicatedServerInstance; }
	
	@SubscribeEvent
	public void setup(FMLCommonSetupEvent event) {
		configFolder = new File(event.getModConfigurationDirectory() + "/" + MODID);
		configFolder.mkdirs();
		configFile = new File(configFolder.getPath(), MODID + ".cfg");
		ConfigHandler.init(configFile);
		NameLibrary.init(configFolder.getPath()); 
		proxy.preInit();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new SignGuiHandler());
		proxy.init();
		ConfigHandler.postInit();
		PostHandler.setNativeWaystones(new StonedHashSet());
		PostHandler.setPosts(new Lurchpaerchensauna<MyBlockPos, DoubleBaseInfo>());
		PostHandler.setBigPosts(new Lurchpaerchensauna<MyBlockPos, BigBaseInfo>());
		PostHandler.awaiting = new Lurchpaerchensauna<UUID, TeleportInformation>();
        
	}

	@SubscribeEvent
	public void serverAboutToStart(FMLServerAboutToStartEvent e){
		dedicatedServerInstance = e.getServer();
		PostHandler.init();
		VillageLibrary.init();
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent e) {
		registerCommands(e.getServer().getCommandManager());
		ConfigHandler.init(configFile);
	}
	
	private void registerCommands(Commands manager) {
		manager.registerCommand(new ConfirmTeleportCommand());
		manager.registerCommand(new GetWaystoneCount());
		manager.registerCommand(new GetSignpostCount());
		manager.registerCommand(new SetWaystoneCount());
		manager.registerCommand(new SetSignpostCount());
		manager.registerCommand(new DiscoverWaystone());
		manager.registerCommand(new ListAllWaystones());
	}

	
}