package org.vexillum;

import javafx.geometry.Bounds;
import javafx.geometry.VerticalDirection;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import java.util.*;

/**
 * A helper object that just populates the {@code items} list, mainly used for display
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class ItemPopulation extends ApplicationTest {
    protected final List<StockItem> testItems = new ArrayList<>();
    protected static final List<StockItem> stockItems = new ArrayList<>();
    private static final List<StockItem> unalteredStockItems = new ArrayList<>();
    protected static final DatabaseControl database = new DatabaseControl();

    /**
     * Saves the original amounts, so they can be reverted
     */
    @BeforeAll
    public static void saveStockInformation() {
        database.openDBSession();

        stockItems.add(database.createFlag("GI", FLAG_SIZE.SMALL));
        stockItems.add(database.createFlag("PR-TG", FLAG_SIZE.LARGE));
        stockItems.add(database.createFlag("CY-DE", FLAG_SIZE.SMALL));
        stockItems.add(database.createFlag("MU", FLAG_SIZE.DESK));
        stockItems.add(database.createFlag("PN", FLAG_SIZE.MEDIUM));

        stockItems.add(database.createCushion("TS", CUSHION_SIZE.LONG, CUSHION_MATERIAL.FEATHERS));
        stockItems.add(database.createCushion("ER", CUSHION_SIZE.LARGE, CUSHION_MATERIAL.EMPTY));
        stockItems.add(database.createCushion("CY-MO", CUSHION_SIZE.LARGE, CUSHION_MATERIAL.COTTON));
        stockItems.add(database.createCushion("MO", CUSHION_SIZE.SMALL, CUSHION_MATERIAL.EMPTY));

        for (StockItem item : stockItems) {
            unalteredStockItems.add(item.clone());
        }

        stockItems.get(2).setTotalAmount(12);
        stockItems.get(2).setRestock(4);

        stockItems.get(6).setTotalAmount(5);
        stockItems.get(6).setRestock(7);

        database.closeDBSession();
    }

    /**
     * Restores all stock with their original stock to the database
     */
    @AfterAll
    public static void restoreStockInformation() {
        database.openDBSession();

        for (StockItem item : unalteredStockItems) {
            database.updateAmountAndRestock(item.getStockID(), item.getSizeID(), item.getTotalAmount(), item.getRestock());
        }
        database.closeDBSession();
    }

    /**
     * Just fetches and modifies each item to the desired amounts
     */
    @BeforeEach
    public void setUpItems() throws InterruptedException {
        database.openDBSession();

        Flag flagSmallGibraltar = (Flag) stockItems.get(0).clone();
        flagSmallGibraltar.setAmount(3);
        flagSmallGibraltar.setHoist(FLAG_HOIST.NONE);
        flagSmallGibraltar.setMaterial(FLAG_MATERIAL.POLYESTER);

        Flag flagLargeTrans = (Flag) stockItems.get(1).clone();
        flagLargeTrans.setAmount(-7);

        database.updateAmountAndRestock(stockItems.get(2).getStockID(), stockItems.get(2).getSizeID(), 12, 4);
        Flag flagSmallDevon = (Flag) stockItems.get(2).clone();
        flagSmallDevon.setAmount(8);
        flagSmallDevon.setHoist(FLAG_HOIST.WOODEN);
        flagSmallDevon.setMaterial(FLAG_MATERIAL.NYLON);

        Flag flagDeskMauritius = (Flag) stockItems.get(3).clone();
        flagDeskMauritius.setAmount(2);
        flagDeskMauritius.setMaterial(FLAG_MATERIAL.PAPER);

        Flag flagMediumPitcairn = (Flag) stockItems.get(4).clone();
        flagMediumPitcairn.setAmount(-5);

        Cushion cushionLongTransnistria = (Cushion) stockItems.get(5).clone();
        cushionLongTransnistria.setAmount(1);

        database.updateAmountAndRestock(stockItems.get(6).getStockID(), stockItems.get(6).getSizeID(), 5, 7);
        Cushion cushionLargeEritrea = (Cushion) stockItems.get(6).clone();
        cushionLargeEritrea.setAmount(-3);
        cushionLargeEritrea.setMaterial(CUSHION_MATERIAL.EMPTY);

        Cushion cushionLargeMonmouthshire = (Cushion) stockItems.get(7).clone();
        cushionLargeMonmouthshire.setAmount(5);

        Cushion cushionSmallMacao = (Cushion) stockItems.get(8).clone();
        cushionSmallMacao.setAmount(-7);
        cushionLargeEritrea.setMaterial(CUSHION_MATERIAL.EMPTY);

        testItems.clear();
        testItems.add(flagSmallGibraltar);
        testItems.add(flagLargeTrans);
        testItems.add(flagSmallDevon);
        testItems.add(flagDeskMauritius);
        testItems.add(flagMediumPitcairn);
        testItems.add(cushionLongTransnistria);
        testItems.add(cushionLargeEritrea);
        testItems.add(cushionLargeMonmouthshire);
        testItems.add(cushionSmallMacao);

        Collections.sort(testItems);

        for (StockItem item : testItems) {
            System.out.println(item.getName());
        }

        database.closeDBSession();
    }

    /**
     * Scrolls to the given location inside {@code BasketController}, scrolls one box more than given so shouldn't be used to scroll
     * to the final item in the list
     */
    protected void scrollToExport(int indexOfBox, List<StockItem> stockItems) {
        ScrollPane scrBackground = lookup("#scrBackground").query();
        Bounds scrollBound = scrBackground.localToScene(scrBackground.getBoundsInLocal());

        int indexOneOver = stockItems.get(indexOfBox + 1).hashCode();
        HBox boxOneOver = lookup("#" + indexOneOver).query();
        Bounds boxBound;

        clickOn(scrBackground);
        do {
            scroll(1, VerticalDirection.DOWN);

            if (scrBackground.getVvalue() == scrBackground.getVmax()) {
                break;
            }

            boxBound = boxOneOver.localToScene(boxOneOver.getBoundsInLocal());
        } while (!scrollBound.intersects(boxBound));;
    }

    /**
     * Just a test to ensure setup is applied, as it was causing issues sometimes without this
     */
    @Test
    @Order(0)
    public void dummyTest() {

    }
}
