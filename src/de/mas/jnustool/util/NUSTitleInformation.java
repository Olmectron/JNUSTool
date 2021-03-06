package de.mas.jnustool.util;

import com.olmectron.jnustoolmod.gui.TIKFile;
import com.olmectron.material.components.MaterialToast;
import de.mas.jnustool.NUSTitle;
import de.mas.jnustool.Progress;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class NUSTitleInformation implements Comparable<NUSTitleInformation>, Serializable{
	private static final long serialVersionUID = 1L;
	public TIKFile TIKFile;
        public void setTIKFile(TIKFile t){
            this.TIKFile=t;
        }
        public TIKFile getTIKFile(){
            return this.TIKFile;
        }
	private String ID6;
	private String product_code;
	private String content_platform;
	private String company_code;
	
	private byte[] key;
	private ObservableList<Integer> versionsList;
        private ObservableList<Integer> getVersionsList(){
            if(versionsList==null){
            
                    versionsList=FXCollections.observableArrayList();
                    versionsList.addListener(new ListChangeListener<Integer>(){
                        @Override
                        public void onChanged(ListChangeListener.Change<? extends Integer> c) {
                            if(c.next()){
                                if(c.wasAdded() || c.wasRemoved()){
                                    updateLatestVersion();
                                }
                            }
                            //To change body of generated methods, choose Tools | Templates.
                        }
                    });
            }
            return versionsList;
        }
	
	private String selectedVersion = "latest";
	private void updateRegion(){
            setRegion(getRegionAsRegion());
        }
private IntegerProperty regionInt;
public IntegerProperty regionIntProperty(){
   if(regionInt==null){
       regionInt=new SimpleIntegerProperty(this,"regionInt");
       regionInt.addListener(new ChangeListener<Number>(){
           @Override
           public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
              updateRegion();
               //To change body of generated methods, choose Tools | Templates.
           }
       });
   }
   return regionInt;
}
public void setRegionInt(int val){
   regionIntProperty().set(val);
}
public int getRegionInt(){
   return regionIntProperty().get();
}

	public enum Region{
		EUR,
		USA,
		JAP,
		UKWN
	}

	public NUSTitleInformation(long titleID, String longnameEN, String ID6, String product_code,String content_platform,String company_code,int region, String[] versions) {
		setTitleID(titleID);
		setLongnameEN(longnameEN);
		setID6(ID6);	
		setProduct_code(product_code);
		setCompany_code(company_code);
		setContent_platform(content_platform);
		setRegionInt(region);
		for(String s : versions){
			if(s != null){
				getVersionsList().add(Integer.parseInt(s));
			}
		}
	}

	public NUSTitleInformation() {
		// TODO Auto-generated constructor stub
	}

	public NUSTitleInformation(long titleID, String longnameEN, String ID6, String product_code,String content_platform,String company_code,int region) {
		this(titleID, longnameEN, ID6, product_code,content_platform,company_code,region,new String[1]);
	}
        
        public static final int JAP=1;
        public static final int USA=2;
        public static final int EUR=4;
        public static final int UKWN=7;
        
	public Region getRegionAsRegion() {		
		switch (getRegionInt()) {
        	case JAP:  return Region.JAP;                 
        	case USA:  return  Region.USA;
        	case EUR:  return  Region.EUR;
        	default: return  Region.UKWN;
		}
	}

	public String getContent_platform() {
		return content_platform;
	}

	public void setContent_platform(String content_platform) {
		this.content_platform = content_platform;
	}

	public String getCompany_code() {
		return company_code;
	}

	public void setCompany_code(String company_code) {
		this.company_code = company_code;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	private LongProperty titleID;
public LongProperty titleIDProperty(){
   if(titleID==null){
       titleID=new SimpleLongProperty(this,"titleID");
       titleID.addListener(new ChangeListener<Number>(){
           @Override
           public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
               
       updateTitleIDString(); //To change body of generated methods, choose Tools | Templates.
           }
       });
   }
   return titleID;
}
public void setTitleID(long val){
   titleIDProperty().set(val);
}
public long getTitleID(){
   return titleIDProperty().get();
}

	
private StringProperty longnameEN;
public StringProperty longnameENProperty(){
   if(longnameEN==null){
       longnameEN=new SimpleStringProperty(this,"longnameEN");
   }
   return longnameEN;
}
public void setLongnameEN(String val){
   longnameENProperty().set(val);
}
public String getLongnameEN(){
   return longnameENProperty().get();
}

	public String getID6() {
		return ID6;
	}

	public void setID6(String iD6) {
		ID6 = iD6;
	}	
	
	
