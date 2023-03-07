package lk.ac.iit.countcrusher;

import java.util.*;

public class Stat {
    private final Item[] items;

    Stat (Item[] items){
        // sorting the items list corresponding to the count of each item
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

    public int getMax(){
        // Since item list is sorted from minimum to maximum, getting the last item would give the maximum value
        return items[items.length - 1].getCount();
    }

    public int getMin(){
        // Since item list is sorted from minimum to maximum, getting the first item would give the maximum value
        return items[0].getCount();
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

        // Finding the maximum occurrence of the items
        for (Item item : items) {
            int count = 0;
            for (Item value : items) {
                if (item.getCount() == value.getCount()) {
                    count++;
                }
            }
            if (count > maxOccurrence) {
                maxOccurrence = count;
            }
        }

        // getting items with the highest occurrence
        for (Item item : items) {
            int count = 0;
            for (Item value : items) {
                if (item.getCount() == value.getCount()) {
                    count++;
                }
            }
            if (count == maxOccurrence) {
                mode.add(item.getCount());
            }
        }

        //removing duplicates
        Set<Integer> tmp = new LinkedHashSet<>(mode);
        mode.clear();
        mode.addAll(tmp);

        return mode;
    }

    public int getRange(){
        return items[items.length - 1].getCount() - items[0].getCount();
    }
}
