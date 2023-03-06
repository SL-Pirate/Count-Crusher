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

    private static final int[] lastInputs;

    static {
        lastInputs = new int[10];
        cleanLastInput();
    }

    public Item(String name, int ID){
        this.name = name;
        this.ID = ID;
        setupButton();
    }

    public void increase(){
        count++;
        updateBtnName();
        for (int i = 0; i < lastInputs.length - 1; i++){
            lastInputs[i] = lastInputs[i + 1];
        }
        lastInputs[lastInputs.length - 1] = this.ID;
    }
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

    void setupButton(){
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

    static void cleanLastInput(){
        Arrays.fill(lastInputs, -1);
    }

    void updateBtnName(){
        btn.setText(name + "\n" + count);
    }
}
