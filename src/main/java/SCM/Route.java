package SCM;


public class Route {
    private final Plant plant;
    private String port;
    private double cost;
    //TODO: add carrier
    public Plant getPlant() {
        return plant;
    }
    public String getPort() {
        return port;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
    public double getCost() {
        return cost;
    }
    public Route(Plant plant) {
        this.plant = plant;
    }
}
