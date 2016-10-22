/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import com.olmectron.material.components.MaterialTransparentPane;
import com.olmectron.material.constants.MaterialColor;
import com.olmectron.material.layouts.MaterialEditableLayout;
import de.mas.jnustool.util.NUSTitleInformation;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Édgar
 */
public class TitlePane extends MaterialTransparentPane {
    private MaterialEditableLayout layout;
    public TitlePane(DownloadPane pane,MaterialEditableLayout layout){
        super();
        getContentCard().setCardPadding(new Insets(0));
        TitleTable table=new TitleTable(pane);
        this.layout=layout;
        HBox statusBox=new HBox();
        statusBox.setMinHeight(32);
        //statusBox.setStyle("-fx-background-color: "+MaterialColor.material.AMBER.getStandardWebCode()+";");
        VBox box=new VBox(table.getTable());
        
        table.getSortedData().addListener(new ListChangeListener<NUSTitleInformation>(){
            @Override
            public void onChanged(ListChangeListener.Change<? extends NUSTitleInformation> c) {
                if(c.next()){
                    if(c.wasAdded() || c.wasRemoved()){
                        setTitleCount(table.getSortedData().size());
                    }
                }
                 //To change body of generated methods, choose Tools | Templates.
            }
        });
        setTitleCount(table.getSortedData().size());
        this.setRootComponent(box);
    }
    
private LongProperty titleCount;
public LongProperty titleCountProperty(){
   if(titleCount==null){
       titleCount=new SimpleLongProperty(this,"titleCount");
   }
   return titleCount;
}
public void setTitleCount(long val){
   titleCountProperty().set(val);
}
public long getTitleCount(){
   return titleCountProperty().get();
}

    @Override
    public void onShown() {
        
        layout.switchToolbarActions(0);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
