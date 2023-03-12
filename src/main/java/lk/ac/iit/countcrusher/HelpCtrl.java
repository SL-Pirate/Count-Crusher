package lk.ac.iit.countcrusher;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

public class HelpCtrl {
    @FXML
    Accordion accordion;
    @FXML
    TitledPane aboutPane;
    private Stage mainStage;
    private Stage helpStage;
    public void init(Stage mainStage, Stage helpStage){
        this.mainStage = mainStage;
        this.helpStage = helpStage;

        //expanding the first (aboutPane) when showing the page
        accordion.setExpandedPane(aboutPane);
    }

    @FXML
    private void goBack(){
        helpStage.close();
        mainStage.show();
    }
}
