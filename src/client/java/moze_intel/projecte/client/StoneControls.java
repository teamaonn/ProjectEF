package moze_intel.projecte.client;

import moze_intel.projecte.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;

/** Ordinary Minecraft key mappings: defaults are suggestions, never hard-coded input checks. */
public final class StoneControls {
 private StoneControls(){}
 public static void register(){
  var category=KeyMapping.Category.register(PECore.id("stone"));
  var charge=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.projecte.stone_charge",InputConstants.KEY_V,category));
  var lower=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.projecte.stone_lower",-1,category));
  var fire=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.projecte.stone_fire",InputConstants.KEY_R,category));
  var craft=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.projecte.stone_craft",InputConstants.KEY_C,category));
  var mode=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.projecte.stone_mode",InputConstants.KEY_G,category));
  ClientTickEvents.END_CLIENT_TICK.register(client->{
   while(charge.consumeClick())send(client,client.player!=null&&client.player.isShiftKeyDown()?1:0);
   while(lower.consumeClick())send(client,1);
   while(fire.consumeClick())send(client,2);
   while(craft.consumeClick())send(client,3);
   while(mode.consumeClick())send(client,4);
  });
 }
 private static void send(Minecraft client,int command){
  if(client.player==null||client.gui.screen()!=null||!ClientPlayNetworking.canSend(PEPackets.StoneAction.TYPE))return;
  boolean offhand=!(client.player.getMainHandItem().getItem() instanceof PhilosophersStoneItem);
  if(!(client.player.getItemInHand(offhand?net.minecraft.world.InteractionHand.OFF_HAND:net.minecraft.world.InteractionHand.MAIN_HAND).getItem() instanceof PhilosophersStoneItem))return;
  ClientPlayNetworking.send(new PEPackets.StoneAction(command,offhand));
 }
}
