package p455w0rd.danknull.inventory;

import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import p455w0rd.danknull.api.DankNullItemModes;
import p455w0rd.danknull.init.ModGlobals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class DankNullHandlerTest {
    private DankNullHandler dankNull;

    @Before
    public void setup() {
        Bootstrap.register();

        dankNull = new DankNullHandler(ModGlobals.DankNullTier.REDSTONE);
    }


    @Test
    public void normalInteractions() {
        ItemStack itemStack = new ItemStack(new Item(), 64);
        dankNull.setExtractionMode(itemStack, DankNullItemModes.ItemExtractionMode.KEEP_NONE);

        assertTrue(dankNull.insertItem(0, itemStack, false).isEmpty());
        assertTrue(dankNull.insertItem(0, itemStack, false).isEmpty());
        assertEquals(dankNull.insertItem(0, itemStack, false).getCount(), 64);
        assertTrue(dankNull.insertItem(1, itemStack, false).isEmpty());
        assertTrue(dankNull.insertItem(1, itemStack, false).isEmpty());
        assertEquals(dankNull.insertItem(1, itemStack, false).getCount(), 64);

        assertEquals(dankNull.extractItem(0, 64, false).getCount(), 64);
        assertEquals(dankNull.extractItem(0, 64, false).getCount(), 64);
        assertEquals(dankNull.extractItem(0, 64, false).getCount(), 0);
    }

    @Test
    public void extractItemLimited() {
        ItemStack itemStack = new ItemStack(new Item(), 64);
        dankNull.setExtractionMode(itemStack, DankNullItemModes.ItemExtractionMode.KEEP_16);

        assertTrue(dankNull.insertItem(0, itemStack, false).isEmpty());
        assertEquals(dankNull.extractItem(0, 64, false).getCount(), 48);
    }
}