/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

import com.olmectron.material.components.MaterialDropdownMenu;
import com.olmectron.material.components.MaterialDropdownMenuItem;
import com.olmectron.material.components.MaterialTable;
import com.olmectron.material.components.MaterialTableColumn;
import com.olmectron.material.components.MaterialToast;
import com.olmectron.material.constants.MaterialColor;
import de.mas.jnustool.Logger;
import de.mas.jnustool.NUSTitle;
import de.mas.jnustool.util.NUSTitleInformation;
import de.mas.jnustool.util.NUSTitleInformation.Region;
import de.mas.jnustool.util.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Édgar
 */
public class TitleTable extends MaterialTable<NUSTitleInformation> {
     private DownloadPane dPane;
    public TitleTable(DownloadPane pane){
        super();
        dPane=pane;
        initTitleTable();
        List<NUSTitleInformation> list=readUpdateCSV("updatetitles.csv");
        Collections.sort(list);
        for(int i=0;i<list.size();i++){
            addItem(list.get(i));
        }
        setupRegionFilter();
        
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


    private void initTitleTable(){
        
            getTable().setPrefHeight(1080);
            MaterialTableColumn titleColumn=new MaterialTableColumn("Title ID");
       titleColumn.setMinWidth(160);
       titleColumn.setCellValueFactory(new PropertyValueFactory<NUSTitleInformation, String>("titleIDString"));
       titleColumn.setCellFactory(column -> {
    return new TableCell<NUSTitleInformation, String>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setText(null);
            } else {
                // Format date.
                setStyle("-fx-font-size:15px;-fx-text-fill: "+MaterialColor.material.INDIGO.getStandardWebCode()+";");
                
                setText(item.toString());
               

                // Style all dates in March with a different color.
                /*if (item.getMonth() == Month.MARCH) {
                    setTextFill(Color.CHOCOLATE);
                    setStyle("-fx-background-color: yellow");
                } else {
                    setTextFill(Color.BLACK);
                    setStyle("");
                }*/
            }
        }
    };
});
       
        MaterialTableColumn regionColumn=new MaterialTableColumn("Region");
       regionColumn.setMinWidth(70);
       
       regionColumn.setCellValueFactory(new PropertyValueFactory<NUSTitleInformation, Region>("region"));
       regionColumn.setCellFactory(column -> {
    return new TableCell<NUSTitleInformation, Region>() {
        @Override
        protected void updateItem(Region item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setText(null);
            } else {
                // Format date.
                setStyle("-fx-font-size: 15px;");
                setText(item.toString());
               

                // Style all dates in March with a different color.
                /*if (item.getMonth() == Month.MARCH) {
                    setTextFill(Color.CHOCOLATE);
                    setStyle("-fx-background-color: yellow");
                } else {
                    setTextFill(Color.BLACK);
                    setStyle("");
                }*/
            }
        }
    };
});
        MaterialTableColumn nameColumn=new MaterialTableColumn("Name");
       nameColumn.setMinWidth(452);
       nameColumn.setCellValueFactory(new PropertyValueFactory<NUSTitleInformation, String>("longnameEN"));
       nameColumn.setCellFactory(column -> {
    return new TableCell<NUSTitleInformation, String>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setText(null);
            } else {
                // Format date.
                setStyle("-fx-font-size:16px;");
                
                setText(item.toString());
               

                // Style all dates in March with a different color.
                /*if (item.getMonth() == Month.MARCH) {
                    setTextFill(Color.CHOCOLATE);
                    setStyle("-fx-background-color: yellow");
                } else {
                    setTextFill(Color.BLACK);
                    setStyle("");
                }*/
            }
        }
    };
});
        MaterialTableColumn versionColumn=new MaterialTableColumn("Version");
       versionColumn.setMinWidth(70);
       versionColumn.setCellValueFactory(new PropertyValueFactory<NUSTitleInformation, String>("latestVersion"));
       
       
       getTable().getColumns().addAll(titleColumn,nameColumn,regionColumn,versionColumn);
        
    }
    private SortedList<NUSTitleInformation> sortedData;
    public SortedList<NUSTitleInformation> getSortedData(){
        return sortedData;
    }
    private void setupRegionFilter(){
           FilteredList<NUSTitleInformation> filteredData = new FilteredList<NUSTitleInformation>(this.getItemList(), p -> true);
          Global.settings.regionFilterProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    filteredData.setPredicate(info -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                if(info.getRegion().toString().equalsIgnoreCase(newValue)){
                    return true;
                    
                }
                else
                return false;
            }); //To change body of generated methods, choose Tools | Templates.
                }
            });
           sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(getTable().comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        getTable().setItems(sortedData);
    }
    @Override
    public void onItemClicked(NUSTitleInformation e, MouseEvent event) {
       if(e!=null){
           if(event.getButton().equals(MouseButton.SECONDARY)){
               MaterialDropdownMenu menu=new MaterialDropdownMenu(event.getScreenX(),event.getScreenY());
               menu.addItem(new MaterialDropdownMenuItem("Download encrypted"){

                   @Override
                   public void onItemClick() {
                      downloadItem(e);
                       
//To change body of generated methods, choose Tools | Templates.
                   }
               
               
               });
               menu.unhide();
           }
       }
    }
    private void downloadItem(NUSTitleInformation item){
        dPane.addTitleInformation(item);
    }
    @Override
    public void onRowAdded(ListChangeListener.Change c, Object addedObject) {
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onRowRemoved(ListChangeListener.Change c, Object removedObject) {
        //To change body of generated methods, choose Tools | Templates.
    }
}
