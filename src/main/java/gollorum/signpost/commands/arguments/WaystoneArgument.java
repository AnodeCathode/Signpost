package gollorum.signpost.commands.arguments;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import gollorum.signpost.management.PostHandler;
import gollorum.signpost.network.messages.ChatMessage;
import gollorum.signpost.util.BaseInfo;
import net.minecraft.command.ISuggestionProvider;

public class WaystoneArgument implements ArgumentType<BaseInfo> {

    public static final DynamicCommandExceptionType INVALID_WAYSTONE_EXCEPTION = new DynamicCommandExceptionType((waystoneName) -> {
		String[] keys = {"<Waystone>"};
		String[] replacement = {(String) waystoneName};
        return new ChatMessage("signpost.waystoneNotFound", keys, replacement);
    });

	@Override
	public <S> BaseInfo parse(StringReader reader) throws CommandSyntaxException {
		String waystoneName = reader.readString();
		BaseInfo base = PostHandler.getNativeWaystones().getByName(waystoneName);
		if(base != null) return base;
		throw INVALID_WAYSTONE_EXCEPTION.create(waystoneName);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		return ISuggestionProvider.suggest(PostHandler.getNativeWaystones().select(b -> b.getName()), builder);
    }
	
}