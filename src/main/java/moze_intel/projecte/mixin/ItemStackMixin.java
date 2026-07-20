package moze_intel.projecte.mixin;

import java.util.function.BiConsumer;
import moze_intel.projecte.gameObjs.items.IHasConditionalAttributes;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds the conditional (stack state dependent) attribute modifiers of ProjectE items, replacing the item attribute modifier event that used to provide this.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

	@Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V", at = @At("TAIL"))
	private void projecte$conditionalAttributesByGroup(EquipmentSlotGroup slotGroup, BiConsumer<Holder<Attribute>, AttributeModifier> action, CallbackInfo ci) {
		ItemStack self = (ItemStack) (Object) this;
		if (self.getItem() instanceof IHasConditionalAttributes conditional) {
			conditional.adjustAttributes(self, (attribute, modifier, group) -> {
				if (group == slotGroup) {
					action.accept(attribute, modifier);
				}
			});
		}
	}

	@Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V", at = @At("TAIL"))
	private void projecte$conditionalAttributesBySlot(EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> action, CallbackInfo ci) {
		ItemStack self = (ItemStack) (Object) this;
		if (self.getItem() instanceof IHasConditionalAttributes conditional) {
			conditional.adjustAttributes(self, (attribute, modifier, group) -> {
				if (group.test(slot)) {
					action.accept(attribute, modifier);
				}
			});
		}
	}
}
