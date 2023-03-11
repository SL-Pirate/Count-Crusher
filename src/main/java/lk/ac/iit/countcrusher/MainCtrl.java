package lk.ac.iit.countcrusher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import lk.ac.iit.countcrusher.Errors.DuplicateFoundError;
import lk.ac.iit.countcrusher.Errors.InvalidInputError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainCtrl{
    // Scene 1 (main scene) fields
    @FXML
    private TextArea inputField;

    // defines how many lines the 2nd scene (the button scene) can have
    private final int numOfLines = 6;

    private static Item[] items;

    // Stages for corresponding scenes
    private Stage mainStage;
    private Stage buttonStage;
    private final Stage helpStage = new Stage();

    // Extracting items from the user input
    private Item[] getItems () throws InvalidInputError, DuplicateFoundError {
        String unformattedTxt = inputField.getText();
        String[] names = unformattedTxt.split("\\r?\\n");

        // Validating user input
        List<String> namesList = new ArrayList<>(List.of(names));
        for (int  i = 0; i < namesList.size(); i++){
            if (namesList.get(i).isEmpty()){
                namesList.remove(i);
                --i;
            }
            else {
                boolean valid = false;
                for (int j = 0; j < namesList.get(i).length(); j++){
                    if (namesList.get(i).charAt(j) != ' ' && namesList.get(i).charAt(j) != '\t'){
                        valid = true;
                        break;
                    }
                }
                if (!valid){
                    namesList.remove(i);
                    --i;
                }
            }
        }

        Item[] items = new Item[namesList.size()];

        for (int i = 0; i < namesList.size(); i++){
            items[i] = new Item(namesList.get(i), i);
        }

        // verifying the user input
        // Checking if valid user input is present
        if (items.length == 0){
            throw new InvalidInputError();
        }
        //checking for duplicate inputs
        for (int i = 0; i < items.length - 1; i++){
            for (int j = i + 1; j < items.length; j++){
                if (items[i].name.equals(items[j].name)){
                    throw new DuplicateFoundError();
                }
            }
        }
        return items;
    }

    // Calculating coordinates for the buttons to be placed in the button scene
    private int[] getCords(int itemNum){
        int[] l = new int[2];
        l[0] = (itemNum / numOfLines);
        l[1] = itemNum % numOfLines;

        return l;
    }

    // Switching to the button scene
    @FXML
    private void showButtonScene(ActionEvent evt) {
        try {
            GridPane root;
            Scene scene;
            items = getItems();
            root = new GridPane();
            root.setPadding(new Insets(20, 20, 20, 20));
            root.setVgap(10);
            root.setHgap(10);
            scene = new Scene(root);
            mainStage = (Stage) (((Node) evt.getSource()).getScene().getWindow());
            mainStage.hide();
            buttonStage = new Stage();
            buttonStage.setScene(scene);
            buttonStage.setTitle(Main.name + " " + Main.version);

            for (Item item : items) {
                int[] cords = getCords(item.ID);
                root.add(item.btn, cords[0], cords[1]);
            }

            Button finishBtn = new Button("Finish");
            finishBtn.setTextAlignment(TextAlignment.CENTER);
            finishBtn.setAlignment(Pos.CENTER);
            finishBtn.setOnAction(event -> {
                try {
                    showStatScene();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            root.add(finishBtn, getCords(items.length)[0] / 2 + 2, numOfLines + 1);

            Button undoBtn = new Button("Undo");
            undoBtn.setTextAlignment(TextAlignment.CENTER);
            undoBtn.setAlignment(Pos.CENTER);
            undoBtn.setOnAction(event -> undo());
            root.add(undoBtn, getCords(items.length)[0] / 2, numOfLines + 1);

            Button backButton = new Button("Back");
            backButton.setTextAlignment(TextAlignment.CENTER);
            backButton.setAlignment(Pos.CENTER);
            backButton.setOnAction(event -> goBackHome());
            root.add(backButton, getCords(items.length)[0] / 2 + 1, numOfLines + 1);

            buttonStage.setScene(scene);
            buttonStage.setResizable(false);
            buttonStage.show();
        }
        catch (InvalidInputError e){
            // If the user input is empty or has no valid inputs,
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(e.toString());
            alert.setContentText("Item list can't be empty. \nPlease insert each item in the above box followed by the \"Enter\" key");
            alert.showAndWait();
        }
        catch (DuplicateFoundError e){
            // If the user input has duplicates,
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(e.toString());
            alert.setContentText("Found duplicates in your input. Please revise");
            alert.showAndWait();
        }
    }

    // Going back to the home page from the buttons page
    @FXML
    private void goBackHome(){
        buttonStage.close();
        Item.cleanLastInput();
        mainStage.show();
    }

    // Go to instructions page
    @FXML
    private void showHelpScene(ActionEvent evt) throws IOException {
        Scene scene;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("help_view.fxml"));
        mainStage = (Stage) (((Node) evt.getSource()).getScene().getWindow());
        scene = new Scene(fxmlLoader.load());
        helpStage.setScene(scene);
        helpStage.setTitle("Help");
        HelpCtrl hw = fxmlLoader.getController();
        hw.init(mainStage, helpStage);

        mainStage.hide();
        helpStage.show();
    }

    // Switching to the Stat scene
    private void showStatScene() throws IOException {
        Scene scene;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("stat_view.fxml"));
        scene = new Scene(fxmlLoader.load());
        buttonStage.hide();
        Stage statStage = new Stage();
        statStage.setScene(scene);
        statStage.setTitle(Main.name + " " + Main.version);
        StatCtrl dc = fxmlLoader.getController();
        dc.init(buttonStage, statStage);


        statStage.show();
    }
    
    private void undo(){
        for (Item i : items){
            if (i.ID == Item.getLastInput()){
                i.decrease();
                break;
            }
        }
    }

    // returns null if not initialized yet
    public static Item[] getItemList(){
        return items;
    }
}