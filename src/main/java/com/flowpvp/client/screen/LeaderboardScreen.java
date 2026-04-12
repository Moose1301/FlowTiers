package com.flowpvp.client.screen;

import com.flowpvp.client.feature.LeaderboardManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class LeaderboardScreen extends Screen {

    private static final String[] MODES = {
        "GLOBAL", "SWORD", "AXE", "UHC", "VANILLA", "MACE", "DIAMOND_POT", "NETHERITE_OP", "SMP", "DIAMOND_SMP"
    };

    private String currentMode = "GLOBAL";
    private int scrollOffset = 0;

    public LeaderboardScreen() {
        super(Text.literal("FlowPvP Leaderboards"));
    }

    @Override
    protected void init() {
        int x = 20;

        // Tabs
        for (String mode : MODES) {
            addDrawableChild(ButtonWidget.builder(Text.literal(mode), btn -> {
                currentMode = mode;
                scrollOffset = 0;
                LeaderboardManager.load(mode);
            }).dimensions(x, 40, 80, 20).build());

            x += 85;
        }

        // Initial load
        LeaderboardManager.load(currentMode);
    }

    @Override
public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    scrollOffset -= verticalAmount * 10;

    if (scrollOffset < 0) scrollOffset = 0;

    return true;
}

    @Override
public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
    // ✅ correct for 1.21.x
    //this.renderBackground(ctx, mouseX, mouseY, delta);

    super.render(ctx, mouseX, mouseY, delta);

    ctx.drawCenteredTextWithShadow(
        textRenderer,
        "FlowPvP Leaderboards (" + currentMode + ")",
        width / 2,
        15,
        0xFFFFFF
    );

    var list = LeaderboardManager.getCached();

    int startY = 80 - scrollOffset;

    for (int i = 0; i < list.size(); i++) {
        var e = list.get(i);

        int y = startY + i * 14;

        if (y < 60 || y > height - 20) continue;

        ctx.drawTextWithShadow(textRenderer,
            "#" + e.position,
            40, y, 0xFFFF55);

        ctx.drawTextWithShadow(textRenderer,
            e.name,
            80, y, 0xFFFFFF);

        ctx.drawTextWithShadow(textRenderer,
            e.elo + " ELO",
            200, y, 0xAAAAAA);
    }
}
    @Override
    public boolean shouldPause() {
        return false;
    }
}