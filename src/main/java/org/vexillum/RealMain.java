package org.vexillum;

import javafx.application.Application;
import javafx.stage.Stage;
import org.controllers.Loader;

public class RealMain extends Application {private final Loader loader = new Loader();
    public static void launchProg (String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Vexillum Management");
        loader.showLogin(stage);
    }
}
