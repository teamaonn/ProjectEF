package moze_intel.projecte.integration.trinkets;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import moze_intel.projecte.integration.IExposesCurioAttributes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

/**
 * Shared {@link Trinket} behavior registered for every ProjectE accessory. Items that expose attributes while worn
 * (see {@link IExposesCurioAttributes}, e.g. the Arcana ring) contribute their modifiers through {@link #getModifiers}.
 * Everything else keeps the default trinket behavior.
 */
public class ProjectETrinket implements Trinket {

	@Override
	public Multimap<Holder<Attribute>, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, ResourceLocation id) {
		Multimap<Holder<Attribute>, AttributeModifier> modifiers = Trinket.super.getModifiers(stack, slot, entity, id);
		if (stack.getItem() instanceof IExposesCurioAttributes exposer) {
			modifiers = HashMultimap.create(modifiers);
			exposer.addAttributes(modifiers);
		}
		return modifiers;
	}
}
