/*  
Name:  Christopher Badolato   
Course: CNT 4714 – Spring 2020   
Assignment title: Project 1 – Event-driven Enterprise Simulation  
Date: Sunday January 26, 2020 */ 

package project1cbadolato_bookshop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;



public class FXMLDocumentController implements Initializable {
        //all fxml controller variables
    @FXML
    private Button processItemButton;
    @FXML
    private Button confirmItemButton;
    @FXML
    private Button viewOrderButton;
    @FXML
    private Button finishOrderButton;
    @FXML
    private Button newOrderButton;
    @FXML
    private Button exitButton;
    @FXML
    private TextField orderSubtotalField;
    @FXML
    private TextField itemInfoField;
    @FXML
    private TextField itemQuantityField;
    @FXML
    private TextField bookIDField;
    @FXML
    private TextField itemInOrderField;
    @FXML
    private Label bookIDText;
    @FXML
    private Label quantityText;
    @FXML
    private Label itemInfoText;
    @FXML
    private Label orderSubtotalText;
        //arrays to store input from inventory.txt
    int[] bookID = new int[50];
    String[] bookTitle = new String[50];
    float[] bookCost = new float[50];
        //variables that represent a single book order (change each new book)
    float singleBookTotalCost = 0, totalDiscount = 0, discountFraction = 0;
    float subtotal = 0, transactionTotal = 0;
    int  totalItemsLeftInOrder, itemNumber = 1, currentBookArrayLoction = 0, totalItemsInOrder;  
    int singleBookQuantity, currentDiscount;
    boolean firstOrder = true;

   
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
   
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
            //initialize buttons
        processItemButton.setDisable(false);
        processItemButton.setOpacity(1);
        confirmItemButton.setDisable(true);
        confirmItemButton.setOpacity(.5);       
        viewOrderButton.setDisable(false);
        viewOrderButton.setOpacity(1);        
        finishOrderButton.setDisable(false);
        finishOrderButton.setOpacity(.5);
        bookIDField.setEditable(true);
        itemQuantityField.setEditable(true); 
        itemInOrderField.setEditable(true);
        firstOrder = true;
            //get book lists.
        try {
            getBooksList();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
     

    @FXML
    private void exitButton(MouseEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close(); 
    }

    @FXML
    private void processItemButton(MouseEvent event) throws FileNotFoundException, UnsupportedEncodingException {
            //reset individual Book discount total, quantity and cost       
        singleBookQuantity = 0;
        singleBookTotalCost = 0;
        totalDiscount = 0;
        String id, quantity, numberOfItemsInOrder;
        int intID;
        int[] idArray = bookID;      
        float cost, totalCost;
            //if nothing is entered into any of the 3 info boxes, we just do nothing.
        if("".equals(itemInOrderField.getText()) | "".equals(bookIDField.getText()) | "".equals(itemQuantityField.getText())){
            
        }
            //otherwise process the item.
        else{
                //get values from text fields
            id = bookIDField.getText();
            quantity = itemQuantityField.getText();
            numberOfItemsInOrder = itemInOrderField.getText();     
            itemInOrderField.setEditable(false);      

                //make sure they are integers
            intID = Integer.parseInt(id);
            singleBookQuantity = Integer.parseInt(quantity);
            totalItemsInOrder = Integer.parseInt(numberOfItemsInOrder);

                //calculate discount fraction
            currentDiscount = discount(singleBookQuantity);
            discountFraction = (float)(currentDiscount/100.0);               
                //for each value on the bookID arry we want to check if our ID's match.
                //if they do match we want to, add item to item info text field. 
                //once we have confirmed our Book with entered ID is correct, 
                //calculate the discount, and the single book subtotal.
            for(currentBookArrayLoction = 0; currentBookArrayLoction < idArray.length; currentBookArrayLoction++){      
                if(intID == idArray[currentBookArrayLoction]){                  
                    cost = bookCost[currentBookArrayLoction];               
                    totalDiscount = (float)(cost * singleBookQuantity) * discountFraction;
                    totalCost = (float)(cost * singleBookQuantity);
                    singleBookTotalCost = totalCost - totalDiscount;
                    String subtotalStr = String.format("%.2f", singleBookTotalCost);              
                        //upadate TextField
                    itemInfoField.setText(bookID[currentBookArrayLoction] + " " + bookTitle[currentBookArrayLoction] 
                            + " " + "$" + bookCost[currentBookArrayLoction] 
                            + " " +  singleBookQuantity + " " + currentDiscount + "% "+ "$" + subtotalStr);
                        //turn off proccess item button, turn on confirm item button.
                    processItemButton.setDisable(true);
                    processItemButton.setOpacity(.5);
                    confirmItemButton.setDisable(false);
                    confirmItemButton.setOpacity(1);
                    break;
                }
                if(idArray.length - 1 == currentBookArrayLoction){
                        //book id is not on file. display warning
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Book ID " + intID + " not in file.");       
                    alert.showAndWait();
                }
                else{                               
                }
            }
                //we only want to set our total items for the rest of the program if it is our first order.
            if(firstOrder == true){
                totalItemsLeftInOrder = totalItemsInOrder;
                firstOrder = false;
            }
        }
    }   
        //confirm item to transaction file.
        //display alert that the item has been confirmed and added to the transaction file.
    @FXML
    private void confirmItemNumberButton(MouseEvent event) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        writeToTransactionFile();
        finishOrderButton.setDisable(false);
        finishOrderButton.setOpacity(1);
        subtotal = subtotal + singleBookTotalCost;
        totalItemsLeftInOrder--;
        String subtotalStr = String.format("$%.2f", subtotal); 
        orderSubtotalField.setText(subtotalStr);
            //if our number of items in order is 0. we can only view, finish, start a new order, or exit.
        if(totalItemsLeftInOrder <= 0){
                //we have no items left in our order.
                //shut off text fields and labels
            confirmItemButton.setDisable(true);
            confirmItemButton.setOpacity(.5);
            processItemButton.setDisable(true);
            processItemButton.setOpacity(.5);          
            bookIDText.setText("");
            quantityText.setText("");
            bookIDField.setText("");
            itemQuantityField.setText("");           
            bookIDField.setEditable(false);
            itemQuantityField.setEditable(false);         
        }
            //otherwise we still have items in our order so continue normally.
        else{              
            confirmItemButton.setDisable(true);
            confirmItemButton.setOpacity(.5);
            processItemButton.setDisable(false);
            processItemButton.setOpacity(1);      
            bookIDField.setText("");
            itemQuantityField.setText("");      
            bookIDText.setText("Enter Book ID for item #" + (itemNumber + 1) + ":");
            quantityText.setText("End quantity for item #" + (itemNumber + 1) + ":");
            bookIDField.setEditable(true);
            itemQuantityField.setEditable(true);
        }       
            //display alert message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Item Accepted");
        alert.setHeaderText(null);
        alert.setContentText("Item #"+ itemNumber + " Accepted"); 
        itemNumber++;           
        alert.showAndWait(); 
            //set button and label text
        processItemButton.setText("Process Item #" + itemNumber);
        confirmItemButton.setText("Confirm Item #" + itemNumber);
        itemInfoText.setText("Item #" + (itemNumber - 1) + " info:");
        orderSubtotalText.setText("Order subtotal for " + (itemNumber - 1) + " item(s):"); 
    }

