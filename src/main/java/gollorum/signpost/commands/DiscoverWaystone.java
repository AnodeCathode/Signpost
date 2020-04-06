package gollorum.signpost.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import gollorum.signpost.commands.arguments.WaystoneArgument;
import gollorum.signpost.management.ConfigHandler;
import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.NetworkHandler;
import gollorum.signpost.network.messages.ChatMessage;
import gollorum.signpost.util.BaseInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class DiscoverWaystone implements SignpostCommand {

	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			Commands.literal("discoverwaystone")
			.requires(source -> source.hasPermissionLevel(2))
			.then(Commands.argument("name", new WaystoneArgument())
				.then(Commands.argument("player", EntityArgument.singleEntity()))
				.executes(this::execute)
			).executes(this::execute)
		);
	}

	private int execute(CommandContext<CommandSource> ctx) {
		
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws PlayerNotFoundException, CommandException {
		if(sender instanceof EntityPlayerMP){
			EntityPlayerMP player = (EntityPlayerMP) sender;
			if (args.length < 1){
				throw new WrongUsageException(getUsage(null), new Object[0]);
			}
			String waystoneName = "";
			String playerName = null;
			for(String now: args){
				waystoneName+=" "+now;
			}
			waystoneName = waystoneName.substring(1);
			BaseInfo base = PostHandler.getNativeWaystones().getByName(waystoneName);
			if(base==null && args.length>1){
				waystoneName="";
				playerName = args[args.length-1];
				for(int i=0; i<args.length-1; i++){
					waystoneName+=" "+args[i];
				}
				waystoneName = waystoneName.substring(1);
				base = PostHandler.getNativeWaystones().getByName(waystoneName);
			}
			EntityPlayerMP target = player;
			if(playerName!=null){
				target = getPlayer(server, sender, playerName);
			}
			if(base==null){
				String[] keys = {"<Waystone>"};
				String[] replacement = {waystoneName};
				NetworkHandler.netWrap.sendTo(new ChatMessage("signpost.waystoneNotFound", keys, replacement), player);
			}else{
				PostHandler.addDiscovered(target.getUniqueID(), base);
				String[] keys = {"<Waystone>"};
				String[] replacement = {waystoneName};
				NetworkHandler.netWrap.sendTo(new ChatMessage("signpost.discovered", keys, replacement), target);
			}
		}
	}
	
	public int getRequiredPermissionLevel(){
		return 2;
	}

	@Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender){
    	return sender instanceof EntityPlayerMP && ConfigHandler.isOp((EntityPlayerMP)sender);
    }

}