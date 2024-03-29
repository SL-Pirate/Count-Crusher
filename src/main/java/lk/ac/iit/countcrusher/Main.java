package lk.ac.iit.countcrusher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static final String name = "Count Crusher";
    public static final double versionNum = 1.7;
    public static final int minorVersionNum = 0;
    public static final String version = "v" + versionNum + "." + minorVersionNum;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(name + " " + version);
        stage.setScene(scene);
        stage.show();
    }
}