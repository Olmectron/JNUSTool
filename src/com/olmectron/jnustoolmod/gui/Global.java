/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import com.olmectron.material.files.FieldsFile;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Édgar
 */
public class Global {
    public static class settings{
        private static IntegerProperty maxDownloads;
        private static FieldsFile settingsFile;
        private static FieldsFile getSettingsFile(){
            if(settingsFile==null){
                try {
                    settingsFile=new FieldsFile("settings");
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Global.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return settingsFile;
        }
        public static IntegerProperty maxDownloadsProperty(){
            if(maxDownloads==null){
                    maxDownloads=new SimpleIntegerProperty(Global.class,"maxDownloads");
                    maxDownloads.set(Integer.parseInt(getSettingsFile().getValue("max_simultaneous","1")));
                    maxDownloads.addListener(new ChangeListener<Number>(){
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                                getSettingsFile().setValue("max_simultaneous",newValue.intValue()+"");
                           
                             //To change body of generated methods, choose Tools | Templates.
                        }
                    });
                    
                
                
            }
            return maxDownloads;
        }
        public static  void setMaxDownloads(int max){
            maxDownloadsProperty().set(max);
        }
        public static int getMaxDownloads(){
            return maxDownloadsProperty().get();
        }
        private static StringProperty regionFilter;
        public static StringProperty regionFilterProperty(){
            
            if(regionFilter==null){
                regionFilter=new SimpleStringProperty(Global.class,"regionFilter");
            }
            return regionFilter;
        }
        public static String getRegionFilter(){
            return regionFilterProperty().get();
        }
        public static void setRegionFilter(String s){
            regionFilterProperty().set(s);
        }
    }
}
