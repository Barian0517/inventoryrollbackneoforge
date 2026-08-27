import os

source = r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\forge-1.20.1\src\main\java\moe\barian\inventoryrollback\gui\BackupViewMenuProvider.java'
dest = r'C:\Users\user\Desktop\code\MCMOD\InventoryRollbackPlus\neoforge1.21.1\src\main\java\moe\barian\inventoryrollback\gui\BackupViewMenuProvider.java'

with open(source, 'r', encoding='utf-8', errors='ignore') as f:
    text = f.read()

# Replace forge with neoforge
text = text.replace('net.minecraftforge', 'net.neoforged')
text = text.replace('MinecraftForge', 'NeoForge')

# Replace createButton logic
old_create_button = '''    private ItemStack createButton(net.minecraft.world.item.Item item, String name) {
        ItemStack stack = new ItemStack(item);
        stack.setHoverName(Component.literal(name));
        return stack;
    }'''

new_create_button = '''    private ItemStack createButton(net.minecraft.world.item.Item item, String name) {
        ItemStack stack = new ItemStack(item);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal(name));
        return stack;
    }'''

text = text.replace(old_create_button, new_create_button)

# Replace giveChestBundle logic
# First find the exact old method using substring or regex, but substring is safer since we know the content
old_give_chest_start = 'private void giveChestBundle(ServerPlayer admin) {'
old_give_chest_full = text[text.find(old_give_chest_start): text.rfind('}')]
old_give_chest_full = old_give_chest_full[:old_give_chest_full.rfind('}')]
old_give_chest_full = text[text.find(old_give_chest_start): text.rfind('}', text.find(old_give_chest_start)) + 1]

# Actually it's easier to just split by the start of the method and rewrite it entirely
prefix = text[:text.find(old_give_chest_start)]

new_give_chest = '''    private void giveChestBundle(ServerPlayer admin) {
        ItemStack chest = new ItemStack(net.minecraft.world.item.Items.CHEST);
        chest.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("§e§l[Backup Bundle: " + targetName + "]").withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)));
        
        // Add glint effect
        chest.set(net.minecraft.core.component.DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        // Add lore
        java.util.List<Component> loreLines = java.util.List.of(
            Component.literal("§7Contains backed up items for " + targetName).withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false)),
            Component.literal("§7Place this chest down to access them.").withStyle(net.minecraft.network.chat.Style.EMPTY.withItalic(false))
        );
        chest.set(net.minecraft.core.component.DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(loreLines));

        // Get items to bundle
        java.util.List<ItemStack> itemsToBundle = viewType == ViewType.ENDER_CHEST ? snapshot.enderChest : 
                                                  (viewType == ViewType.CURIOS ? snapshot.curios : snapshot.mainInventory);
        
        // Sublist to max 27 items (chest size)
        java.util.List<ItemStack> chestItems = new java.util.ArrayList<>();
        for (int i = 0; i < Math.min(27, itemsToBundle.size()); i++) {
            chestItems.add(itemsToBundle.get(i) == null ? ItemStack.EMPTY : itemsToBundle.get(i).copy());
        }
        
        // Set container contents
        chest.set(net.minecraft.core.component.DataComponents.CONTAINER, net.minecraft.world.item.component.ItemContainerContents.fromItems(chestItems));
        
        admin.getInventory().add(chest);
        admin.sendSystemMessage(Component.literal("§aGiven backup chest bundle."));
    }
}
'''

with open(dest, 'w', encoding='utf-8') as f:
    f.write(prefix + new_give_chest)

print("Done.")