    @FXML
   
    private void viewOrderButton(MouseEvent event) throws IOException, InterruptedException{        
        int i = 0, windowSize;
        String outputText = "";
            //read data from transaction file
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("View Order");
        alert.setHeaderText(null);
            //get text for output message from transaction file
        outputText = outputText + textFromTransactionFile();
        alert.setContentText(outputText);
        alert.setResizable(true);
        windowSize = 200 + (i * 30);
        alert.getDialogPane().setPrefSize(800, windowSize);
        alert.showAndWait();     
    }

    @FXML
    private void FinishOrderButton(MouseEvent event) throws IOException {             
        int i = 0, windowSize;
        String outputText = "";       
        float orderTotal, taxValue = (float) 0.06, taxAmount;
            //read data from transaction file     
        LocalDateTime dateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM/d/yy");
        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("hh:mm:ss a");      
        String date = formatter2.format(dateAndTime);
        String time = formatter3.format(dateAndTime);      
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Finish Order");
        alert.setHeaderText(null);
            //grab each string off the file, delimit, then add to our output string
            //that will be displayed in a alert message
        outputText = outputText + "Date: " + date + " " + time + " EST\n\n";
        outputText = outputText + "Number of line items: " + totalItemsInOrder + "\n\n";
        outputText = outputText + "Item#/ID/Title/Price/Qty/Disc %/ Subtotal: \n\n";
            //get data from transaction file for output string.
        outputText = outputText + textFromTransactionFile();
            //formatting and math for getting final output data.
        DecimalFormat df = new DecimalFormat("#.##");
        String finalTotal = df.format(transactionTotal);       
        taxAmount = transactionTotal * taxValue;
        orderTotal = taxAmount + transactionTotal;       
        String taxAmountStr = df.format(taxAmount);
        String orderTotalStr = df.format(orderTotal);
            //create rest of output for alert message.
        outputText = outputText + "\n\n";
        outputText = outputText + "Order subTotal:" + finalTotal + "\n\n";
        outputText = outputText + "Tax Rate: " + "6%" + "\n\n";
        outputText = outputText + "Tax Amount: $" + taxAmountStr + "\n\n";
        outputText = outputText + "OrderTotal: $" + orderTotalStr + "\n\n";
        outputText = outputText + "ye olde bookstore " + "\n\n";
        alert.setContentText(outputText);
        alert.setResizable(true);
        windowSize = 600 + (i * 50);
        alert.getDialogPane().setPrefSize(800, windowSize);
        alert.showAndWait();
        newOrder();
    }

