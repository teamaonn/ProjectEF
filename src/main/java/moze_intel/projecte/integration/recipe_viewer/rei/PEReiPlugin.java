package moze_intel.projecte.integration.recipe_viewer.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.integration.recipe_viewer.FuelUpgradeRecipe;
import moze_intel.projecte.integration.recipe_viewer.RecipeViewerHelper;
import moze_intel.projecte.integration.recipe_viewer.WorldTransmuteEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * REI (Roughly Enough Items) client plugin. Registered via the {@code rei_client} entrypoint in fabric.mod.json.
 * Builds native REI categories/displays from the loader-neutral data exposed by {@link RecipeViewerHelper}, mirroring
 * the JEI and EMI integrations that share the same abstraction layer.
 */
@Environment(EnvType.CLIENT)
public class PEReiPlugin implements REIClientPlugin {

	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.add(new CollectorReiCategory());
		registry.add(new WorldTransmuteReiCategory());

		//Workstations for the ProjectE categories
		registry.addWorkstations(CollectorReiDisplay.CATEGORY, EntryIngredients.ofItemTag(PETags.Items.COLLECTORS));
		registry.addWorkstations(WorldTransmuteReiDisplay.CATEGORY, EntryStacks.of(PEItems.PHILOSOPHERS_STONE));

		//Attach ProjectE items as workstations for the relevant vanilla categories
		registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(PEItems.PHILOSOPHERS_STONE));
		registry.addWorkstations(BuiltinPlugin.SMELTING, EntryIngredients.ofItemTag(PETags.Items.MATTER_FURNACES));
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		for (WorldTransmuteEntry recipe : RecipeViewerHelper.getAllTransmutations()) {
			registry.add(new WorldTransmuteReiDisplay(recipe));
		}
		for (FuelUpgradeRecipe recipe : RecipeViewerHelper.getFuelUpgrades()) {
			registry.add(new CollectorReiDisplay(recipe));
		}
	}
}
