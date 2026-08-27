import os
import re
path = r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\gui\BackupViewMenuProvider.java'
with open(path, 'rb') as f:
    data = f.read()

text = data.decode('utf-8', errors='ignore')

# We can replace the specific bad line using a regex that catches the broken literal
bad_line_pattern = r'chest\.setHoverName\(Component\.literal\(".*?withItalic\(false\)\)\);'
new_line = 'chest.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§e§l[Backup Bundle: " + targetName + "]").withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));'

text = re.sub(bad_line_pattern, new_line, text)

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)
print('Fixed encoding issue.')
