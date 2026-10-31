package moze_intel.projecte.client;
import moze_intel.projecte.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
public final class AlchemicalChestScreen extends AbstractContainerScreen<AlchemicalChestMenu> {
 public AlchemicalChestScreen(AlchemicalChestMenu menu,Inventory inv,Component title){super(menu,inv,title,255,230);}
 @Override public void extractContents(GuiGraphicsExtractor g,int x,int y,float partial){g.blit(RenderPipelines.GUI_TEXTURED,PECore.id("textures/gui/alchchest.png"),leftPos,topPos,0,0,255,230,256,256);super.extractContents(g,x,y,partial);}
 @Override protected void extractLabels(GuiGraphicsExtractor g,int x,int y){}
}
