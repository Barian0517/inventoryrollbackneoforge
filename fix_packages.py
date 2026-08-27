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

files = [
    r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\commands\IRCommands.java',
    r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\events\EventLogs.java',
    r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\InventoryRollbackPlus.java',
    r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\gui\BackupMenuProvider.java',
    r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\gui\BackupViewMenuProvider.java'
]

for f in files:
    fix_file(f, {
        'net.neoforged.event.RegisterCommandsEvent': 'net.neoforged.neoforge.event.RegisterCommandsEvent',
        'net.neoforged.event.entity.living.LivingDeathEvent': 'net.neoforged.neoforge.event.entity.living.LivingDeathEvent',
        'net.neoforged.event.entity.player.PlayerEvent': 'net.neoforged.neoforge.event.entity.player.PlayerEvent',
        'net.neoforged.eventbus.api.SubscribeEvent': 'net.neoforged.bus.api.SubscribeEvent',
        'Mod.EventBusSubscriber.Bus.FORGE': 'net.neoforged.fml.common.EventBusSubscriber.Bus.GAME',
        'net.neoforged.items.IItemHandlerModifiable': 'net.neoforged.neoforge.items.IItemHandlerModifiable',
        'net.neoforged.common.NeoForge': 'net.neoforged.neoforge.common.NeoForge'
    })
print("Fixed package names.")
