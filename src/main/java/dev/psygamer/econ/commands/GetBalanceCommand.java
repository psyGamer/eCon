package dev.psygamer.econ.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.psygamer.econ.banking.BankAccountHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class GetBalanceCommand {
	
	public static void register(final CommandDispatcher<CommandSource> dispatcher) {
		final LiteralArgumentBuilder<CommandSource> getBalanceCommand
				= Commands.literal("balance")
				.then(Commands.argument("target", EntityArgument.player())
						.executes(GetBalanceCommand::handleCommand)
				);
		
		
		dispatcher.register(getBalanceCommand);
	}
	
	private static int handleCommand(final CommandContext<CommandSource> context) throws CommandSyntaxException {
		final ServerPlayerEntity target = EntityArgument.getPlayer(context, "target");
		
		try {
			final long balance = BankAccountHandler.getBalance(target.getUUID());
			
			context.getSource().getEntity().sendMessage(
					new StringTextComponent("Balance of " + target.getDisplayName().getString() + ": " + balance),
					context.getSource().getEntity().getUUID()
			);
			
		} catch (final NullPointerException ex) {
			return 0;
		}
		
		return 1;
	}
}