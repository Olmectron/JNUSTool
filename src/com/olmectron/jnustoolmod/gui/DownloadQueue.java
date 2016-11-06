/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import de.mas.jnustool.util.NUSTitleInformation;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class DownloadQueue {
        private ObservableList<NUSTitleInformation> queueList;
        private ObservableList<NUSTitleInformation> getQueueList(){
            if(queueList==null){
                queueList=FXCollections.observableArrayList();
                queueList.addListener(new ListChangeListener<NUSTitleInformation>(){
                    @Override
                    public void onChanged(ListChangeListener.Change<? extends NUSTitleInformation> c) {
                       if(c.next()){
                           if(c.wasAdded()){
                               for(int i=0;i<c.getAddedSubList().size();i++){
                                   
                                   
                                   NUSTitleInformation info=c.getAddedSubList().get(i);
                                  System.err.println(info.getTitleIDString()+" added");
                                  if(queueList.size()>1){ 
                                      int indice=-1;
                                      for(int q=0;q<queueList.size();q++){
                                          if(queueList.get(q).getTitleID()==info.getTitleID()){
                                              indice=q;
                                              break;
                                          }
                                      }
                                      System.out.println("Indice anterior: "+(indice-1));
                                      if(queueList.get(indice-1).getProgress().isInProgress()){
                                      
                                        queueList.get(indice-1).finishedProperty().addListener(new ChangeListener<Boolean>(){
                                            @Override
                                            public void changed(ObservableValue<? extends Boolean> observable, 
                                                    Boolean oldValue, Boolean newValue) {
                                                System.err.println("Finished: "+oldValue+", "+newValue);
                                                if(newValue){
                                                    info.startDownload();
                                                }
                                                //To change body of generated methods, choose Tools | Templates.
                                            }
                                        });
                                      }
                                      else{
                                          info.startDownload();
                                      }
                                  }
                                  else{
                                      
                                      info.startDownload();
                                  }
                                  
                                  
                                  
                               }
                           }
                       }
                            //To change body of generated methods, choose Tools | Templates.
                    }
                });
            }
            return queueList;
        }
        public DownloadQueue(){
            
        }
        public void add(NUSTitleInformation item){
            getQueueList().add(item);
        }
        public void remove(NUSTitleInformation item){
            for (NUSTitleInformation info : getQueueList()) {
                if(info.getTitleID()==item.getTitleID()){
                    getQueueList().remove(info);
                }
            }
        }
       
}