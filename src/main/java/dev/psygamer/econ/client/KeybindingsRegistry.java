package dev.psygamer.econ.client;

import net.minecraft.client.settings.KeyBinding;

import net.minecraftforge.fml.client.registry.ClientRegistry;

import org.lwjgl.glfw.GLFW;

public final class KeybindingsRegistry {
	
	public static final KeyBinding BANK_ACCOUNT_KEYBINDING = KeybindingsRegistry.register("bank_account", "econ", GLFW.GLFW_KEY_B);
	
	private KeybindingsRegistry() {
	}
	
	public static void load() {
	}
	
	private static KeyBinding register(final String keybindingName, final String keybindingCategory, final int glfwKeyID) {
		final KeyBinding keyBinding = new KeyBinding("key.econ." + keybindingName, glfwKeyID, "key.categories." + keybindingCategory);
		
		ClientRegistry.registerKeyBinding(keyBinding);
		
		return keyBinding;
	}
}
