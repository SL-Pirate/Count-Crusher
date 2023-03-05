package lk.ac.iit.countcrusher;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;

public class Item {
    public final String name;
    public final int ID;
    public Button btn;
    private int count;

    public Item(String name, int ID){
        this.name = name;
        this.ID = ID;
        setupButton();
    }

    public void increase(){
        count++;
    }
    public void decrease(){
        count--;
    }

    void setupButton(){
        btn = new Button(name + "\n" + count);
        btn.setTextAlignment(TextAlignment.CENTER);
        btn.setAlignment(Pos.CENTER);
        btn.setId(Integer.toString(ID));
        btn.setOnAction(event -> {
            increase();
            btn.setText(name + "\n" + count);
        });
    }
}
