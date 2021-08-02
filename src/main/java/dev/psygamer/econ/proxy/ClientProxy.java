package dev.psygamer.econ.proxy;

import dev.psygamer.econ.banking.BankAccountHandler;
import dev.psygamer.econ.gui.widgets.PlayerHead;

public class ClientProxy implements IProxy {
	
	@Override
	public void preparePlayerHeadWidget() {
		PlayerHead.preparePlayerData(BankAccountHandler.bankAccountPlayerNames.keySet());
	}
}
