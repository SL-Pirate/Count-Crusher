package lk.ac.iit.countcrusher;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class HelpCtrl {
    private Stage mainStage;
    private Stage helpStage;
    public void init(Stage mainStage, Stage helpStage){
        this.mainStage = mainStage;
        this.helpStage = helpStage;
    }

    @FXML
    private void goBack(){
        helpStage.close();
        mainStage.show();
    }
}
