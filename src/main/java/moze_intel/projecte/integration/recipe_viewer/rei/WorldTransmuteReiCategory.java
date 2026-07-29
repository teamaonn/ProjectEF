package moze_intel.projecte.integration.recipe_viewer.rei;

import java.util.ArrayList;
import java.util.List;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Arrow;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.integration.recipe_viewer.RecipeViewerHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;

public class WorldTransmuteReiCategory implements DisplayCategory<WorldTransmuteReiDisplay> {

	@Override
	public CategoryIdentifier<? extends WorldTransmuteReiDisplay> getCategoryIdentifier() {
		return WorldTransmuteReiDisplay.CATEGORY;
	}

	@Override
	public Component getTitle() {
		return PELang.WORLD_TRANSMUTE.translate();
	}

	@Override
	public Renderer getIcon() {
		return EntryStacks.of(PEItems.PHILOSOPHERS_STONE);
	}

	@Override
	public int getDisplayHeight() {
		return 30;
	}

	@Override
	public int getDisplayWidth(WorldTransmuteReiDisplay display) {
		return 100;
	}

	@Override
	public List<Widget> setupDisplay(WorldTransmuteReiDisplay display, Rectangle bounds) {
		List<Widget> widgets = new ArrayList<>();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createSlot(new Point(bounds.x + 8, bounds.y + 6))
				.entries(display.getInputEntries().get(0))
				.markInput());
		Arrow arrow = Widgets.createArrow(new Point(bounds.x + 30, bounds.y + 7))
				.animationDurationTicks(100);
		widgets.add(Widgets.withTooltip(arrow, RecipeViewerHelper.getTransmuteDescription()));
		int x = bounds.x + 56;
		for (EntryIngredient output : display.getOutputEntries()) {
			widgets.add(Widgets.createSlot(new Point(x, bounds.y + 6))
					.entries(output)
					.markOutput());
			x += 18;
		}
		return widgets;
	}
}
