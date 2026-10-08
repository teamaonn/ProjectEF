package moze_intel.projecte;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public final class EnergyCondenserBlockEntity extends BlockEntity implements WorldlyContainer {
    private final ItemStack[] items = {ItemStack.EMPTY};
    private UUID owner;

    public EnergyCondenserBlockEntity(BlockPos pos, BlockState state) {
        super(PERegistries.ENERGY_CONDENSER_ENTITY, pos, state);
    }

    public void bind(UUID player) {
        if (owner == null) { owner = player; setChanged(); }
    }

    public void tickServer() {
        if (level == null || owner == null) return;
        ItemStack stack = items[0];
        if (stack.isEmpty()) return;
        long unit = PEEmcOverrides.forServer(level.getServer()).value(stack);
        if (unit <= 0) return;
        int count = Math.min(stack.getCount(), 64);
        if (PETransmutationState.deposit(level.getServer(), owner, unit, count)) {
            stack.shrink(count);
            setChanged();
        }
    }

    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return items[0].isEmpty(); }
    @Override public ItemStack getItem(int slot) { return slot == 0 ? items[0] : ItemStack.EMPTY; }
    @Override public ItemStack removeItem(int slot, int amount) {
        if (slot != 0 || items[0].isEmpty()) return ItemStack.EMPTY;
        ItemStack result = items[0].split(amount);
        if (items[0].isEmpty()) items[0] = ItemStack.EMPTY;
        setChanged();
        return result;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) {
        if (slot != 0) return ItemStack.EMPTY;
        ItemStack result = items[0];
        items[0] = ItemStack.EMPTY;
        return result;
    }
    @Override public void setItem(int slot, ItemStack stack) { if (slot == 0) { items[0] = stack; setChanged(); } }
    @Override public void setChanged() { super.setChanged(); }
    @Override public boolean stillValid(net.minecraft.world.entity.player.Player player) { return player.distanceToSqr(worldPosition.getX()+.5, worldPosition.getY()+.5, worldPosition.getZ()+.5) < 64; }
    @Override public void clearContent() { items[0] = ItemStack.EMPTY; setChanged(); }
    @Override public int[] getSlotsForFace(Direction side) { return new int[] {0}; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return slot == 0 && PETransmutationState.valueWithUpgrades(stack, PETransmutationState.value(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())) > 0; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return false; }

    @Override protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        if (owner != null) output.putString("Owner", owner.toString());
        if (!items[0].isEmpty()) output.store("Item", ItemStack.CODEC, items[0]);
    }
    @Override protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        input.getString("Owner").flatMap(value -> {
            try { return java.util.Optional.of(UUID.fromString(value)); }
            catch (IllegalArgumentException ignored) { return java.util.Optional.empty(); }
        }).ifPresent(value -> owner = value);
        input.read("Item", ItemStack.CODEC).ifPresent(value -> items[0] = value);
    }
}
