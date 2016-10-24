/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import com.olmectron.material.components.MaterialCard;
import com.olmectron.material.components.MaterialDisplayText;
import com.olmectron.material.components.MaterialDropdownMenu;
import com.olmectron.material.components.MaterialDropdownMenuItem;
import com.olmectron.material.components.MaterialFlowList;
import com.olmectron.material.components.MaterialIconButton;
import com.olmectron.material.components.MaterialProgressBar;
import com.olmectron.material.components.MaterialStandardListItem;
import com.olmectron.material.components.MaterialTextField;
import com.olmectron.material.components.MaterialToast;
import com.olmectron.material.constants.MaterialColor;
import com.olmectron.material.utils.BackgroundTask;
import de.mas.jnustool.Logger;
import de.mas.jnustool.NUSTitle;
import de.mas.jnustool.Progress;
import de.mas.jnustool.ProgressUpdateListener;
import de.mas.jnustool.Starter;
import de.mas.jnustool.util.NUSTitleInformation;
import de.mas.jnustool.util.Settings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.utilities.Focus;

/**
 *
 * @author Édgar
 */
public class DownloadList extends MaterialFlowList<NUSTitleInformation>{

    public DownloadList(){
        super(new StackPane());
        
    }
    
    
    private ObservableList<NUSTitleInformation> currentItems;
    private ObservableList<NUSTitleInformation> getCurrentItemsList(){
        
        if(currentItems==null){
            currentItems=FXCollections.observableArrayList();
            currentItems.addListener(new ListChangeListener<NUSTitleInformation>(){
                @Override
                public void onChanged(ListChangeListener.Change<? extends NUSTitleInformation> c) {
                    if(c.next()){
                        if(c.wasAdded()){
                            
                           for(NUSTitleInformation info: c.getAddedSubList()){
                             startDownload(getItemBox(getItemIndex(info)));
                           }
                        }
                    }
                    
//To change body of generated methods, choose Tools | Templates.
                }
            });
            
        }
        
        return currentItems;
    }
    private void addToCurrentItems(NUSTitleInformation g){
        getCurrentItemsList().add(g);
    }
    private void removeFromCurrentItems(NUSTitleInformation g){
        for (int i=0;i<getCurrentItemsList().size();i++) {
            if(getCurrentItemsList().get(i).getTitleID()==g.getTitleID()){
                currentItems.remove(i);
                break;
            }
        }
    }
    @Override
    public void cardConverter(MaterialCard card, NUSTitleInformation item, MaterialStandardListItem<NUSTitleInformation> itemContainer) {
            card.setCardWidth(500);
            card.setCardPadding(new Insets(16));
            MaterialDisplayText titleText=new MaterialDisplayText(item.getTitleIDString());
            titleText.setFontSize(14);
            titleText.setPadding(new Insets(0,8,0,0));
            titleText.setColorCode(MaterialColor.material.BLACK_54);
            
            MaterialDisplayText nameText=new MaterialDisplayText("");
            try{
                nameText.setText(item.getLongnameEN()+" ("+item.getRegion().toString()+")");
                
            }
            catch(NullPointerException ex){
                nameText.setText(item.getLongnameEN());
            }
            nameText.setFontSize(17);
            //nameText.setPadding(new Insets(0,0,8,0));
            nameText.setColorCode(MaterialColor.material.BLACK_87);
            HBox spanBox=new HBox();
            HBox.setHgrow(spanBox, Priority.ALWAYS);
            MaterialProgressBar bar=new MaterialProgressBar();
            MaterialDisplayText progress=new MaterialDisplayText("0%");
            MaterialDisplayText total=new MaterialDisplayText("0 MB / 0 MB");
            
            total.setFontSize(14);
            total.setColorCode(MaterialColor.material.GREEN);
            
            progress.setFontSize(18);
            progress.setColorCode(MaterialColor.material.BLUE);
            progress.setId("progress");
            bar.setId("barra");
            progress.setMinWidth(60);
            total.setId("total");
            progress.setAlignment(Pos.CENTER_RIGHT);
            bar.setProgress(0);
            MaterialIconButton options=new MaterialIconButton(MaterialIconButton.MORE_VERT_ICON);
            options.setOnClick(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent event) {
                    MaterialDropdownMenu menu=new  MaterialDropdownMenu(options);
                    menu.addItem(new MaterialDropdownMenuItem("Remove",MaterialIconButton.DELETE_ICON,true){

                        @Override
                        public void onItemClick() {
                            removeItem(item);
//To change body of generated methods, choose Tools | Templates.
                        }
                    
                    });
                    
                    
                    menu.unhide();
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
            options.setColorCode(MaterialColor.material.BLACK_54);
            HBox box=new HBox(new VBox(titleText,nameText),spanBox,progress,options);
            box.setPadding(new Insets(0,0,12,0));
            box.setAlignment(Pos.CENTER);
            total.setPadding(new Insets(0,0,8,0));
            card.addComponent(box);
            card.addComponent(new HBox(total));
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
        BackgroundTask task=new BackgroundTask() {
            //private boolean startDownload=false;
            @Override
            public Object onAction() {
                 if(getCurrentItemsList().size()<Global.settings.getMaxDownloads()){
                     addToCurrentItems(itemContainer.getItem());
        //startDownload=true;
        }else{
                     getCurrentItemsList().addListener(new ListChangeListener<NUSTitleInformation>(){
                         @Override
                         public void onChanged(ListChangeListener.Change<? extends NUSTitleInformation> c) {
                             if(c.next()){
                                 if(c.wasAdded() || c.wasRemoved()){
                                     if(getCurrentItemsList().size()<Global.settings.getMaxDownloads()){
                                         
                                         getCurrentItemsList().removeListener(this);
                                         addToCurrentItems(itemContainer.getItem());
                                         
                                     }
                                 }
                             }
                             //To change body of generated methods, choose Tools | Templates.
                         }
                     });
            /*currentItemProperty().addListener(new ChangeListener<NUSTitleInformation>(){
                @Override
                public void changed(ObservableValue<? extends NUSTitleInformation> observable, NUSTitleInformation oldValue, NUSTitleInformation newValue) {
                        
                        if(oldValue!=null && newValue==null){
                            int oldIndex=getItemIndex(oldValue);
                            int newIndex=getItemIndex(itemContainer.getItem());
                            if(oldIndex==newIndex-1){
                                startDownload=true;
                            }
                            
                        }

//To change body of generated methods, choose Tools | Templates.
                }
            });
            */
            
        }

                return null;//To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSucceed(Object valor) {
                
                 //To change body of generated methods, choose Tools | Templates.
            }
        };
             new Timer().schedule(
    new TimerTask() {

        @Override
        public void run() {
            task.play();
        }
    }, 1000);
        
    }
    private void startDownload(MaterialStandardListItem<NUSTitleInformation> itemContainer){
        MaterialProgressBar barra=(MaterialProgressBar)itemContainer.lookup("#barra");
        MaterialDisplayText progressText=(MaterialDisplayText)itemContainer.lookup("#progress");
        
        MaterialDisplayText totalText=(MaterialDisplayText)itemContainer.lookup("#total");
        Progress progress=new Progress();
        progress.setProgressUpdateListener(new ProgressUpdateListener() {
			
			@Override
			public void updatePerformed(Progress p) {
                            Platform.runLater(new Runnable(){
                                @Override
                                public void run() {
                                    
            totalText.setText(Focus.decimal.format((double)progress.getCurrent()/1024/1024)+" MB"+"/"+Focus.decimal.format((double)progress.getTotal()/1024/1024)+" MB");
                            progressText.setText((p.statusInPercent())+"%"); //To change body of generated methods, choose Tools | Templates.
                                }
                            });
				barra.setProgress((double)(((double)p.statusInPercent())/100));
			}
		});
        ArrayList<NUSTitleInformation> lista=new ArrayList<NUSTitleInformation>();
        lista.add(itemContainer.getItem());
        //setCurrentItem(lista.get(0));
        
        NUSTitle title = new NUSTitle(lista.get(0).getTitleID(),-1, null);
	title.setTIKFile(lista.get(0).getTIKFile());
        new Thread(new Runnable(){
						@Override
						public void run() {
                                                    
                                                    try {
                                                        barra.setProgress(0);
                                                        progress.clear();
                                                        //title.decryptFEntries(title.getFst().getFileEntriesByFilePath(path), null);
                    
                                                        title.downloadEncryptedFiles(progress);
                                                        //Starter.downloadEncrypted(lista,progress);
                                                        progress.operationFinish();
                                                        Platform.runLater(new Runnable(){
                                                            @Override
                                                            public void run() {
                                                                getCurrentItemsList().remove(itemContainer.getItem());
                                                                    new MaterialToast(itemContainer.getItem().getLongnameEN()+" finished!").unhide();
                                                                
                                                                
                                                                    
//To change body of generated methods, choose Tools | Templates.
                                                            }
                                                        });
                                                    } catch (IOException ex) {
                                                        java.util.logging.Logger.getLogger(DownloadList.class.getName()).log(Level.SEVERE, null, ex);
                                                    }
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
