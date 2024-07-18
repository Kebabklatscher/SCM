package SCM;

import java.time.LocalDate;
import java.util.ArrayList;

public class Order {

    private final int id;
    private final LocalDate date;
    private final String customer;
    private final ServiceLevel serviceLevel;
    private final int product;
    private final int quantity;
    private final double weight;
    private int possibleRoutes;
    private int chosenRoute;
    private final ArrayList<Route> routes;

    public int getId() {
        return id;
    }
    public String getCustomer() {
        return customer;
    }
    public ServiceLevel getServiceLevel() {
        return serviceLevel;
    }
    public int getProduct() {
        return product;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getWeight() {
        return weight;
    }
    public void planRoutes(ArrayList<Product> products, ArrayList<Plant> plants, ArrayList<FreightRate> freightRates){
        //get possible plants for product
        ArrayList<String> possiblePlantsString = new ArrayList<>();
        ArrayList<Plant> possiblePlants = new ArrayList<>();
        for (Product p: products) {
            if(p.getId()==product){
                possiblePlantsString = p.getPossiblePlants();
                break;
            }
        }
        //get plants with capacity > 0
        for (String s:possiblePlantsString) {
            for (Plant p:plants) {
                if(p.getName().equals(s) && (p.getCapacity() > 0)){
                    possiblePlants.add(p);
                    break;
                }
            }
        }
        //remove exclusive plants where this customer is not an exclusive customer
        possiblePlants.removeIf(p -> p.isExclusive() && !p.isExclusiveCustomer(customer));

        if (possiblePlants.isEmpty())return;

        //calculate transportation cost
        if (serviceLevel == ServiceLevel.CRF) {
            //create new routes
            for (Plant p: possiblePlants) {
                routes.add(new Route(p));
            }
            possibleRoutes = routes.size();
            //transportation cost = 0
            for (Route r : routes) {
                r.setCost(r.getPlant().getCost(quantity));
            }
        }else{  //Service Levels DTD and DTP
            //create new routes
            for (Plant p: possiblePlants) {
                for (String s: p.getPossiblePorts()){
                    routes.add(new Route(p,s));
                }
            }
            possibleRoutes = routes.size();
            //get all possible FreightRates for every route
            ArrayList<Route> impossibleRoute = new ArrayList<>();
            for (Route r : routes) {
                double bufferCost = Double.MAX_VALUE;
                FreightRate bufferFreightRate = null;
                for (FreightRate f: freightRates) {
                    //choose FreightRate with the lowest cost
                    if(f.getOrigPort().equals(r.getPort()) && f.getDestPort().equals("PORT09") && f.isInWeightRange(weight) && f.getServiceLevel().equals(serviceLevel) && (f.getCost(weight)<bufferCost)){
                        bufferCost = f.getCost(weight);
                        bufferFreightRate = f;
                    }
                }
                //if there are no possible FreightRates for this order, mark this as impossible route
                if(bufferFreightRate == null){
                    impossibleRoute.add(r);
                }else{
                    r.setCost(r.getPlant().getCost(quantity)+bufferCost);
                    r.setFreightRate(bufferFreightRate);
                    r.setCarrier(bufferFreightRate.getCarrier());
                }
            }
            //remove impossible routes
            if(!impossibleRoute.isEmpty()){
                for (Route r: impossibleRoute) {
                    routes.remove(r);
                }
                possibleRoutes = routes.size();
            }
        }
    }
    public void checkRoutes(){
        ArrayList<Route> impossibleRoute = new ArrayList<>();
        for (Route r: routes) {
            if (r.getPlant().getCapacity()<=0){
                impossibleRoute.add(r);
            }
        }
        //remove impossible routes
        if(!impossibleRoute.isEmpty()){
            for (Route r: impossibleRoute) {
                routes.remove(r);
            }
            possibleRoutes = routes.size();
        }
    }
    public void chooseCheapestRoute(){
        double bufferCost = Double.MAX_VALUE;
        Route bufferRoute = null;
        for (Route r: routes) {
            if (r.getCost()<bufferCost){
                bufferCost = r.getCost();
                bufferRoute = r;
            }
        }
        chosenRoute = routes.indexOf(bufferRoute);
    }
    public int getPossibleRoutes() {
        return possibleRoutes;
    }
    public Route getChosenRoute(){
        return routes.get(chosenRoute);
    }
    public Order(int id, LocalDate date, String customer, ServiceLevel serviceLevel, int product, int quantity, double weight) {
        this.id = id;
        this.date = date;
        this.customer = customer;
        this.serviceLevel = serviceLevel;
        this.product = product;
        this.quantity = quantity;
        this.weight = weight;
        this.routes = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date=" + date +
                ", customer='" + customer + '\'' +
                ", serviceLevel=" + serviceLevel +
                ", product=" + product +
                ", quantity=" + quantity +
                ", weight=" + weight +
                ", cost=" + routes.get(chosenRoute).getCost() +
                '}';
    }
}