private ObjectProperty<Region> region;
public ObjectProperty<Region> regionProperty(){
   if(region==null){
       region=new SimpleObjectProperty<Region>(this,"region");
   }
   return region;
}
public void setRegion(Region val){
   regionProperty().set(val);
}
public Region getRegion(){
   return regionProperty().get();
}
	public void updateTitleIDString() {
		setTitleIDString(String.format("%08X-%08X", getTitleID()>>32,getTitleID()<<32>>32));
		
	}
        private Progress prog;
        public void setProgress(Progress p){
            this.prog=p;
            
        }
        public Progress getProgress(){
            if(prog==null){
                prog=new Progress();
               
            }
            return this.prog;
        }
        private Thread getRunningThread(){
            if(getProgress().isInProgress()){
                return downloadThread;
            }
            return null;
        }
       
        private Thread downloadThread=new Thread(new Runnable(){
						@Override
						public void run() {
                                                    
 
                                                    try {
                                                        getProgress().clear();
                                                        //title.decryptFEntries(title.getFst().getFileEntriesByFilePath(path), null);
                                                            getActualTitle().downloadEncryptedFiles(getProgress());
                                                        
                                                        
                                                        //Starter.downloadEncrypted(lista,progress);
                                                        getProgress().operationFinish();
                                                        
                                                    } catch (IOException ex) {
                                                        java.util.logging.Logger.getLogger(NUSTitleInformation.class.getName()).log(Level.SEVERE, null, ex);
                                                    }
							}
	        			
	        		});
       
        public void interruptDownload(){
            if(
            this.actualTitle!=null)
                actualTitle.stopTMDDownload();
        }
        public void startDownload(){
          
        downloadThread.start();
        
            
            
        }
        public String getFolderPath(){
            if(actualTitle!=null){
                return actualTitle.getContentPath();
            }
            return null;
        }
        private NUSTitle actualTitle;
        public NUSTitle getActualTitle(){
            if(actualTitle==null){
            String nombre;
            if(this.getRegion()!=null){
            nombre=(this.getLongnameEN()+" ("+this.getRegion().toString()+")");
        }
        else{
            nombre=(this.getLongnameEN());
        }
            if(this.getTitleIDString().startsWith("00050000")){
                nombre=nombre+"_FULL";
            }
            else if(this.getTitleIDString().startsWith("0005000E")){
                nombre=nombre+"_LATEST_UPDATE";
            }
            actualTitle= new NUSTitle(getTitleID(),-1, null,nombre);
	actualTitle.setTIKFile(getTIKFile());
        
            }
        return actualTitle;
        }
private StringProperty titleIDString;
public StringProperty titleIDStringProperty(){
   if(titleIDString==null){
       titleIDString=new SimpleStringProperty(this,"titleIDString");
   }
   return titleIDString;
}
private BooleanProperty finished;
        public BooleanProperty finishedProperty(){
            if(finished==null){
                finished=new SimpleBooleanProperty(this,"finished");
                finished.set(false);
                finished.bind(getActualTitle().finishedProperty());
            }
            return finished;
        }
        public boolean getFinished(){
            return finishedProperty().get();
        } 
public void setTitleIDString(String val){
   titleIDStringProperty().set(val);
}
public String getTitleIDString(){
   return titleIDStringProperty().get();
}

	
	@Override
	public String toString(){
		String result =  getTitleIDString() + ";" + region +";" + getContent_platform() + ";" + getCompany_code() + ";"+ getProduct_code()+ ";" + getID6() + ";" + getLongnameEN();
		for(Integer i :getVersionsList()){
			result += ";" + i;
		}
		//result += ";" + getSelectedVersion();
		return result;
	}

	@Override
	public int compareTo(NUSTitleInformation o) {
		return getLongnameEN().compareTo(o.getLongnameEN());
	}

	public void init(NUSTitleInformation n) {
		setTitleID(n.getTitleID());
		setRegionInt(n.getRegionInt());
		setCompany_code(n.company_code);
		setContent_platform(n.content_platform);
		setID6(n.ID6);
		setLongnameEN(n.getLongnameEN());
		setProduct_code(n.product_code);
		setKey(n.key);
	}

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}
	
	@Override
	public boolean equals(Object o){		
                if(o!=null)
		return titleID == ((NUSTitleInformation)o).titleID;
                
                return false;
	}

private StringProperty latestVersion;
public StringProperty latestVersionProperty(){
   if(latestVersion==null){
       latestVersion=new SimpleStringProperty(this,"latestVersion");
   }
   return latestVersion;
}
public void setLatestVersion(String val){
   latestVersionProperty().set(val);
}
public int getLatestVersionInteger(){
    return getVersionsList().get(getVersionsList().size()-1);
}
public String getLatestVersion(){
   return latestVersionProperty().get();
}

	private void updateLatestVersion() {
		String result = "latest";
		if(getVersionsList() != null && !getVersionsList().isEmpty()){
			result = getVersionsList().get(getVersionsList().size()-1) + "";
		}
		setLatestVersion(result);
	}

	public List<Integer> getAllVersions() {
		return getVersionsList();
	}
	
	public List<String> getAllVersionsAsString() {
		List<String> list = new ArrayList<>();
		if(getVersionsList() != null && !getVersionsList().isEmpty()){
			for(Integer v: getVersionsList()){
				list.add(v + "");
			}			
		}
		list.add("latest");
		return list;
	}

	public void setSelectedVersion(String string) {
		this.selectedVersion = string;		
	}
	
	public int getSelectedVersion() {
		int version = -1;
		if(this.selectedVersion == "latest"){
			version = -1;
		}else{
			try{
				version = Integer.parseInt(this.selectedVersion);
			}catch(Exception e){
				
			}
		}		
		return version;		
	}
}
