package net.ditto.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.ditto.ability.ShikaiType;
import net.ditto.levelling.LevelSystem;
import net.ditto.levelling.PlayerLevelData;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;

public class CurseCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("curse")
                .requires(source -> source.hasPermissionLevel(2)) // Requires OP

                // Subcommand: /curse add ...
                .then(CommandManager.literal("add")
                        .then(CommandManager.literal("xp")
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(CurseCommand::runAddXp)))
                        .then(CommandManager.literal("level")
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(CurseCommand::runAddLevel)))
                )

                // Subcommand: /curse set ...
                .then(CommandManager.literal("set")
                        .then(CommandManager.literal("level")
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(CurseCommand::runSetLevel)))
                        .then(CommandManager.literal("xp")
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(CurseCommand::runSetXp)))

                        // NEW: ... shikai <type>
                        .then(CommandManager.literal("shikai")
                                .then(CommandManager.argument("type", StringArgumentType.word())
                                        .suggests((context, builder) -> CommandSource.suggestMatching(
                                                Arrays.stream(ShikaiType.values()).map(Enum::name), builder))
                                        .executes(CurseCommand::runSetShikai)))
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

    // NEW: Set Shikai Handler
    private static int runSetShikai(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String typeName = StringArgumentType.getString(context, "type");

        try {
            ShikaiType type = ShikaiType.valueOf(typeName.toUpperCase());

            if (player instanceof PlayerLevelData levelData) {
                levelData.ditto$setShikaiType(type);
                levelData.ditto$setForm(ShikaiType.Form.SEALED); // Reset to base form when changing swords
                levelData.ditto$syncAbilities();

                context.getSource().sendFeedback(() -> Text.literal("§bSet Shikai to " + type.name()), false);
            }
        } catch (IllegalArgumentException e) {
            context.getSource().sendError(Text.literal("Invalid Shikai Type! Use tab-complete to see options."));
        }
        return 1;
    }
}
