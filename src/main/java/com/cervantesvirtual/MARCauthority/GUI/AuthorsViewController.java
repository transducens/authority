package com.cervantesvirtual.MARCauthority.GUI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cervantesvirtual.MARCauthority.AuthorityField;
import com.cervantesvirtual.MARCauthority.AuthorityRecord;
import com.cervantesvirtual.MARCauthority.AuthorityType;
import java.io.IOException;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Impact
 */
public class AuthorsViewController
{
    @FXML
    Label candidateRol;
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
    TitledPane candidateExpandPane;
    @FXML
    ListView establishedAuthorsList;
    
    private MARCAuthorityGUI mainApp = null;

    public void setCandidateContent(AuthorityField candidate)
    {
        candidateRol.setText(candidate.getRol());
        candidateName.setText(candidate.getName());
        candidateTitle.setText(candidate.getTitle());
        candidatePeriod.setText(candidate.getOriginalDate());
        
        originalCandidateText.setText(candidate.getValue());
    }
    
    public void setAuthorListContent(List<AuthorityRecord> authors)
    {
        ObservableList<AuthorityRecord> myObservableList = FXCollections.observableList(authors);
        establishedAuthorsList.setItems(myObservableList);
         
        establishedAuthorsList.setCellFactory(new Callback<ListView<AuthorityRecord>, ListCell<AuthorityRecord>>(){
 
            @Override
            public ListCell<AuthorityRecord> call(ListView<AuthorityRecord> p) {
                 
                ListCell<AuthorityRecord> cell = new ListCell<AuthorityRecord>(){
 
                    @Override
                    protected void updateItem(AuthorityRecord t, boolean bln) 
                    {
                        super.updateItem(t, bln);
                        if (t != null) 
                        {
                            AuthorCellController cell = loadCell().getController();
                            cell.setEstablishedContent(t);
                            setGraphic(cell.getVBox());
                        }
                    }
 
                };
                 
                return cell;
            }
        });
    }
    
    public AuthorityRecord getSelectedEstablished()
    {
        if (establishedAuthorsList.getSelectionModel().isEmpty())
        {
            return (AuthorityRecord) establishedAuthorsList.getItems().get(0);
        }
        else
        {
            return (AuthorityRecord) establishedAuthorsList.getSelectionModel().getSelectedItem();
        }
        
    }
    
    public FXMLLoader loadCell()
    {
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
        
        return loader;
    }

    public void setMainApp(MARCAuthorityGUI main)
    {
        mainApp = main;
    }
    
    @FXML
    void initialize()
    {
        candidateRol.setText("");
        candidateName.setText("");
        candidateTitle.setText("");
        candidatePeriod.setText("");
        
        originalCandidateText.setText("");
        
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
    }

}
