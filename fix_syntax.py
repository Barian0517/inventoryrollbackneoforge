import os

def fix_file(path, fixes):
    if not os.path.exists(path): return
    with open(path, 'r', encoding='utf-8') as f:
        text = f.read()
    
    changed = False
    for old, new in fixes.items():
        if old in text:
            text = text.replace(old, new)
            changed = True
            
    if changed:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(text)
            
# 1. EventLogs.java
event_logs = r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\events\EventLogs.java'
fix_file(event_logs, {
    'import net.minecraftforge.eventbus.api.SubscribeEvent;': 'import net.neoforged.bus.api.SubscribeEvent;',
    '@Mod.EventBusSubscriber(modid = "inventoryrollbackplus", bus = Mod.EventBusSubscriber.Bus.FORGE)': '@Mod.EventBusSubscriber(modid = "inventoryrollbackplus", bus = net.neoforged.fml.common.EventBusSubscriber.Bus.GAME)',
    'CuriosApi.getCuriosInventory(player)': 'top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player)',
    'IItemHandlerModifiable equipped': 'net.neoforged.items.IItemHandlerModifiable equipped'
})

# 2. IRCommands.java
ir_commands = r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\commands\IRCommands.java'
fix_file(ir_commands, {
    'import net.minecraftforge.eventbus.api.SubscribeEvent;': 'import net.neoforged.bus.api.SubscribeEvent;'
})

# 3. BackupStorage.java
backup_storage = r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\data\BackupStorage.java'
fix_file(backup_storage, {
    'net.minecraftforge.server.ServerLifecycleHooks': 'net.neoforged.neoforge.server.ServerLifecycleHooks',
    'net.neoforged.server.ServerLifecycleHooks': 'net.neoforged.neoforge.server.ServerLifecycleHooks',
    'NbtIo.writeCompressed(snapshot.toNBT(), backupFile.toFile());': 'NbtIo.writeCompressed(snapshot.toNBT(), backupFile);',
    'CompoundTag tag = NbtIo.readCompressed(path.toFile());': 'CompoundTag tag = NbtIo.readCompressed(path, net.minecraft.nbt.NbtAccounter.unlimitedHeap());'
})

# 4. BackupMenuProvider.java
backup_menu = r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\gui\BackupMenuProvider.java'
fix_file(backup_menu, {
    'icon.setHoverName(Component.literal("§e§l" + backup.logType.name()).withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));': 'icon.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§e§l" + backup.logType.name()).withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));',
    'CompoundTag display = icon.getOrCreateTagElement("display");': 'java.util.List<Component> lore = new java.util.ArrayList<>();',
    'net.minecraft.nbt.ListTag lore = new net.minecraft.nbt.ListTag();': '',
    'lore.add(net.minecraft.nbt.StringTag.valueOf(net.minecraft.network.chat.Component.Serializer.toJson(Component.literal("§7Reason: §f" + backup.deathReason).withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)))));': 'lore.add(Component.literal("§7Reason: §f" + backup.deathReason).withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));',
    'lore.add(net.minecraft.nbt.StringTag.valueOf(net.minecraft.network.chat.Component.Serializer.toJson(Component.literal("§7Date: §f" + sdf.format(new java.util.Date(backup.timestamp))).withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)))));': 'lore.add(Component.literal("§7Date: §f" + sdf.format(new java.util.Date(backup.timestamp))).withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));',
    'display.put("Lore", lore);': 'icon.set(net.minecraft.core.component.DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(lore));',
    'back.setHoverName(Component.literal("§fBack").withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));': 'back.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§fBack").withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));'
})

# 5. MainMenuProvider.java & PlayerMenuProvider.java
main_menu = r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\gui\MainMenuProvider.java'
player_menu = r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\gui\PlayerMenuProvider.java'

fix_file(main_menu, {
    'new net.minecraft.world.item.component.ResolvableProfile(entry.name)': 'new net.minecraft.world.item.component.ResolvableProfile(new com.mojang.authlib.GameProfile(java.util.UUID.nameUUIDFromBytes(entry.name.getBytes()), entry.name))'
})

fix_file(player_menu, {
    'new net.minecraft.world.item.component.ResolvableProfile(targetName)': 'new net.minecraft.world.item.component.ResolvableProfile(new com.mojang.authlib.GameProfile(java.util.UUID.nameUUIDFromBytes(targetName.getBytes()), targetName))'
})

# 6. PlayerDataSnapshot.java
snapshot_file = r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\data\PlayerDataSnapshot.java'
fix_file(snapshot_file, {
    'stack.save(itemTag);': 'net.minecraft.nbt.Tag savedTag = ItemStack.CODEC.encodeStart(net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer().registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), stack).getOrThrow(); itemTag.put("item", savedTag);',
    'list.set(slot, ItemStack.of(itemTag));': 'list.set(slot, ItemStack.parseOptional(net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer().registryAccess(), itemTag.getCompound("item")));',
    'import net.minecraft.world.item.ItemStack;': 'import net.minecraft.world.item.ItemStack;\nimport net.minecraft.core.RegistryAccess;'
})

print("Fixed syntax errors.")
