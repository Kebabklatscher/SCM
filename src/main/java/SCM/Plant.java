package SCM;

import java.util.ArrayList;

public class Plant {
    private final String name;
    private final ArrayList<String> possiblePorts = new ArrayList<>();
    private int capacity;
    private final double unitCost;
    private Boolean exclusive;
    private final ArrayList<String> exclusiveCustomers = new ArrayList<>();

    public Plant(String name, double unitCost) {
        this.name = name;
        this.unitCost = unitCost;
        this.exclusive = false;
    }
    public void addPort(String port){
        this.possiblePorts.add(port);
    }
    public void addExclusiveCustomer(String customer){
        this.exclusiveCustomers.add(customer);
    }
    public void makeExclusive(){
        this.exclusive =true;
    }
    public Boolean isExclusive() {
        return exclusive;
    }
    public Boolean isExclusiveCustomer(String customer){
        return this.exclusiveCustomers.contains(customer);
    }
    public String getName() {
        return name;
    }
    public ArrayList<String> getPossiblePorts() {
        return possiblePorts;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public void decrementCapacity(){
        this.capacity = this.capacity - 1;
    }
    public int getCapacity() {
        return capacity;
    }
    public double getCost(int quantity) {
        return unitCost*quantity;
    }

    @Override
    public String toString() {
        return "Plant{" +
                "name='" + name + '\'' +
                ", possiblePorts=" + possiblePorts +
                ", capacity=" + capacity +
                ", unitCost=" + unitCost +
                ", isExclusive=" + exclusive +
                '}';
    }
}
