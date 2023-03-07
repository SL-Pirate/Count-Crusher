package lk.ac.iit.countcrusher;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;

import java.util.Arrays;

public class Item {
    public final String name;
    public final int ID;
    public Button btn;
    private int count;

    // List of items whose values were increased by user where the most recent button is at the end of the list
    private static final int[] lastInputs;

    // Number of inputs that are kept track of user input
    // This means a user can undo this many times
    private static final int lenOfLastInputs = 10;

    // Initializing the lastInputs list to -1 so when a value hits -1 means there are no valid records
    static {
        lastInputs = new int[lenOfLastInputs];
        cleanLastInput();
    }

    public Item(String name, int ID){
        this.name = name;
        this.ID = ID;
        setupButton();
    }

    // increase the count of the item by 1 and add that particular item to the lastInputs list
    public void increase(){
        count++;
        updateBtnName();
        for (int i = 0; i < lastInputs.length - 1; i++){
            lastInputs[i] = lastInputs[i + 1];
        }
        lastInputs[lastInputs.length - 1] = this.ID;
    }

    // decrease the count of the item by 1 and remove that item from the lastInputs list
    public void decrease(){
        count--;
        updateBtnName();
        for (int i = (lastInputs.length - 1); i > 0; i--){
            lastInputs[i] = lastInputs[i - 1];
        }
        lastInputs[0] = -1;
    }

    public int getCount(){
        return count;
    }

    // Creating and configuring the button created for each individual item to be placed in the buttons scene
    private void setupButton(){
        btn = new Button(name + "\n" + count);
        btn.setTextAlignment(TextAlignment.CENTER);
        btn.setAlignment(Pos.CENTER);
        btn.setId(Integer.toString(ID));
        btn.setOnAction(event -> increase());
    }

    // Returns the ID of the last pressed button
    // Returns -1 if the list is empty
    static int getLastInput(){
        return lastInputs[lastInputs.length - 1];
    }

    // Cleans or sets all values of the lastInputs list to -1 which makes then invalid
    static void cleanLastInput(){
        Arrays.fill(lastInputs, -1);
    }

    // Updates the name of the corresponding button after the count of the item has been updated
    void updateBtnName(){
        btn.setText(name + "\n" + count);
    }
}
