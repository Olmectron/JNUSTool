/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import com.olmectron.material.MaterialDesign;
import com.olmectron.material.components.MaterialDropdownMenu;
import com.olmectron.material.components.MaterialDropdownMenuItem;
import com.olmectron.material.components.MaterialIconButton;
import com.olmectron.material.components.MaterialTable;
import com.olmectron.material.components.MaterialTableColumn;
import com.olmectron.material.components.MaterialTextField;
import com.olmectron.material.components.MaterialToast;
import com.olmectron.material.components.MaterialTooltip;
import com.olmectron.material.constants.MaterialColor;
import com.olmectron.material.files.ExportFile;
import com.olmectron.material.layouts.MaterialEditableLayout;
import de.mas.jnustool.NUSTitle;
import de.mas.jnustool.Starter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.utilities.StringTrimmer;

/**
 *
 * @author Édgar
 */
public class Start extends Application {
    private DownloadPane downPane;
    private void setDragAndDropFunction(Scene scene){
        scene.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
               
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });
        
        // Dropping over surface
        scene.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    //if(db.getFiles().size()>1){
                    //new MaterialToast(R.string.drop_only_one.get(),MaterialToast.LENGTH_SHORT).unhide();
                    //}
                    //else{
                    List<File> archivos=db.getFiles();
                    if(archivos!=null){
                        if(archivos.size()==1){
                            if(archivos.get(0).getName().toLowerCase().endsWith(".tik")){
                            TIKFile archivo=new  TIKFile(archivos.get(0));
                        if(archivo.getTitleID()!=null){
                            downPane.addFromTitleID(archivo.getTitleID(),archivo);
                        }
                            }
                else{
                                new MaterialToast("The dropped file isn't a .tik file").unhide();
                            }
                        }
                        else if(archivos.size()>1){
                            new MaterialToast("Just drag & drop one file at a time").unhide();
                        }
                    }
                    
                    
                   
                    //}
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }
    private static HostServices host;
    public static void showFile(String file){
        host.showDocument(new File(file).toURI().toString());
    }
    @Override
    public void start(Stage primaryStage) {
        host=getHostServices();
        MaterialDesign.initSystemProperties();
        try {
            ExportFile.exportFile("/res/fulltitles.csv", "fulltitles.csv");
            Starter.readConfig(primaryStage);
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        StackPane pane=new StackPane();
        Scene scene=MaterialDesign.getMaterialScene(primaryStage, pane, 850, 480);
        setDragAndDropFunction(scene);
        MaterialEditableLayout layout=new MaterialEditableLayout(true) {
            @Override
            public void onMenuButtonPressed(Button button) {
                
                 //To change body of generated methods, choose Tools | Templates.
            }
        };
        downPane=new DownloadPane(layout);
        TitlePane titlePane=new TitlePane(downPane,layout);
        SettingsPane settingsPane=new SettingsPane(layout);
        //layout.showModule("Title List", titlePane);
        layout.setShowMiniButton(false);
        layout.setMiniDrawer(true);
        layout.addDrawerItem("Download queue", "/res/download.png", downPane);
        layout.addDrawerItem("Title list", "/res/list.png", titlePane);
        
        layout.addDrawerItem("Settings", "/res/settings.png", settingsPane);
        
        layout.setTabTooltip("Title list","Title list");
        layout.setTabTooltip("Download queue","Download queue");
        
        layout.setTabTooltip("Settings","Settings");
        
        layout.showTab("Download queue");
        MaterialIconButton searchNameButton=new MaterialIconButton(MaterialIconButton.SEARCH_ICON);
        searchNameButton.setOnClick(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                getSearchMenu(searchNameButton).unhide();
//To change body of generated methods, choose Tools | Templates.
            }
        });
        MaterialTooltip searchNameTooltip=new MaterialTooltip(searchNameButton);
        searchNameTooltip.setText("Search by name");
        Global.settings.nameFilterProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue!=null)
                searchNameTooltip.setText("Found "+titlePane.getTitleCount()+" titles "+"matching \n"+newValue+"");
                if(newValue.isEmpty())
                searchNameTooltip.setText("Search by name");
                    
                //To change body of generated methods, choose Tools | Templates.
            }
        });
        MaterialIconButton filterRegionButton=new MaterialIconButton();
        filterRegionButton.setIcon("/res/filter.png");
        MaterialTooltip tooltipFilter=new MaterialTooltip(filterRegionButton);
        filterRegionButton.setOnClick(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                getFilterRegionMenu(filterRegionButton).unhide();
//To change body of generated methods, choose Tools | Templates.
            }
        });
        titlePane.titleCountProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                tooltipFilter.setText("Filtered by region: "+newValue.longValue());
                //To change body of generated methods, choose Tools | Templates.
            }
        });
        tooltipFilter.setText("Filtered by region: "+titlePane.getTitleCount());
        
        layout.addToolbarActionButton(filterRegionButton,0);
        layout.addToolbarActionButton(searchNameButton,0);
        
         MaterialIconButton clearDownloadListButton=new MaterialIconButton();
        clearDownloadListButton.setIcon("/res/clear.png");
        MaterialTooltip tooltipList=new MaterialTooltip(clearDownloadListButton);
        clearDownloadListButton.setOnClick(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                downPane.clearList();
//To change body of generated methods, choose Tools | Templates.
            }
        });
        tooltipList.setText("Clear download list");
        
        layout.addToolbarActionButton(clearDownloadListButton,1);
        
        //layout.setTitle("Title List");
        layout.setWindowTitle("JNUSTool GUI mod v0.6.5");
        MaterialDesign.setPrimaryColorCode(MaterialColor.material.TEAL);
        
        pane.getChildren().add(layout);
        primaryStage.show();
    }
    private MaterialDropdownMenu filterRegionMenu;
    private MaterialDropdownMenu getSearchMenu(Region origen){
        MaterialDropdownMenu menu=new MaterialDropdownMenu(origen);
        MaterialTextField searchField=new  MaterialTextField("Search");
        searchField.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                menu.hideMenu();
                //To change body of generated methods, choose Tools | Templates.
            }
        });
        searchField.setText(Global.settings.getNameFilter());
        searchField.allowSpace();
        searchField.allowDiagonal();
        searchField.allowDot();
        searchField.allowBottomLine();
        searchField.allowPercentage();
        searchField.textField().textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
               
                if(newValue!=null){
                //System.out.println(newValue);
                    Global.settings.setNameFilter(StringTrimmer.trim(newValue));     
                }
               
