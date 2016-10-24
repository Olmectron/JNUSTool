/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import com.olmectron.material.components.MaterialTextField;
import com.olmectron.material.components.MaterialTransparentPane;
import com.olmectron.material.files.FieldsFile;
import com.olmectron.material.layouts.Material;
import com.olmectron.material.layouts.MaterialEditableLayout;
import de.mas.jnustool.util.Util;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 *
 * @author Édgar
 */
public class SettingsPane extends Material {
    private MaterialTextField commonKey;
    private MaterialEditableLayout layout;
    public SettingsPane(MaterialEditableLayout layout){
        super();
        this.layout=layout;
        try {
            setMaterialPadding(new Insets(16));
            VBox box=new VBox();
            FieldsFile file=new FieldsFile("settings");
            MaterialTextField maxField=new MaterialTextField(file.getValue("max_simultaneous","1"),"Simultaneous downloads");
            maxField.lockLetters();
            MaterialTextField serverField=new MaterialTextField(file.getValue("server"),"Server");
            serverField.setPadding(new Insets(0,0,16,0));
            serverField.setLimite(255);
            serverField.textField().textProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    file.setValue("server", newValue);
                    
                    //To change body of generated methods, choose Tools | Templates.
                }
            });
            commonKey=new MaterialTextField(file.getValue("common_key"),"Common Key");
            commonKey.setPadding(new Insets(0,0,16,0));
            commonKey.setLimite(32);
            commonKey.textField().textProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    file.setValue("common_key", newValue);
                    try{
                        if(newValue.length()==32){
                                            Util.commonKey =  Util.hexStringToByteArray(newValue);

                        }
                        else{
                            Util.commonKey=null;
                        }
                    }
                    catch(StringIndexOutOfBoundsException ex){
                        
                    }
                    //To change body of generated methods, choose Tools | Templates.
                }
            });
            maxField.textField().textProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if(!newValue.trim().equals("")){
                        Global.settings.setMaxDownloads(Integer.parseInt(newValue));
                    }
                    else{
                        
                        Global.settings.setMaxDownloads(1);
                      
                    }
                    //To change body of generated methods, choose Tools | Templates.
                }
            });
            maxField.setLimite(3);
            maxField.textField().setAlignment(Pos.CENTER);
            maxField.setMaxWidth(200);
            box.getChildren().addAll(serverField,commonKey,maxField);
            setRootComponent(box);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SettingsPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void onShown() {
        layout.switchToolbarActions(2);
        commonKey.requestFocus();
         //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
         //To change body of generated methods, choose Tools | Templates.
    }
    
}
