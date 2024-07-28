package SCM;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        //measure execution time
        long timeStart = System.currentTimeMillis();
        //import testdata
        Data data = new Data();
        try{
            data.readFile();
        }catch (IOException error){
            System.out.println("Error while reading the file:" + error.getMessage());
            return;
        }

        int unresolvedOrders = 0;
        //calculate all possible routes for every order
        for (Order o: data.orders){
            o.planRoutes(data.products,data.plants,data.freightRates);
        }
        //apply route choosing algorithm
        Data outputData = chooseRoutes(data);

        //count unresolved orders
        for (Order o: outputData.orders){
            if (o.getPossibleRoutes()==0){
                unresolvedOrders++;
            }
        }
        System.out.println("Total orders: " + outputData.orders.size());
        System.out.println("Shipped orders: " + (outputData.orders.size() - unresolvedOrders));
        System.out.println("Unresolved orders: " + unresolvedOrders);

        //TODO: calculate GROUND shipping cost (combining all routes from the same port on the same day in 1 freightrate)
        //calculate total cost
        double totalCost = 0;
        for (Order o: outputData.orders){
            if(o.getPossibleRoutes()>0){
                totalCost+=o.getChosenRoute().getCost();
            }
        }
        //round total cost to cents
        totalCost = (double) Math.round(totalCost * 100) /100;
        System.out.println("Total cost: " + totalCost + " €");
        //write all orders to Excel file
        outputData.writeFile();
        long timeEnd = System.currentTimeMillis();

        System.out.println("Total execution time: " + (timeEnd-timeStart) + "ms");
    }

    public static Data chooseRoutes(Data data){
        Data outputData = new Data();
        boolean totalCapacityChanged = false;
        Plant buffer;
        while (!data.orders.isEmpty()){
            for (Order o: data.orders) {
                //check if routes are now impossible due to capacity
                o.checkRoutes();
                switch (o.getPossibleRoutes()){
                    case 0:
                        outputData.orders.add(o);
                        break;
                    case 1:
                        o.chooseCheapestRoute();
                        buffer = o.getChosenRoute().getPlant();
                        buffer.decrementCapacity();
                        data.plants.set(data.plants.indexOf(o.getChosenRoute().getPlant()), buffer);
                        outputData.orders.add(o);
                        totalCapacityChanged = true;
                        break;
                    default:
                        //when there are no more orders with 0 and 1, choose a route
                        if(!totalCapacityChanged){
                            o.chooseCheapestRoute();
                            buffer = o.getChosenRoute().getPlant();
                            buffer.decrementCapacity();
                            data.plants.set(data.plants.indexOf(o.getChosenRoute().getPlant()), buffer);
                            outputData.orders.add(o);
                            totalCapacityChanged = true;
                        }
                        break;
                }
            }
            data.orders.removeAll(outputData.orders);
            totalCapacityChanged = false;
        }
        return outputData;
    }
}

