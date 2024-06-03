package org.vexillum;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StockItemTest extends ItemPopulation {
    @BeforeAll
    public static void itemSetup() {

    }

    @Test
    @Order(1)
    public void stockItemTest() {
        assertEquals(testItems.get(0).hashCode(), testItems.get(0).hashCode());
    }
}
