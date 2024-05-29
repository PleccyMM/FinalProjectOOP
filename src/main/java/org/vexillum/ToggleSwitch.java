package org.vexillum;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * My own implementation of a toggle switch, since JavaFX does not have a suitable one by default
 * It's a fairly hacky implementation, not allowing recolouring and the relevant labels must be placed in a HBox around
 * the button itself, but it is a sufficient implementation for the project
 */
public class ToggleSwitch extends HBox
{
    //SimpleBooleanProperty rather than boolean means that whenever it is updated an event is run, allowing the design
    //to be updated, it's still really annoying to use as you have to do .get() however when comparing
    private SimpleBooleanProperty toLeft = new SimpleBooleanProperty(true);
    private Button btnLeft = new Button();
    private Button btnRight = new Button();

    /**
     * This constructor concerns itself nearly exclusively with design, and attaching {@code toLeft} to a listener
     */
    public ToggleSwitch()
    {
        //Hacky implementation to get it to a fixed size, JavaFX has issues with making buttons too small, which
        //sizeOfBall is in charge of, so 20 was a sweet spot for my design and not breaking
        int sizeOfBall = 20;
        btnLeft.setMinSize(sizeOfBall, sizeOfBall);
        btnLeft.setMaxSize(sizeOfBall, sizeOfBall);
        btnRight.setMinSize(sizeOfBall, sizeOfBall);
        btnRight.setMaxSize(sizeOfBall, sizeOfBall);
        //Clicking either side always inverts, this improves usability as it means you aren't forced to click either the
        //blue dot or the empty space, which in testing new users seemed almost random on which side they thought
        //correct to click
        btnLeft.setOnAction(t -> toLeft.set(!toLeft.get()));
        btnRight.setOnAction(t -> toLeft.set(!toLeft.get()));

        btnLeft.setStyle("-fx-background-radius: 360; -fx-border-color: transparent; -fx-background-color: #3551B4;");
        btnRight.setStyle("-fx-background-radius: 360; -fx-border-color: transparent; -fx-background-color: transparent;");

        setAlignment(Pos.CENTER_LEFT);
        setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 360; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 7, 0.0, 0, 1);");
        getChildren().add(btnLeft);
        getChildren().add(btnRight);

        toLeft.addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1)
            {
                if (t1)
                {
                    btnLeft.setStyle("-fx-background-radius: 360; -fx-border-color: transparent; -fx-background-color: #3551B4;");
                    btnRight.setStyle("-fx-background-radius: 360; -fx-border-color: transparent; -fx-background-color: transparent;");
                }
                else
                {
                    btnLeft.setStyle("-fx-background-radius: 360; -fx-border-color: transparent; -fx-background-color: transparent;");
                    btnRight.setStyle("-fx-background-radius: 360; -fx-border-color: transparent; -fx-background-color: #3551B4;");
                }
            }
        });

        toLeft.set(true);
    }

    public SimpleBooleanProperty getToLeft() { return toLeft; }
    public void setToLeft(boolean b) { toLeft.set(b); }
}