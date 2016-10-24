/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import com.olmectron.material.MaterialDesign;
import com.olmectron.material.components.MaterialConfirmDialog;
import com.olmectron.material.components.MaterialDisplayText;
import com.olmectron.material.components.MaterialDropdownMenu;
import com.olmectron.material.components.MaterialDropdownMenuItem;
import com.olmectron.material.components.MaterialFloatingButton;
import com.olmectron.material.components.MaterialIconButton;
import com.olmectron.material.components.MaterialTextField;
import com.olmectron.material.components.MaterialToast;
import com.olmectron.material.components.MaterialTransparentPane;
import com.olmectron.material.constants.MaterialColor;
import com.olmectron.material.layouts.MaterialEditableLayout;
import de.mas.jnustool.util.NUSTitleInformation;
import de.mas.jnustool.util.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

/**
 *
 * @author Édgar
 */
public class DownloadPane extends MaterialTransparentPane{
    private DownloadList list;
    private void showTIKChooser(){
          FileChooser f=new FileChooser();
                        f.getExtensionFilters().add(new FileChooser.ExtensionFilter("TIK files","*.tik"));
                        File archivo=f.showOpenDialog(MaterialDesign.primary);
                           if(archivo !=null){
                               TIKFile c=new TIKFile(archivo);
                               if(c.getTitleID()!=null){
                                   addFromTitleID(c.getTitleID(), c);
                               }
                           }
                       
    }
    private void showNewDialog(){
        MaterialTextField titleField=new MaterialTextField("Title ID");
                
                titleField.setLimite(16);
                MaterialTextField nameField=new MaterialTextField("Name");
                nameField.allowDot();
                nameField.allowSpace();
                nameField.setLimite(100);
                titleField.setPadding(new Insets(8));
                nameField.setPadding(new Insets(8));
                
                MaterialConfirmDialog dialog=new  MaterialConfirmDialog("Add a title id","Write the needed fields for adding a title id not in the title list:",
                "Add","Cancel"
                ) {

                    @Override
                    public void onPositiveButton() {
                            if(titleField.getTrimmedText().length()==16 && nameField.getTrimmedText().length()>=1){
                                NUSTitleInformation info=new NUSTitleInformation();
                                info.setTitleID(Util.StringToLong(titleField.getTrimmedText()));
                                info.setLongnameEN(nameField.getTrimmedText());
                                addTitleInformation(info);
                                this.dismiss();
                            }
                            else if(titleField.getTrimmedText().length()!=16){
                                titleField.setErrorText("Write a valid Title ID");
                                titleField.showError();
                                titleField.requestFocus();
                            }
                            else if(nameField.getTrimmedText().length()==0){
                                
                                nameField.setErrorText("Write a name");
                                nameField.showError();
                                nameField.requestFocus();
                            }
                        //To change body of generated methods, choose Tools | Templates.
                    }
                    @Override
                    public void onDialogShown() {
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void onDialogHidden() {
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void onDialogKeyReleased(KeyEvent event) {
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void onDialogKeyPressed(KeyEvent event) {
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };
                dialog.setCustomContent(new VBox(titleField,nameField));
                dialog.unhide();
    }
    private void showNameDialog(NUSTitleInformation info){
            if(info.getLongnameEN().replace("-", "").equals(info.getTitleIDString().replace("-",""))){
                MaterialTextField nameField=new MaterialTextField("Name");
                nameField.allowDot();
                nameField.allowSpace();
                nameField.allowMiddleLine();
                
                nameField.setLimite(100);
                nameField.setPadding(new Insets(8));
                MaterialConfirmDialog dialog=new  MaterialConfirmDialog("Set the name",
                        "Write the name for the title ID "+info.getTitleIDString()+":",
                "Add","Cancel"
                ) {

                    @Override
                    public void onPositiveButton() {
                            if(nameField.getTrimmedText().length()>=1){
                                //NUSTitleInformation info=new NUSTitleInformation();
                                
                                info.setLongnameEN(nameField.getTrimmedText());
                                addTitleInformation(info);
                                this.dismiss();
                            }
                            else if(nameField.getTrimmedText().length()==0){
                                nameField.setErrorText("Write a name");
                                nameField.showError();
                                nameField.requestFocus();
                            }
                        //To change body of generated methods, choose Tools | Templates.
                    }
                    @Override
                    public void onDialogShown() {
                        MaterialDesign.primary.requestFocus();
                        nameField.requestFocus();
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void onDialogHidden() {
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void onDialogKeyReleased(KeyEvent event) {
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void onDialogKeyPressed(KeyEvent event) {
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };
                
                nameField.setOnAction(new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent event) {
                        dialog.onPositiveButton(); //To change body of generated methods, choose Tools | Templates.
                    }
                });
                dialog.setCustomContent(new VBox(nameField));
                dialog.unhide();
            }
            else{
                addTitleInformation(info);
            }
    }
    private MaterialEditableLayout layout;
    public  DownloadPane(MaterialEditableLayout layout){
        super();
        
        this.getContentCard().setCardPadding(new Insets(0));
        list=new DownloadList();
        list.itemCountProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue.intValue()>0){
                    
        setRootComponent(list.getPerfectSizeFlowListPane());
                }
                else{
                    MaterialDisplayText dText=new MaterialDisplayText("Drag and drop a TIK file");
                    dText.setColorCode(MaterialColor.material.BLACK_54);
                    dText.setFontSize(30);
                    StackPane empty=new StackPane(dText);                   empty.setPrefHeight(1080);
                    
                    
                    setRootComponent(empty);
                }
//To change body of generated methods, choose Tools | Templates.
            }
        });
        this.layout=layout;
        MaterialFloatingButton addButton=new MaterialFloatingButton(MaterialIconButton.PLUS_ICON);
        addButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                MaterialDropdownMenu addOptionsMenu=new MaterialDropdownMenu(addButton);
                addOptionsMenu.addItem(new MaterialDropdownMenuItem("Import TIK file"){

                    @Override
                    public void onItemClick() {
                        addOptionsMenu.setOnHidden(new EventHandler<WindowEvent>(){
                            @Override
                            public void handle(WindowEvent event) {
                                showTIKChooser(); //To change body of generated methods, choose Tools | Templates.
                            }
                        });
                        addOptionsMenu.hideMenu();
//super.onItemClick(); //To change body of generated methods, choose Tools | Templates.
                    }
                });
                addOptionsMenu.addItem(new MaterialDropdownMenuItem("Write title ID"){

                    @Override
                    public void onItemClick() {
                        showNewDialog();
                        //super.onItemClick(); //To change body of generated methods, choose Tools | Templates.
                    }
                });
                addOptionsMenu.unhide();
                
                //To change body of generated methods, choose Tools | Templates.
            }
        });
        this.getContentCard().setFloatingButton(addButton);
        MaterialDisplayText dText=new MaterialDisplayText("Drag and drop a TIK file");
                    dText.setColorCode(MaterialColor.material.BLACK_54);
                    dText.setFontSize(30);
                    StackPane empty=new StackPane(dText);
                    empty.setPrefHeight(1080);
                    setRootComponent(empty);
    }
    public void clearList(){
        list.clear();
    }
    public void addFromTitleID(String ID, TIKFile tikFile){
        NUSTitleInformation info=new NUSTitleInformation();
                                info.setTitleID(Util.StringToLong(ID));
                                info.setTIKFile(tikFile);
                                
                                
                                info.setLongnameEN(ID);
                                List<NUSTitleInformation> titleList=
                                        Titles.readTitles();
                                for(NUSTitleInformation title: titleList){
                                    if(title.getTitleIDString().equalsIgnoreCase(info.getTitleIDString())){
                                        info.setLongnameEN(title.getLongnameEN());
                                        break;
                                    }
                                }
                                
                                showNameDialog(info);
    }
    public void addTitleInformation(NUSTitleInformation info){
        if(Util.commonKey!=null){
            
        
        boolean add=true;
        for(int i=0;i<list.size();i++){
            if(list.getItem(i).getTitleID()==info.getTitleID()){
                
                add=false;
                break;
            }
        }
        if(add){
        list.addItem(info);
        
        if(info.getRegion()!=null){
            new MaterialToast(info.getLongnameEN()+" ("+info.getRegion().toString()+") added to download queue").unhide();
        
        }
        else{
            new MaterialToast(info.getLongnameEN()+" added to download queue").unhide();
        
        }
        
        
        }
        else
            new MaterialToast("This title is already in the download queue").unhide();}
        
        else{
            new MaterialToast("Common key invalid or null").unhide();
        }
    } 
    @Override
    public void onShown() {
        layout.switchToolbarActions(1);
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        
    }
    
}
