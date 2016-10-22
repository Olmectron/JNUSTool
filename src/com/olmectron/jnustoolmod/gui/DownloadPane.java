/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import com.olmectron.material.components.MaterialToast;
import com.olmectron.material.components.MaterialTransparentPane;
import com.olmectron.material.layouts.MaterialEditableLayout;
import de.mas.jnustool.util.NUSTitleInformation;
import javafx.geometry.Insets;
import javafx.scene.input.KeyEvent;

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
