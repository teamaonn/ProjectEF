package moze_intel.projecte.mixin;

import moze_intel.projecte.events.TickEvents;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Runs ProjectE's per player tick logic (alchemical bag item ticking, internal abilities, fire resistance), replacing the player tick event.
 */
@Mixin(Player.class)
public abstract class PlayerMixin {

	@Inject(method = "tick", at = @At("TAIL"))
	private void projecte$onPlayerTick(CallbackInfo ci) {
		TickEvents.playerTick((Player) (Object) this);
	}
}
