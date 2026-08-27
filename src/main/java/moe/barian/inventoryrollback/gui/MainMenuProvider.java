package moe.barian.inventoryrollback.gui;

import com.mojang.authlib.GameProfile;
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
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class MainMenuProvider implements MenuProvider {

    private final int page;

    public MainMenuProvider(int page) {
        this.page = page;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Player Backups");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        SimpleContainer container = new SimpleContainer(54); // 6 rows
        
        List<PlayerEntry> entries = getAllPlayersWithBackups();
        
        int itemsPerPage = 45; // 5 rows
        int calculatedTotalPages = (int) Math.ceil((double) entries.size() / itemsPerPage);
        if (calculatedTotalPages == 0) calculatedTotalPages = 1;
        
        final int totalPages = calculatedTotalPages;
        int currentPage = Math.max(1, Math.min(page, totalPages));
        int startIndex = (currentPage - 1) * itemsPerPage;
        
        // Populate players
        for (int i = 0; i < itemsPerPage; i++) {
            int index = startIndex + i;
            if (index >= entries.size()) break;
            
            PlayerEntry entry = entries.get(index);
            ItemStack head = new ItemStack(Items.PLAYER_HEAD);
            head.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§e" + entry.name).withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
            
            // Set profile data component
            head.set(net.minecraft.core.component.DataComponents.PROFILE, new net.minecraft.world.item.component.ResolvableProfile(new com.mojang.authlib.GameProfile(java.util.UUID.nameUUIDFromBytes(entry.name.getBytes()), entry.name)));
            
            CompoundTag tag = new CompoundTag();
            tag.putString("SkullOwner", entry.name);
            tag.putString("TargetUUID", entry.uuid.toString());
            head.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
            
            container.setItem(i, head);
        }
        
        // Navigation buttons
        if (currentPage > 1) {
            ItemStack prev = new ItemStack(Items.ARROW);
            prev.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§fPrevious Page").withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
            container.setItem(45, prev);
        }
        if (currentPage < totalPages) {
            ItemStack next = new ItemStack(Items.ARROW);
            next.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§fNext Page").withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
            container.setItem(53, next);
        }

        return new ReadOnlyChestMenu(MenuType.GENERIC_9x6, containerId, playerInventory, container, 6, null) {
            @Override
            public void clicked(int slotId, int button, net.minecraft.world.inventory.ClickType clickType, Player playerEntity) {
                super.clicked(slotId, button, clickType, playerEntity);
                
                if (playerEntity instanceof ServerPlayer serverPlayer) {
                    if (slotId == 45 && currentPage > 1) {
                        serverPlayer.openMenu(new MainMenuProvider(currentPage - 1));
                    } else if (slotId == 53 && currentPage < totalPages) {
                        serverPlayer.openMenu(new MainMenuProvider(currentPage + 1));
                    } else if (slotId >= 0 && slotId < itemsPerPage) {
                        ItemStack item = container.getItem(slotId);
                        if (!item.isEmpty() && item.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA)) {
                            net.minecraft.world.item.component.CustomData customData = item.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
                            if (customData != null && customData.contains("TargetUUID")) {
                                CompoundTag tag = customData.copyTag();
                                UUID targetUUID = UUID.fromString(tag.getString("TargetUUID"));
                                String targetName = tag.getString("SkullOwner");
                                serverPlayer.openMenu(new PlayerMenuProvider(targetUUID, targetName));
                            }
                        }
                    }
                }
            }
        };
    }

    private List<PlayerEntry> getAllPlayersWithBackups() {
        List<PlayerEntry> list = new ArrayList<>();
        File worldDir = ServerLifecycleHooks.getCurrentServer().getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).toFile();
        Path backupsDir = worldDir.toPath().resolve("inventoryrollbackplus").resolve("backups");
        
        if (!Files.exists(backupsDir)) {
            return list;
        }
        
        try (Stream<Path> paths = Files.list(backupsDir)) {
            paths.filter(Files::isDirectory).forEach(path -> {
                String uuidStr = path.getFileName().toString();
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    String name = uuidStr;
                    Optional<GameProfile> profile = ServerLifecycleHooks.getCurrentServer().getProfileCache().get(uuid);
                    if (profile.isPresent()) {
                        name = profile.get().getName();
                    }
                    list.add(new PlayerEntry(uuid, name));
                } catch (IllegalArgumentException e) {
                    // Not a UUID directory, ignore
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        list.sort((a, b) -> a.name.compareToIgnoreCase(b.name));
        return list;
    }
    
    private static class PlayerEntry {
        UUID uuid;
        String name;
        PlayerEntry(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }
    }
}
