package moe.barian.inventoryrollback.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import moe.barian.inventoryrollback.data.LogType;
import moe.barian.inventoryrollback.events.EventLogs;
import moe.barian.inventoryrollback.gui.MainMenuProvider;
import moe.barian.inventoryrollback.gui.PlayerMenuProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@net.neoforged.fml.common.EventBusSubscriber(modid = "inventoryrollbackplus", bus = net.neoforged.fml.common.EventBusSubscriber.Bus.GAME)
public class IRCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("ir")
                .requires(source -> source.hasPermission(2)) // Equivalent to OP
                .then(Commands.literal("restore")
                        .executes(IRCommands::executeRestoreAll)
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(IRCommands::executeRestore)))
                .then(Commands.literal("forcebackup")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(IRCommands::executeForceBackup)))
                .then(Commands.literal("help")
                        .executes(IRCommands::executeHelp))
        );
    }

    private static int executeRestoreAll(CommandContext<CommandSourceStack> context) {
        try {
            CommandSourceStack source = context.getSource();
            if (source.getEntity() instanceof ServerPlayer execPlayer) {
                execPlayer.sendSystemMessage(Component.literal("Opening player backups list..."));
                execPlayer.openMenu(new MainMenuProvider(1));
            } else {
                source.sendSuccess(() -> Component.literal("Only players can use this command."), false);
            }
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error executing command."));
            e.printStackTrace();
        }
        return 1;
    }

    private static int executeRestore(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
            CommandSourceStack source = context.getSource();
            
            if (source.getEntity() instanceof ServerPlayer execPlayer) {
                execPlayer.sendSystemMessage(Component.literal("Opening restore menu for " + targetPlayer.getName().getString() + "..."));
                execPlayer.openMenu(new PlayerMenuProvider(targetPlayer.getUUID(), targetPlayer.getName().getString()));
            } else {
                source.sendSuccess(() -> Component.literal("Only players can use this command."), false);
            }
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error executing command."));
            e.printStackTrace();
        }
        return 1;
    }

    private static int executeForceBackup(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
            EventLogs.savePlayerInventory(targetPlayer, LogType.FORCE, "Forced by admin");
            context.getSource().sendSuccess(() -> Component.literal("Successfully created forced backup for " + targetPlayer.getName().getString()), true);
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error executing command."));
        }
        return 1;
    }

    private static int executeHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal("§6InventoryRollbackPlus Commands:"), false);
        source.sendSuccess(() -> Component.literal("§e/ir restore <player> §7- Open a menu to view backups"), false);
        source.sendSuccess(() -> Component.literal("§e/ir forcebackup <player> §7- Create a manual backup"), false);
        return 1;
    }
}
