package moze_intel.projecte.integration.trinkets;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import moze_intel.projecte.api.item_handlers.IItemHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

/**
 * Trinkets specific bridge. This class is only classloaded once the trinkets mod is confirmed present (the calls are
 * guarded in {@link moze_intel.projecte.integration.IntegrationHelper}), so ProjectE keeps loading fine without trinkets.
 */
public final class TrinketsIntegration {

	private static final ProjectETrinket PROJECTE_TRINKET = new ProjectETrinket();

	private TrinketsIntegration() {
	}

	public static void registerTrinket(Item item) {
		TrinketsApi.registerTrinket(item, PROJECTE_TRINKET);
	}

	@Nullable
	public static IItemHandler getInventory(Player player) {
		Optional<TrinketComponent> optional = TrinketsApi.getTrinketComponent(player);
		if (optional.isEmpty()) {
			return null;
		}
		TrinketComponent component = optional.get();
		List<TrinketInventory> inventories = new ArrayList<>();
		for (Map<String, TrinketInventory> group : component.getInventory().values()) {
			inventories.addAll(group.values());
		}
		return inventories.isEmpty() ? null : new TrinketItemHandler(inventories);
	}
}
