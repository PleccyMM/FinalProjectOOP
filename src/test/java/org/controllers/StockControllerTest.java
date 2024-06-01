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
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

public class StockControllerTest extends ApplicationTest {
    private StockController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("stock_screen.fxml"));
        Parent root = loader.load();

        controller = loader.getController();
        controller.load(stage, Collections.emptyList(), new Operator(), new SearchConditions());

        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This unit test checks that the first design in the database and the last design in the database are present
     */
    @Test
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
    public void searchEmptyTest() throws InterruptedException {
        PlatformImpl.runAndWait(() -> {
            try {
                Button btnSearch = lookup("#btnSearch").query();
                clickOn(btnSearch);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(3000);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(112, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    @Test
    public void searchNoReturnTest() throws InterruptedException {
        TextField searchBar = lookup("#enrSearch").query();
        clickOn(searchBar);
        write("@(13--__daw");

        PlatformImpl.runAndWait(() -> {
            try {
                Button btnSearch = lookup("#btnSearch").query();
                clickOn(btnSearch);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(3000);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(2, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    /**
     * A search test using the expected case, that being the first letter capitalised
     */
    @Test
    public void searchJustTextExpectedCaseTest() throws InterruptedException {
        TextField searchBar = lookup("#enrSearch").query();
        clickOn(searchBar);
        write("Germany");

        PlatformImpl.runAndWait(() -> {
            try {
                Button btnSearch = lookup("#btnSearch").query();
                clickOn(btnSearch);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(3000);

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
    public void searchJustTextRandomCaseTest() throws InterruptedException {
        TextField searchBar = lookup("#enrSearch").query();
        clickOn(searchBar);
        write("iSle oF MAn");

        PlatformImpl.runAndWait(() -> {
            try {
                Button btnSearch = lookup("#btnSearch").query();
                clickOn(btnSearch);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(3000);

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
    public void searchJustTypeTest() throws InterruptedException {
        Node box = lookup("#Type").query();
        clickOn(box);

        PlatformImpl.runAndWait(() -> {
            try {
                RadioButton radioButton = lookup("#Pride").query();
                clickOn(radioButton);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(3000);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(7, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    @Test
    public void searchJustRegionTest() throws InterruptedException {
        Node box = lookup("#Region").query();
        clickOn(box);

        PlatformImpl.runAndWait(() -> {
            try {
                RadioButton radioButton = lookup("#Oceania").query();
                clickOn(radioButton);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(3000);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(11, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    @Test
    public void searchJustInitialTest() throws InterruptedException {
        Node box = lookup("#Initial").query();
        clickOn(box);

        PlatformImpl.runAndWait(() -> {
            try {
                RadioButton radioButton = lookup("#G-L").query();
                clickOn(radioButton);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(3000);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(23, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    @Test
    public void searchAllTagsTest() throws InterruptedException {
        Node box = lookup("#Type").query();
        clickOn(box);

        PlatformImpl.runAndWait(() -> {
            try {
                RadioButton radioButton = lookup("#National").query();
                clickOn(radioButton);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        Thread.sleep(1000);

        box = lookup("#Region").query();
        clickOn(box);

        PlatformImpl.runAndWait(() -> {
            try {
                RadioButton radioButton = lookup("#Europe").query();
                clickOn(radioButton);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        Thread.sleep(1000);

        box = lookup("#Initial").query();
        clickOn(box);

        PlatformImpl.runAndWait(() -> {
            try {
                RadioButton radioButton = lookup("#A-F").query();
                clickOn(radioButton);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(3000);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(9, boxScroll.getChildren().size(), "Wrong amount of children found");
    }

    @Test
    public void searchEverythingTest() throws InterruptedException {
        TextField searchBar = lookup("#enrSearch").query();
        clickOn(searchBar);
        write("land");

        PlatformImpl.runAndWait(() -> {
            try {
                Button btnSearch = lookup("#btnSearch").query();
                clickOn(btnSearch);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        Thread.sleep(1000);

        Node box = lookup("#Type").query();
        clickOn(box);

        PlatformImpl.runAndWait(() -> {
            try {
                RadioButton radioButton = lookup("#International").query();
                clickOn(radioButton);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        Thread.sleep(1000);

        box = lookup("#Region").query();
        clickOn(box);

        PlatformImpl.runAndWait(() -> {
            try {
                RadioButton radioButton = lookup("#Europe").query();
                clickOn(radioButton);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        Thread.sleep(1000);

        box = lookup("#Initial").query();
        clickOn(box);

        PlatformImpl.runAndWait(() -> {
            try {
                RadioButton radioButton = lookup("#M-S").query();
                clickOn(radioButton);
            } catch (Exception e) {
                fail("Failed performing search");
            }
        });
        // This gives the database time to get the required values
        Thread.sleep(3000);

        VBox boxScroll = lookup("#boxScroll").query();
        assertEquals(3, boxScroll.getChildren().size(), "Wrong amount of children found");
    }
}
