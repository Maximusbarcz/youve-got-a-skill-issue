package dev.mayaqq.ygasi.gui;

import dev.mayaqq.ygasi.util.GetAdvancementProgress;
import dev.mayaqq.ygasi.util.GrantAdvancementCriterion;
import eu.pb4.sgui.api.elements.*;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static dev.mayaqq.ygasi.registry.StatRegistry.SKILL_POINTS;

import dev.mayaqq.ygasi.registry.ConfigRegistry;
import net.minecraft.util.Identifier;

public class BranchGui {
    public static void gui(ServerPlayerEntity player) {
        int skillPoints = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(SKILL_POINTS));

        try {
            SkillGui gui = new SkillGui(ScreenHandlerType.GENERIC_9X3, player, false) {};

            gui.setTitle(Text.of("§3Skill Points: " + skillPoints));

            //background items
            for (int x = 0; x <= 26; x++) {
                gui.setSlot(x, new GuiElementBuilder()
                        .setItem(Items.GRAY_STAINED_GLASS_PANE)
                        .setName(Text.of(" "))
                );
            }
            for (int x = 10; x <= 16; x++) {
                gui.setSlot(x, new GuiElementBuilder()
                        .setItem(Items.LIGHT_BLUE_STAINED_GLASS_PANE)
                        .setName(Text.of(" "))
                );
            }
            for (int x = 0; x <= 2; x++) {
                for (int y = 2; y <= 6; y+=2) {
                    gui.setSlot(x * 9 + y, new GuiElementBuilder()
                            .setItem(Items.LIGHT_BLUE_STAINED_GLASS_PANE)
                            .setName(Text.of(" "))
                    );
                }
            }

            //branch items
            if (!GetAdvancementProgress.get(player, "mercenary")) {
                gui.setSlot(11, new GuiElementBuilder()
                        .setItem(Items.IRON_SWORD)
                        .setCustomModelData(1)
                        .hideFlag(ItemStack.TooltipSection.MODIFIERS)
                        .addLoreLine(Text.literal("Cost: " + ConfigRegistry.CONFIG.branchCost).setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY)))
                        .setName(Text.literal("Mercenary")
                                .setStyle(Style.EMPTY.withBold(true).withFormatting(Formatting.RED)))
                        .setCallback((index, clickType, actionType) -> save(player, "mercenary", "§cMercenary"))
                );
            } else {
                gui.setSlot(11, new GuiElementBuilder()
                        .setItem(Items.IRON_SWORD)
                        .hideFlag(ItemStack.TooltipSection.MODIFIERS)
                        .glow()
                        .setName(Text.literal("Mercenary")
                                .setStyle(Style.EMPTY.withBold(true).withFormatting(Formatting.RED)))
                        .setCallback((index, clickType, actionType) -> MercenaryGui.gui(player))
                );
            }

            if (!GetAdvancementProgress.get(player, "wizardry")) {
                gui.setSlot(13, new GuiElementBuilder()
                        .setItem(Items.BLAZE_ROD)
                        .addLoreLine(Text.literal("Cost: " + ConfigRegistry.CONFIG.branchCost).setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY)))
                        .setName(Text.literal("Wizardry")
                                .setStyle(Style.EMPTY.withBold(true).withFormatting(Formatting.DARK_PURPLE)))
                        .setCallback((index, clickType, actionType) -> save(player, "wizardry", "§5Wizardry"))
                );
            } else {
                gui.setSlot(13, new GuiElementBuilder()
                        .setItem(Items.BLAZE_ROD)
                        .glow()
                        .setName(Text.literal("Wizardry")
                                .setStyle(Style.EMPTY.withBold(true).withFormatting(Formatting.DARK_PURPLE)))
                        .setCallback((index, clickType, actionType) -> WizardryGui.gui(player))
                );
            }

            if (!GetAdvancementProgress.get(player, "druidry")) {
                gui.setSlot(15, new GuiElementBuilder()
                        .setItem(Items.OAK_SAPLING)
                        .addLoreLine(Text.literal("Cost: " + ConfigRegistry.CONFIG.branchCost).setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY)))
                        .setName(Text.literal("Druidry")
                                .setStyle(Style.EMPTY.withBold(true).withFormatting(Formatting.GREEN)))
                        .setCallback((index, clickType, actionType) -> save(player, "druidry", "§aDruidry"))
                );
            } else {
                gui.setSlot(15, new GuiElementBuilder()
                        .setItem(Items.OAK_SAPLING)
                        .glow()
                        .setName(Text.literal("Druidry")
                                .setStyle(Style.EMPTY.withBold(true).withFormatting(Formatting.GREEN)))
                        .setCallback((index, clickType, actionType) -> DruidryGui.gui(player))
                );
            }
            GrantAdvancementCriterion.grantAdvancementCriterion(player, new Identifier("minecraft", "ygasi/root"), "opened_skill_menu");

            gui.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void save(ServerPlayerEntity player, String branch, String branchName) {
        if (player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(SKILL_POINTS)) >= ConfigRegistry.CONFIG.branchCost) {
            player.getStatHandler().setStat(player, Stats.CUSTOM.getOrCreateStat(SKILL_POINTS), player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(SKILL_POINTS)) - ConfigRegistry.CONFIG.branchCost);
            player.sendMessage(Text.of("You have selected the §a" + branchName + " §fbranch!"), false);
            player.closeHandledScreen();
            if (branch.equals("mercenary")) {
                GrantAdvancementCriterion.grantAdvancementCriterion(player, new Identifier("minecraft", "ygasi/mercenary"), "unlocked_mercenary");
                MercenaryGui.gui(player);
            } else if (branch.equals("wizardry")) {
                GrantAdvancementCriterion.grantAdvancementCriterion(player, new Identifier("minecraft", "ygasi/wizardry"), "unlocked_wizardry");
                WizardryGui.gui(player);
            } else if (branch.equals("druidry")) {
                GrantAdvancementCriterion.grantAdvancementCriterion(player, new Identifier("minecraft", "ygasi/druidry"), "unlocked_druidry");
                DruidryGui.gui(player);
            }

        } else {
            player.sendMessage(Text.translatable("gui.ygasi.no.skill").setStyle(Style.EMPTY.withBold(true).withFormatting(Formatting.RED)), false);
            player.closeHandledScreen();
        }
    }
}