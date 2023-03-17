package lk.ac.iit.countcrusher;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.Notifications;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class StatCtrl {
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

    private Stage btnStage;
    private Stage statStage;

    private final Item[] items = MainCtrl.getItemList();

    // Floating point number conversions to String
    private final DecimalFormat df = new DecimalFormat("0.###");

    // To track if the user has saved data before existing
    private boolean hasSaved = false;

    //Initialize the scene
    public void init (Stage btnStage, Stage statStage) {
        this.btnStage = btnStage;
        this.statStage = statStage;

        statStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::exitByX);

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
    }
    @FXML
    private boolean saveToDisk(){
        String filename = new SimpleDateFormat("yyyy.MM.dd_HH.mm").format(Calendar.getInstance().getTime()) + ".txt";
        File dir = new File("SaveFiles");
        try{
            // Creating a new folder called SaveFiles if not exists to save files
            if (!dir.exists()){
                if(!dir.mkdir()){
                    System.out.println("Could not create folder save files");
                    throw new IOException();
                }
            }

            File checker = new File(dir + "/" + filename);
            int checkCycle = 0;
            while (checker.exists() && checkCycle <= 1){
                filename = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(Calendar.getInstance().getTime()) + ".txt";
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
            writer.append("\nrange: ").append(range.getText()).append("\n");

            writer.close();

            hasSaved = true;

            Notifications notification = Notifications.create().title("Save successful!").text("Data saved successfully into " + dir.getAbsolutePath() + "/" + filename);
            notification.show();

            return true;
        }
        catch (IOException e){
            // File saving failed
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save failed!");
            alert.setContentText("Failed to save data into " + dir.getAbsolutePath() + "/" + filename);
            alert.showAndWait();
        }

        return false;
    }
    @FXML
    private void exit(){
        if (hasSaved) {
            System.exit(0);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.CANCEL, ButtonType.APPLY, ButtonType.YES);
            alert.setTitle("Confirm exit");
            alert.setContentText("You have unsaved data. Are you sure you want to exit? ");

            Button okBtn = ((Button) alert.getDialogPane().lookupButton(ButtonType.YES));
            Button noBtn = ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL));
            Button saveBtn = ((Button) alert.getDialogPane().lookupButton(ButtonType.APPLY));

            okBtn.setText("Exit Anyway!");
            saveBtn.setText("Save and exit");
            okBtn.setOnAction(e -> {
                Platform.exit();});
            saveBtn.setOnAction(e -> {
                if (saveToDisk()){
                    Platform.exit();
                }
            });

            alert.showAndWait();
        }
    }

    private void exitByX(WindowEvent evt){
        exit();
        evt.consume();
    }

    // Going back to the buttons scene from the stat scene
    @FXML
    private void goBackToButtons(){
        statStage.close();
        btnStage.show();
    }
}
