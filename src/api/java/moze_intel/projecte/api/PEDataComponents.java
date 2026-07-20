package moze_intel.projecte.api;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class PEDataComponents {

	public static final DataComponentType<Integer> CHARGE = get("charge");

	private PEDataComponents() {
	}

	@SuppressWarnings("unchecked")
	private static <TYPE> DataComponentType<TYPE> get(String name) {
		return (DataComponentType<TYPE>) BuiltInRegistries.DATA_COMPONENT_TYPE.getOrThrow(
				ResourceKey.create(Registries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(ProjectEAPI.PROJECTE_MODID, name)));
	}
}