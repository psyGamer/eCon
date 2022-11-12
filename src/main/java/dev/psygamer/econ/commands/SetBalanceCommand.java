package dev.psygamer.econ.commands;

import dev.psygamer.econ.ECon;
import dev.psygamer.econ.banking.BankAccountHandler;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;

public class SetBalanceCommand {
	
	public static void register(final CommandDispatcher<CommandSource> dispatcher) {
		final LiteralArgumentBuilder<CommandSource> setBalanceCommand
				= Commands.literal("setbalance")
				.requires((commandSource) -> commandSource.hasPermission(2))
				.then(Commands.argument("target", EntityArgument.player())
						.then(Commands.argument("balance", LongArgumentType.longArg(0))
								.executes(SetBalanceCommand::handleCommand))
				);
		
		
		dispatcher.register(setBalanceCommand);
	}
	
	private static int handleCommand(final CommandContext<CommandSource> context) throws CommandSyntaxException {
		if (context.getSource().getServer().isSingleplayer()) {
			context.getSource().getEntity().sendMessage(ECon.COMMAND_DISABLED_MESSAGE, context.getSource().getEntity().getUUID());
			
			return 0;
		}
		
		final ServerPlayerEntity target = EntityArgument.getPlayer(context, "target");
		final long balance = LongArgumentType.getLong(context, "balance");
		
		try {
			BankAccountHandler.setBalance(target.getUUID(), balance);
			
			context.getSource().getServer().getPlayerList().broadcastMessage(
					new TranslationTextComponent("econ.command.setBalance", target.getDisplayName().getString(), TextFormatting.BOLD + (balance + ECon.MONEY_SYMBOL.getString())),
					ChatType.CHAT,
					context.getSource().getEntity().getUUID()
			);
			
		} catch (final NullPointerException ex) {
			return 0;
		}
		
		return 1;
	}
}
