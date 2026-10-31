package moze_intel.projecte;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class AlchemicalChestMenu extends AbstractContainerMenu {
 private final Container chest;
 public AlchemicalChestMenu(int id,Inventory inv){this(id,inv,new SimpleContainer(104));}
 public AlchemicalChestMenu(int id,Inventory inv,Container chest){
  super(PERegistries.ALCHEMICAL_CHEST_MENU,id);this.chest=chest;chest.startOpen(inv.player);
  for(int row=0;row<8;row++)for(int col=0;col<13;col++)addSlot(new Slot(chest,row*13+col,12+col*18,5+row*18));
  for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inv,9+row*9+col,48+col*18,152+row*18));
  for(int col=0;col<9;col++)addSlot(new Slot(inv,col,48+col*18,210));
 }
 public boolean stillValid(Player p){return chest.stillValid(p);}
 public ItemStack quickMoveStack(Player p,int index){
  if(index<0||index>=slots.size()||!slots.get(index).hasItem())return ItemStack.EMPTY;
  Slot slot=slots.get(index);ItemStack stack=slot.getItem(),copy=stack.copy();
  if(index<104?!moveItemStackTo(stack,104,slots.size(),true):!moveItemStackTo(stack,0,104,false))return ItemStack.EMPTY;
  if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();return copy;
 }
 @Override public void removed(Player p){super.removed(p);chest.stopOpen(p);}
}
