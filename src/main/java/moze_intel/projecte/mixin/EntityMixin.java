package moze_intel.projecte.mixin;

import moze_intel.projecte.events.PlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes players wearing fire protecting ProjectEF gear invulnerable to fire damage, replacing the invulnerability check event.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
	private void projecte$fireProtection(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		if ((Object) this instanceof ServerPlayer player && PlayerEvents.isFireInvulnerable(player, source)) {
			cir.setReturnValue(true);
		}
	}
}
