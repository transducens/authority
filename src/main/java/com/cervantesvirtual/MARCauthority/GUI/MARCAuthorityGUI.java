/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cervantesvirtual.MARCauthority.GUI;

import com.cervantesvirtual.MARCauthority.AuthorityCollection;
import com.cervantesvirtual.MARCauthority.AuthorityField;
import com.cervantesvirtual.MARCauthority.AuthorityRecord;
import com.cervantesvirtual.MARCauthority.AuthorityType;
import com.cervantesvirtual.MARCauthority.MARCAuthorityBuilder;
import com.cervantesvirtual.metadata.Collection;
import com.cervantesvirtual.metadata.Field;
import com.cervantesvirtual.metadata.MARCDataField;
import com.cervantesvirtual.metadata.MetadataFormat;
import com.cervantesvirtual.metadata.Record;
import com.cervantesvirtual.xml.DocumentParser;
import com.sun.deploy.uitoolkit.impl.fx.ui.FXMessageDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Impact
 */
public class MARCAuthorityGUI extends Application
{

    private File authoOut = null;
    private File authoIn = null;
    private List<File> listFiles = null;
    private ListIterator<File> fileIterator = null;
    private List<Record> listRecords = null;
    private ListIterator<Record> recordIterator = null;
    private List<Field> listFields = null;
    private ListIterator<Field> fieldIterator = null;
    private AuthorityCollection authority = null;
    private MARCAuthorityBuilder builder = null;
    private AuthorityField candidateField = null;
    private AuthorityRecord authorRecord = null;

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

    public void setStageTitle(String title)
    {
        rootStage.setTitle(title +" :MARC AUTHORITY GUI");
    }
    
    @Override
    public void start(Stage primaryStage)
    {
        rootStage = primaryStage;
        setStageTitle("Untitled");

        Scene scene = cargarPrincipal();
        if (scene != null)
        {
            rootStage.setScene(scene);
            rootStage.show();
        }

        //show authors view
        Node authorView = cargarAuthorsView();

        principalController.setCenterPane(authorView);
        
        rootStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Exit Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Do you want to save the changes before exit?");

                ButtonType buttonTypeYes = new ButtonType("Yes");
                ButtonType buttonTypeNo = new ButtonType("No");                
                ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonTypeYes)
                {
                    if(authoOut!=null)
                    {
                        saveAuthority();
                    }
                    else
                    {
                        principalController.buttonSaveAs();
                    }
                    
                } 
                else if (result.get() == buttonTypeNo) 
                {
                    //exit withoutsave
                }  
                else 
                {
                    //cancel exit
                    event.consume();
                }
                
            }
        });

        //Inicializar
        builder = new MARCAuthorityBuilder();
        
        setToggleDefault();

        //por defecto
        //setAuthoDirIn(new File("C:\\investigacion\\authority exp\\data"));
        //authoOut = new File("C:\\investigacion\\authority exp\\autho.xml");
        
        authoOut = null;
        principalController.setDisableMenuSaveButton(true);
        principalController.setInfoText("");
        
        //setAuthoDirIn(new File("/home/aureo/experimentosAuth/data/"));
        //authoOut = new File("/home/aureo/experimentosAuth/autho.xml");

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
        } else
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
        if (recordIterator != null && recordIterator.hasNext())
        {
            listFields = recordIterator.next().getFieldsMatching("[17][01][01]");
            fieldIterator = listFields.listIterator();

            sigField();
        } else
        {
            sigFile();
        }
    }

    public void sigField()
    {
        //insertar la ocurrencia anterior
        if(authorRecord!=null)
        {
            //insertar como variante o error, o saltar si no seleccionado             
            Object type = authorsViewController.getSelectedToggle();
            if(type != null)
            {
                if(type instanceof AuthorityType)
                {
                    AuthorityType atype = (AuthorityType) type;
                    if(atype == AuthorityType.ESTABLISHED)
                    {
                        AuthorityType authoType = authorsViewController.getEstablishedToggle();                                               
                        
                        builder.addFieldToRecord(candidateField, atype, authoType, authorRecord);
                        authorRecord = null;
                        
                    }
                    else
                    {                        
                        builder.addFieldToRecord(candidateField, atype, authorRecord);
                        authorRecord = null;
                    }
                }
                else if( ((String)type).equals("New") )
                {
                    builder.addAuthorityRecord(candidateField, "2", "");
                    authorRecord = null;
                }
            }
        }
        //informar de los registros introducidos
        int numRecords = builder.toAuthorityCollection().getAuthorityRecords().size();
        
        principalController.setInfoText(numRecords + ": Authority Records in the collection");
        
        if (fieldIterator != null && fieldIterator.hasNext())
        {
            Field field = fieldIterator.next();
            String tag = "1" + field.getTag().substring(1);
            MARCDataField marcField = new MARCDataField(tag, field.getValue());
            candidateField = new AuthorityField(tag, marcField);
            
            if(!builder.collectionContains(candidateField))
            {
                try
                {
                    authorRecord = builder.selectPrincipal(candidateField);
                } catch (IOException ex)
                {
                    System.out.println("Error al seleccionar principal "+ex.toString());
                }
                if(authorRecord != null)
                {
                    authorsViewController.setCandidateContent(candidateField);
                    authorsViewController.setEstablishedContent(authorRecord);
                }
                else
                {
                    builder.addAuthorityRecord(candidateField);
                    sigField();
                }
            }
            else
            {
                sigField();
            }                                
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
        if (dir.isDirectory())
        {
            listFiles = new ArrayList<>(Arrays.asList(dir.listFiles(new FilenameFilter()
            {
                @Override
                public boolean accept(File file, String name)
                {
                    return name.toLowerCase().endsWith(".xml");
                }
            })));

            fileIterator = listFiles.listIterator();
            
        }
    }

    public void saveAuthority()
    {
        if (builder != null)
        {
            authority = builder.toAuthorityCollection();

            if (authority != null && authoOut != null)
            {
                authority.writeXML(authoOut);
            }
        }
    }
    
    public void openAuth(File file)
    {
        if (file.isFile())
        {
            authority = null;
            builder = null;
            
            try
            {
                builder = new MARCAuthorityBuilder(file.getAbsolutePath());
                
            } catch (Exception ex)
            {
                System.err.println("Error al abrir fichero "+ex.toString());
            }
            
            authoOut = file;
            principalController.setDisableMenuSaveButton(false);
            setStageTitle(authoOut.getName());
            
            //informar de los registros introducidos
            int numRecords = builder.toAuthorityCollection().getAuthorityRecords().size();

            principalController.setInfoText(numRecords + ": Authority Records in the collection");
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }

    public void setToggleDefault()
    {
        authorsViewController.setToggleDefault();
    }

    void saveAs(File selectedFile)
    {
        authoOut = selectedFile;
        setStageTitle(authoOut.getName());
        principalController.setDisableMenuSaveButton(false);
        
        saveAuthority();
    }

}
