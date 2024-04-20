package fr.tofuxia.zaap.commands.arguments;

import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import fr.tofuxia.zaap.Zaap;
import fr.tofuxia.zaap.data.zaap.ZaapData;
import fr.tofuxia.zaap.data.zaap.ZaapSavedData;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData.Factory;

public class ZaapArgument implements ArgumentType<ResourceLocation> {

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return ResourceLocation.read(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (!(context.getSource() instanceof SharedSuggestionProvider))
            return Suggestions.empty();
        S source = context.getSource();
        if (source instanceof Player) {
            Player player = (Player) source;
            ServerLevel level = (ServerLevel) player.level();
            ZaapSavedData data = level.getDataStorage().computeIfAbsent(
                    new Factory<>(ZaapSavedData::create, (tag) -> ZaapSavedData.load(tag)),
                    ZaapSavedData.SAVE_KEY);
            return SharedSuggestionProvider.suggestResource(data.getZaapIds(), builder);
        }
        return Suggestions.empty();
    }

    public static ZaapArgument zaap(CommandBuildContext pContext) {
        return new ZaapArgument();
    }

    public static ZaapData getZaap(CommandContext<CommandSourceStack> context, String key) {
        ZaapSavedData data = context.getSource().getLevel().getDataStorage().computeIfAbsent(
                new Factory<>(ZaapSavedData::create, (tag) -> ZaapSavedData.load(tag)),
                ZaapSavedData.SAVE_KEY);
        ZaapData zaap = data.getZaap(context.getArgument(key, ResourceLocation.class));
        return zaap;
    }

}
