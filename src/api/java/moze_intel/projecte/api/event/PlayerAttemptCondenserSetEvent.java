package moze_intel.projecte.api.event;

import moze_intel.projecte.api.ItemInfo;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired on the server when a player is attempting to place an item in the condenser.
 * <p>
 * This event is fired on {@link #EVENT} and may be canceled to prevent the item from being set.
 */
public class PlayerAttemptCondenserSetEvent {

	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, listeners -> event -> {
		for (Callback listener : listeners) {
			if (event.isCanceled()) {
				break;
			}
			listener.onAttemptCondenserSet(event);
		}
	});

	private final Player player;
	private final ItemInfo sourceInfo;
	private final ItemInfo reducedInfo;
	private boolean canceled;

	public PlayerAttemptCondenserSetEvent(@NotNull Player entityPlayer, @NotNull ItemInfo sourceInfo, @NotNull ItemInfo reducedInfo) {
		player = entityPlayer;
		this.sourceInfo = sourceInfo;
		this.reducedInfo = reducedInfo;
	}

	/**
	 * @return The player who is attempting to put in the condenser slot.
	 */
	@NotNull
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return The {@link ItemInfo} that the player is trying to condense.
	 */
	@NotNull
	public ItemInfo getSourceInfo() {
		return sourceInfo;
	}

	/**
	 * Gets the "cleaned" {@link ItemInfo} that the player is trying to condense. This {@link ItemInfo} may have reduced data component information.
	 *
	 * @return The "cleaned" {@link ItemInfo} that the player is trying to learn.
	 */
	@NotNull
	public ItemInfo getReducedInfo() {
		return reducedInfo;
	}

	/**
	 * @return Whether this event has been canceled, preventing the item from being set in the condenser.
	 */
	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	@FunctionalInterface
	public interface Callback {

		void onAttemptCondenserSet(PlayerAttemptCondenserSetEvent event);
	}
}
