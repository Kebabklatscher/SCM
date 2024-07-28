package SCM;

import java.util.ArrayList;

public class Plant {
    private final String name;
    private final ArrayList<String> possiblePorts = new ArrayList<>();
    private int dailyCapacity;
    private int initialCapacity;
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
        this.initialCapacity = capacity;
        this.dailyCapacity = capacity;
    }
    public void decrementCapacity(){
        this.dailyCapacity = this.dailyCapacity - 1;
    }
    public int getDailyCapacity() {
        return dailyCapacity;
    }
    public void resetCapacity(){
        this.dailyCapacity = initialCapacity;
    }
    public double getCost(int quantity) {
        return unitCost*quantity;
    }

    @Override
    public String toString() {
        return "Plant{" +
                "name='" + name + '\'' +
                ", possiblePorts=" + possiblePorts +
                ", capacity=" + dailyCapacity +
                ", unitCost=" + unitCost +
                ", isExclusive=" + exclusive +
                '}';
    }
}
