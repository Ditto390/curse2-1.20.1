package net.ditto.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.ditto.levelling.LevelSystem;
import net.ditto.levelling.PlayerLevelData;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class CurseCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("curse")
                .requires(source -> source.hasPermissionLevel(2)) // Requires OP

                // Subcommand: /curse add ...
                .then(CommandManager.literal("add")
                        // ... xp <amount>
                        .then(CommandManager.literal("xp")
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(CurseCommand::runAddXp)))
                        // ... level <amount>
                        .then(CommandManager.literal("level")
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(CurseCommand::runAddLevel)))
                )

                // Subcommand: /curse set ...
                .then(CommandManager.literal("set")
                        // ... level <amount>
                        .then(CommandManager.literal("level")
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(CurseCommand::runSetLevel)))
                        // ... xp <amount>
                        .then(CommandManager.literal("xp")
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(CurseCommand::runSetXp)))
                )
        );
    }

    private static int runAddXp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        if (player instanceof PlayerLevelData levelData) {
            boolean leveledUp = LevelSystem.addXp(levelData, amount);
            levelData.ditto$syncLevel();
            context.getSource().sendFeedback(() -> Text.literal("Added " + amount + " XP"), false);
            if (leveledUp) {
                player.sendMessage(Text.literal("§aLevel Up! Now Level " + levelData.ditto$getLevel()), true);
            }
        }
        return 1;
    }

    private static int runAddLevel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        if (player instanceof PlayerLevelData levelData) {
            int newLevel = levelData.ditto$getLevel() + amount;
            levelData.ditto$setLevel(newLevel);
            levelData.ditto$syncLevel();
            context.getSource().sendFeedback(() -> Text.literal("Added " + amount + " levels. Total: " + newLevel), false);
        }
        return 1;
    }

    private static int runSetLevel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        if (player instanceof PlayerLevelData levelData) {
            levelData.ditto$setLevel(amount);
            levelData.ditto$syncLevel();
            context.getSource().sendFeedback(() -> Text.literal("Set level to " + amount), false);
        }
        return 1;
    }

    private static int runSetXp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        if (player instanceof PlayerLevelData levelData) {
            levelData.ditto$setCurrentXp(amount);
            levelData.ditto$syncLevel();
            context.getSource().sendFeedback(() -> Text.literal("Set XP to " + amount), false);
        }
        return 1;
    }
}
