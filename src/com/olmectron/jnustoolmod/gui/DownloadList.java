/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import com.olmectron.material.components.MaterialCard;
import com.olmectron.material.components.MaterialDisplayText;
import com.olmectron.material.components.MaterialFlowList;
import com.olmectron.material.components.MaterialProgressBar;
import com.olmectron.material.components.MaterialStandardListItem;
import com.olmectron.material.components.MaterialTextField;
import com.olmectron.material.components.MaterialToast;
import com.olmectron.material.constants.MaterialColor;
import com.olmectron.material.utils.BackgroundTask;
import de.mas.jnustool.Logger;
import de.mas.jnustool.Progress;
import de.mas.jnustool.ProgressUpdateListener;
import de.mas.jnustool.Starter;
import de.mas.jnustool.util.NUSTitleInformation;
import de.mas.jnustool.util.Settings;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Édgar
 */
public class DownloadList extends MaterialFlowList<NUSTitleInformation>{

    public DownloadList(){
        super(new StackPane());
        
    }
    private ObjectProperty<NUSTitleInformation> currentItem;
    private ObjectProperty<NUSTitleInformation> currentItemProperty(){
        if(currentItem==null){
            currentItem=new SimpleObjectProperty<NUSTitleInformation>(this,"currentItem");
            currentItem.set(null);
        }
        return currentItem;
    }
    private void setCurrentItem(NUSTitleInformation g){
        currentItemProperty().set(g);
    }
    private NUSTitleInformation getCurrentItem(){
        return currentItemProperty().get();
    }
    @Override
    public void cardConverter(MaterialCard card, NUSTitleInformation item, MaterialStandardListItem<NUSTitleInformation> itemContainer) {
            card.setCardWidth(500);
            card.setCardPadding(new Insets(16));
            MaterialDisplayText titleText=new MaterialDisplayText(item.getTitleIDString());
            titleText.setFontSize(12);
            titleText.setPadding(new Insets(0,8,0,0));
            titleText.setColorCode(MaterialColor.material.BLACK_54);
            MaterialDisplayText nameText=new MaterialDisplayText(item.getLongnameEN()+" ("+item.getRegion().toString()+")");
            nameText.setFontSize(15);
            //nameText.setPadding(new Insets(0,0,8,0));
            nameText.setColorCode(MaterialColor.material.BLACK_87);
            HBox spanBox=new HBox();
            HBox.setHgrow(spanBox, Priority.ALWAYS);
            MaterialProgressBar bar=new MaterialProgressBar();
            MaterialDisplayText progress=new MaterialDisplayText("0%");
            progress.setFontSize(18);
            progress.setColorCode(MaterialColor.material.BLUE);
            progress.setId("progress");
            bar.setId("barra");
            progress.setMinWidth(60);
            progress.setAlignment(Pos.CENTER_RIGHT);
            bar.setProgress(0);
            HBox box=new HBox(new VBox(titleText,nameText),spanBox,progress);
            box.setPadding(new Insets(0,0,12,0));
            box.setAlignment(Pos.CENTER);
            
            card.addComponent(box);
            card.addComponent(bar);
        //To change body of generated methods, choose Tools | Templates.
    }
    private int getItemIndex(NUSTitleInformation item){
        for(int i=0;i<this.size();i++){
            if(this.getItemBox(i).getItem().getTitleID()==item.getTitleID()){
                return i;
            }
        }
        return -1;
    }
    @Override
    public void onCardAttached(MaterialStandardListItem<NUSTitleInformation> itemContainer) {
        
        itemContainer.removeRipple(); //To change body of generated methods, choose Tools | Templates.
        if(getCurrentItem()==null){
        startDownload(itemContainer);
        }else{
            currentItemProperty().addListener(new ChangeListener<NUSTitleInformation>(){
                @Override
                public void changed(ObservableValue<? extends NUSTitleInformation> observable, NUSTitleInformation oldValue, NUSTitleInformation newValue) {
                        
                        if(oldValue!=null && newValue==null){
                            int oldIndex=getItemIndex(oldValue);
                            int newIndex=getItemIndex(itemContainer.getItem());
                            if(oldIndex==newIndex-1){
                                startDownload(itemContainer);
                            }
                            
                        }

//To change body of generated methods, choose Tools | Templates.
                }
            });
            
            
        }
        
    }
    private void startDownload(MaterialStandardListItem<NUSTitleInformation> itemContainer){
        MaterialProgressBar barra=(MaterialProgressBar)itemContainer.lookup("#barra");
        MaterialDisplayText progressText=(MaterialDisplayText)itemContainer.lookup("#progress");
        Progress progress=new Progress();
        progress.setProgressUpdateListener(new ProgressUpdateListener() {
			
			@Override
			public void updatePerformed(Progress p) {
                            Platform.runLater(new Runnable(){
                                @Override
                                public void run() {
                                    
                            progressText.setText((p.statusInPercent())+"%"); //To change body of generated methods, choose Tools | Templates.
                                }
                            });
				barra.setProgress((double)(((double)p.statusInPercent())/100));
			}
		});
        ArrayList<NUSTitleInformation> lista=new ArrayList<NUSTitleInformation>();
        lista.add(itemContainer.getItem());
        setCurrentItem(lista.get(0));
        new Thread(new Runnable(){
						@Override
						public void run() {
                                                    
							barra.setProgress(0);
							progress.clear();
							
        Starter.downloadEncrypted(lista,progress);
							progress.operationFinish();
                                                        Platform.runLater(new Runnable(){
                                                            @Override
                                                            public void run() {
                                                                setCurrentItem(null);
                                                               new MaterialToast(itemContainer.getItem().getLongnameEN()+" ("+itemContainer.getItem().getRegion().toString()+") finished!").unhide();							
						 //To change body of generated methods, choose Tools | Templates.
                                                            }
                                                        });
							}
	        			
	        		}).start();
    }
    @Override
    public boolean asCard() {
        return true;
//To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void onItemClick(NUSTitleInformation item, MouseEvent event) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node itemConverter(NUSTitleInformation item, MaterialStandardListItem<NUSTitleInformation> itemContainer) {
        return null;
//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
