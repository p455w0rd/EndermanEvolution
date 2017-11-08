package p455w0rd.endermanevo.items;

import p455w0rd.endermanevo.init.ModBlocks;

/**
 * @author p455w0rd
 *
 */
public class ItemSkullEnderman extends ItemSkullBase {

	public ItemSkullEnderman() {
		super("enderman", ModBlocks.ENDERMAN_SKULL);
	}

	@Override
	public boolean isEndermanSkull() {
		return true;
	}

}