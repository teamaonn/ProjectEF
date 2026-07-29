package moze_intel.projecte.integration.recipe_viewer.rei;

import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import moze_intel.projecte.PECore;
import moze_intel.projecte.integration.recipe_viewer.FuelUpgradeRecipe;

/**
 * REI display for a collector fuel upgrade (input fuel + EMC -> upgraded fuel).
 * Mirrors {@link moze_intel.projecte.integration.recipe_viewer.emi.CollectorEmiRecipe} on top of the shared
 * {@link FuelUpgradeRecipe} data produced by {@link moze_intel.projecte.integration.recipe_viewer.RecipeViewerHelper}.
 */
public class CollectorReiDisplay extends BasicDisplay {

	public static final CategoryIdentifier<CollectorReiDisplay> CATEGORY = CategoryIdentifier.of(PECore.rl("collector"));

	private final long upgradeEMC;

	public CollectorReiDisplay(FuelUpgradeRecipe recipe) {
		super(
				List.of(EntryIngredients.of(recipe.input().value())),
				List.of(EntryIngredients.of(recipe.output().value())),
				Optional.of(recipe.syntheticId())
		);
		this.upgradeEMC = recipe.upgradeEMC();
	}

	public long getUpgradeEMC() {
		return upgradeEMC;
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return CATEGORY;
	}
}
