package org.vexillum;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import java.util.*;

public class ToggleSwitch extends HBox
{
    private SimpleBooleanProperty toLeft = new SimpleBooleanProperty(true);
    private Button btnLeft = new Button();
    private Button btnRight = new Button();

    public ToggleSwitch()
    {
        int sizeOfBall = 20;
        btnLeft.setMinSize(sizeOfBall, sizeOfBall);
        btnLeft.setMaxSize(sizeOfBall, sizeOfBall);
        btnRight.setMinSize(sizeOfBall, sizeOfBall);
        btnRight.setMaxSize(sizeOfBall, sizeOfBall);
        btnLeft.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent t)
            {
                toLeft.set(!toLeft.get());
            }
        });
        btnRight.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent t)
            {
                toLeft.set(!toLeft.get());
            }
        });

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
}