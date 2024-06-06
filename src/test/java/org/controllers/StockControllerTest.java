package org.controllers;

import com.sun.javafx.application.*;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.testfx.api.*;
import org.testfx.framework.junit5.*;
import org.vexillum.*;
import org.junit.jupiter.api.*;
import org.testfx.matcher.control.*;

import java.io.File;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StockControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        Loader l = new Loader();
        l.showStock(stage, Collections.emptyList(), new Operator(), new SearchConditions());
    }

    @AfterAll
    public static void deleteFile() {
        File f = new File("AllStock.txt");
        f.delete();
    }

    /**
     * This unit test checks that the first design in the database and the last design in the database are present
     */
    @Test
    @Order(1)
    public void headAndTailTest() {
        assertTrue(() -> {
            try {
                //AC = Ascension Island
                lookup("#btnFlag_AC").query();
                lookup("#btnCushion_AC").query();
                return true;
            } catch (Exception e) {
                return false;
            }
        }, "Failed to find top level design");

        assertTrue(() -> {
            try {
                //ZW = Zimbabwe
                lookup("#btnFlag_ZW").query();
                lookup("#btnCushion_ZW").query();
                return true;
            } catch (Exception e) {
                return false;
            }
        }, "Failed to find bottom level design");
    }

    @Test
    @Order(2)
    public void searchEmptyTest() throws InterruptedException {
        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#btnSearch");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(500);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(112, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    @Test
    @Order(2)
    public void searchNoReturnTest() throws InterruptedException {
        clickOn("#enrSearch");
        write("@(13--__daw");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#btnSearch");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(500);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(2, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    /**
     * A search test using the expected case, that being the first letter capitalised
     */
    @Test
    @Order(3)
    public void searchJustTextExpectedCaseTest() throws InterruptedException {
        clickOn("#enrSearch");
        write("Germany");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#btnSearch");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(500);

        assertTrue(() -> {
            try {
                //DE = Germany
                lookup("#btnFlag_DE").query();
                lookup("#btnCushion_DE").query();
                return true;
            } catch (Exception e) {
                return false;
            }
        }, "Failed to find the design on screen");

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(3, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    /**
     * A search test using random cases
     */
    @Test
    @Order(4)
    public void searchJustTextRandomCaseTest() throws InterruptedException {
        clickOn("#enrSearch");
        write("iSle oF MAn");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#btnSearch");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(500);

        assertTrue(() -> {
            try {
                //IM = Isle of Man
                lookup("#btnFlag_IM").query();
                lookup("#btnCushion_IM").query();
                return true;
            } catch (Exception e) {
                return false;
            }
        }, "Failed to find the design on screen");

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(3, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    @Test
    @Order(4)
    public void searchJustTextPartialTest() throws InterruptedException {
        clickOn("#enrSearch");
        write("der");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#btnSearch");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(500);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(4, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    @Test
    @Order(5)
    public void searchJustTypeTest() throws InterruptedException {
        clickOn("#Type");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#Pride");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(500);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(7, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    @Test
    @Order(5)
    public void searchJustRegionTest() throws InterruptedException {
        clickOn("#Region");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#Oceania");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(500);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(11, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    @Test
    @Order(5)
    public void searchJustInitialTest() throws InterruptedException {
        clickOn("#Initial");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#G-L");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(500);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(23, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    @Test
    @Order(6)
    public void searchAllTagsTest() throws InterruptedException {
        clickOn("#Type");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#National");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        Thread.sleep(500);

        clickOn("#Region");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#Europe");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        Thread.sleep(500);

        clickOn("#Initial");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#A-F");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(500);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(9, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    @Test
    @Order(7)
    public void searchEverythingTest() throws InterruptedException {
        clickOn("#enrSearch");
        write("land");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#btnSearch");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        Thread.sleep(500);

        clickOn("#Type");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#International");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        Thread.sleep(500);

        clickOn("#Region");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#Europe");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        Thread.sleep(500);

        clickOn("#Initial");

        PlatformImpl.runAndWait(() -> {
            try {
                clickOn("#M-S");
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(500);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(3, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    @Test
    @Order(8)
    public void printTest() {
        clickOn("#btnPrint");

        assertTrue(new File("AllStock.txt").exists(), "File was not made");
    }
}
