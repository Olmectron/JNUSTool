/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import com.olmectron.material.utils.JSONFile;
import de.mas.jnustool.util.NUSTitleInformation;
import de.mas.jnustool.util.Util;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Édgar
 */
public class NUSTitleJSON extends JSONFile<NUSTitleInformation> {
    public NUSTitleJSON(File file){
        super(file);
    }
    @Override
    public NUSTitleInformation parseObject(JSONObject object) {
        try {
            if(object.getString("ticket").equals("1")){
            NUSTitleInformation info=new NUSTitleInformation();
            info.setTitleID(Util.StringToLong(object.getString("titleID")));
            try{
                info.setLongnameEN(object.getString("name"));
            switch(object.getString("region")){
                case "USA":
                    info.setRegion(NUSTitleInformation.Region.USA);
                    break;
                case "JPN":
                    info.setRegion(NUSTitleInformation.Region.JAP);
                    break;
                    case "EUR":
                    info.setRegion(NUSTitleInformation.Region.EUR);
                    break;
                    default:
                    info.setRegion(NUSTitleInformation.Region.UKWN);
                    break;
                    
                        
            }
            }
            catch(JSONException ex){
                return null;
            }
            
            //info.setRegion(NUSTitleInformation.Region.JAP);
            return info;
            }
            else{
                return null;
            }
            //To change body of generated methods, choose Tools | Templates.
        } catch (JSONException ex) {
            Logger.getLogger(NUSTitleJSON.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
