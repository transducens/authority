/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cervantesvirtual.MARCauthority.GUI;


import com.cervantesvirtual.metadata.Collection;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;

/**
 * FXML Controller class
 *
 * @author Impact
 */
public class GUIPrincipalController
{
    
    @FXML
    BorderPane panelPrincipal;
    
    private MARCAuthorityGUI mainApp = null;
    
    
    public void setMainApp(MARCAuthorityGUI main)
    {
        mainApp = main;
    }
    
    public void setCenterPane(Node content)
    {
        panelPrincipal.setCenter(content);
    }
    
    @FXML
    private void setAuthDir()
    {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory = directoryChooser.showDialog(mainApp.getRootStage());
        
        if (selectedDirectory != null)
        {
            mainApp.setAuthoDirIn(selectedDirectory);
        }            
    }
    
    @FXML
    private void nextField()
    {
        mainApp.sigField();
    }
    
    @FXML
    private void buttonSave()
    {
        mainApp.saveAuthority();
    }
    
    
}