    @FXML
    private void newOrderButton(MouseEvent event) throws FileNotFoundException, IOException {
        newOrder();
    }
    
        //opening inventory.txt to get the book data
    public void getBooksList() throws FileNotFoundException, IOException{          
        BufferedReader br = new BufferedReader(new FileReader("src\\project1cbadolato_bookshop\\inventory.txt")); 
            //gathering data from our inventory file            
        String title, ID, cost, string;       
        int i = 0;
        while((string = br.readLine()) != null){
                //delimit the string, grab, ID, title and cost of each book.
            String[] individualData = string.split(",");
            ID = individualData[0];
            title = individualData[1];
            cost = individualData[2];
                //store the data on indvidual arrays
            bookID[i] = Integer.parseInt(ID);
            bookTitle[i] = title;
            bookCost[i] = Float.parseFloat(cost);                                      
            i++;
        }               
    }
        //writes to transaction file when needed.
    public void writeToTransactionFile() throws FileNotFoundException, UnsupportedEncodingException, IOException{
            //get local date and time and set up formatters for printing.
        LocalDateTime dateAndTime = LocalDateTime.now();                   
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyHHmm");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM/d/yy");
        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("hh:mm:ss a");      
        String dateString = formatter.format(dateAndTime);
        String date = formatter2.format(dateAndTime);
        String time = formatter3.format(dateAndTime);
        DecimalFormat df = new DecimalFormat("#.##");
        String formatCost = df.format(bookCost[currentBookArrayLoction]);
        String formatTotal = df.format(singleBookTotalCost);
            //append to the new line of our output file.             
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\project1cbadolato_bookshop\\transactions.txt", true))) {
             writer.append(dateString + ", " + bookID[currentBookArrayLoction] + ", " + bookTitle[currentBookArrayLoction] + ", " + formatCost
                     + ", " + singleBookQuantity + ", " + discountFraction + ", " + formatTotal + ", " + date + ", " + time + " EST\n");        
        }       
    }
        //get discount percentage based on number of book.
    int discount(int quantity){
        if(quantity >= 20){
            return 20;
        }
        if (quantity >= 10 & quantity < 20 ){
            return 15;
        }
        if(quantity >= 5 & quantity < 10){
            return 10;
        }
        else{
            return 0;
        }
    }
    
    public String[] readTransactionFile() throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader("src\\project1cbadolato_bookshop\\transactions.txt")); 
            //gathering data from our inventory file                 
        String tempString;
        String[] dataFromFile = new String[300];       
        int i = 0;
        while((tempString = br.readLine()) != null){
                //grab each line from the file, turn them into their own strings for parsing later.
            dataFromFile[i] = tempString;   
            i++;
        }                    
        return dataFromFile;
    }
    
        //grab each string off the file, delimit, then add to our output string
        //that will be displayed in a alert message
    public String textFromTransactionFile() throws IOException{       
        String[] dataFromTransactionFile;
        dataFromTransactionFile = readTransactionFile();
        String ID, title, cost, quantity, discount, total, outputText = "";
        float floatCost;
        int i = 0, intDiscount, booksOnFile = 0, startOfCurrentOrder;
        transactionTotal = 0;
            //because we only want to view the current transactions, we need to start the search through
            //the file at the current position within the transaction file.
            //so if we currently add 3 books, we will search the file for the total number of books
            //subtract the item number we are currently viewing from the total number of books, this will
            //give us our current position in the transaction file for viewing!    
        while(dataFromTransactionFile[booksOnFile] != null) {   
            booksOnFile++;
        }
        startOfCurrentOrder = (booksOnFile - (itemNumber - 1));          
        while(dataFromTransactionFile[startOfCurrentOrder] != null) {   
            String string = dataFromTransactionFile[startOfCurrentOrder];
            String[] individualData  = string.split(",");           
            ID = individualData[1];
            title = individualData[2];
            cost = individualData[3];
            quantity = individualData[4];
            discount = individualData[5];
            total = individualData[6];    
            transactionTotal = transactionTotal + Float.parseFloat(total);
            floatCost = Float.parseFloat(cost);           
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            total = formatter.format(transactionTotal);
            cost = formatter.format(floatCost);
            intDiscount = (int)(Float.parseFloat(discount) * 100.0);           
            outputText = outputText + (i + 1) + "." + " " + ID + " " + title + " " +
                    cost + " " + quantity + " " + intDiscount  + "% " + total + "\n";           
            startOfCurrentOrder++;
            i++;
        }         
        return outputText;
    }
    
    public void newOrder(){
        itemNumber = 1;
        subtotal = 0;
            //reconfigure buttons
        finishOrderButton.setDisable(true);
        finishOrderButton.setOpacity(1);
        processItemButton.setDisable(false);
        processItemButton.setOpacity(1); 
        processItemButton.setText("Process Item #" + itemNumber);    
        confirmItemButton.setDisable(true);
        confirmItemButton.setOpacity(.5); 
        confirmItemButton.setText("Confirm Item #" + itemNumber);
        viewOrderButton.setDisable(false);
        viewOrderButton.setOpacity(1);               
        finishOrderButton.setDisable(true);
        finishOrderButton.setOpacity(.5);
            //reconfigure Text Fields
        bookIDField.setEditable(true);
        itemQuantityField.setEditable(true); 
        itemInOrderField.setEditable(true);      
        itemInOrderField.setText("");
        bookIDField.setText("");
        itemQuantityField.setText("");  
        itemInfoField.setText("");
        orderSubtotalField.setText("");
            //reset Labels
        bookIDText.setText("Enter Book ID for item #" + (itemNumber) + ":");
        quantityText.setText("End quantity for item #" + (itemNumber) + ":");
        itemInfoText.setText("Item #" + (itemNumber) + " info:");
        orderSubtotalText.setText("Order subtotal for " + (itemNumber - 1) + " item(s):");  
            //get book lists.
        try {
            getBooksList();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        firstOrder = true;    
    }
}
