package moze_intel.projecte.integration;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * Implemented by items that expose additional attributes while worn in an accessory (trinkets) slot.
 */
public interface IExposesCurioAttributes {

	void addAttributes(Multimap<Holder<Attribute>, AttributeModifier> attributes);
}
