package dev.psygamer.econ.proxy;

import dev.psygamer.econ.banking.BankAccountHandler;
import dev.psygamer.econ.client.screen.widgets.PlayerHead;

public class ClientProxy implements IProxy {
	
	@Override
	public void preparePlayerHeadWidget() {
		PlayerHead.preparePlayerData(BankAccountHandler.bankAccountPlayerNames.keySet());
	}
}
