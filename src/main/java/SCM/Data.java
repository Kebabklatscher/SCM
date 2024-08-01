package SCM;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class Data {
    public ArrayList<Plant> plants = new ArrayList<>();
    public ArrayList<Product> products = new ArrayList<>();
    public ArrayList<FreightRate> freightRates = new ArrayList<>();
    public ArrayList<Order> orders = new ArrayList<>();

    public void readFile() throws IOException {
        FileInputStream fis = new FileInputStream("Supply chain logistics problem.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        //Sheet 0 - OrderList
        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); //skip first row
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next(); //iterate through all rows
            Cell cell = row.getCell(0);
            int id = (int) cell.getNumericCellValue();
            cell = row.getCell(1);
            LocalDate date = cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            cell = row.getCell(8);
            String customer = cell.getStringCellValue();
            cell = row.getCell(5);
            String serviceLevel = cell.getStringCellValue().replaceAll("\\s+","");  //remove whitespaces from cells
            cell = row.getCell(9);
            int product = (int) cell.getNumericCellValue();
            cell = row.getCell(12);
            int quantity = (int) cell.getNumericCellValue();
            cell = row.getCell(13);
            double weight = cell.getNumericCellValue();
            //some columns are ignored for now
            orders.add(new Order(id,date,customer,ServiceLevel.valueOf(serviceLevel),product,quantity,weight));
        }
        //Sheet 1 - FreightRates
        sheet = wb.getSheetAt(1);
        rowIterator = sheet.iterator();
        rowIterator.next(); //skip first row
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next(); //iterate through all rows
            Cell cell = row.getCell(0);
            String carrier = cell.getStringCellValue();
            cell = row.getCell(1);
            String originPort = cell.getStringCellValue();
            cell = row.getCell(2);
            String destinationPort = cell.getStringCellValue();
            cell = row.getCell(3);
            double minWeight = cell.getNumericCellValue();
            cell = row.getCell(4);
            double maxWeight = cell.getNumericCellValue();
            cell = row.getCell(5);
            String serviceLevel = cell.getStringCellValue();
            cell = row.getCell(6);
            double minRate = cell.getNumericCellValue();
            cell = row.getCell(7);
            double rate = cell.getNumericCellValue();
            cell = row.getCell(8);
            String modeOfTransport = cell.getStringCellValue().replaceAll("\\s+","");   //remove whitespaces from cells
            cell = row.getCell(9);
            int transportTime = (int) cell.getNumericCellValue();
            freightRates.add(new FreightRate(carrier,originPort,destinationPort,minWeight,maxWeight,ServiceLevel.valueOf(serviceLevel),minRate,rate,ModeOfTransport.valueOf(modeOfTransport),transportTime));
        }
        //Sheet 2 - WhCosts
        sheet = wb.getSheetAt(2);
        rowIterator = sheet.iterator();
        rowIterator.next(); //skip first row
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next(); //iterate through all rows
            Cell cell = row.getCell(0);
            String name = cell.getStringCellValue();
            cell = row.getCell(1);
            double cost = cell.getNumericCellValue();
            plants.add(new Plant(name, cost));
        }
        //Sheet 3 - WhCapacities
        sheet = wb.getSheetAt(3);
        rowIterator = sheet.iterator();
        rowIterator.next(); //skip first row
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next(); //iterate through all rows
            Cell cell = row.getCell(0);
            String name = cell.getStringCellValue();
            cell = row.getCell(1);
            int capacity = (int) cell.getNumericCellValue();
            for (Plant p : plants) {
                if (p.getName().equals(name)) p.setCapacity(capacity);  //add capacity to existing plants
            }
        }
        //Sheet 4 - ProductPerPlant
        sheet = wb.getSheetAt(4);
        rowIterator = sheet.iterator();
        rowIterator.next(); //skip first row
        ArrayList<Integer> allProductsBuffer = new ArrayList<>();    //buffer to search for existing products
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next(); //iterate through all rows
            Cell cell = row.getCell(0);
            String plant = cell.getStringCellValue();
            cell = row.getCell(1);
            int id = (int) cell.getNumericCellValue();
            if(allProductsBuffer.contains(id)){ //search for existing product
                for (Product p : products) {
                    if (p.getId()== id) p.addPlant(plant);  //add plant to existing product
                }
            }else{
                products.add(new Product(id,plant));
                allProductsBuffer.add(id);
            }
        }
        //Sheet 5 - VmiCustomers
        sheet = wb.getSheetAt(5);
        rowIterator = sheet.iterator();
        rowIterator.next(); //skip first row
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next(); //iterate through all rows
            Cell cell = row.getCell(0);
            String plant = cell.getStringCellValue();
            cell = row.getCell(1);
            String name = cell.getStringCellValue();
            for (Plant p : plants) {
                if (p.getName().equals(plant)) p.makeExclusive();  //set exclusivity-flag for corresponding plant
                p.addExclusiveCustomer(name);
            }
        }
        //Sheet 6 - PlantPorts
        sheet = wb.getSheetAt(6);
        rowIterator = sheet.iterator();
        rowIterator.next(); //skip first row
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next(); //iterate through all rows
            Cell cell = row.getCell(0);
            String plant = cell.getStringCellValue();
            cell = row.getCell(1);
            String port = cell.getStringCellValue();
            for (Plant p : plants) {
                if (p.getName().equals(plant)) p.addPort(port);  //add port to existing plants
            }
        }

        fis.close();
        System.out.println("File was read successfully");
    }

    public void writeFile(){
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Orders");
        CreationHelper createHelper = wb.getCreationHelper();


        //TODO: cell formatting
        //first row
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue(createHelper.createRichTextString("ID"));
        row.createCell(1).setCellValue(createHelper.createRichTextString("Customer"));
        row.createCell(2).setCellValue(createHelper.createRichTextString("Servicelevel"));
        row.createCell(3).setCellValue(createHelper.createRichTextString("Product"));
        row.createCell(4).setCellValue(createHelper.createRichTextString("Quantity"));
        row.createCell(5).setCellValue(createHelper.createRichTextString("Weight"));
        row.createCell(6).setCellValue(createHelper.createRichTextString("Plant"));
        row.createCell(7).setCellValue(createHelper.createRichTextString("Origin Port"));
        row.createCell(8).setCellValue(createHelper.createRichTextString("Destination Port"));
        row.createCell(9).setCellValue(createHelper.createRichTextString("Carrier"));
        row.createCell(10).setCellValue(createHelper.createRichTextString("Mode of Transport"));
        row.createCell(11).setCellValue(createHelper.createRichTextString("Transport Time"));
        row.createCell(12).setCellValue(createHelper.createRichTextString("Cost"));

        //other rows
        int i = 1;
        for (Order o:orders) {
            row = sheet.createRow(i);
            i++;
            row.createCell(0).setCellValue(o.getId());
            row.createCell(1).setCellValue(createHelper.createRichTextString(o.getCustomer()));
            row.createCell(2).setCellValue(createHelper.createRichTextString(o.getServiceLevel().toString()));
            row.createCell(3).setCellValue(o.getProduct());
            row.createCell(4).setCellValue(o.getQuantity());
            row.createCell(5).setCellValue(o.getWeight());
            //if there was an error while generating routes, write empty strings
            if(o.getPossibleRoutes()!=0){
                row.createCell(6).setCellValue(createHelper.createRichTextString(o.getChosenRoute().getPlant().getName()));
                row.createCell(7).setCellValue(createHelper.createRichTextString(o.getChosenRoute().getPort()));
                row.createCell(8).setCellValue(createHelper.createRichTextString("PORT09"));

                //CRF doesn't have carrier and origin port, the other service levels do
                if(o.getServiceLevel().equals(ServiceLevel.CRF)){
                    row.createCell(9).setCellValue(createHelper.createRichTextString(""));
                    row.createCell(10).setCellValue(createHelper.createRichTextString(""));
                }else{
                    row.createCell(9).setCellValue(createHelper.createRichTextString(o.getChosenRoute().getFreightRate().getCarrier()));
                    row.createCell(10).setCellValue(createHelper.createRichTextString(o.getChosenRoute().getFreightRate().getModeOfTransport().toString()));
                }
                row.createCell(11).setCellValue(o.getTransportTime());
                row.createCell(12).setCellValue(o.getChosenRoute().getCost());
            }else{
                row.createCell(6).setCellValue(createHelper.createRichTextString(""));
                row.createCell(7).setCellValue(createHelper.createRichTextString(""));
                row.createCell(8).setCellValue(createHelper.createRichTextString(""));
                row.createCell(9).setCellValue(createHelper.createRichTextString(""));
                row.createCell(10).setCellValue(createHelper.createRichTextString(""));
                row.createCell(11).setCellValue(0);
                row.createCell(12).setCellValue(0);
            }
        }

        try {
            FileOutputStream fos = new FileOutputStream("orders.xlsx");
            wb.write(fos);
            fos.close();
            System.out.println("Order file was exported successfully");
        }
        catch (IOException error) {
            System.out.println("Error while writing the file: " + error.getMessage());
        }
    }

    public void calculateGroundCost(){
        //combine the freight rates of orders of the same day and origin port and updates the orders

        ArrayList<Order> groundOrders = new ArrayList<>();
        for(Order o:orders){
            if(o.getServiceLevel()!=ServiceLevel.CRF && o.getChosenRoute().getFreightRate().getModeOfTransport().equals(ModeOfTransport.GROUND)){
                groundOrders.add(o);
            }
        }
        orders.removeAll(groundOrders);
        //TODO: for simplicity, this function combines orders regardless of service level, further work needed here
        Map<String, List<Order>> groundOrdersMap = groundOrders.stream().collect(Collectors.groupingBy(o -> o.getChosenRoute().getPort()));
        double weight;
        for(String key:groundOrdersMap.keySet()){
            weight=0;
            for(Order o:groundOrdersMap.get(key)){
                weight+=o.getWeight();
            }
            //search for possible FreightRates
            double bufferCost = Double.MAX_VALUE;
            FreightRate bufferFreightRate = null;
            for (FreightRate f: freightRates) {
                //choose GROUND FreightRate with the lowest cost
                if(f.getOrigPort().equals(key) && f.getDestPort().equals("PORT09") && f.getModeOfTransport().equals(ModeOfTransport.GROUND) && f.isInWeightRange(weight) && (f.getCost(weight)<bufferCost)){
                    bufferCost = f.getCost(weight);
                    bufferFreightRate = f;
                }
            }
            //if there are possible FreightRates, choose the new GROUND Route
            if(!(bufferFreightRate == null)){
                for(Order o:groundOrdersMap.get(key)){
                    Route route = o.getChosenRoute();
                    route.setFreightRate(bufferFreightRate);
                    route.setCost(bufferCost/groundOrdersMap.get(key).size());
                    o.setChosenRoute(route);
                }
            }
            orders.addAll(groundOrdersMap.get(key));
        }
    }
}