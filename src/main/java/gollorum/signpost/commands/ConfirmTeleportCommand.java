package gollorum.signpost.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import gollorum.signpost.SPEventHandler;
import gollorum.signpost.management.PostHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;

public class ConfirmTeleportCommand implements SignpostCommand {

	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
			Commands.literal("signpostconfirm")
			.requires(source -> {
				try {
					return source.asPlayer() != null;
				} catch (CommandSyntaxException e) {
					return false;
				}
			})
			.executes(ctx -> execute(ctx.getSource().asPlayer()))
		);
	}

	public int execute(final EntityPlayerMP sender) {
		SPEventHandler.scheduleTask(() -> PostHandler.confirm((EntityPlayerMP) sender), 0);
		return 1;
	}

}