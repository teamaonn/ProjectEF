package moze_intel.projecte.mixin;

import java.util.UUID;
import moze_intel.projecte.events.PlayerEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.jetbrains.annotations.Nullable;

/**
 * Lets alchemical bags with a suction item (black hole band) grab items before they enter the player inventory, replacing the item pickup event.
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

	@Shadow
	@Nullable
	private UUID target;

	@Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
	private void projecte$bagSuction(Player player, CallbackInfo ci) {
		if (target != null && !player.getUUID().equals(target)) {
			return;
		}
		if (PlayerEvents.onItemPickup((ItemEntity) (Object) this, player)) {
			ci.cancel();
		}
	}
}
