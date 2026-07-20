package moze_intel.projecte.api.event;

import moze_intel.projecte.api.ItemInfo;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired on the server when a player is attempting to learn a new item
 * <p>
 * This event is fired on {@link #EVENT} and may be canceled to prevent the item from being learned.
 */
public class PlayerAttemptLearnEvent {

	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, listeners -> event -> {
		for (Callback listener : listeners) {
			if (event.isCanceled()) {
				break;
			}
			listener.onAttemptLearn(event);
		}
	});

	private final Player player;
	private final ItemInfo sourceInfo;
	private final ItemInfo reducedInfo;
	private boolean canceled;

	public PlayerAttemptLearnEvent(@NotNull Player player, @NotNull ItemInfo sourceInfo, @NotNull ItemInfo reducedInfo) {
		this.player = player;
		this.sourceInfo = sourceInfo;
		this.reducedInfo = reducedInfo;
	}

	/**
	 * @return The player who is attempting to learn a new item.
	 */
	@NotNull
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return The {@link ItemInfo} that the player is trying to learn.
	 */
	@NotNull
	public ItemInfo getSourceInfo() {
		return sourceInfo;
	}

	/**
	 * Gets the "cleaned" {@link ItemInfo} that the player is trying to learn. This {@link ItemInfo} may have reduced data component information.
	 *
	 * @return The "cleaned" {@link ItemInfo} that the player is trying to learn.
	 */
	@NotNull
	public ItemInfo getReducedInfo() {
		return reducedInfo;
	}

	/**
	 * @return Whether this event has been canceled, preventing the item from being learned.
	 */
	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	@FunctionalInterface
	public interface Callback {

		void onAttemptLearn(PlayerAttemptLearnEvent event);
	}
}
