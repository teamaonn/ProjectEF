package moze_intel.projecte;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.*;

/** EE2 world conversions, with five charge levels and server-owned actions. */
public final class PhilosophersStoneItem extends Item {
 public static final int MAX_CHARGE=4;
 private static final List<List<Block>> CYCLES=List.of(
  List.of(Blocks.OAK_LOG,Blocks.BIRCH_LOG,Blocks.SPRUCE_LOG,Blocks.JUNGLE_LOG,Blocks.ACACIA_LOG,Blocks.DARK_OAK_LOG,Blocks.MANGROVE_LOG,Blocks.CHERRY_LOG,Blocks.PALE_OAK_LOG),
  List.of(Blocks.OAK_WOOD,Blocks.BIRCH_WOOD,Blocks.SPRUCE_WOOD,Blocks.JUNGLE_WOOD,Blocks.ACACIA_WOOD,Blocks.DARK_OAK_WOOD,Blocks.MANGROVE_WOOD,Blocks.CHERRY_WOOD,Blocks.PALE_OAK_WOOD),
  List.of(Blocks.STRIPPED_OAK_LOG,Blocks.STRIPPED_BIRCH_LOG,Blocks.STRIPPED_SPRUCE_LOG,Blocks.STRIPPED_JUNGLE_LOG,Blocks.STRIPPED_ACACIA_LOG,Blocks.STRIPPED_DARK_OAK_LOG,Blocks.STRIPPED_MANGROVE_LOG,Blocks.STRIPPED_CHERRY_LOG,Blocks.STRIPPED_PALE_OAK_LOG),
  List.of(Blocks.STRIPPED_OAK_WOOD,Blocks.STRIPPED_BIRCH_WOOD,Blocks.STRIPPED_SPRUCE_WOOD,Blocks.STRIPPED_JUNGLE_WOOD,Blocks.STRIPPED_ACACIA_WOOD,Blocks.STRIPPED_DARK_OAK_WOOD,Blocks.STRIPPED_MANGROVE_WOOD,Blocks.STRIPPED_CHERRY_WOOD,Blocks.STRIPPED_PALE_OAK_WOOD),
  List.of(Blocks.OAK_LEAVES,Blocks.BIRCH_LEAVES,Blocks.SPRUCE_LEAVES,Blocks.JUNGLE_LEAVES,Blocks.ACACIA_LEAVES,Blocks.DARK_OAK_LEAVES,Blocks.MANGROVE_LEAVES,Blocks.CHERRY_LEAVES,Blocks.PALE_OAK_LEAVES));
 public PhilosophersStoneItem(Properties p){super(p.stacksTo(1).fireResistant());}
 public static int charge(ItemStack s){return Math.clamp(data(s).getIntOr("StoneCharge",0),0,MAX_CHARGE);}
 public static int mode(ItemStack s){return Math.floorMod(data(s).getIntOr("StoneMode",0),3);}
 private static net.minecraft.nbt.CompoundTag data(ItemStack s){return s.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
 public static void setCharge(ItemStack s,int charge){var tag=data(s);tag.putInt("StoneCharge",Math.clamp(charge,0,MAX_CHARGE));s.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
 public static void action(ServerPlayer p,int command,InteractionHand hand){
  ItemStack s=p.getItemInHand(hand);
  if(!p.isAlive()||p.isSpectator()||!(s.getItem() instanceof PhilosophersStoneItem)||p.containerMenu!=p.inventoryMenu)return;
  switch(command){
   case 0,1->{setCharge(s,charge(s)+(command==0?1:-1));p.sendSystemMessage(Component.literal("Stone charge: "+(charge(s)+1)+"/5"),true);}
   case 2->{if(!p.getCooldowns().isOnCooldown(s)){if(charge(s)==0)p.sendSystemMessage(Component.literal("Charge the Stone before firing"),true);else{StoneMobTransmutation.shoot(p,s);p.getCooldowns().addCooldown(s,10);}}}
   case 3->openCrafting(p);
   case 4->{var tag=data(s);tag.putInt("StoneMode",(mode(s)+1)%3);s.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));p.sendSystemMessage(Component.literal("Stone area: "+List.of("Cube","Panel","Line").get(mode(s))),true);}
  }
 }
 public static Block transform(Block block,boolean reverse){
  if(block==Blocks.STONE)return reverse?Blocks.GRASS_BLOCK:Blocks.COBBLESTONE;
  if(block==Blocks.COBBLESTONE)return reverse?Blocks.GRASS_BLOCK:Blocks.STONE;
  if(block==Blocks.GRASS_BLOCK)return reverse?Blocks.COBBLESTONE:Blocks.SAND;
  if(block==Blocks.DIRT)return Blocks.SAND;
  if(block==Blocks.SAND)return reverse?Blocks.COBBLESTONE:Blocks.GRASS_BLOCK;
  if(block==Blocks.GRAVEL)return Blocks.SANDSTONE;
  if(block==Blocks.SANDSTONE)return reverse?Blocks.GRAVEL:null;
  if(block==Blocks.GLASS)return reverse?Blocks.SAND:null;
  if(block==Blocks.NETHERRACK)return Blocks.COBBLESTONE;
  if(block==Blocks.SHORT_GRASS||block==Blocks.FERN)return Blocks.DEAD_BUSH;
  if(block==Blocks.MELON)return Blocks.PUMPKIN;if(block==Blocks.PUMPKIN)return Blocks.MELON;
  if(block==Blocks.DANDELION)return Blocks.POPPY;if(block==Blocks.POPPY)return Blocks.DANDELION;
  if(block==Blocks.RED_MUSHROOM)return Blocks.BROWN_MUSHROOM;if(block==Blocks.BROWN_MUSHROOM)return Blocks.RED_MUSHROOM;
  if(block==Blocks.WATER)return Blocks.ICE;if(block==Blocks.LAVA)return Blocks.OBSIDIAN;
  if(block==Blocks.OBSIDIAN)return Blocks.LAVA;
  for(var cycle:CYCLES){int i=cycle.indexOf(block);if(i>=0)return cycle.get(Math.floorMod(i+(reverse?-1:1),cycle.size()));}
  return null;
 }
 public static BlockState transform(BlockState old,boolean reverse){
  if(old.getBlock() instanceof LiquidBlock&&!old.getFluidState().isSource())return null;
  Block block=transform(old.getBlock(),reverse);
  // EE2's oil came from another mod. Recognize a supplied source fluid without adding oil ourselves.
  if(block==null&&!old.getFluidState().isEmpty()&&old.getFluidState().isSource()){
   String fluid=net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(old.getFluidState().getType()).getPath();
   if(fluid.equals("oil")||fluid.equals("crude_oil"))block=Blocks.ICE;
  }
  if(block==null)return null;
  BlockState result=block.defaultBlockState();
  for(Property<?> property:old.getProperties())if(result.hasProperty(property))result=copyProperty(old,result,property);
  return result;
 }
 private static <T extends Comparable<T>> BlockState copyProperty(BlockState from,BlockState to,Property<T> property){return to.setValue(property,from.getValue(property));}
 public static List<BlockPos> targets(BlockPos pos,Direction face,Direction horizontal,int charge,int mode){
  int r=Math.clamp(charge,0,MAX_CHARGE);List<BlockPos> result=new ArrayList<>();
  for(int x=-r;x<=r;x++)for(int y=-r;y<=r;y++)for(int z=-r;z<=r;z++){
   if(mode==1&&(face.getAxis()==Direction.Axis.X?x!=0:face.getAxis()==Direction.Axis.Y?y!=0:z!=0))continue;
   if(mode==2&&(horizontal.getAxis()==Direction.Axis.X?y!=0||z!=0:x!=0||y!=0))continue;
   result.add(pos.offset(x,y,z));
  }return result;
 }
 public static InteractionResult transmute(Level level,Player player,ItemStack stack,BlockPos pos,Direction face,boolean reverse){
  BlockState origin=level.getBlockState(pos);BlockState replacement=transform(origin,reverse);
  if(replacement==null)return InteractionResult.PASS;
  if(!player.mayBuild())return InteractionResult.FAIL;
  if(level.isClientSide())return InteractionResult.SUCCESS;
  int changed=0;
  for(BlockPos target:targets(pos,face,player.getDirection(),charge(stack),mode(stack))){
   if(!level.isLoaded(target)||!level.mayInteract(player,target)||!player.mayUseItemAt(target,face,stack))continue;
   BlockState old=level.getBlockState(target);
   if(old.getBlock()!=origin.getBlock()||old.hasBlockEntity())continue;
   BlockState next=transform(old,reverse);if(next==null)continue;
   if(level.setBlock(target,next,3))changed++;
  }
  if(changed>0){level.levelEvent(2001,pos,Block.getId(origin));return InteractionResult.SUCCESS;}
  return InteractionResult.FAIL;
 }
 @Override public InteractionResult useOn(UseOnContext c){
  Player p=c.getPlayer();if(p==null)return InteractionResult.PASS;
  BlockHitResult hit=getPlayerPOVHitResult(c.getLevel(),p,ClipContext.Fluid.SOURCE_ONLY);
  BlockPos pos=c.getClickedPos();Direction face=c.getClickedFace();
  if(hit.getType()==HitResult.Type.BLOCK&&!c.getLevel().getFluidState(hit.getBlockPos()).isEmpty()){pos=hit.getBlockPos();face=hit.getDirection();}
  return transmute(c.getLevel(),p,c.getItemInHand(),pos,face,c.isSecondaryUseActive());
 }
 public static void openCrafting(ServerPlayer p){p.openMenu(new SimpleMenuProvider((id,inv,player)->new CraftingMenu(id,inv,ContainerLevelAccess.NULL),Component.literal("Philosopher's Stone")));}
 @Override public InteractionResult use(Level level,Player player,InteractionHand hand){
  BlockHitResult hit=getPlayerPOVHitResult(level,player,ClipContext.Fluid.SOURCE_ONLY);
  if(hit.getType()==HitResult.Type.BLOCK){InteractionResult result=transmute(level,player,player.getItemInHand(hand),hit.getBlockPos(),hit.getDirection(),player.isShiftKeyDown());if(result!=InteractionResult.PASS)return result;}
  if(player instanceof ServerPlayer p)openCrafting(p);return InteractionResult.SUCCESS;
 }
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,net.minecraft.world.item.component.TooltipDisplay display,java.util.function.Consumer<Component> lines,TooltipFlag flag){
  lines.accept(Component.literal("Charge: "+(charge(stack)+1)+"/5 · "+List.of("Cube","Panel","Line").get(mode(stack))));
  lines.accept(Component.literal("Controls: charge, lower charge, mode, fire, crafting"));
  lines.accept(Component.literal("Mob transmutation: 384 EMC from inventory fuel"));
 }
}
