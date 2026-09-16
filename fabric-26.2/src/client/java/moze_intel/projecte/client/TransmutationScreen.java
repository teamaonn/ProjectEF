package moze_intel.projecte.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

public class TransmutationScreen extends Screen {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("projecte", "textures/gui/transmute.png");
    // ProjectE's sixteen output positions from TransmutationContainer (1.21.1).
    private static final int[][] ICONS = {{158,9},{176,13},{193,30},{199,50},
            {193,70},{176,87},{158,91},{140,87},{123,70},{116,50},
            {123,30},{140,13},{158,31},{177,50},{158,69},{139,50}};
    private static String rememberedSearch = "";
    private static int rememberedPage;
    private final boolean tablet;
    private final long position;
    private final long balance;
    private final List<String> learned;
    private List<String> filtered = List.of();
    private EditBox search;
    private int page, left, top;

    public TransmutationScreen(PEPackets.Snapshot snapshot) {
        super(Component.literal(snapshot.tablet() ? "Transmutation Tablet" : "Transmutation Table"));
        tablet = snapshot.tablet();
        position = snapshot.position();
        balance = snapshot.balance();
        learned = snapshot.learned();
        page = rememberedPage;
    }

    @Override protected void init() {
        left = (width - 228) / 2;
        top = (height - 196) / 2;
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
        filtered = matches;
        page = Math.max(0, Math.min(page, Math.max(0, (matches.size() - 1) / ICONS.length)));
        rememberedPage = page;
    }

    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, left, top, 0, 0, 228, 196, 256, 256);
        // Screen extracts its widgets before this textured panel. Extract search again above it.
        search.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
        if (page > 0) graphics.text(font, "<", left + 129, top + 102, 0xFF404040, false);
        if ((page + 1) * ICONS.length < filtered.size())
            graphics.text(font, ">", left + 197, top + 102, 0xFF404040, false);
        graphics.text(font, tablet ? "Tablet" : "Transmutation", left + 6, top + 8, 0xFF404040, false);
        graphics.text(font, "EMC: " + balance, left + 6, top + 100, 0xFF404040, false);
        if (Minecraft.getInstance().player != null) {
            var inventory = Minecraft.getInstance().player.getInventory();
            for (int i = 0; i < 36; i++) {
                int slot = i < 27 ? i + 9 : i - 27;
                ItemStack stack = inventory.getItem(slot);
                int x = left + 35 + (i % 9) * 18, y = top + 116 + (i / 9) * 20;
                if (!stack.isEmpty()) {
                    graphics.item(stack, x, y);
                    graphics.itemDecorations(font, stack, x, y);
                    if (inside(mouseX, mouseY, x, y, 18))
                        graphics.setTooltipForNextFrame(font, stack.getHoverName(), mouseX, mouseY);
                }
            }
        }
        for (int i = 0; i < ICONS.length && page * ICONS.length + i < filtered.size(); i++) {
            String id = filtered.get(page * ICONS.length + i);
            Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(id));
            int x = left + ICONS[i][0], y = top + ICONS[i][1];
            ItemStack stack = new ItemStack(item);
            graphics.item(stack, x, y);
            if (inside(mouseX, mouseY, x, y, 17))
                graphics.setTooltipForNextFrame(font,
                        Component.literal(stack.getHoverName().getString() + " · " + PETransmutationState.value(id) + " EMC"),
                        mouseX, mouseY);
        }
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int size) {
        return mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size;
    }

    @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;
        double mouseX = event.x(), mouseY = event.y();
        int button = event.button();
        if (page > 0 && inside(mouseX, mouseY, left + 125, top + 100, 14)) {
            page--; refreshList(); return true;
        }
        if ((page + 1) * ICONS.length < filtered.size()
                && inside(mouseX, mouseY, left + 193, top + 100, 14)) {
            page++; refreshList(); return true;
        }
        if (Minecraft.getInstance().player != null) {
            var inventory = Minecraft.getInstance().player.getInventory();
            for (int i = 0; i < 36; i++) {
                int slot = i < 27 ? i + 9 : i - 27;
                int x = left + 35 + (i % 9) * 18, y = top + 116 + (i / 9) * 20;
                ItemStack stack = inventory.getItem(slot);
                if (inside(mouseX, mouseY, x, y, 18) && !stack.isEmpty()) {
                    String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                    if (PETransmutationState.value(id) > 0)
                        send("sell", slot, "", button == 1 ? 1 : stack.getCount());
                    return true;
                }
            }
        }
        for (int i = 0; i < ICONS.length && page * ICONS.length + i < filtered.size(); i++) {
            int x = left + ICONS[i][0], y = top + ICONS[i][1];
            if (inside(mouseX, mouseY, x, y, 17)) {
                send("buy", 0, filtered.get(page * ICONS.length + i), button == 1 ? 64 : 1);
                return true;
            }
        }
        return false;
    }

    private void send(String command, int slot, String id, int count) {
        ClientPlayNetworking.send(new PEPackets.Action(tablet, position, command, slot, id, count));
    }

    @Override public boolean isPauseScreen() { return false; }
}
