package org.controllers;

import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.io.*;
import java.nio.charset.*;
import java.text.NumberFormat;
import java.util.*;
import org.vexillum.*;

/**
 * This screen is used for displaying the main scrolling stock for the system
 * <p>
 * Main design is found in {@code stock_screen.fxml}, with {@code stock_item.fxml} being used for each individual design and
 * {@code stock_tag.fxml} for the tag selection boxes
 */
public class StockController extends ControllerParent {
    @FXML private VBox boxScroll;
    @FXML private BorderPane panMain;
    @FXML private HBox boxTagOps;
    @FXML private Region rgnButtonPush;
    @FXML private VBox boxTagSelect;

    private List<RadioButton> rdbList;
    private ToggleGroup tg;
    private List<Design> allDesigns;

    @Override
    protected void stageChangeHandle() {}

    /**
     * Used to get the header for loading and attaching relevant information to the tag selection
     */
    public void load(Stage stage, List<StockItem> items, Operator operator, SearchConditions searchConditions) {
        try {
            openDB();

            HBox headerBox = (HBox) panMain.lookup("#boxHeader");
            if (headerBox == null) { throw new Exception(); }

            loadHeader(stage, operator, items, headerBox, searchConditions);

            //This section below is for setting up the tag selection boxes
            String[] tagBoxes = new String[] {"Type", "Region", "Initial"};

            int i = 0;
            for (String tagBox : tagBoxes) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("stock_tag.fxml"));
                Parent box = loader.load();

                box.setId(tagBox);
                ((Label) box.lookup("#lblTag")).setText(tagBox);

                try {
                    Image img = new Image("org/Assets/Icons/DownArrow.png");
                    ((ImageView) box.lookup("#imgArrow")).setImage(img);
                }
                catch (Exception ignored) { }

                box.setOnMouseClicked(tagClick);
                boxTagOps.getChildren().add(i++, box);
            }

