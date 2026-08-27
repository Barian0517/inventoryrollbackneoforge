package moe.barian.inventoryrollback.gui;

import moe.barian.inventoryrollback.data.BackupStorage;
import moe.barian.inventoryrollback.data.LogType;
import moe.barian.inventoryrollback.data.PlayerDataSnapshot;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BackupMenuProvider implements MenuProvider {

    private final UUID targetUUID;
    private final String targetName;
    private final LogType logType;

    public BackupMenuProvider(UUID targetUUID, String targetName, LogType logType) {
        this.targetUUID = targetUUID;
        this.targetName = targetName;
        this.logType = logType;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Backups: " + targetName);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        SimpleContainer container = new SimpleContainer(54); // 6 rows
        
        List<PlayerDataSnapshot> backups = BackupStorage.getBackups(targetUUID, logType);
        
        // Populate container with backup icons
        int slot = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (PlayerDataSnapshot backup : backups) {
            if (slot >= 54) break;
            
            ItemStack icon = new ItemStack(Items.PAPER);
            if (backup.logType == LogType.DEATH) icon = new ItemStack(Items.SKELETON_SKULL);
            else if (backup.logType == LogType.JOIN) icon = new ItemStack(Items.SLIME_BALL);
            else if (backup.logType == LogType.QUIT) icon = new ItemStack(Items.REDSTONE);
            else if (backup.logType == LogType.WORLD_CHANGE) icon = new ItemStack(Items.ENDER_PEARL);
            
            icon.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§e§l" + backup.logType.name()).withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
            
            java.util.List<Component> lore = new java.util.ArrayList<>();
            lore.add(Component.literal("§7Time: §f" + sdf.format(new Date(backup.timestamp))).withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
            if (backup.deathReason != null) {
                lore.add(Component.literal("§7Reason: §c" + backup.deathReason).withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
            }
            icon.set(net.minecraft.core.component.DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(lore));
            
            container.setItem(slot, icon);
            slot++;
        }
        
        // Back Button
        ItemStack back = new ItemStack(Items.ARROW);
        back.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§fBack").withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
        container.setItem(53, back);
        
        return new ReadOnlyChestMenu(MenuType.GENERIC_9x6, containerId, playerInventory, container, 6, null) {
            @Override
            public void clicked(int slotId, int button, net.minecraft.world.inventory.ClickType clickType, Player player) {
                super.clicked(slotId, button, clickType, player);
                if (slotId == 53 && player instanceof ServerPlayer sp) {
                    sp.openMenu(new PlayerMenuProvider(targetUUID, targetName));
                    return;
                }
                
                moe.barian.inventoryrollback.InventoryRollbackPlus.LOGGER.info("Clicked slot " + slotId + " in BackupMenuProvider");
                if (slotId >= 0 && slotId < backups.size()) {
                    PlayerDataSnapshot selectedBackup = backups.get(slotId);
                    if (player instanceof ServerPlayer serverPlayer) {
                        moe.barian.inventoryrollback.InventoryRollbackPlus.LOGGER.info("Opening BackupViewMenuProvider for backup at " + selectedBackup.timestamp);
                        serverPlayer.openMenu(new BackupViewMenuProvider(selectedBackup, targetUUID, targetName, BackupViewMenuProvider.ViewType.MAIN, 0, () -> {
                            openMenu(serverPlayer, targetUUID, targetName, logType);
                        }));
                    }
                }
            }
        };
    }

    public static void openMenu(ServerPlayer execPlayer, UUID targetUUID, String targetName, LogType logType) {
        execPlayer.openMenu(new BackupMenuProvider(targetUUID, targetName, logType));
    }

    public static void openMenu(ServerPlayer execPlayer, ServerPlayer targetPlayer, LogType logType) {
        openMenu(execPlayer, targetPlayer.getUUID(), targetPlayer.getName().getString(), logType);
    }
}
