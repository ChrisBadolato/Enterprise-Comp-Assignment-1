/*  
Name:  Christopher Badolato   
Course: CNT 4714 – Spring 2020   
Assignment title: Project 1 – Event-driven Enterprise Simulation  
Date: Sunday January 26, 2020 */ 

package project1cbadolato_bookshop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Project1CBadolato_bookShop extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {       
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));       
        Scene scene = new Scene(root);      
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {      
        launch(args);       
    }
    
}
