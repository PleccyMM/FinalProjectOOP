package org.vexillum;

import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StockItemTest extends ItemPopulation {
    private List<StockItem> sort(List<StockItem> unsortedItems) {
        List<StockItem> sortedItems = new ArrayList<>();
        for (StockItem item : unsortedItems) {
            sortedItems.add(item.clone());
        }
        Collections.sort(sortedItems);
        return sortedItems;
    }

    /**
     * Annoyingly long setup for the equality tests, just creates a bunch of flags and cushions with some identical, some similar that they
     * should be seen as equals and others completely different. Each time you go down the list they have one feature changed
     * @return the full list of items, completely unsorted
     */
    private List<StockItem> equityHashList() {
        List<StockItem> list = new ArrayList<>();
        Cushion c1 = database.createCushion("CF", CUSHION_SIZE.LONG, CUSHION_MATERIAL.FEATHERS);
        c1.setAmount(5);
        list.add(c1.clone());
        Cushion c2 = database.createCushion("CF", CUSHION_SIZE.LONG, CUSHION_MATERIAL.FEATHERS);
        c2.setAmount(11);
        list.add(c2.clone());
        Cushion c3 = database.createCushion("CF", CUSHION_SIZE.SMALL, CUSHION_MATERIAL.FEATHERS);
        c3.setAmount(7);
        list.add(c3.clone());
        Cushion c4 = database.createCushion("CF", CUSHION_SIZE.LONG, CUSHION_MATERIAL.EMPTY);
        c4.setAmount(2);
        list.add(c4.clone());
        Cushion c5 = database.createCushion("CF", CUSHION_SIZE.LONG, CUSHION_MATERIAL.FEATHERS);
        c5.setAmount(-5);
        list.add(c5.clone());
        Flag f1 = database.createFlag("CF", FLAG_SIZE.SMALL);
        f1.setAmount(-3);
        f1.setHoist(FLAG_HOIST.WOODEN);
        f1.setMaterial(FLAG_MATERIAL.NYLON);
        list.add(f1.clone());
        Flag f2 = database.createFlag("CF", FLAG_SIZE.SMALL);
        f2.setAmount(-11);
        f2.setHoist(FLAG_HOIST.WOODEN);
        f2.setMaterial(FLAG_MATERIAL.NYLON);
        list.add(f2.clone());
        Flag f3 = database.createFlag("CF", FLAG_SIZE.LARGE);
        f3.setAmount(7);
        f3.setHoist(FLAG_HOIST.WOODEN);
        f3.setMaterial(FLAG_MATERIAL.NYLON);
        list.add(f3.clone());
        Flag f4 = database.createFlag("CF", FLAG_SIZE.SMALL);
        f4.setAmount(-2);
        f4.setHoist(FLAG_HOIST.METAL);
        f4.setMaterial(FLAG_MATERIAL.NYLON);
        list.add(f4.clone());
        Flag f5 = database.createFlag("CF", FLAG_SIZE.SMALL);
        f5.setAmount(-8);
        f5.setHoist(FLAG_HOIST.WOODEN);
        f5.setMaterial(FLAG_MATERIAL.POLYESTER);
        list.add(f5.clone());

        return list;
    }

    /**
     * Ensures that the {@code equals()} function works properly for all potential conditions
     */
    @Test
    @Order(0)
    public void stockItemEqualityTest() {
        database.openDBSession();

        List<StockItem> equityList = equityHashList();

        Cushion c1 = database.createCushion("MP", CUSHION_SIZE.SMALL, CUSHION_MATERIAL.FOAM);
        c1.setAmount(2);
        Cushion c2 = database.createCushion("MP", CUSHION_SIZE.SMALL, CUSHION_MATERIAL.FOAM);
        c2.setAmount(2);

        database.closeDBSession();

        //Ignore all the warnings, it has to use the equals() function as that's the point of this test scenario

        assertTrue(c1.equals(c2));

        assertTrue(equityList.get(0).equals(equityList.get(1)));
        assertFalse(equityList.get(0).equals(equityList.get(2)));
        assertFalse(equityList.get(0).equals(equityList.get(3)));
        assertFalse(equityList.get(0).equals(equityList.get(4)));
        assertFalse(equityList.get(0).equals(equityList.get(5)));

        assertTrue(equityList.get(5).equals(equityList.get(6)));
        assertFalse(equityList.get(5).equals(equityList.get(7)));
        assertFalse(equityList.get(5).equals(equityList.get(8)));
        assertFalse(equityList.get(5).equals(equityList.get(9)));
    }

    /**
     * Ensures the {@code hashCode()} function is producing unique codes when appropriate
     */
    @Test
    @Order(0)
    public void stockItemHashTest() {
        database.openDBSession();

        List<StockItem> hashList = equityHashList();

        database.closeDBSession();

        assertEquals(hashList.get(0).hashCode(), hashList.get(1).hashCode());
        assertNotEquals(hashList.get(0).hashCode(), hashList.get(2).hashCode());
        assertNotEquals(hashList.get(0).hashCode(), hashList.get(3).hashCode());
        assertNotEquals(hashList.get(0).hashCode(), hashList.get(4).hashCode());
        assertNotEquals(hashList.get(0).hashCode(), hashList.get(5).hashCode());

        assertEquals(hashList.get(5).hashCode(), hashList.get(6).hashCode());
        assertNotEquals(hashList.get(5).hashCode(), hashList.get(7).hashCode());
        assertNotEquals(hashList.get(5).hashCode(), hashList.get(8).hashCode());
        assertNotEquals(hashList.get(5).hashCode(), hashList.get(9).hashCode());
    }

    /**
     * Ensures that an already sorted list isn't sorted out of order
     */
    @Test
    @Order(0)
    public void stockItemAlreadySortedTest() {
        database.openDBSession();

        List<StockItem> unsortedItems = new ArrayList<>();
        unsortedItems.add(database.createFlag("AG", FLAG_SIZE.MEDIUM));
        unsortedItems.add(database.createCushion("PY", CUSHION_SIZE.LARGE, CUSHION_MATERIAL.FOAM));

        database.closeDBSession();

        unsortedItems.get(0).setAmount(-3);
        unsortedItems.get(1).setAmount(3);

        List<StockItem> sortedItems = sort(unsortedItems);

        assertNotSame(sortedItems, unsortedItems);

        assertEquals(sortedItems, unsortedItems);
    }

    /**
     * Ensures imported items are placed ahead of exported items when sorting
     */
    @Test
    @Order(1)
    public void stockItemImportSortTest() {
        database.openDBSession();

        List<StockItem> unsortedItems = new ArrayList<>();
        unsortedItems.add(database.createFlag("FR", FLAG_SIZE.MEDIUM));
        unsortedItems.add(database.createFlag("FR", FLAG_SIZE.MEDIUM));

        database.closeDBSession();

        unsortedItems.get(0).setAmount(3);
        unsortedItems.get(1).setAmount(-7);

        List<StockItem> sortedItems = sort(unsortedItems);

        assertNotSame(sortedItems, unsortedItems);

        assertEquals(sortedItems.get(0), unsortedItems.get(1));
        assertEquals(sortedItems.get(1), unsortedItems.get(0));

    }

    /**
     * Ensures items are lexicographically ordered
     */
    @Test
    @Order(1)
    public void stockItemNameSortTest() {
        database.openDBSession();

        List<StockItem> unsortedItems = new ArrayList<>();
        unsortedItems.add(database.createFlag("DE", FLAG_SIZE.MEDIUM));
        unsortedItems.add(database.createFlag("AM", FLAG_SIZE.MEDIUM));

        database.closeDBSession();

        unsortedItems.get(0).setAmount(5);
        unsortedItems.get(1).setAmount(3);

        List<StockItem> sortedItems = sort(unsortedItems);

        assertNotSame(sortedItems, unsortedItems);

        assertNotEquals(sortedItems, unsortedItems);
        assertEquals(sortedItems.get(0), unsortedItems.get(1));
        assertEquals(sortedItems.get(1), unsortedItems.get(0));
    }

    /**
     * Ensures items are sorted with smaller sizes first
     */
    @Test
    @Order(1)
    public void stockItemSizeSortTest() {
        database.openDBSession();

        List<StockItem> unsortedItems = new ArrayList<>();
        unsortedItems.add(database.createFlag("OR-AF", FLAG_SIZE.LARGE));
        unsortedItems.add(database.createFlag("OR-AF", FLAG_SIZE.MEDIUM));

        database.closeDBSession();

        unsortedItems.get(0).setAmount(-2);
        unsortedItems.get(1).setAmount(-11);

        List<StockItem> sortedItems = sort(unsortedItems);

        assertNotSame(sortedItems, unsortedItems);

        assertEquals(sortedItems.get(0), unsortedItems.get(1));
        assertEquals(sortedItems.get(1), unsortedItems.get(0));
    }

    /**
     * Ensures items are sorted with flags ahead of cushions
     */
    @Test
    @Order(1)
    public void stockItemTypeSortTest() {
        database.openDBSession();

        List<StockItem> unsortedItems = new ArrayList<>();
        unsortedItems.add(database.createCushion("TF", CUSHION_SIZE.SMALL, CUSHION_MATERIAL.EMPTY));
        unsortedItems.add(database.createFlag("TF", FLAG_SIZE.LARGE));

        database.closeDBSession();

        unsortedItems.get(0).setAmount(-7);
        unsortedItems.get(1).setAmount(-4);

        List<StockItem> sortedItems = sort(unsortedItems);

        assertNotSame(sortedItems, unsortedItems);

        assertEquals(sortedItems.get(0), unsortedItems.get(1));
        assertEquals(sortedItems.get(1), unsortedItems.get(0));
    }

    /**
     * Tests all parts of the sorting conditions together on a larger unsorted list, to see if it can correctly place them
     */
    @Test
    @Order(2)
    public void stockItemFullSortTest() {
        database.openDBSession();

        List<StockItem> unsortedItems = new ArrayList<>();
        unsortedItems.add(database.createFlag("GB", FLAG_SIZE.SMALL));
        unsortedItems.add(database.createFlag("GB", FLAG_SIZE.LARGE));
        unsortedItems.add(database.createFlag("AM", FLAG_SIZE.MEDIUM));
        unsortedItems.add(database.createFlag("DE", FLAG_SIZE.MEDIUM));

        unsortedItems.add(database.createCushion("CY-CO", CUSHION_SIZE.MEDIUM, CUSHION_MATERIAL.FOAM));
        unsortedItems.add(database.createCushion("IE", CUSHION_SIZE.SMALL, CUSHION_MATERIAL.COTTON));
        unsortedItems.add(database.createCushion("KM", CUSHION_SIZE.LARGE, CUSHION_MATERIAL.EMPTY));
        unsortedItems.add(database.createCushion("MM", CUSHION_SIZE.LARGE, CUSHION_MATERIAL.FEATHERS));
        unsortedItems.add(database.createCushion("ZM", CUSHION_SIZE.LONG, CUSHION_MATERIAL.EMPTY));

        database.closeDBSession();

        unsortedItems.get(0).setAmount(5);
        unsortedItems.get(1).setAmount(2);
        unsortedItems.get(2).setAmount(-4);
        unsortedItems.get(3).setAmount(3);
        unsortedItems.get(4).setAmount(-2);
        unsortedItems.get(5).setAmount(7);
        unsortedItems.get(6).setAmount(-8);
        unsortedItems.get(7).setAmount(-8);
        unsortedItems.get(8).setAmount(1);

        List<StockItem> sortedItems = sort(unsortedItems);

        assertNotSame(sortedItems, unsortedItems);

        assertEquals(sortedItems.get(0), unsortedItems.get(2));
        assertEquals(sortedItems.get(1), unsortedItems.get(6));
        assertEquals(sortedItems.get(2), unsortedItems.get(4));
        assertEquals(sortedItems.get(3), unsortedItems.get(7));
        assertEquals(sortedItems.get(4), unsortedItems.get(3));
        assertEquals(sortedItems.get(5), unsortedItems.get(5));
        assertEquals(sortedItems.get(6), unsortedItems.get(0));
        assertEquals(sortedItems.get(7), unsortedItems.get(1));
        assertEquals(sortedItems.get(8), unsortedItems.get(8));
    }

    /**
     * Ensures cloning provides exact matches
     */
    @Test
    @Order(3)
    public void cloneTest() {
        database.openDBSession();

        Flag flag = database.createFlag("PR-PR", FLAG_SIZE.SMALL);
        flag.setHoist(FLAG_HOIST.WOODEN);
        flag.setAmount(7);

        Cushion cushion = database.createCushion("OR-UN", CUSHION_SIZE.MEDIUM, CUSHION_MATERIAL.EMPTY);
        cushion.setAmount(-7);

        database.closeDBSession();

        StockItem flagClone = flag.clone();
        assertEquals(flagClone, flag);

        StockItem cushionClone = cushion.clone();
        assertEquals(cushionClone, cushion);
    }

    /**
     * Ensures the calculation of prices returns expected values
     */
    @Test
    @Order(4)
    public void priceTest() {
        database.openDBSession();

        List<StockItem> priceList = new ArrayList<>();
        Cushion c1 = database.createCushion("TG", CUSHION_SIZE.MEDIUM, CUSHION_MATERIAL.COTTON);
        c1.setAmount(1);
        priceList.add(c1.clone());
        Cushion c2 = database.createCushion("OR-CN", CUSHION_SIZE.LONG, CUSHION_MATERIAL.EMPTY);
        c2.setAmount(1);
        priceList.add(c2.clone());
        Cushion c3 = database.createCushion("CY-WT", CUSHION_SIZE.SMALL, CUSHION_MATERIAL.EMPTY);
        c3.setAmount(-1);
        priceList.add(c3.clone());

        Flag f1 = database.createFlag("NO", FLAG_SIZE.MEDIUM);
        f1.setHoist(FLAG_HOIST.WOODEN);
        f1.setMaterial(FLAG_MATERIAL.POLYESTER);
        f1.setAmount(1);
        priceList.add(f1.clone());
        Flag f2 = database.createFlag("CY-SX", FLAG_SIZE.DESK);
        f2.setMaterial(FLAG_MATERIAL.PAPER);
        f2.setAmount(1);
        priceList.add(f2.clone());
        Flag f3 = database.createFlag("PY", FLAG_SIZE.LARGE);
        f3.setAmount(-1);
        priceList.add(f3.clone());

        database.closeDBSession();

        assertEquals(22.0, priceList.get(0).calculatePrice());
        assertEquals(4.5, priceList.get(1).calculatePrice());
        assertEquals(3.0, priceList.get(2).calculatePrice());
        assertEquals(14.0, priceList.get(3).calculatePrice());
        assertEquals(3.6, priceList.get(4).calculatePrice());
        assertEquals(3.0, priceList.get(5).calculatePrice());
    }
}
