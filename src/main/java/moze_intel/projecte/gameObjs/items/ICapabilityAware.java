package moze_intel.projecte.gameObjs.items;

/**
 * Implemented by items that need to register additional (non ProjectEF) capabilities for themselves, for example fluid or item transfer apis. Called once during
 * item registration.
 */
@FunctionalInterface
public interface ICapabilityAware {

	void attachCapabilities();
}
