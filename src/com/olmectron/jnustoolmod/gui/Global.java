/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import com.olmectron.material.components.MaterialToast;
import com.olmectron.material.files.ExportFile;
import com.olmectron.material.files.FieldsFile;
import static de.mas.jnustool.Starter.sameArrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Édgar
 */
public class Global {
    public static void downloadTIK(String titleID,File toFile){
        try {
            String valor=settings.getSettingsFile().getValue("title_site");
            if(valor.endsWith("/")){
                valor=valor.substring(0,valor.length()-1);
            }
            URL website = new URL(valor+"/ticket/"+titleID+".tik");
            
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            
            FileOutputStream fos = new FileOutputStream(toFile);
            
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            System.out.println("Ticket downloaded from "+valor);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Global.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            new MaterialToast("TIK file for "+titleID+" doesn't exist").unhide();
            Logger.getLogger(Global.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Global.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static class settings{
        private static  StringProperty downloadHome;
        public static StringProperty downloadHomeProperty(){
            if(downloadHome==null){
                downloadHome=new SimpleStringProperty(Global.class,"downloadHome");
                downloadHome.setValue(getSettingsFile().getValue("download_home",System.getProperty("user.home").replace("\\", "/")+"/JNUSToolMOD"));
                downloadHome.addListener(new ChangeListener<String>(){
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if(newValue!=null && !newValue.trim().equals("")){
                            getSettingsFile().setValue("download_home", newValue);
                        }
                        //To change body of generated methods, choose Tools | Templates.
                    }
                });
                
            }
            return downloadHome;
        }
        public static void setDownloadHome(String s){
            downloadHomeProperty().set(s);
        }
        public static String getDownloadHome(){
            return downloadHomeProperty().get();
        }
        public static void downloadTitleListFiles(String valor){
try{
    
    File titleList=new File(".title_list");
    File updateList=new File(".update_title_list");
    if(valor.endsWith("/")){
        valor=valor.substring(0,valor.length()-1);
    }
    System.out.println("Trying to connect: "+valor);
    URL website = new URL(valor+"/json");
    try{
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        
        FileOutputStream fos = new FileOutputStream(".update_title_list");
        
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        if(titleList.exists()){
            if(updateList.exists()){
                byte[] titleData=Files.readAllBytes(titleList.toPath());
                byte[] updateData=Files.readAllBytes(updateList.toPath());
                if(!sameArrays(titleData,updateData)){
                    
                    System.out.println("TitleData length: "+titleData.length);
                    System.out.println("UpdateData length: "+updateData.length);
                    
                    titleList.delete();
                    ExportFile.hideWindowsFile(updateList);
                    updateList.renameTo(titleList);
                }
                else{
                    updateList.delete();
                }
            }
            
            
            
            //System.out.println(new TextFile("title_list").getText());
        }
        else{
            if(updateList.exists()){
                
                Files.copy(updateList.toPath(), titleList.toPath());
                ExportFile.hideWindowsFile(titleList);
                boolean u=updateList.delete();
                System.out.println("Update deleted: "+u);
                
                
            }
        }
    }
    catch(java.net.ConnectException ex){
        if(ex.getMessage().contains("Connection timed out")){
            System.err.println("Non successful title site address");
        }
    }
    catch(FileNotFoundException ex){
        System.err.println(ex.getMessage());
    } catch (IOException ex) {
        Logger.getLogger(Global.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    if(titleList.exists()){
        Global.settings.setNusTitleJSON(new NUSTitleJSON(titleList));
    }
}
catch(MalformedURLException ex){
    System.err.println(ex.getMessage());
                //Logger.getLogger(Global.class.getName()).log(Level.SEVERE, null, ex);
}
        }
        private static ObjectProperty<NUSTitleJSON> nusTitleJSON;
        public static ObjectProperty<NUSTitleJSON> nusTitleJSONProperty(){
            if(nusTitleJSON==null){
                nusTitleJSON=new SimpleObjectProperty<NUSTitleJSON>(Global.class,"nusTitleJSON");
                nusTitleJSON.set(null);
            }
            return nusTitleJSON;
        }
        public static void setNusTitleJSON(NUSTitleJSON json){
            nusTitleJSONProperty().set(json);
        }
        public static NUSTitleJSON getNusTitleJSON(){
            return nusTitleJSONProperty().get();
        }
        
        private static IntegerProperty maxDownloads;
        private static FieldsFile settingsFile;
        public static FieldsFile getSettingsFile(){
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
        
        
        
        private static StringProperty nameFilter;
        public static StringProperty nameFilterProperty(){
            
            if(nameFilter==null){
                nameFilter=new SimpleStringProperty(Global.class,"nameFilter");
            }
            return nameFilter;
        }
        public static String getNameFilter(){
            return nameFilterProperty().get();
        }
        public static void setNameFilter(String s){
            nameFilterProperty().set(s);
        }
    }
}