            loadStock();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeDB();
        }
    }

    /**
     * Used to create all the needed selection boxes for the designs in the system.
     * Prior to this method being called designs must be loaded with accordance to the filters present.
     * @throws Exception loading the relevant .fxml can fail
     */
    private void loadStock() throws Exception {
        allDesigns = getDatabase().searchDesigns(sc);
        boxScroll.getChildren().clear();

        boxScroll.getChildren().add(new HBox());

        final int designsToLoad;
        final int maxFlagLoad = allDesigns.size(); //This variable can be modified to restrict the max number of viewed designs

        designsToLoad = Math.min(allDesigns.size(), maxFlagLoad);

        System.out.println("Loaded Designs: " + designsToLoad);

        int runAmount = 3;
        //The design boxes are laid out in rows of 3, hence the +=runAmount
        for (int i = 0; i < designsToLoad; i+=runAmount) {
            HBox box = new HBox();
            box.setSpacing(48);
            box.setAlignment(Pos.CENTER);

            //This deals with the logic for when the amount of designs is not divisible by 3 and must form a partial row
            if (i + 2 >= designsToLoad) {
                runAmount = designsToLoad - i;
            }

            for (int j = 0; j < runAmount; j++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("stock_item.fxml"));
                Parent productView = loader.load();

                Design currentDesign = allDesigns.get(i+j);

                Button btnFlag = ((Button) productView.lookup("#btnFlag"));
                btnFlag.setId(btnFlag.getId() + "_" + currentDesign.getIsoID());
                btnFlag.setOnAction(btnFlagHandle);

                Button btnCushion = ((Button) productView.lookup("#btnCushion"));
                btnCushion.setId(btnCushion.getId() + "_" + currentDesign.getIsoID());
                btnCushion.setOnAction(btnCushionHandle);

                try {
                    //FlagSmall folder contains flags with a maximum width of 100, rather than FlagsLarge that has 1000,
                    //this massively boosts performance at the cost of surprisingly little storage space
                    Image img = new Image("org/Assets/FlagsSmall/" + currentDesign.getIsoID() + ".png");
                    ((ImageView) productView.lookup("#imgDisp")).setImage(img);
                }
                catch (Exception ignored) { }

                ((Label) productView.lookup("#lblStockName")).setText(currentDesign.getName());

                //The default price is set in stock_item.fxml, but because national flags are cheaper it has to be
                //modified here
                if (currentDesign.getType() == TYPE.NATIONAL.getValue()) {
                    ((Label) productView.lookup("#lblStockPrice")).setText("\u00A32.25-\u00A317.20+");
                }

                box.getChildren().add(productView);
            }
            boxScroll.getChildren().add(box);
        }

        boxScroll.getChildren().add(new HBox());
        System.out.println("CHILDREN " + boxScroll.getChildren().size());
    }

    EventHandler<ActionEvent> btnFlagHandle = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                showItemScreen(event, true);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    EventHandler<ActionEvent> btnCushionHandle = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                showItemScreen(event, false);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    /**
     * Used to move over to the new screen
     * @param isFlag needed for the next screen to help with defining how the page should be setup
     * @throws Exception loading .fxml file could fail
     */
    private void showItemScreen(ActionEvent event, Boolean isFlag) throws Exception {
        Object source = event.getSource();
        Button b = (Button) source;
        //The relevant isoID is the latter half of the boxes fx:id, seperated by a "_" - so splitting and getting the
        //tail is necessary
        String isoID = b.getId().split("_")[1];
        //The getDatabase() loads in designs alphabetically rather than by isoID, so simply indexing can't work. This may seem
        //less beneficial, but it's significantly more convenient to have a slightly shitty iterative search for an isoID
        //than it is to try and organise by alphabetical order in other parts of the program
        for (Design d : allDesigns) {
            if (d.getIsoID().equals(isoID)) {
                l.showItem(stage, getItems(), operator, d, isFlag, null);
                break;
            }
        }
    }

    /**
     * This method is a bit confusing with its logic, but it's used to display the dropdown menu for the tag selection,
     * this means it only is invoked when the menu is actually expanded and not when it's collapsed
     * @param tagSelect this is the name of the label that the box has
     */
    private void generateRadioButtons(String tagSelect) {
        tg = new ToggleGroup();
        rdbList = new ArrayList<>();

        switch (tagSelect) {
            case "type":
                rdbList.add(createRdb("Any", tg, rdbType));
                rdbList.add(createRdb("National", tg, rdbType));
                rdbList.add(createRdb("International", tg, rdbType));
                rdbList.add(createRdb("Pride", tg, rdbType));

                //Get the type in the searchConditions attribute, so we can decide which radiobutton to have selected
                Integer type = sc.getType();
                if (type == null) rdbList.get(0).setSelected(true);
                else {
                    //MySQL getDatabase() has the relevant ID for the type, which aligns with their positions in the button list
                    Integer i = getDatabase().getTypeId(rdbList.get(type + 1).getText());
                    rdbList.get(i + 1).setSelected(true);
                }
                break;
            case "region":
                rdbList.add(createRdb("Any", tg, rdbRegion));
                rdbList.add(createRdb("Africa", tg, rdbRegion));
                rdbList.add(createRdb("Asia", tg, rdbRegion));
                rdbList.add(createRdb("Europe", tg, rdbRegion));
                rdbList.add(createRdb("North America", tg, rdbRegion));
                rdbList.add(createRdb("Oceania", tg, rdbRegion));
                rdbList.add(createRdb("South America", tg, rdbRegion));

                //Exactly the same as type (see above) but obviously using region instead
                Integer region = sc.getRegion();
                if (region == null) rdbList.get(0).setSelected(true);
                else {
                    Integer i = getDatabase().getRegionId(rdbList.get(region + 1).getText());
                    rdbList.get(i + 1).setSelected(true);
                }
                break;
            case "initial":
                rdbList.add(createRdb("All", tg, rdbInitial));
                rdbList.add(createRdb("A-F", tg, rdbInitial));
                rdbList.add(createRdb("G-L", tg, rdbInitial));
                rdbList.add(createRdb("M-S", tg, rdbInitial));
                rdbList.add(createRdb("T-Z", tg, rdbInitial));

                //This logic is a bit confusing, but by doing it this way I have removed the need for a lot of additional
                //if statements

                //Gets the last letter of the search metric (see searchConditions for a more explanation)
                String lastInitial = sc.getStartLetters()[1];
                if (lastInitial == null) rdbList.get(0).setSelected(true);
                else {
                    //Convert the letter of the search metric to its unicode value, from now on we use only b[0] as
                    //.getBytes returns an array, but we know that only 1 letter has been provided
                    byte[] b = lastInitial.getBytes(StandardCharsets.UTF_8);

                    //So in the tags the alphabet is divided into 4, but 26/4 is not an integer, therefore the first 2
                    //(A-F & G-L) are 6 letter intervals whilst the last 2 (M-S & T-Z) are 7 letter intervals
                    //
                    //The unicode location of "A" is at position 65, which is why it's relevant to subtract it, to get
                    //a value from 0-25, with 0=A & 25=Z, whilst 77 is the position of M, which is the first character
                    //of the 7 intervals.
                    //By performing integer division by 77 you get 0 if it's below M in the alphabet, all the 6 intervals
                    //and 1 if it's above (inclusive), all the 7 intervals. By adding this to 6 of course you then get
                    //the correct offset for the interval to get the final character, when dividing by the previously
                    //mentioned unicode offset between 0-25. Ultimately, this returns a value between 1-4, which we can
                    //use to select the correct position in the array of radiobuttons.
                    int i = (b[0] - 65) / (6 + b[0] / 77);
                    rdbList.get(i + 1).setSelected(true);
                }
        }
        boxTagSelect.getChildren().addAll(rdbList);
    }

    private RadioButton createRdb(String text, ToggleGroup tg, EventHandler<ActionEvent> handler) {
        RadioButton radioButton = new RadioButton(text);
        radioButton.setId(text);
        radioButton.setToggleGroup(tg);
        radioButton.setOnAction(handler);
        return radioButton;
    }

    /**
     * Used to print all the information about stock prices and total stock to a file in the system directory. This
     * does utilise the current search conditions meaning that it will only print all when the search conditions are blank,
     * this feature is explained to the user on a tooltip when hovering over the button
     * @throws Exception creating a new file and writing to it could fail
     */
    @FXML
    protected void btnPrintClick(ActionEvent event) throws Exception {
        String msg = "";

        openDB();
        //Uses the current search conditions, so that if filtered only those are printed to the file
        List<Design> designs = getDatabase().searchDesigns(sc);
        //HashMaps are used to make it significantly easier to find the relevant designs from their isoID
        HashMap<String, Flag> flags = getDatabase().getAllFlags();
        HashMap<String, Cushion> cushions = getDatabase().getAllCushions();
        closeDB();

        NumberFormat eurFormatter = NumberFormat.getCurrencyInstance(Locale.UK);

        //Value is the import (buy) price
        double totalValueFlag = 0;
        double totalValueCushion = 0;

        //Sell is the export price
        double totalSellFlag = 0;
        double totalSellCushion = 0;

        for (Design d : designs) {
            msg += "==== " + d.getName() + " ====\n";

            msg +="\n== Flags ==\n";
            for (FLAG_SIZE size : FLAG_SIZE.values()) {
                Flag f = flags.get(d.getIsoID() + "_" + FLAG_SIZE.getSizeId(size));

                //Since flags can have different materials, but these are only used for amendments to the export, we
                //assign the default to the cheapest material to get the base price
                if (size == FLAG_SIZE.HAND || size == FLAG_SIZE.DESK) f.setMaterial(FLAG_MATERIAL.PAPER);
                else f.setMaterial(FLAG_MATERIAL.POLYESTER);

                //Get the relevant costs for appending and use in printing
                double totalValue = f.getCostToProduce() * f.getTotalAmount();
                double totalSell = f.calculatePrice() * f.getTotalAmount();

                //Append the new calculated prices onto the rest
                totalValueFlag += totalValue;
                totalSellFlag += totalSell;

                msg += "\nSize: " + FLAG_SIZE.getString(size) +
                        "\nCost to produce single: " + eurFormatter.format(f.getCostToProduce()) +
                        "\nBase sell price single: " + eurFormatter.format(f.calculatePrice()) +
                        "\nAmount in stock: " + f.getTotalAmount() +
                        "\nLimit before needing restock: " + f.getRestock() +
                        "\nTotal cost to produce: " + eurFormatter.format(totalValue) +
                        "\nTotal sell price: " + eurFormatter.format(totalSell) + "\n";
            }

            msg +="\n== Cushions ==\n";
            for (CUSHION_SIZE size : CUSHION_SIZE.values()) {
                Cushion c = cushions.get(d.getIsoID() + "_" + CUSHION_SIZE.getSizeId(size));

                double totalValue = c.getCostToProduce() * c.getTotalAmount();
                double totalSell = c.calculatePrice() * c.getTotalAmount();

                totalValueCushion += totalValue;
                totalSellCushion += totalSell;

                msg += "\nSize: " + CUSHION_SIZE.getString(size) +
                        "\nCost to produce single: " + eurFormatter.format(c.getCostToProduce()) +
                        "\nBase sell price single: " + eurFormatter.format(c.calculatePrice()) +
                        "\nAmount in stock: " + c.getTotalAmount() +
                        "\nLimit before needing restock: " + c.getRestock() +
                        "\nTotal cost to produce: " + eurFormatter.format(totalValue) +
                        "\nTotal sell price: " + eurFormatter.format(totalSell) + "\n";
            }
            msg += "\n\n";
        }

        //This fills in the top of the file, with all the previously printed information being appended after
        msg = "Total Stock Report for Vexillum Management\nThe overall cost to purchase all held stock is:\n"
                + eurFormatter.format(totalValueFlag) + " for flags\n"
                + eurFormatter.format(totalValueCushion) + " for cushions\n"
                + eurFormatter.format(totalValueFlag + totalValueCushion) + " overall\n\n"
                + "The base sell price for all held stock is:\n"
                + eurFormatter.format(totalSellFlag) + " for flags\n"
                + eurFormatter.format(totalSellCushion) + " for cushions\n"
                + eurFormatter.format(totalSellFlag + totalSellFlag) + " overall\n\n"
                + "Beneath is a individual breakdown by design of all stock held in the system:\n\n\n" + msg;

        String file = "AllStock.txt";
        try {
            File f = new File(file);
            f.createNewFile();
            FileWriter fw = new FileWriter(file);
            fw.write(msg);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    EventHandler<ActionEvent> rdbType = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                RadioButton r = (RadioButton) source;

                openDB();
                sc.setType(getDatabase().getTypeId(r.getText()));
                performSearch();
                closeDB();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    EventHandler<ActionEvent> rdbRegion = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                RadioButton r = (RadioButton) source;

                openDB();
                sc.setRegion(getDatabase().getRegionId(r.getText()));
                performSearch();
                closeDB();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    EventHandler<ActionEvent> rdbInitial = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                Object source = event.getSource();
                RadioButton r = (RadioButton) source;

                //Since the label itself defines the range we can just split across the dividing "-"
                String[] initials = r.getText().split("-");
                if (initials.length == 1) {
                    sc.setStartLetters(new String[2]);
                }
                else {
                    sc.setStartLetters(initials);
                }
                performSearch();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    EventHandler<MouseEvent> tagClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            try {
                Object source = event.getSource();
                Node box = (Node) source;

                Image downArr = new Image("org/Assets/Icons/DownArrow.png");
                Image upArr = new Image("org/Assets/Icons/UpArrow.png");
                ImageView imgView = ((ImageView) box.lookup("#imgArrow"));

                //Any open tag selections must be closed and their arrows changed to reflect this
                boxTagSelect.getChildren().clear();
                if (Objects.equals(box.getId(), "up")) {
                    box.setId("down");
                    imgView.setImage(downArr);
                }
                else {
                    int index = 0;
                    //-2 to account for the label and the image of the arrow
                    for (int i = 0; i < boxTagOps.getChildren().size() - 2; i++) {
                        Node n = boxTagOps.getChildren().get(i);

                        //Ensures only the selected box has the correct arrow rotation
                        if (n == box) {
                            index = i;
                            box.setId("up");
                            imgView.setImage(upArr);
                        }
                        else {
                            n.setId("down");
                            ((ImageView) n.lookup("#imgArrow")).setImage(downArr);
                        }
                    }

                    rgnButtonPush.setMinWidth((((HBox) box).getWidth() + boxTagOps.getSpacing()) * (index));

                    String labelText = ((Label) box.lookup("#lblTag")).getText().toLowerCase();
                    openDB();
                    generateRadioButtons(labelText);
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                closeDB();
            }
        }
    };
}
