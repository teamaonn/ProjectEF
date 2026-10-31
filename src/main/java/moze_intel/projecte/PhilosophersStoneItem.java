package moze_intel.projecte;
import net.minecraft.world.*;import net.minecraft.world.item.*;import net.minecraft.world.item.context.UseOnContext;import net.minecraft.world.level.*;import net.minecraft.world.level.block.*;import net.minecraft.world.entity.player.Player;
public final class PhilosophersStoneItem extends Item {
 public PhilosophersStoneItem(Properties p){super(p.stacksTo(1).fireResistant());}
 public static Block transform(Block block,boolean reverse){
  if(block==Blocks.STONE)return reverse?Blocks.GRASS_BLOCK:Blocks.COBBLESTONE;
  if(block==Blocks.COBBLESTONE)return reverse?Blocks.SAND:Blocks.STONE;
  if(block==Blocks.GRASS_BLOCK||block==Blocks.DIRT)return Blocks.SAND;if(block==Blocks.SAND)return reverse?Blocks.COBBLESTONE:Blocks.GRASS_BLOCK;
  if(block==Blocks.GRAVEL)return Blocks.SANDSTONE;if(block==Blocks.SANDSTONE)return Blocks.GRAVEL;
  if(block==Blocks.NETHERRACK)return Blocks.COBBLESTONE;return null;
 }
 @Override public InteractionResult useOn(UseOnContext c){
  var player=c.getPlayer();if(player==null)return InteractionResult.PASS;var level=c.getLevel();var pos=c.getClickedPos();var old=level.getBlockState(pos);
  if(old.hasBlockEntity()||!level.mayInteract(player,pos)||!player.mayUseItemAt(pos,c.getClickedFace(),c.getItemInHand()))return InteractionResult.FAIL;
  Block replacement=transform(old.getBlock(),player.isShiftKeyDown());if(replacement==null)return InteractionResult.PASS;
  if(!level.isClientSide()){level.setBlock(pos,replacement.defaultBlockState(),3);level.levelEvent(2001,pos,Block.getId(old));}
  return InteractionResult.SUCCESS;
 }
 @Override public InteractionResult use(Level level,Player player,InteractionHand hand){
  if(player instanceof net.minecraft.server.level.ServerPlayer server)server.openMenu(new SimpleMenuProvider((id,inv,p)->new net.minecraft.world.inventory.CraftingMenu(id,inv,net.minecraft.world.inventory.ContainerLevelAccess.NULL),net.minecraft.network.chat.Component.literal("Philosopher's Stone")));
  return InteractionResult.SUCCESS;
 }
}
