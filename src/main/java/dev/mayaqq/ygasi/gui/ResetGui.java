package dev.mayaqq.ygasi.gui;

import dev.mayaqq.ygasi.abilities.mercenary.Offence1;
import dev.mayaqq.ygasi.abilities.mercenary.Offence2;
import dev.mayaqq.ygasi.gui.common.SkillGui;
import dev.mayaqq.ygasi.registry.ConfigRegistry;
import dev.mayaqq.ygasi.util.AdvUtils;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import static dev.mayaqq.ygasi.registry.StatRegistry.SKILL_POINTS;
import static dev.mayaqq.ygasi.registry.StatRegistry.SKILL_POINTS_TOTAL;

public class ResetGui {
    public static void gui(ServerPlayerEntity player) {

        SkillGui gui = new SkillGui(ScreenHandlerType.GENERIC_9X3, player, false) {};

        //background items
        for (int x = 0; x <= 26; x++) {
            gui.setSlot(x, new GuiElementBuilder()
                    .setItem(Items.GRAY_STAINED_GLASS_PANE)
                    .setName(Text.of(" "))
            );
        }

        gui.setSlot(12, new GuiElementBuilder()
                .setItem(Items.GREEN_CONCRETE)
                .setName(Text.translatable("gui.ygasi.reset.confirm.title"))
                .addLoreLine(Text.translatable("gui.ygasi.reset.confirm.lore"))
                .setCallback((index, clickType, actionType) -> {
                    reset(player);
                    BranchGui.gui(player);
                    if (player.experienceLevel >= ConfigRegistry.CONFIG.resetCost) {
                        player.closeHandledScreen();
                        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        player.experienceLevel -= ConfigRegistry.CONFIG.resetCost;

                    } else {
                        player.sendMessage(Text.translatable("gui.ygasi.reset.fail"), true);
                        player.closeHandledScreen();
                    }
                })
        );

        gui.setSlot(14, new GuiElementBuilder()
                .setItem(Items.RED_CONCRETE)
                .setName(Text.translatable("gui.ygasi.reset.deny.title"))
                .addLoreLine(Text.translatable("gui.ygasi.reset.deny.lore"))
                .setCallback((index, clickType, actionType) -> BranchGui.gui(player))
        );

        gui.open();
    }
    public static void reset(ServerPlayerEntity player) {
        //check if player experience level is greater than 10
        //revoke the abilities first
        if (AdvUtils.getAdvancementProgress(player, "minecraft", "ygasi/mercenary")) {
            Offence1.revoke(player);
            Offence2.revoke(player);
        }
        AdvUtils.revokeAllAdvancements(player, "minecraft", "ygasi/");
        player.sendMessage(Text.translatable("gui.ygasi.reset.success"), true);
        player.getStatHandler().setStat(player, Stats.CUSTOM.getOrCreateStat(SKILL_POINTS), player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(SKILL_POINTS_TOTAL)));

    }
}