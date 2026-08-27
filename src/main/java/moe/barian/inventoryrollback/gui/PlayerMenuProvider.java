package moe.barian.inventoryrollback.gui;

import moe.barian.inventoryrollback.data.LogType;
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

import java.util.UUID;

public class PlayerMenuProvider implements MenuProvider {

    private final UUID targetUUID;
    private final String targetName;

    public PlayerMenuProvider(UUID targetUUID, String targetName) {
        this.targetUUID = targetUUID;
        this.targetName = targetName;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Category: " + targetName);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        SimpleContainer container = new SimpleContainer(9);
        
        // Slot 0: Player Head (Back button)
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        head.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§e" + targetName).withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
        head.set(net.minecraft.core.component.DataComponents.PROFILE, new net.minecraft.world.item.component.ResolvableProfile(new com.mojang.authlib.GameProfile(java.util.UUID.nameUUIDFromBytes(targetName.getBytes()), targetName)));
        container.setItem(0, head);
        
        // Slot 2: Death
        ItemStack death = new ItemStack(Items.SKELETON_SKULL);
        death.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§cDeath Backups").withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
        container.setItem(2, death);
        
        // Slot 3: Join
        ItemStack join = new ItemStack(Items.SLIME_BALL);
        join.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§aJoin Backups").withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
        container.setItem(3, join);
        
        // Slot 4: Quit
        ItemStack quit = new ItemStack(Items.REDSTONE);
        quit.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§4Quit Backups").withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
        container.setItem(4, quit);
        
        // Slot 5: World Change
        ItemStack worldChange = new ItemStack(Items.ENDER_PEARL);
        worldChange.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§dWorld Change Backups").withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
        container.setItem(5, worldChange);
        
        // Slot 6: Force Save
        ItemStack force = new ItemStack(Items.PAPER);
        force.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§bForce Backups").withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
        container.setItem(6, force);

        return new ReadOnlyChestMenu(MenuType.GENERIC_9x1, containerId, playerInventory, container, 1, null) {
            @Override
            public void clicked(int slotId, int button, net.minecraft.world.inventory.ClickType clickType, Player playerEntity) {
                super.clicked(slotId, button, clickType, playerEntity);
                
                if (playerEntity instanceof ServerPlayer serverPlayer) {
                    if (slotId == 0) {
                        serverPlayer.openMenu(new MainMenuProvider(1));
                    } else if (slotId == 2) {
                        serverPlayer.openMenu(new BackupMenuProvider(targetUUID, targetName, LogType.DEATH));
                    } else if (slotId == 3) {
                        serverPlayer.openMenu(new BackupMenuProvider(targetUUID, targetName, LogType.JOIN));
                    } else if (slotId == 4) {
                        serverPlayer.openMenu(new BackupMenuProvider(targetUUID, targetName, LogType.QUIT));
                    } else if (slotId == 5) {
                        serverPlayer.openMenu(new BackupMenuProvider(targetUUID, targetName, LogType.WORLD_CHANGE));
                    } else if (slotId == 6) {
                        serverPlayer.openMenu(new BackupMenuProvider(targetUUID, targetName, LogType.FORCE));
                    }
                }
            }
        };
    }
}
