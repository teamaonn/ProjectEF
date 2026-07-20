package moze_intel.projecte.integration;

import moze_intel.projecte.api.item_handlers.IItemHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class IntegrationHelper {

	public static final String TRINKETS_MODID = "trinkets";
	public static final String EMI_MODID = "emi";
	public static final String JEI_MODID = "jei";
	public static final String REI_MODID = "roughlyenoughitems";

	/**
	 * Called during common mod init to hook up any loaded optional integrations.
	 */
	public static void init() {
		//TODO - Fabric port: hook up the trinkets integration here once it is implemented (phase 6)
	}

	/**
	 * Gets the accessory (trinkets) inventory of the given player, or null if the trinkets mod is not loaded.
	 */
	@Nullable
	public static IItemHandler getCuriosInventory(Player player) {
		//TODO - Fabric port: return the trinkets inventory once the integration is implemented (phase 6)
		return null;
	}

	/**
	 * Registers the accessory (trinkets) capability for the given item when the trinkets mod is loaded.
	 */
	public static void registerCuriosCapability(Item item) {
		//TODO - Fabric port: register with the trinkets api once the integration is implemented (phase 6)
	}
}
