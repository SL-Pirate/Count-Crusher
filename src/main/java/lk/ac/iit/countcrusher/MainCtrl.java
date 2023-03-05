package lk.ac.iit.countcrusher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MainCtrl{
    @FXML
    private TextArea inputField;
    private final int numOfLines = 6;

    private Item[] getItems () {
        String unformattedTxt = inputField.getText();
        String[] names = unformattedTxt.split("\\r?\\n");
        int numOfValidItems = 0;

        // Calculating the number of valid items
        for (String name : names){
            if (!name.isEmpty())
                numOfValidItems++;
        }
        Item[] items = new Item[numOfValidItems];

        // Filtering through empty items
        int nameCount = 0;
        for (int i = 0; i < items.length; i++){
            while ((names[nameCount]).isEmpty()){
                nameCount++;
            }
            items[i] = new Item(names[nameCount], i);
            nameCount++;
        }

        return items;
    }

    private int[] getCords(int itemNum){
        int[] l = new int[2];
        l[0] = (itemNum / numOfLines);
        l[1] = itemNum % numOfLines;

        return l;
    }
    @FXML
    private void showButtonScene(ActionEvent evt) {
        GridPane root;
        Scene scene;
        Stage stage;
        Item[] items;
        items = getItems();
        if (items.length == 0) {
            // TODO
        } else {
            root = new GridPane();
            root.setPadding(new Insets(20, 20, 20, 20));
            root.setVgap(10);
            root.setHgap(10);
            scene = new Scene(root);
            stage = (Stage) (((Node) evt.getSource()).getScene().getWindow());
            stage.hide();
            stage = new Stage();
            stage.setTitle(Main.name + " " + Main.version);

            for (Item item : items) {
                int[] cords = getCords(item.ID);
                root.add(item.btn, cords[0], cords[1]);
            }

            Button finishBtn = new Button("Finish");
            finishBtn.setTextAlignment(TextAlignment.CENTER);
            finishBtn.setAlignment(Pos.CENTER);
            finishBtn.setOnAction(event -> showStatScene());
            root.add(finishBtn, getCords(items.length)[0] / 2, numOfLines + 1);

            stage.setScene(scene);
            stage.show();
        }
    }

    private void undo(){
        // TODO
    }

    @FXML
    private void showHelpScene(){
        // TODO
    }

    private void showStatScene(){
        // TODO
    }
}