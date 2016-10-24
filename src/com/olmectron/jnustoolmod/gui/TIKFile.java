/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Édgar
 */
public class TIKFile {
    public String getTitleID(){
        return this.titleID;
    }
    private String titleID;
    private File file;
    public File getFile(){
        return file;
    }
    public TIKFile(File f){
        if(f!=null){
            this.file=f;
            try {
                RandomAccessFile raf=new RandomAccessFile(f,"r");
                raf.seek(0x1DC);
                String c="";
                for(int i=0;i<8;i++){
                  c=c+Hex.getHexPair(raf.read());
                }
                titleID=c;
                System.out.println(c);
            } catch (FileNotFoundException ex) {
                
            } catch(NullPointerException ex){
                System.err.println("Invalid TIK file");
            } catch (IOException ex) {
                Logger.getLogger(TIKFile.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            
        }
        else{
            System.err.println("TIK file is invalid");
        }
    }
}
