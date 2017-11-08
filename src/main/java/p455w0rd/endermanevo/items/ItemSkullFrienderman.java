package p455w0rd.endermanevo.items;

import p455w0rd.endermanevo.init.ModBlocks;

/**
 * @author p455w0rd
 *
 */
public class ItemSkullFrienderman extends ItemSkullBase {

	public ItemSkullFrienderman() {
		super("frienderman", ModBlocks.FRIENDERMAN_SKULL);
	}

	@Override
	public boolean isEndermanSkull() {
		return true;
	}

}