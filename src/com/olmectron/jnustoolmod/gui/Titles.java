/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import de.mas.jnustool.Logger;
import de.mas.jnustool.util.NUSTitleInformation;
import de.mas.jnustool.util.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Édgar
 */
public class Titles {
    private static List<NUSTitleInformation> titleList;
    
    public static List<NUSTitleInformation> readTitles(){
        if(titleList==null){
            titleList=readUpdateCSV("fulltitles.csv");
        }
        return titleList;
    }
    @SuppressWarnings("resource")
	private static List<NUSTitleInformation> readUpdateCSV(String updateCSVPath) {
		if(updateCSVPath == null) return null;
		BufferedReader in = null;
		List<NUSTitleInformation> list = new ArrayList<>();
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(updateCSVPath)), "UTF-8"));
			String line;
		    while((line = in.readLine()) != null){
		    	String[] infos = line.split(";");
		    	if(infos.length != 8) {
		    		Logger.messageBox("Updatelist is broken!");
		    		Logger.log("Updatelist is broken!");
		    		return null;
		    	}
		    	long titleID = Util.StringToLong(infos[0].replace("-", ""));
		    	int region = Integer.parseInt(infos[1]);
		    	String  content_platform = infos[2];
		    	String  company_code = infos[3];
		    	String  product_code = infos[4];
		    	String  ID6 = infos[5];
		    	String  longnameEN = infos[6];
		    	String[]  versions = infos[7].split(",");		    	
		    	NUSTitleInformation info = new NUSTitleInformation(titleID, longnameEN, ID6, product_code, content_platform, company_code, region,versions);
		    	
		    	list.add(info);
		    }
		    in.close();
		} catch (IOException | NumberFormatException e) {
			try {
				if(in != null)in.close();
			} catch (IOException e1) {
			}
			Logger.messageBox("Updatelist is broken or missing");
			Logger.log("Updatelist is broken!");
			return null;
		}
		return list;		
	}


}
