package moze_intel.projecte.integration.recipe_viewer.rei;

import java.util.ArrayList;
import java.util.List;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;

public class CollectorReiCategory implements DisplayCategory<CollectorReiDisplay> {

	@Override
	public CategoryIdentifier<? extends CollectorReiDisplay> getCategoryIdentifier() {
		return CollectorReiDisplay.CATEGORY;
	}

	@Override
	public Component getTitle() {
		return PELang.JEI_COLLECTOR.translate();
	}

	@Override
	public Renderer getIcon() {
		return EntryStacks.of(PEBlocks.COLLECTOR);
	}

	@Override
	public int getDisplayHeight() {
		return 40;
	}

	@Override
	public int getDisplayWidth(CollectorReiDisplay display) {
		return 90;
	}

	@Override
	public List<Widget> setupDisplay(CollectorReiDisplay display, Rectangle bounds) {
		List<Widget> widgets = new ArrayList<>();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createLabel(new Point(bounds.getCenterX(), bounds.y + 5), PELang.EMC.translate(display.getUpgradeEMC()))
				.centered()
				.noShadow()
				.color(0xFF808080));
		widgets.add(Widgets.createSlot(new Point(bounds.x + 8, bounds.y + 16))
				.entries(display.getInputEntries().get(0))
				.markInput());
		widgets.add(Widgets.createArrow(new Point(bounds.x + 31, bounds.y + 17))
				.animationDurationTicks(100));
		widgets.add(Widgets.createSlot(new Point(bounds.x + 58, bounds.y + 16))
				.entries(display.getOutputEntries().get(0))
				.markOutput());
		return widgets;
	}
}
