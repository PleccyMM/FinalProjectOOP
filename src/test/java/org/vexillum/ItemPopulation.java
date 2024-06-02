package org.vexillum;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ItemPopulation extends ApplicationTest {
    protected final List<StockItem> testItems = new ArrayList<>();
    protected static final List<StockItem> stockItems = new ArrayList<>();

    @BeforeAll
    public static void saveStockInformation() {
        stockItems.add(DatabaseControl.createFlag("GI", FLAG_SIZE.SMALL));
        stockItems.add(DatabaseControl.createFlag("PR-TG", FLAG_SIZE.LARGE));
        stockItems.add(DatabaseControl.createFlag("CY-DE", FLAG_SIZE.SMALL));
        stockItems.add(DatabaseControl.createFlag("MU", FLAG_SIZE.DESK));
        stockItems.add(DatabaseControl.createFlag("PN", FLAG_SIZE.MEDIUM));

        stockItems.add(DatabaseControl.createCushion("TS", CUSHION_SIZE.LONG, CUSHION_MATERIAL.FEATHERS));
        stockItems.add(DatabaseControl.createCushion("ER", CUSHION_SIZE.LARGE, CUSHION_MATERIAL.EMPTY));
        stockItems.add(DatabaseControl.createCushion("CY-MO", CUSHION_SIZE.LARGE, CUSHION_MATERIAL.COTTON));
        stockItems.add(DatabaseControl.createCushion("MO", CUSHION_SIZE.SMALL, CUSHION_MATERIAL.EMPTY));
    }

    @AfterAll
    public static void restoreStockInformation() {
        for (StockItem item : stockItems) {
            DatabaseControl.updateAmountAndRestock(item.getStockID(), item.getSizeID(), item.getTotalAmount(), item.getRestock());
        }
    }

    @BeforeEach
    public void setUpItems() throws InterruptedException {
        DatabaseControl.updateAmountAndRestock(stockItems.get(0).getStockID(), stockItems.get(0).getSizeID(), 11, 4);
        Flag flagSmallGibraltar = (Flag) stockItems.get(0).clone();
        flagSmallGibraltar.setAmount(3);
        flagSmallGibraltar.setHoist(FLAG_HOIST.NONE);
        flagSmallGibraltar.setMaterial(FLAG_MATERIAL.POLYESTER);

        Flag flagLargeTrans = (Flag) stockItems.get(1).clone();
        flagLargeTrans.setAmount(-7);

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

        DatabaseControl.updateAmountAndRestock(stockItems.get(6).getStockID(), stockItems.get(6).getSizeID(), 5, 7);
        Cushion cushionLargeEritrea = (Cushion) stockItems.get(6).clone();
        cushionLargeEritrea.setAmount(-3);

        Cushion cushionLargeMonmouthshire = (Cushion) stockItems.get(7).clone();
        cushionLargeMonmouthshire.setAmount(5);

        Cushion cushionSmallMacao = (Cushion) stockItems.get(8).clone();
        cushionSmallMacao.setAmount(-7);

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
    }
}
