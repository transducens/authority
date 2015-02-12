/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cervantesvirtual.MARCauthority.GUI;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Impact
 */
public class MARCAuthorityGUI extends Application
{
    
    
    
    @Override
    public void start(Stage primaryStage)
    {
        FXMLLoader loader = new FXMLLoader();                
        
        loader.setLocation(this.getClass().getResource("/fxml/GUIPrincipal.fxml"));
        
        Scene scene;        
        try
        {
            scene = new Scene((Parent) loader.load());
            primaryStage.setTitle("MARC AUTHORITY GUI");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (IOException ex)
        {
            System.err.println("No se encuentra el fxml indicado");
        }
        
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
    
}
