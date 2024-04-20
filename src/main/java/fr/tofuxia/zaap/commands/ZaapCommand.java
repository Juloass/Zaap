package fr.tofuxia.zaap.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import fr.tofuxia.zaap.Config;
import fr.tofuxia.zaap.Zaap;
import fr.tofuxia.zaap.attachments.ZaapMemory;
import fr.tofuxia.zaap.commands.arguments.ZaapArgument;
import fr.tofuxia.zaap.data.zaap.ZaapData;
import fr.tofuxia.zaap.data.zaap.ZaapSavedData;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

public class ZaapCommand extends Command {

    public ZaapCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        super(dispatcher, context);
    }

    public String getCommandName() {
        return "zaap";
    }

    @Override
    void buildCommand(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext pContext) {
        literal
        .then(
            Commands.literal("teleport")
            .then(
                Commands.argument("zaap", ZaapArgument.zaap(pContext))
                .executes((ctx) -> {
                    ZaapData data = ZaapArgument.getZaap(ctx, "zaap");
                                            if(data == null) {
                                                ctx.getSource().sendFailure(Component.literal("Zaap not found"));
                                                return 0;
                                            }
                                            // If on server, teleport player
                                            teleportToPos(ctx.getSource(), ctx.getSource().getPlayerOrException(),
                                                    ctx.getSource().getLevel(), data.getPos());
                                            return 1;
                })   
            )
        )
        .then(
            Commands.literal("create")
            .then(
                Commands.argument("name", ResourceLocationArgument.id())
                .executes((ctx) -> {
                    Player player = ctx.getSource().getPlayerOrException();
                                            ServerLevel level = ctx.getSource().getLevel();
                                            BlockPos pos = player.blockPosition();
                                            ResourceLocation loc = ResourceLocationArgument.getId(ctx, "name");
                                            ZaapData data = new ZaapData(loc, pos);
                                            ZaapSavedData.fromLevel(level).addZaap(data);
                                            ctx.getSource().sendSuccess(() -> Component
                                                    .literal("Zaap " + loc.toString() + " created at " + pos.toString()),
                                                    false);
                                            return 1;
                })
            ).requires((source) -> source.hasPermission(2))
        )
        .then(
            Commands.literal("remove")
            .then(
                Commands.argument("zaap", ZaapArgument.zaap(pContext))
                .executes((ctx) -> {
                    ZaapData data = ZaapArgument.getZaap(ctx, "zaap");
                                            if(data == null) {
                                                ctx.getSource().sendFailure(Component.literal("Zaap not found"));
                                                return 0;
                                            }
                                            ServerLevel level = ctx.getSource().getLevel();
                                            ZaapSavedData.fromLevel(level).removeZaap(data);
                                            ctx.getSource().sendSuccess(() -> Component
                                                    .literal("Zaap " + data.getID().toString() + " removed"), false);
                                            return 1;
                })
            ).requires((source) -> source.hasPermission(2))
        )
        .then(
            Commands.literal("list")
            .executes((ctx) -> {
                ctx.getSource().sendSuccess(() -> Component.literal("Zaaps : "), false);
                            ZaapSavedData.fromLevel(ctx.getSource().getLevel()).getZaaps().forEach((zaap) -> {
                                MutableComponent arrow = Component.literal("→");
                                ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zaap teleport " + zaap.getID().toString());
                                Style arrowStyle = arrow.getStyle();
                                arrowStyle = arrowStyle.withClickEvent(click);
                                arrowStyle = arrowStyle.withColor(0x00FF00);
                                arrow.setStyle(arrowStyle);
                                ctx.getSource().sendSuccess(() -> (arrow).append(Component.literal(" " + zaap.getID().toString())), false);
                            });
                            return 1;
            })
            .requires((source) -> source.hasPermission(2))
        )
        .then(
            Commands.literal("memory")
                .requires((source) -> {
                    return Config.enableDiscover;
                })
                .then(
                    Commands.literal("list")
                    .executes((ctx) -> {
                        Player player = ctx.getSource().getPlayerOrException();
                        ZaapMemory memory = player.getData(Zaap.ZAAP_MEMORY);
                        memory.getZaapLocations().forEach(location -> {
                            ctx.getSource().sendSuccess(() -> Component.literal(location.toString()), false);
                        });
                        return 1;
                    })
                )
                .then(
                    Commands.literal("clear")
                    .executes((ctx) -> {
                        Player player = ctx.getSource().getPlayerOrException();
                        ZaapMemory memory = player.getData(Zaap.ZAAP_MEMORY);
                        memory.getZaapLocations().clear();
                        ctx.getSource().sendSuccess(() -> Component.literal("Zaap memory cleared"), false);
                        return 1;
                    }).requires((source) -> source.hasPermission(2))
                )
                .then(
                    Commands.literal("add")
                    .then(
                        Commands.argument("zaap", ZaapArgument.zaap(pContext))
                        .executes((ctx) -> {
                            Player player = ctx.getSource().getPlayerOrException();
                            ZaapMemory memory = player.getData(Zaap.ZAAP_MEMORY);
                            ZaapData data = ZaapArgument.getZaap(ctx, "zaap");
                            if(data == null) {
                                ctx.getSource().sendFailure(Component.literal("Zaap not found"));
                                return 0;
                            }
                            memory.addZaapLocation(data.getID());
                            ctx.getSource().sendSuccess(() -> Component.literal("Zaap " + data.getID().toString() + " added to memory"), false);
                            return 1;
                        })
                    ).requires((source) -> source.hasPermission(2))
                )
                .then(
                    Commands.literal("remove")
                    .then(
                        Commands.argument("zaap", ZaapArgument.zaap(pContext))
                        .executes((ctx) -> {
                            Player player = ctx.getSource().getPlayerOrException();
                            ZaapMemory memory = player.getData(Zaap.ZAAP_MEMORY);
                            ZaapData data = ZaapArgument.getZaap(ctx, "zaap");
                            if(data == null) {
                                ctx.getSource().sendFailure(Component.literal("Zaap not found"));
                                return 0;
                            }
                            memory.removeZaapLocation(data.getID());
                            ctx.getSource().sendSuccess(() -> Component.literal("Zaap " + data.getID().toString() + " removed from memory"), false);
                            return 1;
                        })
                    ).requires((source) -> source.hasPermission(2))
                )
        )
        .executes(context -> {
            // If no arg, open Zaap GUI to player
            // TODO: Open Zaap GUI
            return 1;
        });
    }

    private static int teleportToPos(CommandSourceStack pSource, Player player, ServerLevel pLevel,
            BlockPos pPosition) {
        double pX = pPosition.getX(), pY = pPosition.getY(), pZ = pPosition.getZ();

        EntityTeleportEvent.TeleportCommand event = EventHooks.onEntityTeleportCommand(player, pX, pY, pZ);
        if (!event.isCanceled()) {
            pX = event.getTargetX();
            pY = event.getTargetY();
            pZ = event.getTargetZ();
            player.teleportTo(pX, pY, pZ);
        }
        return 1;
    }

}
