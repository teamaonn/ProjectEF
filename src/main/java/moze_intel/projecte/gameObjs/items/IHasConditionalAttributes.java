package moze_intel.projecte.gameObjs.items;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

/**
 * Implemented by items whose attribute modifiers depend on the stack's current state (for example the stored charge). Applied through a mixin into
 * {@link ItemStack#forEachModifier}.
 */
public interface IHasConditionalAttributes {

	/**
	 * Called when the attribute modifiers of a stack of this item are being collected.
	 */
	void adjustAttributes(ItemStack stack, AttributeCollector collector);

	@FunctionalInterface
	interface AttributeCollector {

		void accept(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slotGroup);
	}
}