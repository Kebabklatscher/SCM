package SCM;

import java.io.IOException;
import java.util.ArrayList;

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
        System.out.println("Total orders: " + data.orders.size());

        int unresolvedOrders = 0;

        //apply route choosing algorithm
        Data outputData = chooseRoutes(data);

        //count unresolved orders
        for (Order o: outputData.orders){
            if (o.getPossibleRoutes()==0){
                unresolvedOrders++;
            }
        }
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
        int totalCostRounded;
        totalCost = (double) Math.round(totalCost * 100) /100;
        System.out.printf("Total cost: " + "%10.2f" + " €\n", totalCost);

        //write all orders to Excel file
        outputData.writeFile();
        long timeEnd = System.currentTimeMillis();

        System.out.println("Total execution time: " + (timeEnd-timeStart) + "ms");
    }

    public static Data chooseRoutes(Data data){
        Data outputData = new Data();
        boolean totalCapacityChanged = false;
        Plant buffer;
        ArrayList<Order> nextDayBuffer = new ArrayList<>();
        int dayCounter = 0;
        int unshippedOrders = data.orders.size();
        //calculate shipping day by day
        while(dayCounter<15 && !data.orders.isEmpty()){
            //calculate all possible routes for every order
            for (Order o: data.orders){
                o.planRoutes(data.products,data.plants,data.freightRates);
            }
            while (!data.orders.isEmpty()){
                for (Order o: data.orders) {
                    //check if routes are now impossible due to capacity
                    o.checkRoutes();
                    switch (o.getPossibleRoutes()){
                        case 0:
                            nextDayBuffer.add(o);
                            break;
                        case 1:
                            o.chooseCheapestRoute();
                            buffer = o.getChosenRoute().getPlant();
                            buffer.decrementCapacity();
                            data.plants.set(data.plants.indexOf(o.getChosenRoute().getPlant()), buffer);
                            o.setTransportTime(dayCounter);
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
                                o.setTransportTime(dayCounter);
                                outputData.orders.add(o);
                                totalCapacityChanged = true;
                            }
                            break;
                    }
                }
                data.orders.removeAll(outputData.orders);
                data.orders.removeAll(nextDayBuffer);
                totalCapacityChanged = false;
            }

            System.out.println("Day " + dayCounter + ": " + (unshippedOrders - nextDayBuffer.size()) + " orders shipped");
            unshippedOrders = nextDayBuffer.size();
            dayCounter++;
            //add all remaining orders to the next day
            data.orders.addAll(nextDayBuffer);
            nextDayBuffer.clear();
            //reset capacities
            for(Plant p: data.plants){p.resetCapacity();}
        }
        return outputData;
    }
}

