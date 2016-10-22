/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Édgar
 */
public class Global {
    public static class settings{
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
