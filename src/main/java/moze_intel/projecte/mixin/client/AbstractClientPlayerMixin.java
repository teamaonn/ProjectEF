package moze_intel.projecte.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Gem boots reduce the fov change from the speed boost, replacing the fov modifier event.
 */
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {

	@ModifyReturnValue(method = "getFieldOfViewModifier", at = @At("RETURN"))
	private float projecte$gemBootsFov(float original) {
		AbstractClientPlayer self = (AbstractClientPlayer) (Object) this;
		ItemStack boots = self.getItemBySlot(EquipmentSlot.FEET);
		if (!boots.isEmpty() && boots.is(PEItems.GEM_BOOTS)) {
			return original - 0.5F * Minecraft.getInstance().options.fovEffectScale().get().floatValue();
		}
		return original;
	}
}
