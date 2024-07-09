package SCM;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

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
            //TODO: convert provided date from int to LocalDate, for now working with LocalDate.now()
            cell = row.getCell(1);
            int date = (int) cell.getNumericCellValue();
            cell = row.getCell(8);
            String customer = cell.getStringCellValue();
            cell = row.getCell(5);
            String serviceLevel = cell.getStringCellValue();
            cell = row.getCell(9);
            int product = (int) cell.getNumericCellValue();
            cell = row.getCell(12);
            int quantity = (int) cell.getNumericCellValue();
            cell = row.getCell(13);
            double weight = cell.getNumericCellValue();
            //some columns are ignored for now
            orders.add(new Order(id,LocalDate.now(),customer,ServiceLevel.valueOf(serviceLevel),product,quantity,weight));
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
            String modeOfTransport = cell.getStringCellValue();
            //tpt_day_cnt and carrier type are ignored for now
            freightRates.add(new FreightRate(carrier,originPort,destinationPort,minWeight,maxWeight,ServiceLevel.valueOf(serviceLevel),minRate,rate,modeOfTransport));
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
                if (Objects.equals(p.getName(), name)) p.setCapacity(capacity);  //add capacity to existing plants
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
                    if (Objects.equals(p.getId(), id)) p.addPlant(plant);  //add plant to existing product
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
                if (Objects.equals(p.getName(), plant)) p.makeExclusive();  //set exclusivity-flag for corresponding plant
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
                if (Objects.equals(p.getName(), plant)) p.addPort(port);  //add port to existing plants
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
        row.createCell(6).setCellValue(createHelper.createRichTextString("possible Routes"));
        row.createCell(7).setCellValue(createHelper.createRichTextString("Plant"));
        row.createCell(8).setCellValue(createHelper.createRichTextString("Origin Port"));
        row.createCell(9).setCellValue(createHelper.createRichTextString("Destination Port"));
        //carrier not yet implemented
        //row.createCell(10).setCellValue(createHelper.createRichTextString("Carrier"));
        row.createCell(10).setCellValue(createHelper.createRichTextString("Cost"));

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
            row.createCell(6).setCellValue(o.getPossibleRoutes());
            if(o.getPossibleRoutes()!=0){
                row.createCell(7).setCellValue(createHelper.createRichTextString(o.getChosenRoute().getPlant().getName()));
                row.createCell(8).setCellValue(createHelper.createRichTextString(o.getChosenRoute().getPort()));
                row.createCell(9).setCellValue(createHelper.createRichTextString("PORT09"));
                //carrier not yet implemented
                //row.createCell(10).setCellValue(createHelper.createRichTextString(o.getChosenRoute().getCarrier()));
                row.createCell(10).setCellValue(o.getChosenRoute().getCost());
            }else{
                row.createCell(7).setCellValue(createHelper.createRichTextString(""));
                row.createCell(8).setCellValue(createHelper.createRichTextString(""));
                row.createCell(9).setCellValue(createHelper.createRichTextString(""));
                //carrier not yet implemented
                //row.createCell(10).setCellValue(createHelper.createRichTextString(o.getChosenRoute().getCarrier()));
                row.createCell(10).setCellValue(0);
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
}