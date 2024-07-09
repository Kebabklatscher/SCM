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
                if(p.getName().equals(s) && (p.getCapacity() > 1)){
                    possiblePlants.add(p);
                    break;
                }
            }
        }
        //remove exclusive plants where this customer is not en exclusive customer
        possiblePlants.removeIf(p -> p.isExclusive() && !p.isExclusiveCustomer(customer));

        //possibleRoutes = possiblePlants
        possibleRoutes = possiblePlants.size();
        if (possibleRoutes==0)return;
        //create new routes
        for (Plant p: possiblePlants) {
            routes.add(new Route(p));
        }

        //calculate transportation cost
        switch (serviceLevel){
            case CRF:
                //transportation cost = 0
                for (Route r:routes) {
                    r.setCost(r.getPlant().getUnitCost()*quantity);
                }
                break;
            case DTP:
                //TODO: transportation cost
                break;
            case DTD:
                //TODO: transportation cost
                break;
            default: //unused
        }
        //choose the cheapest route
        double bufferCost = 999999999;
        Route bufferRoute = null;
        for (Route r: routes) {
            if (r.getCost()<bufferCost){
                bufferCost = r.getCost();
                bufferRoute = r;
                break;
            }
        }
        chosenRoute = routes.indexOf(bufferRoute);
    }
    public int getPossibleRoutes() {
        return possibleRoutes;
    }
    public Route getChosenRoute(){
        return this.routes.get(chosenRoute);
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
                ", cost=" + this.routes.get(chosenRoute).getCost() +
                '}';
    }
}
