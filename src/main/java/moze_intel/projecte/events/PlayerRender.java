package moze_intel.projecte.events;

import moze_intel.projecte.gameObjs.registries.PEItems;

/**
 * Client-side FOV modification for Gem Boots. This is handled by a Mixin on
 * {@code GameRenderer#getFov}; this class exists as a marker for where the
 * old event was registered.
 */
public class PlayerRender {

	private PlayerRender() {
	}
}