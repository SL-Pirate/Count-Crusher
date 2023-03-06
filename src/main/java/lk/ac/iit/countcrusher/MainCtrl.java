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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainCtrl{
    @FXML
    private TextArea inputField;
    @FXML
    private Label mean;

    @FXML
    private Label median;

    @FXML
    private Label mode;

    @FXML
    private ListView<?> outView;

    @FXML
    private Label range;

    @FXML
    private Label total;
    private final int numOfLines = 6;

    private Item[] items;

    private Stage mainStage;
    private Stage buttonStage;
    private Stage statStage;
    private final DecimalFormat df = new DecimalFormat("0.###");

    private Item[] getItems () {
        String unformattedTxt = inputField.getText();
        String[] names = unformattedTxt.split("\\r?\\n");
        int numOfValidItems = 0;

        // Calculating the number of valid items
        for (String name : names){
            if (!name.isEmpty())
                numOfValidItems++;
        }
        items = new Item[numOfValidItems];

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

    public Item[] getItemsList(){
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
            buttonStage.show();
        }
    }

    @FXML
    private void goBackHome(){
        buttonStage.close();
        Item.cleanLastInput();
        mainStage.show();
    }

    @FXML
    private void showHelpScene(){
        // TODO
    }

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
            for (int i = 0; i <= spaceLen; i++){
                itemOutput.append(" ");
            }
            itemOutput.append(" : " + item.getCount());
            listviewContent.add(itemOutput.toString());
        }
        outView.setItems(listviewContent);

        Stat stat = new Stat(items);
        total.setText(Integer.toString(stat.getTotal()));
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
                    modeStr.append(Integer.toString(modeVals.get(i)));
                }
                else{
                    modeStr.append(Integer.toString(modeVals.get(i))).append(", ");
                }
            }
        }
        mode.setText(modeStr.toString());
        range.setText(Integer.toString(stat.getRange()));

        statStage.show();
    }

    @FXML
    private void goBackToButtons(){
        statStage.close();
        buttonStage.show();
    }

    @FXML
    private void saveToDisk(){
        try{
            File dir = new File("SaveFiles");
            if (!dir.exists()){
                dir.mkdir();
            }
            String filename = new SimpleDateFormat("yyyy.MM.dd_HH:mm").format(Calendar.getInstance().getTime()) + ".txt";
            FileWriter writer = new FileWriter(dir+ "/" + filename);
            for(Item item : items){
                writer.append(item.name + " : " + item.getCount() + "\n");
            }
            writer.append("\ntotal: " + total.getText());
            writer.append("\nmean: " + mean.getText());
            writer.append("\nmedian: " + median.getText());
            writer.append("\nmode: " + mode.getText());
            writer.append("\nrange: " + range.getText());

            writer.close();

            // TODO
        }
        catch (IOException e){
            System.out.println(e);
            // TODO
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