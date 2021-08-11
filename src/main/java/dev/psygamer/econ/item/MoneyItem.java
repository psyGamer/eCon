package dev.psygamer.econ.item;

import dev.psygamer.econ.ECon;
import dev.psygamer.econ.setup.TabRegistry;
import dev.psygamer.econ.banking.BankAccountHandler;

import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.stats.Stats;
import net.minecraft.entity.player.PlayerEntity;

public class MoneyItem extends Item {
	
	private final long value;
	
	public MoneyItem(final long value) {
		super(new Item.Properties().tab(TabRegistry.TAB_ECON));
		
		this.value = value;
	}
	
	@Override
	public ActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
		final ItemStack itemStack = player.getItemInHand(hand);
		final boolean useEntireStack = player.isCrouching();
		
		player.awardStat(Stats.ITEM_USED.get(this));
		player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.05f, 2f);
		player.displayClientMessage(
				new TranslationTextComponent(useEntireStack ? "econ.moneyItem.useStack" : "econ.moneyItem.useSingle", useEntireStack ? String.valueOf(itemStack.getCount()) : "", this.value + ECon.MONEY_SYMBOL.getString())
						.withStyle(TextFormatting.GREEN),
				
				true
		);
		
		if (!world.isClientSide()) {
			BankAccountHandler.modifyAccountBalance(player.getUUID(), useEntireStack ? itemStack.getCount() * this.value : this.value);
		}
		
		if (!player.abilities.instabuild) {
			if (useEntireStack) {
				itemStack.shrink(itemStack.getCount());
			} else {
				itemStack.shrink(1);
			}
		}
		
		return ActionResult.sidedSuccess(itemStack, world.isClientSide());
	}
}
