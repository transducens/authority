/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cervantesvirtual.MARCauthority.GUI;

import com.cervantesvirtual.MARCauthority.AuthorityField;
import com.cervantesvirtual.MARCauthority.AuthorityRecord;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Aureo
 */
public class AuthorCellController
{
@FXML
Label establishedRol;
@FXML
Label establishedName;
@FXML
Label establishedTitle;
@FXML
Label establishedPeriod;
@FXML
Label originalEstablishedText;
@FXML
TitledPane establishedExpandPane;
@FXML
VBox authorCell;
    
AnchorPane cell;
    
    public AuthorCellController()
    {
        /*
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/fxml/AuthorCell.fxml"));
        
        try
        {
            loader.load();
            
        } catch (IOException ex)
        {            
            System.err.println("No se encuentra el fxml indicado");
            System.err.println(ex.toString());
        }
        */
    }
    
    /*
    public static void loadFXML()
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/fxml/AuthorCell.fxml"));
        
        try
        {
            cell = (AnchorPane) loader.load();
            
        } catch (IOException ex)
        {            
            System.err.println("No se encuentra el fxml indicado");
            System.err.println(ex.toString());
        }
    }
    */
    
    @FXML
    void initialize()
    {
        
    }
    
    public void setEstablishedContent(AuthorityRecord established)
    {                
        AuthorityField autorized = established.authorized();
        
        
        establishedName.setText(autorized.getName());
        establishedTitle.setText(autorized.getTitle());
        establishedPeriod.setText(autorized.getOriginalDate());
        establishedRol.setText(autorized.getRol());
        
        StringBuilder variants = new StringBuilder("");
        for (AuthorityField aut:established.getAuthorityFields())
        {
            if(aut!= established.authorized())
            {
                variants.append(aut.getFullName());
                variants.append(" ");
                variants.append(aut.getOriginalDate()==null?"":aut.getOriginalDate());
                variants.append(" ");
                variants.append(aut.getRol()==null?"":aut.getRol());
                variants.append("\n");
            }
        }
        
        originalEstablishedText.setText(variants.toString());
        
        
    }
    
    public VBox getVBox()
    {
        return authorCell;
    }
    
}
