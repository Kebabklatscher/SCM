package SCM;

import java.util.ArrayList;
public class Product {
    private final int id;
    private final ArrayList<String> possiblePlants = new ArrayList<>();

    public Product(int id, String plant){
        this.id=id;
        this.addPlant(plant);
    }
    public void addPlant(String plant){
        this.possiblePlants.add(plant);
    }
    public int getId() {
        return id;
    }
    public ArrayList<String> getPossiblePlants() {
        return possiblePlants;
    }
}
