package moze_intel.projecte.integration.recipe_viewer.rei;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import moze_intel.projecte.PECore;
import moze_intel.projecte.integration.recipe_viewer.FluidInfo;
import moze_intel.projecte.integration.recipe_viewer.WorldTransmuteEntry;
import net.minecraft.world.item.ItemStack;

/**
 * REI display for a world transmutation (philosopher's stone right click), supporting both item and fluid
 * inputs/outputs plus an optional sneak alternate output. Mirrors
 * {@link moze_intel.projecte.integration.recipe_viewer.emi.WorldTransmuteEmiRecipe}.
 */
public class WorldTransmuteReiDisplay extends BasicDisplay {

	public static final CategoryIdentifier<WorldTransmuteReiDisplay> CATEGORY = CategoryIdentifier.of(PECore.rl("world_transmutation"));

	public WorldTransmuteReiDisplay(WorldTransmuteEntry recipe) {
		super(
				List.of(asIngredient(recipe.input())),
				buildOutputs(recipe),
				Optional.of(recipe.syntheticId())
		);
	}

	private static List<EntryIngredient> buildOutputs(WorldTransmuteEntry recipe) {
		if (recipe.altOutput() == null) {
			return List.of(asIngredient(recipe.output()));
		}
		return List.of(asIngredient(recipe.output()), asIngredient(recipe.altOutput()));
	}

	private static EntryIngredient asIngredient(Either<ItemStack, FluidInfo> ingredient) {
		return ingredient.map(EntryIngredients::of, info -> EntryIngredients.of(info.fluid(), info.amount()));
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return CATEGORY;
	}
}
