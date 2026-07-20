package moze_intel.projecte.integration.recipe_viewer;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;

/**
 * Loader neutral representation of a fluid with a display amount (in droplets, 1 bucket = {@link FluidConstants#BUCKET}), used by the recipe viewer
 * integrations in place of the old FluidStack.
 */
public record FluidInfo(Fluid fluid, long amount) {

	/**
	 * Serializes just the fluid, always using a bucket as the amount.
	 */
	public static final Codec<FluidInfo> BUCKET_CODEC = BuiltInRegistries.FLUID.byNameCodec()
			.xmap(fluid -> new FluidInfo(fluid, FluidConstants.BUCKET), FluidInfo::fluid);

	public static FluidInfo bucket(Fluid fluid) {
		return new FluidInfo(fluid, FluidConstants.BUCKET);
	}

	public Holder<Fluid> holder() {
		return fluid.builtInRegistryHolder();
	}
}
