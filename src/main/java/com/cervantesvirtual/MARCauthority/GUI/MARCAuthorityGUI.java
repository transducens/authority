/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cervantesvirtual.MARCauthority.GUI;

import com.cervantesvirtual.MARCauthority.AuthorityCollection;
import com.cervantesvirtual.MARCauthority.AuthorityField;
import com.cervantesvirtual.MARCauthority.MARCAuthorityBuilder;
import com.cervantesvirtual.io.Backup;
import com.cervantesvirtual.metadata.Collection;
import com.cervantesvirtual.metadata.Field;
import com.cervantesvirtual.metadata.MARCDataField;
import com.cervantesvirtual.metadata.MetadataFormat;
import com.cervantesvirtual.metadata.Record;
import com.cervantesvirtual.xml.DocumentParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Impact
 */
public class MARCAuthorityGUI extends Application
{

    private final File authoOut = null;
    private final File authoIn = null;
    private List<File> listFiles = null;
    private ListIterator<File> fileIterator = null;
    private List<Record> listRecords = null;
    private ListIterator<Record> recordIterator = null;
    private List<Field> listFields = null;
    private ListIterator<Field> fieldIterator = null;
    private AuthorityCollection authority = null;
    private MARCAuthorityBuilder builder = null;
    private AuthorityField candidateField = null;
    
    GUIPrincipalController principalController;
    AuthorsViewController authorsViewController;

    private Stage rootStage;

    public Stage getRootStage()
    {
        return rootStage;
    }
    
    private Scene cargarPrincipal()
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/fxml/GUIPrincipal.fxml"));
        
        Scene scene = null;
        try
        {
            scene = new Scene((Parent) loader.load());            
        } catch (IOException ex)
        {
            System.err.println("No se encuentra el fxml indicado");
        }
        
        principalController = loader.getController();
        principalController.setMainApp(this);
        
        return scene;
        
    }
    
    private Node cargarAuthorsView()
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/fxml/AuthorsView.fxml"));
        
        Node node = null;
        try
        {
            node = (Node) loader.load();            
        } catch (IOException ex)
        {
            System.err.println("No se encuentra el fxml indicado");
        }
        
        authorsViewController = loader.getController();
        authorsViewController.setMainApp(this);
        
        return node;
        
    }
    
    @Override
    public void start(Stage primaryStage)
    {        
        rootStage = primaryStage;                        
        rootStage.setTitle("MARC AUTHORITY GUI");
        
        Scene scene = cargarPrincipal();
        if(scene!=null)
        {
            rootStage.setScene(scene);        
            rootStage.show();
        }                                        
        
        //show authors view
        Node authorView = cargarAuthorsView();
        
        principalController.setCenterPane(authorView);
        
        //por defecto
        setAuthoDirIn(new File("C:\\investigacion\\authority exp\\data"));
    }

    
    
    public void sigFile(File fil)
    {                    
        if (!fil.isDirectory())
        {            
            Collection bibliographic = new Collection(
                    MetadataFormat.MARC, DocumentParser.parse(fil));
            builder.addBibliographicCollection(bibliographic);
        }                
    }
    
    public void sigFile()
    {                    
        if (fileIterator != null && fileIterator.hasNext())
        {            
            Collection bibliographic = new Collection(
                    MetadataFormat.MARC, DocumentParser.parse(fileIterator.next()));            
            
            listRecords = bibliographic.getRecords();
            recordIterator = listRecords.listIterator();
            
            sigRecord();                                   
        }
        else
        {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("No existen más registros en la carpeta seleccionada");

            alert.showAndWait();
        }
    }
    
    public void sigRecord()
    {
        if(recordIterator != null && recordIterator.hasNext())
        {
            listFields = recordIterator.next().getFieldsMatching("[17][01][01]");
            fieldIterator = listFields.listIterator();
            
            sigField();
        }
        else
        {
            sigFile();
        }
    }
    
    public void sigField()
    {
        if(fieldIterator!=null && fieldIterator.hasNext())
        {
            Field field = fieldIterator.next();
            String tag = "1" + field.getTag().substring(1);
            MARCDataField marcField = new MARCDataField(tag, field.getValue());
            candidateField = new AuthorityField(tag, marcField);                                  
           
        }
        else
        {
            sigRecord();
        }
            
        /*
        String tag = "1" + field.getTag().substring(1);
        AuthorityField afield = new AuthorityField(tag, field.getValue());
        if (!acollection.contains(afield)) //Habria que mirar en la lista?
        {
            try {
                addAuthorityField(afield);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return;
            }
        }
        */
    }

    public void setAuthoDirIn(File dir)
    {
        if(dir.isDirectory())
        {           
            listFiles =  new ArrayList<>(Arrays.asList(dir.listFiles(new FilenameFilter()
            {
                @Override
                public boolean accept(File file, String name)
                {
                    return name.toLowerCase().endsWith(".xml");
                }
            })));
            
            fileIterator = listFiles.listIterator();
            /*
            for (File file : dir.listFiles()) 
            {
                if (file.getName().endsWith(".xml")) {
                    Collection bibliographic = new Collection(
                            MetadataFormat.MARC,
                            DocumentParser.parse(file));
                    builder.addBibliographicCollection(bibliographic);
                }
            }
            */
        }
    }
    
    public void saveAuthority()
    {
        if (authority != null && authoOut != null)
        {
            authority.writeXML(authoOut);
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
