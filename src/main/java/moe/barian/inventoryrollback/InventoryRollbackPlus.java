package moe.barian.inventoryrollback;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(InventoryRollbackPlus.MODID)
public class InventoryRollbackPlus {
    public static final String MODID = "inventoryrollbackplus";
    public static final Logger LOGGER = LogManager.getLogger();

    public InventoryRollbackPlus(IEventBus modEventBus) {
        modEventBus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("InventoryRollbackPlus setup");
    }
}
