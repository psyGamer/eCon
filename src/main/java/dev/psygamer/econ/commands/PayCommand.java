package dev.psygamer.econ.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.psygamer.econ.banking.BankAccountHandler;
import dev.psygamer.econ.banking.Transaction;
import dev.psygamer.econ.banking.TransactionHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class PayCommand {
	
	public static void register(final CommandDispatcher<CommandSource> dispatcher) {
		final LiteralArgumentBuilder<CommandSource> payCommand
				= Commands.literal("pay")
				.then(Commands.argument("target", EntityArgument.player())
						.then(Commands.argument("amount", LongArgumentType.longArg(1))
								.executes(PayCommand::handleCommand))
				);
		
		
		dispatcher.register(payCommand);
	}
	
	private static int handleCommand(final CommandContext<CommandSource> context) throws CommandSyntaxException {
		final ServerPlayerEntity target = EntityArgument.getPlayer(context, "target");
		final long amount = LongArgumentType.getLong(context, "amount");
		
		try {
			final Entity player = context.getSource().getEntity();
			
			if (amount > BankAccountHandler.getBalance(player.getUUID())) {
				player.sendMessage(
						new StringTextComponent(TextFormatting.RED + "You don't have that much money!"),
						player.getUUID()
				);
				player.playSound(SoundEvents.NOTE_BLOCK_PLING, 0.05f, 0.1f);
				
				return 0;
			}
			
			TransactionHandler.processTransaction(
					new Transaction(context.getSource().getEntity().getUUID(), target.getUUID(), amount)
			);
			
			player.sendMessage(
					new StringTextComponent(TextFormatting.GREEN + "You sent " + TextFormatting.GREEN + TextFormatting.BOLD + amount + TextFormatting.GREEN + "\u20AC to " + target.getDisplayName().getString()),
					player.getUUID()
			);
			target.sendMessage(
					new StringTextComponent(TextFormatting.GREEN + "You received " + TextFormatting.GREEN + TextFormatting.BOLD + amount + TextFormatting.GREEN + "\u20AC from " + player.getDisplayName().getString()),
					player.getUUID()
			);
			
			player.playSound(SoundEvents.NOTE_BLOCK_PLING, 0.05f, 2f);
			target.playSound(SoundEvents.NOTE_BLOCK_PLING, 0.05f, 2f);
		} catch (final NullPointerException ex) {
			return 0;
		}
		
		return 1;
	}
}
