package moze_intel.projecte.integration.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Shared {@link Trinket} behavior registered for every ProjectE accessory. Its whole job is forwarding the trinket tick
 * to {@link net.minecraft.world.item.Item#inventoryTick}, which is what drives the accessories' passive abilities.
 * Everything else keeps the default trinket behavior.
 */
public class ProjectETrinket implements Trinket {

	/**
	 * Trinkets keeps worn stacks in a Cardinal Components backed {@link dev.emi.trinkets.api.TrinketInventory} rather
	 * than the player's {@link Inventory}, so vanilla never calls {@link net.minecraft.world.item.Item#inventoryTick}
	 * for them. Forward the trinket tick to it so the accessories' passive abilities keep working while worn, matching
	 * the behavior they have in the hotbar.
	 * <p>
	 * {@link Inventory#SLOT_OFFHAND} is passed as the slot because it satisfies
	 * {@link moze_intel.projecte.gameObjs.items.ItemPE#hotBarOrOffHand(int)} while not being a valid hotbar index. No
	 * ProjectE accessory uses the slot argument for anything besides that check.
	 */
	@Override
	public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
		Level level = entity.level();
		if (level.isClientSide) {
			stack.getItem().inventoryTick(stack, level, entity, Inventory.SLOT_OFFHAND, false);
			return;
		}
		//Worn stacks are live references, and mutating one (draining EMC, toggling active, repairing) does not by
		// itself sync to the client the way a vanilla inventory slot would. Flag the inventory when the tick changed
		// something so trinkets pushes the update.
		ItemStack before = stack.copy();
		stack.getItem().inventoryTick(stack, level, entity, Inventory.SLOT_OFFHAND, false);
		if (!ItemStack.matches(before, stack)) {
			slot.inventory().markUpdate();
		}
	}
}
