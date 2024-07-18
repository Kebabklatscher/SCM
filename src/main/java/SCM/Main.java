/*
This program is used to provide a possible solution for a supply chain logistics problem.
It was written for a supply chain management course at Hochschule Merseburg.
Due to limited time, this program does not fully solve the problem. Some constraints from the original paper were left out, e.g. the shipping time. This program is only able to solve orders that are ordered during one single day.
There are also likely numerous optimization potentials.

original supply chain logistics problem source:
https://www.sciencedirect.com/science/article/pii/S0360835220303442?via%3Dihub
original dataset source:
https://brunel.figshare.com/articles/dataset/Supply_Chain_Logistics_Problem_Dataset/7558679

© Markus Heinze, 2024
 */

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
        double totalCost = 0;
        Data outputData = new Data();
        boolean totalCapacityChanged = false;
        for (Order o: data.orders){
            o.planRoutes(data.products,data.plants,data.freightRates);
        }
        Plant buffer = null;
        while (!data.orders.isEmpty()){
            for (Order o: data.orders) {
                //calculate, how many routes are possible for every order
                o.checkRoutes();
                switch (o.getPossibleRoutes()){
                    case 0:
                        unresolvedOrders++;
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
                //TODO: add optimization of combining orders that use the same carrier (add weight -> reduce cost)
            }
            data.orders.removeAll(outputData.orders);
            totalCapacityChanged = false;
        }

        System.out.println(unresolvedOrders + " Orders out of " + outputData.orders.size() + " could not be shipped");
        //calculate total cost
        for (Order o: outputData.orders){
            if(o.getPossibleRoutes()>0){
                totalCost+=o.getChosenRoute().getCost();
            }
        }
        //round total cost to cents
        totalCost = (double) Math.round(totalCost * 100) /100;
        System.out.println("Total cost: " + totalCost + " €");
        //write all orders to excel file
        outputData.writeFile();
        long timeEnd = System.currentTimeMillis();

        System.out.println("Total execution time: " + (timeEnd-timeStart) + "ms");
    }
}

