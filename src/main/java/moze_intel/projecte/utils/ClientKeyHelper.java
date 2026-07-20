package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableBiMap;
import com.mojang.blaze3d.platform.InputConstants;
import moze_intel.projecte.network.PENetwork;
import moze_intel.projecte.network.packets.to_server.KeyPressPKT;
import moze_intel.projecte.utils.text.PELang;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class ClientKeyHelper {

	private static ImmutableBiMap<PEKeybind, KeyMapping> peToMc = ImmutableBiMap.of();

	/**
	 * Called by client mod initializer (stage 4) to register all ProjectE key bindings.
	 * Uses Fabric KeyBindingHelper instead of NeoForge RegisterKeyMappingsEvent.
	 * KeyConflictContext and KeyModifier have been dropped — now uses pure vanilla KeyMapping.
	 */
	public static void registerKeyBindings() {
		ImmutableBiMap.Builder<PEKeybind, KeyMapping> builder = ImmutableBiMap.builder();
		addKeyBinding(builder, PEKeybind.HELMET_TOGGLE, GLFW.GLFW_KEY_X);
		addKeyBinding(builder, PEKeybind.BOOTS_TOGGLE, GLFW.GLFW_KEY_X);
		addKeyBinding(builder, PEKeybind.CHARGE, GLFW.GLFW_KEY_V);
		addKeyBinding(builder, PEKeybind.EXTRA_FUNCTION, GLFW.GLFW_KEY_C);
		addKeyBinding(builder, PEKeybind.FIRE_PROJECTILE, GLFW.GLFW_KEY_R);
		addKeyBinding(builder, PEKeybind.MODE, GLFW.GLFW_KEY_G);
		peToMc = builder.build();
	}

	private static void addKeyBinding(ImmutableBiMap.Builder<PEKeybind, KeyMapping> builder, PEKeybind keyBind, int keyCode) {
		KeyMapping keyMapping = new PEKeyMapping(keyBind, keyCode);
		builder.put(keyBind, keyMapping);
		KeyBindingHelper.registerKeyBinding(keyMapping);
	}

	public static Component getKeyName(PEKeybind k) {
		KeyMapping keyMapping = peToMc.get(k);
		if (keyMapping == null) {
			return TextComponentUtil.build(k);
		}
		return keyMapping.getTranslatedKeyMessage();
	}

	private static class PEKeyMapping extends KeyMapping {

		private final PEKeybind keybind;
		private boolean lastState;

		PEKeyMapping(PEKeybind keybind, int keyCode) {
			super(keybind.getTranslationKey(), InputConstants.Type.KEYSYM, keyCode, PELang.PROJECTE.getTranslationKey());
			this.keybind = keybind;
		}

		@Override
		public void setDown(boolean value) {
			super.setDown(value);
			boolean state = isDown();
			if (state != lastState) {
				if (state) {
					PENetwork.sendToServer(new KeyPressPKT(keybind));
				}
				lastState = state;
			}
		}
	}
}