//To change body of generated methods, choose Tools | Templates.
            }
        });
        searchField.setPadding(new Insets(8,16,8,16));
        menu.addNodeAsItem(searchField);
        return menu;
    }
    
    private MaterialDropdownMenu getFilterRegionMenu(Region origen){
        int indice=-1;
            if(filterRegionMenu!=null){
                for(int i=0;i<filterRegionMenu.size();i++){
                    if(filterRegionMenu.getItemAt(i).isSelected()){
                        
                        indice=i;
                        break;
                    }
                }
            }
            filterRegionMenu=new MaterialDropdownMenu(origen);   
            
            filterRegionMenu.addItem(new MaterialDropdownMenuItem("American"){

                    @Override
                    public void onItemClick() {
                        Global.settings.setRegionFilter("USA"); //To change body of generated methods, choose Tools | Templates.
                    }
                
                });
                filterRegionMenu.addItem(new MaterialDropdownMenuItem("European"){

                    @Override
                    public void onItemClick() {
                         Global.settings.setRegionFilter("EUR"); //To change body of generated methods, choose Tools | Templates.
                    }
                
                });
                filterRegionMenu.addItem(new MaterialDropdownMenuItem("Japanese"){

                    @Override
                    public void onItemClick() {
                         Global.settings.setRegionFilter("JAP");//To change body of generated methods, choose Tools | Templates.
                    }
                
                });
                filterRegionMenu.addItem(new MaterialDropdownMenuItem("Region Free"){

                    @Override
                    public void onItemClick() {
                         Global.settings.setRegionFilter("UKWN"); //To change body of generated methods, choose Tools | Templates.
                    }
                
                });
                filterRegionMenu.addItem(new MaterialDropdownMenuItem("All"){

                    @Override
                    public void onItemClick() {
                         Global.settings.setRegionFilter(null); //To change body of generated methods, choose Tools | Templates.
                    }
                
                });
        if(indice>-1){
            filterRegionMenu.getItemAt(indice).setSelected(true);
        }
        else{
            filterRegionMenu.getItemAt(filterRegionMenu.size()-1).setSelected(true);
        }
        
        return filterRegionMenu;
    }
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
