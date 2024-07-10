package SCM;

public class Route {
    private final Plant plant;
    private String port;
    private double cost;
    private String carrier;
    private FreightRate freightRate;

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
    public String getCarrier() {
        return carrier;
    }
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
    public void setFreightRate(FreightRate freightRate) {
        this.freightRate = freightRate;
    }
    public FreightRate getFreightRate() {
        return freightRate;
    }

    public Route(Plant plant) {
        this.plant = plant;
    }
    public Route(Plant plant, String port) {
        this.plant = plant;
        this.port = port;
    }
}
