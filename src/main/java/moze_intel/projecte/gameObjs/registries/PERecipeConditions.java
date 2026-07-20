package moze_intel.projecte.gameObjs.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.customRecipes.FullKleinStarsCondition;
import moze_intel.projecte.gameObjs.customRecipes.TomeEnabledCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;

public class PERecipeConditions {

	private PERecipeConditions() {
	}

	public static final ResourceConditionType<TomeEnabledCondition> TOME_ENABLED = ResourceConditionType.create(PECore.rl("tome_enabled"), TomeEnabledCondition.CODEC);
	public static final ResourceConditionType<FullKleinStarsCondition> FULL_KLEIN_STARS = ResourceConditionType.create(PECore.rl("full_klein_stars"), FullKleinStarsCondition.CODEC);

	public static void init() {
		ResourceConditions.register(TOME_ENABLED);
		ResourceConditions.register(FULL_KLEIN_STARS);
	}
}
