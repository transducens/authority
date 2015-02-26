package com.cervantesvirtual.MARCauthority.GUI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cervantesvirtual.MARCauthority.AuthorityField;
import com.cervantesvirtual.MARCauthority.AuthorityRecord;
import com.cervantesvirtual.MARCauthority.AuthorityType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author Impact
 */
public class AuthorsViewController
{
    @FXML
    Label establishedId;
    @FXML
    Label establishedName;
    @FXML
    Label establishedTitle;
    @FXML
    Label establishedPeriod;
    @FXML
    Label candidateId;
    @FXML
    Label candidateName;
    @FXML
    Label candidateTitle;
    @FXML
    Label candidatePeriod;
    @FXML
    ToggleGroup candidate;
    @FXML
    ToggleButton buttonError;
    @FXML
    ToggleButton buttonEstablished;
    @FXML
    ToggleButton buttonVariant;
    @FXML
    ToggleButton buttonNew;
    @FXML
    ToggleGroup typeEstablished;
    @FXML
    ToggleButton buttonEstabVariant;
    @FXML
    ToggleButton buttonEstabError;
    @FXML
    HBox establishedBox;
    @FXML
    Label originalCandidateText;
    @FXML
    Label originalEstablishedText;
    @FXML
    TitledPane candidateExpandPane;
    @FXML
    TitledPane establishedExpandPane;
    
    private MARCAuthorityGUI mainApp = null;

    public void setCandidateContent(AuthorityField candidate)
    {
        candidateId.setText(candidate.getId());
        candidateName.setText(candidate.getName());
        candidateTitle.setText(candidate.getTitle());
        candidatePeriod.setText(candidate.getOriginalDate());
        
        originalCandidateText.setText(candidate.getValue());
    }
    
     public void setEstablishedContent(AuthorityRecord established)
    {                
        AuthorityField autorized = established.authorized();
        
        establishedId.setText(autorized.getId());
        establishedName.setText(autorized.getName());
        establishedTitle.setText(autorized.getTitle());
        establishedPeriod.setText(autorized.getOriginalDate());
        
        originalEstablishedText.setText(autorized.getValue());
        
    }

    public void setMainApp(MARCAuthorityGUI main)
    {
        mainApp = main;
    }
    
    @FXML
    void initialize()
    {
        candidateId.setText("");
        candidateName.setText("");
        candidateTitle.setText("");
        candidatePeriod.setText("");
        
        establishedId.setText("");
        establishedName.setText("");
        establishedTitle.setText("");
        establishedPeriod.setText("");
        
        originalCandidateText.setText("");
        originalEstablishedText.setText("");
        
        buttonEstablished.setUserData(AuthorityType.ESTABLISHED);
        buttonVariant.setUserData(AuthorityType.VARIANT);
        buttonError.setUserData(AuthorityType.ERROR);  
        buttonNew.setUserData("New");
        
        buttonEstabVariant.setUserData(AuthorityType.VARIANT);
        buttonEstabError.setUserData(AuthorityType.ERROR);
        
        establishedBox.setVisible(false);
        
        candidate.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {

            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue)
            {
                if(newValue != null)
                {
                    Object type = newValue.getUserData();
                    
                    if(type instanceof AuthorityType && ((AuthorityType)type) == AuthorityType.ESTABLISHED)
                    {
                        establishedBox.setVisible(true);
                    }
                    else
                    {
                        establishedBox.setVisible(false);
                    }
                }
            }
        });
        
    }
    
    public Object getSelectedToggle()
    {
        if (candidate.getSelectedToggle() != null)
        {
            return candidate.getSelectedToggle().getUserData();
        }
        else
        {
            return null;
        }
    }

    public AuthorityType getEstablishedToggle()
    {
        if(typeEstablished.getSelectedToggle() != null)
        {
            return (AuthorityType) typeEstablished.getSelectedToggle().getUserData();
        }
        else
        {
            return AuthorityType.VARIANT;
        }
    }

    public void setToggleDefault()
    {
        candidate.selectToggle(buttonVariant);
        typeEstablished.selectToggle(buttonEstabVariant);
        candidateExpandPane.setExpanded(false);
        establishedExpandPane.setExpanded(false);
    }

}
