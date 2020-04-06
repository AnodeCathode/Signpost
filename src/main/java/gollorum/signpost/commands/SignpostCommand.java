package gollorum.signpost.commands;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;

public interface SignpostCommand {
	public void register(CommandDispatcher<CommandSource> dispatcher);
}
