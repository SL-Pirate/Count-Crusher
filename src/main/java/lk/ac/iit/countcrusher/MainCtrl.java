package lk.ac.iit.countcrusher;

//import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class MainCtrl{
    @FXML
    private TextArea inputField;

    private String[] getItems () {
        String unformattedTxt = inputField.getText();
        return unformattedTxt.split("\\r?\\n");
    }
}