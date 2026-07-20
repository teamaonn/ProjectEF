package moze_intel.projecte.utils.text;

import net.minecraft.network.chat.Component;

/**
 * @apiNote From Mekanism
 */
public interface IHasTranslationKey {

	String getTranslationKey();

	interface IHasEnumNameTranslationKey extends IHasTranslationKey {

		default Component getTranslatedName() {
			return TextComponentUtil.translate(getTranslationKey());
		}
	}
}