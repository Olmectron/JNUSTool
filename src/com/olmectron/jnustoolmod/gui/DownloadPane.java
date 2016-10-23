/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import com.olmectron.material.components.MaterialConfirmDialog;
import com.olmectron.material.components.MaterialFloatingButton;
import com.olmectron.material.components.MaterialIconButton;
import com.olmectron.material.components.MaterialTextField;
import com.olmectron.material.components.MaterialToast;
import com.olmectron.material.components.MaterialTransparentPane;
import com.olmectron.material.layouts.MaterialEditableLayout;
import de.mas.jnustool.util.NUSTitleInformation;
import de.mas.jnustool.util.Util;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 *
 * @author Édgar
 */
public class DownloadPane extends MaterialTransparentPane{
    private DownloadList list;
    private MaterialEditableLayout layout;
    public  DownloadPane(MaterialEditableLayout layout){
        super();
        this.getContentCard().setCardPadding(new Insets(0));
        list=new DownloadList();
        this.layout=layout;
        MaterialFloatingButton addButton=new MaterialFloatingButton(MaterialIconButton.PLUS_ICON);
        addButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                MaterialTextField titleField=new MaterialTextField("Title ID");
                titleField.lockLetters();
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
                //To change body of generated methods, choose Tools | Templates.
            }
        });
        this.getContentCard().setFloatingButton(addButton);
        setRootComponent(list.getPerfectSizeFlowListPane());
    }
    public void clearList(){
        list.clear();
    }
    
    public void addTitleInformation(NUSTitleInformation info){
        boolean add=true;
        for(int i=0;i<list.size();i++){
            if(list.getItem(i).getTitleID()==info.getTitleID()){
                
                add=false;
                break;
            }
        }
        if(add){
        list.addItem(info);
        
        new MaterialToast(info.getLongnameEN()+" ("+info.getRegion().toString()+") added to download queue").unhide();
        }
        else
            new MaterialToast("This title is already in the download queue").unhide();
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
