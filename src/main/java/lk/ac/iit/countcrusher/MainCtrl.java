package lk.ac.iit.countcrusher;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.controlsfx.control.Notifications;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainCtrl{
    // Scene 1 (main scene) fields
    @FXML
    private TextArea inputField;

    // Scene 3 (the stat scene) fields
    @FXML
    private Label total;
    @FXML
    private Label mean;
    @FXML
    private Label median;
    @FXML
    private Label mode;
    @FXML
    private Label range;
    @FXML
    private Label min;
    @FXML
    private Label max;
    @FXML
    private ListView<?> outView;

    // defines how many lines the 2nd scene (the button scene) can have
    private final int numOfLines = 6;

    private Item[] items;

    // Stages for corresponding scenes
    private Stage mainStage;
    private Stage buttonStage;
    private Stage statStage;

    // Floating point number conversions to String
    private final DecimalFormat df = new DecimalFormat("0.###");

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
    private void showHelpScene(){
        // TODO
    }

    // Switching to the Stat scene
    private void showStatScene() throws IOException {
        Scene scene;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("stat_view.fxml"));
        fxmlLoader.setController(this);
        scene = new Scene(fxmlLoader.load());
        buttonStage.hide();
        statStage = new Stage();
        statStage.setScene(scene);
        statStage.setTitle(Main.name + " " + Main.version);

        ObservableList listviewContent = FXCollections.observableArrayList();
        int maxLen = 0;
        for (Item item : items){
            maxLen = Math.max(maxLen, item.name.length());
        }
        for (Item item : items){
            StringBuilder itemOutput = new StringBuilder();
            itemOutput.append(item.name);
            int spaceLen = maxLen - item.name.length();
            itemOutput.append(" ".repeat(Math.max(0, spaceLen + 1)));
            itemOutput.append(" : ").append(item.getCount());
            listviewContent.add(itemOutput.toString());
        }
        outView.setItems(listviewContent);

        Stat stat = new Stat(items);
        total.setText(Integer.toString(stat.getTotal()));
        min.setText(Integer.toString(stat.getMin()));
        max.setText(Integer.toString(stat.getMax()));
        mean.setText(df.format(stat.getMean()));
        median.setText(df.format(stat.getMedian()));

        StringBuilder modeStr = new StringBuilder();
        List<Integer> modeVals = stat.getMode();
        if (modeVals.size() == 1){
            modeStr = new StringBuilder(Integer.toString(modeVals.get(0)));
        }
        else {
            for (int i = 0; i < modeVals.size(); i++) {
                if (i == modeVals.size() - 1) {
                    modeStr.append(modeVals.get(i));
                }
                else{
                    modeStr.append(modeVals.get(i)).append(", ");
                }
            }
        }
        mode.setText(modeStr.toString());
        range.setText(Integer.toString(stat.getRange()));

        statStage.show();
    }

    // Going back to the buttons scene from the stat scene
    @FXML
    private void goBackToButtons(){
        statStage.close();
        buttonStage.show();
    }

    @FXML
    private void saveToDisk(){
        String filename = new SimpleDateFormat("yyyy.MM.dd_HH:mm").format(Calendar.getInstance().getTime()) + ".txt";
        File dir = new File("SaveFiles");
        try{
            // Creating a new folder called SaveFiles if not exists to save files
            if (!dir.exists()){
                if(!dir.mkdir()){
                    System.out.println("Could not create folder save files??");
                    throw new IOException();
                }
            }

            File checker = new File(dir + "/" + filename);
            int checkCycle = 0;
            while (checker.exists() && checkCycle <= 1){
                filename = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss").format(Calendar.getInstance().getTime()) + ".txt";
                checker = new File(dir + "/" + filename);
                checkCycle++;
            }
            FileWriter writer = new FileWriter(dir + "/" + filename);
            for(Item item : items){
                writer.append(item.name).append(" : ").append(String.valueOf(item.getCount())).append("\n");
            }
            writer.append("\ntotal: ").append(total.getText());
            writer.append("\nminimum: ").append(min.getText());
            writer.append("\nmaximum: ").append(max.getText());
            writer.append("\nmean: ").append(mean.getText());
            writer.append("\nmedian: ").append(median.getText());
            writer.append("\nmode: ").append(mode.getText());
            writer.append("\nrange: ").append(range.getText());

            writer.close();

            Notifications notification = Notifications.create().title("Save successful!").text("Data saved successfully into " + dir.getAbsolutePath() + "/" + filename);
            notification.show();
        }
        catch (IOException e){
            // File saving failed
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save failed!");
            alert.setContentText("Failed to save data into " + dir.getAbsolutePath() + "/" + filename);
            alert.showAndWait();
        }
    }
    @FXML
    private void exit(){
        System.exit(0);
    }

    private void undo(){
        for (Item i : items){
            if (i.ID == Item.getLastInput()){
                i.decrease();
                break;
            }
        }
    }
}