package moze_intel.projecte;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.*;
public final class AlchemicalChestBlockEntity extends BaseContainerBlockEntity implements LidBlockEntity {
 final ChestAnimation animation = new ChestAnimation(this,
        player -> player.containerMenu instanceof AlchemicalChestMenu menu && menu.uses(this));

 @Override public void startOpen(ContainerUser user) { animation.startOpen(user); }
 @Override public void stopOpen(ContainerUser user) { animation.stopOpen(user); }
 @Override public boolean triggerEvent(int id, int value) {
  return animation.handleEvent(id, value) || super.triggerEvent(id, value);
 }
 @Override public float getOpenNess(float partialTick) { return animation.openness(partialTick); }

 private NonNullList<ItemStack> items=NonNullList.withSize(104,ItemStack.EMPTY);
 public AlchemicalChestBlockEntity(BlockPos pos,BlockState state){super(PERegistries.ALCHEMICAL_CHEST_ENTITY,pos,state);}
 public int getContainerSize(){return 104;}
 protected Component getDefaultName(){return Component.translatable("block.projecte.alchemical_chest");}
 protected NonNullList<ItemStack> getItems(){return items;}
 protected void setItems(NonNullList<ItemStack> value){items=value;}
 protected AbstractContainerMenu createMenu(int id,Inventory inv){return new AlchemicalChestMenu(id,inv,this);}
 @Override protected void saveAdditional(ValueOutput out){super.saveAdditional(out);ContainerHelper.saveAllItems(out,items);}
 @Override protected void loadAdditional(ValueInput in){super.loadAdditional(in);items=NonNullList.withSize(104,ItemStack.EMPTY);ContainerHelper.loadAllItems(in,items);}
}
