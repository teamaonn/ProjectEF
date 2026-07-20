package moze_intel.projecte.gameObjs.customRecipes;

import com.mojang.serialization.MapCodec;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.registries.PERecipeConditions;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

public class FullKleinStarsCondition implements ResourceCondition {

	public static final FullKleinStarsCondition INSTANCE = new FullKleinStarsCondition();
	public static final MapCodec<FullKleinStarsCondition> CODEC = MapCodec.unit(INSTANCE);

	private FullKleinStarsCondition() {
	}

	@Override
	public ResourceConditionType<?> getType() {
		return PERecipeConditions.FULL_KLEIN_STARS;
	}

	@Override
	public boolean test(HolderLookup.Provider registryLookup) {
		return ProjectEConfig.common.fullKleinStars.get();
	}
}
