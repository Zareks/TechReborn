package techreborn.client.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import reborncore.client.gui.slots.BaseSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.common.container.RebornContainer;
import techreborn.tiles.TileAesu;

public class ContainerAESU extends RebornContainer {

	public int euOut;
	public int storedEu;
	public int euChange;
	EntityPlayer player;
	TileAesu tile;

	public ContainerAESU(TileAesu tileaesu, EntityPlayer player) {
		tile = tileaesu;
		this.player = player;
		addPlayersHotbar(player);
		addPlayersInventory(player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int i = 0; i < this.listeners.size(); i++) {
			IContainerListener IContainerListener = this.listeners.get(i);
			if (this.euOut != tile.getMaxOutput()) {
				IContainerListener.sendProgressBarUpdate(this, 0, (int) tile.getMaxOutput());
			}
			if (this.storedEu != tile.getEnergy()) {
				IContainerListener.sendProgressBarUpdate(this, 1, (int) tile.getEnergy());
			}
			if (this.euChange != tile.getEuChange() && tile.getEuChange() != -1) {
				IContainerListener.sendProgressBarUpdate(this, 2, (int) tile.getEuChange());
			}
		}
	}

	@Override
	public void addListener(IContainerListener crafting) {
		super.addListener(crafting);
		crafting.sendProgressBarUpdate(this, 0, (int) tile.getMaxOutput());
		crafting.sendProgressBarUpdate(this, 1, (int) tile.getEnergy());
		crafting.sendProgressBarUpdate(this, 2, (int) tile.getEuChange());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int value) {
		if (id == 0) {
			this.euOut = value;
		} else if (id == 1) {
			this.storedEu = value;
		} else if (id == 2) {
			this.euChange = value;
		}
	}

}
