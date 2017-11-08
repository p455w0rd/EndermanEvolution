package p455w0rd.endermanevo.items;

import p455w0rd.endermanevo.init.ModBlocks;

/**
 * @author p455w0rd
 *
 */
public class ItemSkullEnderman2 extends ItemSkullBase {

	public ItemSkullEnderman2() {
		super("enderman2", ModBlocks.ENDERMAN2_SKULL);
	}

	@Override
	public boolean isEndermanSkull() {
		return true;
	}

}