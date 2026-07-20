package moze_intel.projecte.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moze_intel.projecte.events.PlayerEvents;
import moze_intel.projecte.gameObjs.items.armor.PEArmor.ReductionInfo;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies the damage reduction of worn ProjectE armor, replacing the incoming damage event.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	private void projecte$fullDamageBlock(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (amount > 0) {
			ReductionInfo reductionInfo = PlayerEvents.getArmorReduction((LivingEntity) (Object) this, source);
			if (reductionInfo.percentReduced() >= 1) {
				cir.setReturnValue(false);
			}
		}
	}

	@ModifyReturnValue(method = "getDamageAfterArmorAbsorb", at = @At("RETURN"))
	private float projecte$armorDamageReduction(float damage, DamageSource source, float originalDamage) {
		if (damage > 0) {
			ReductionInfo reductionInfo = PlayerEvents.getArmorReduction((LivingEntity) (Object) this, source);
			if (reductionInfo.maxDamagedAbsorbed() > 0 && reductionInfo.percentReduced() > 0 && reductionInfo.percentReduced() < 1) {
				float damageAbsorbed = Math.min(damage * reductionInfo.percentReduced(), reductionInfo.maxDamagedAbsorbed());
				return Math.max(0, damage - damageAbsorbed);
			}
		}
		return damage;
	}
}
