package moze_intel.projecte;

import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class StoneBehaviorTests {
 @GameTest
 public void alternateConversions(GameTestHelper h){
  h.assertTrue(PhilosophersStoneItem.transform(Blocks.OBSIDIAN,false)==Blocks.LAVA,"obsidian to lava");
  h.assertTrue(PhilosophersStoneItem.transform(Blocks.OBSIDIAN,true)==Blocks.LAVA,"sneaking obsidian to lava");
  h.assertTrue(PhilosophersStoneItem.transform(Blocks.STONE,true)==Blocks.GRASS_BLOCK,"sneaking stone to grass");
  h.assertTrue(PhilosophersStoneItem.transform(Blocks.COBBLESTONE,true)==Blocks.GRASS_BLOCK,"sneaking cobble to grass");
  h.assertTrue(PhilosophersStoneItem.transform(Blocks.GLASS,true)==Blocks.SAND,"sneaking glass to sand");
  h.assertTrue(PhilosophersStoneItem.transform(Blocks.GLASS,false)==null,"glass is alternate only");
  h.assertTrue(PhilosophersStoneItem.transform(Blocks.SANDSTONE,true)==Blocks.GRAVEL,"alternate sandstone to gravel");
  h.assertTrue(PhilosophersStoneItem.transform(Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL,4),false)==null,"flowing fluid is not a source");
  h.succeed();
 }
 @GameTest
 public void woodPropertiesAndChargeExtents(GameTestHelper h){
  var log=Blocks.OAK_LOG.defaultBlockState().setValue(BlockStateProperties.AXIS,Direction.Axis.X);
  h.assertTrue(PhilosophersStoneItem.transform(log,false).getValue(BlockStateProperties.AXIS)==Direction.Axis.X,"log axis survives");
  for(int charge=0;charge<=4;charge++){
   int width=charge*2+1;
   h.assertTrue(PhilosophersStoneItem.targets(BlockPos.ZERO,Direction.UP,Direction.NORTH,charge,0).size()==width*width*width,"cube size");
   h.assertTrue(PhilosophersStoneItem.targets(BlockPos.ZERO,Direction.UP,Direction.NORTH,charge,1).size()==width*width,"panel size");
   h.assertTrue(PhilosophersStoneItem.targets(BlockPos.ZERO,Direction.UP,Direction.NORTH,charge,2).size()==width,"line size");
  }
  h.succeed();
 }
 @GameTest
 public void reusableStoneKeepsChargeAndName(GameTestHelper h){
  var stone=new ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(PECore.id("philosophers_stone")));
  PhilosophersStoneItem.setCharge(stone,4);stone.set(DataComponents.CUSTOM_NAME,Component.literal("Test Stone"));
  var input=CraftingInput.of(1,1,List.of(stone));var remainder=CraftingRecipe.defaultCraftingReminder(input).getFirst();
  h.assertTrue(ItemStack.isSameItemSameComponents(stone,remainder),"crafting returns all Stone components");
  h.assertTrue(stone!=remainder,"remainder is an independent stack");
  h.succeed();
 }
}
