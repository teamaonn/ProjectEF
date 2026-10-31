package moze_intel.projecte.client;
import com.mojang.blaze3d.platform.InputConstants;
/** Input decisions shared by the screen and the client regression checks. */
public final class TransmutationInput {
 private TransmutationInput(){}
 public static int purchaseCount(int button,int modifiers){
  if(button==InputConstants.MOUSE_BUTTON_RIGHT)return 64;
  if(button==InputConstants.MOUSE_BUTTON_LEFT)return (modifiers&InputConstants.MOD_SHIFT)!=0?64:1;
  return 0;
 }
 public static boolean searchOwnsKey(boolean focused,int key){return focused&&key!=InputConstants.KEY_ESCAPE;}
}
