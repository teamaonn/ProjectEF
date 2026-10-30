package moze_intel.projecte.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import moze_intel.projecte.PEPackets;
import moze_intel.projecte.PETransmutationState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TransmutationScreen extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<moze_intel.projecte.TransmutationMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("projecte", "textures/gui/transmute.png");
    // ProjectE's sixteen output positions from TransmutationContainer (1.21.1).
    private static final int[][] ICONS = {{158,9},{176,13},{193,30},{199,50},
            {193,70},{176,87},{158,91},{140,87},{123,70},{116,50},
            {123,30},{140,13},{158,31},{177,50},{158,69},{139,50}};
    private static String rememberedSearch = "";
    private static int rememberedPage;
    private boolean tablet;
    private long position;
    private long balance;
    private List<String> learned = List.of();
    private Map<String, Long> prices = Map.of();
    private List<ItemStack> variants = List.of();
    private List<String> favorites = List.of();
    private boolean favoritesOnly;
    private boolean sweeping;
    private final java.util.Set<Integer> sweptSlots = new java.util.HashSet<>();
    private List<String> filtered = List.of();
    private EditBox search;
    private int page, left, top;

    public TransmutationScreen(moze_intel.projecte.TransmutationMenu menu, net.minecraft.world.entity.player.Inventory inventory, Component title) {
        super(menu, inventory, title, 228, 196);
        page = rememberedPage;
    }
    public void update(PEPackets.Snapshot snapshot) {
        tablet = snapshot.tablet(); position = snapshot.position(); balance = snapshot.balance();
        learned = snapshot.learned(); prices = snapshot.prices(); variants = snapshot.variants(); favorites = snapshot.favorites();
        refreshList();
    }
    private long value(String id) { return moze_intel.projecte.PEEmcBlacklist.contains(id) ? 0 : prices.getOrDefault(id, PETransmutationState.value(id)); }

    private int inventoryY(int index) {
        int row = index / 9;
        return top + 117 + row * 18 + (row == 3 ? 4 : 0);
    }

    private String label(ItemStack stack) {
        StringBuilder text = new StringBuilder(stack.getHoverName().getString());
        var enchantments = stack.getOrDefault(net.minecraft.core.component.DataComponents.ENCHANTMENTS,
                net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
        if (stack.is(net.minecraft.world.item.Items.ENCHANTED_BOOK))
            enchantments = stack.getOrDefault(net.minecraft.core.component.DataComponents.STORED_ENCHANTMENTS,
                    net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
        for (var entry : enchantments.entrySet())
            text.append(" · ").append(entry.getKey().value().description().getString()).append(" ").append(entry.getIntValue());
        return text.toString();
    }
    private ItemStack exemplar(String key) {
        if (key.startsWith("variant:")) return variants.get(Integer.parseInt(key.substring(8)));
        return new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(key)));
    }
    private long price(ItemStack stack) {
        return PETransmutationState.valueWithUpgrades(stack,
                value(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()));
    }

    @Override protected void init() {
        super.init();
        left = leftPos;
        top = topPos;
        search = addRenderableWidget(new EditBox(font, left + 83, top + 8, 55, 12, Component.literal("Search")));
        search.setValue(rememberedSearch);
        search.setResponder(value -> { rememberedSearch = value; page = 0; refreshList(); });
        refreshList();
    }

    private void refreshList() {
        String query = search == null ? "" : search.getValue().toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String id : learned) {
            Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(id));
            if (item.getName(new ItemStack(item)).getString().toLowerCase(Locale.ROOT).contains(query)) matches.add(id);
        }
        for (int i = 0; i < variants.size(); i++) {
            ItemStack stack = variants.get(i);
            if (label(stack).toLowerCase(Locale.ROOT).contains(query)) matches.add("variant:" + i);
        }
        if (favoritesOnly) matches.removeIf(key -> !favorites.contains(key));
        matches.sort(java.util.Comparator.comparingInt(key -> favorites.contains(key) ? 0 : 1));
        filtered = matches;
        page = Math.max(0, Math.min(page, Math.max(0, (matches.size() - 1) / ICONS.length)));
        rememberedPage = page;
    }

    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int x, int y) {}
    @Override public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, left, top, 0, 0, 228, 196, 256, 256);
        graphics.text(font, tablet ? "Tablet" : "Transmutation", left + 6, top + 8, 0xFF404040, false);
        graphics.text(font, "EMC: " + balance, left + 6, top + 100, 0xFF404040, false);
        graphics.fill(left + 41, top + 43, left + 59, top + 61, 0xFF777777);
        super.extractContents(graphics, mouseX, mouseY, partialTick);
        // The real input item is extracted by AbstractContainerScreen after this background.
        graphics.text(font, "Deposit", left + 8, top + 65, 0xFF404040, false);
        graphics.text(font, favoritesOnly ? "[Favorites]" : "[All]", left + 6, top + 84, 0xFF404040, false);
        if (page > 0) graphics.text(font, "<", left + 129, top + 102, 0xFF404040, false);
        if ((page + 1) * ICONS.length < filtered.size()) graphics.text(font, ">", left + 197, top + 102, 0xFF404040, false);
        for (int i = 0; i < ICONS.length && page * ICONS.length + i < filtered.size(); i++) {
            String id = filtered.get(page * ICONS.length + i);
            int x = left + ICONS[i][0], y = top + ICONS[i][1];
            ItemStack stack = exemplar(id);
            graphics.item(stack, x, y);
            if (favorites.contains(id)) graphics.text(font, "*", x + 11, y - 3, 0xFFFFAA00, false);
            if (inside(mouseX, mouseY, x, y, 17))
                graphics.setTooltipForNextFrame(font,
                        Component.literal(label(stack) + " · " + price(stack) + " EMC · Middle click: favorite"),
                        mouseX, mouseY);
        }
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int size) {
        return mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size;
    }

    @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x(), mouseY = event.y();
        int button = event.button();
        if (page > 0 && inside(mouseX, mouseY, left + 125, top + 100, 14)) {
            page--; refreshList(); return true;
        }
        if ((page + 1) * ICONS.length < filtered.size()
                && inside(mouseX, mouseY, left + 193, top + 100, 14)) {
            page++; refreshList(); return true;
        }
        if (mouseX >= left + 5 && mouseX < left + 75 && mouseY >= top + 81 && mouseY < top + 95) {
            favoritesOnly = !favoritesOnly; page = 0; refreshList(); return true;
        }
        // Shift-drag sells each visited inventory stack once. Ordinary dragging stays vanilla.
        if ((event.modifiers() & 1) != 0 && button == 0 && mouseY >= top + 117) {
            sweeping = true; sweptSlots.clear(); sweep(mouseX, mouseY); return true;
        }
        for (int i = 0; i < ICONS.length && page * ICONS.length + i < filtered.size(); i++) {
            int x = left + ICONS[i][0], y = top + ICONS[i][1];
            if (inside(mouseX, mouseY, x, y, 17)) {
                send(button == 2 ? "favorite" : "buy", 0, filtered.get(page * ICONS.length + i), button == 1 || (event.modifiers() & 1) != 0 ? 64 : 1);
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
    private void sweep(double x, double y) {
        for (int i = 0; i < 36; i++) {
            int slot = i < 27 ? i + 9 : i - 27;
            if (inside(x, y, left + 35 + i % 9 * 18, inventoryY(i), 18) && sweptSlots.add(slot)) send("sell", slot, "", 64);
        }
    }
    @Override public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (sweeping) { sweep(event.x(), event.y()); return true; }
        return super.mouseDragged(event, dx, dy);
    }
    @Override public boolean mouseReleased(MouseButtonEvent event) {
        if (sweeping) { sweeping = false; sweptSlots.clear(); return true; }
        return super.mouseReleased(event);
    }

    private void send(String command, int slot, String id, int count) {
        ClientPlayNetworking.send(new PEPackets.Action(tablet, position, command, slot, id, count));
    }

    @Override public boolean isPauseScreen() { return false; }
}
