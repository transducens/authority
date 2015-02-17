package com.cervantesvirtual.MARCauthority.GUI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Impact
 */
public class AuthorsViewController
{
    @FXML
    Label tablaCandidate;
    
    private MARCAuthorityGUI mainApp = null;
    
    
    public void setCandidateContent(String content)
    {
        tablaCandidate.setText(content);
    }
            
    public void setMainApp(MARCAuthorityGUI main)
    {
        mainApp = main;
    }
    
}
