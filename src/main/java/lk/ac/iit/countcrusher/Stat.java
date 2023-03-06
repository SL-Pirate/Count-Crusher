package lk.ac.iit.countcrusher;

import java.util.*;

public class Stat {
    private final Item[] items;

    Stat (Item[] items){
        for (int i = 1; i < items.length; i++){
            for (int j = 0; j < items.length - i; j++){
                if (items[j].getCount() > items[j + 1].getCount()){
                    Item tmp = items[j];
                    items[j] = items[j + 1];
                    items[j + 1] = tmp;
                }
            }
        }
        this.items = items;
    }

    public int getTotal() {
        int total = 0;
        for (Item item : items) {
            total += item.getCount();
        }
        return total;
    }

    public double getMean(){
        return getTotal() / (double) items.length;
    }

    public double getMedian(){
        double median;
        if (items.length % 2 == 0){
            int num1 = items[(items.length / 2) - 1].getCount();
            int num2 = items[items.length / 2].getCount();

            median = (num1 + num2) / 2.0;
        }
        else{
            median = items[items.length / 2].getCount();
        }

        return median;
    }

    public List<Integer> getMode(){
        int maxOccurrence = 0;
        List<Integer> mode = new ArrayList<>();

        for (int i = 0; i < items.length; i++){
            int count = 0;
            for (int j = 0; j < items.length; j++){
                if (i == j){
                    count++;
                }
            }
            if (count > maxOccurrence){
                maxOccurrence = count;
            }
        }
        for (int i = 0; i < items.length; i++){
            int count = 0;
            for (int j = 0; j < items.length; j++){
                if (i == j){
                    count++;
                }
            }
            if (count == maxOccurrence){
                mode.add(items[i].getCount());
            }
        }
        return mode;
    }

    public int range(){
        return items[items.length - 1].getCount() - items[0].getCount();
    }
}
