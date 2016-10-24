package de.mas.jnustool;

import com.olmectron.material.MaterialDesign;
import com.olmectron.material.components.MaterialToast;
import com.olmectron.material.files.FieldsFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;

import de.mas.jnustool.gui.NUSGUI;
import de.mas.jnustool.gui.UpdateChooser;
import de.mas.jnustool.util.Downloader;
import de.mas.jnustool.util.NUSTitleInformation;
import de.mas.jnustool.util.Util;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Starter {

	private static String updateCSVPath;
	
	public static void main(String[] args) {
	    Logger.log("JNUSTool 0.0.8c - alpha - by Maschell");
		Logger.log("");
		try {
			readConfig(null);
		} catch (IOException e) {
			System.err.println("Error while reading config! Needs to be:");
			System.err.println("DOWNLOAD URL BASE");
			System.err.println("COMMONKEY");
			System.err.println("updateinfos.csv");
			return;
		}

		long titleID = 0;
		String key = null;
		if(args.length != 0 ){				
			titleID = Util.StringToLong(args[0]);
			int version = -1;
			if( args.length > 1 && args[1].length() == 32){
				key = args[1].substring(0, 32);
			}
			
			if(titleID != 0){
			    String path = "";
				boolean dl_encrypted = false;
				boolean download_file = false;
				
				for(int i =0; i< args.length;i++){
					if(args[i].startsWith("v")){
						version = Integer.parseInt((args[i].substring(1)));
					}
					if(args[i].equals("-dlEncrypted")){
						dl_encrypted = true;						
					}					
					
					if(args[i].equals("-file")){
                        if(args.length > i){
                            i++;
                            path = args[i];                           
                        }
                        download_file = true;                        
                    }       
				}
				if(dl_encrypted){
					NUSTitle title = new NUSTitle(titleID,version, key);
					try {
						title.downloadEncryptedFiles(null);
					} catch (IOException e) {
						e.printStackTrace();
					}	
                                        
					System.exit(0);
				}else if(download_file){
                    NUSTitle title = new NUSTitle(titleID,version, key);
                    
                    title.decryptFEntries(title.getFst().getFileEntriesByFilePath(path), null);
                    
                    System.exit(0);
                }
				
				NUSGUI m = new NUSGUI(new NUSTitle(titleID,version, key));
		        m.setVisible(true);			
			}
		}else{
			for(final NUSTitleInformation nus : getTitleID()){
				
				final long tID = nus.getTitleID();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						NUSGUI m = new NUSGUI(new NUSTitle(tID,nus.getSelectedVersion(), null));
				        m.setVisible(true);						
					}
				}).start();;
			}			
		}
	}
	


	private static List<NUSTitleInformation> getTitleID() {
		List<NUSTitleInformation> updatelist = readUpdateCSV();
		List<NUSTitleInformation> result = null;
		if(updatelist != null){
			result = new ArrayList<>();
			UpdateChooser.createAndShowGUI(updatelist,result);
			synchronized (result) {			    
		    	try {
					result.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			Logger.messageBox("Updatefile is missing or not in config?");
			System.exit(2);
		}
		return result;
	}



	@SuppressWarnings("resource")
	private static List<NUSTitleInformation> readUpdateCSV() {
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



	public static void readConfig(Stage primaryStage) throws IOException {
                FieldsFile settingsFile=new FieldsFile("settings");
                
		Downloader.URL_BASE =  settingsFile.getValue("server", "http://ccs.cdn.wup.shop.nintendo.net/ccs/download");
                
		String commonkey = settingsFile.getValue("common_key",null);
		if(commonkey==null || commonkey.length() != 32){
                    Logger.log("CommonKey is invalid or null.");
                        
                   primaryStage.setOnShown(new EventHandler<WindowEvent>(){
                        @Override
                        public void handle(WindowEvent event) {
                            MaterialToast toast=new MaterialToast("CommonKey is null or invalid. The titles won't download.");
                        toast.setOnToastHidden(new EventHandler<WindowEvent>(){
                            @Override
                            public void handle(WindowEvent event) {
                                //System.exit(1);
                                 //To change body of generated methods, choose Tools | Templates.
                            }
                        });
                        toast.unhide(); //To change body of generated methods, choose Tools | Templates.
                        }
                    });
                    
			//Logger.messageBox("CommonKey is invalid");
			
			
		}
		if(commonkey!=null && !commonkey.equals("null") && commonkey.length()==32)
                    try{
                    Util.commonKey =  Util.hexStringToByteArray(commonkey);
		
                    }
                catch(StringIndexOutOfBoundsException ex$){
                    
                }
                //updateCSVPath =  in.readLine();
		//in.close();
		
	}

	public static boolean deleteFolder(File element) {
	    if (element.isDirectory()) {	    	
	        for (File sub : element.listFiles()) {
	        	if(sub.isFile()){
	        		return false;
	        	}
	        }
	        for (File sub : element.listFiles()) {
	        	if(!deleteFolder(sub)) return false;
	        }	        
	    }
	    element.delete();	    
	    return true;
	}

	public static void downloadMeta(List<NUSTitleInformation> output_, final Progress totalProgress) {
		ForkJoinPool pool = ForkJoinPool.commonPool();
		List<ForkJoinTask<Boolean>> list = new ArrayList<>();
		
		for(final NUSTitleInformation nus : output_){
			final long tID = nus.getTitleID();
			list.add(pool.submit(new Callable<Boolean>(){
				@Override
				public Boolean call() throws Exception {
					NUSTitle nusa  = new NUSTitle(tID,nus.getSelectedVersion(),Util.ByteArrayToString(nus.getKey()));
					Progress childProgress = new Progress();
					
					totalProgress.add(childProgress);
					deleteFolder(new File(nusa.getLongNameFolder() + "/updates"));
					nusa.setTargetPath(nusa.getLongNameFolder());
					nusa.decryptFEntries(nusa.getFst().getMetaFolder(),childProgress);					
					return true;
				}				
			}));
		}
		for(ForkJoinTask<Boolean> task : list){
			try {
				task.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void downloadEncrypted(List<NUSTitleInformation> output_, final Progress progress) {
		ForkJoinPool pool = ForkJoinPool.commonPool();
		List<ForkJoinTask<Boolean>> list = new ArrayList<>();
		
		for(final NUSTitleInformation nus : output_){
			final long tID = nus.getTitleID();
			list.add(pool.submit(new Callable<Boolean>(){
				@Override
				public Boolean call() throws Exception {
					NUSTitle nusa  = new NUSTitle(tID,nus.getSelectedVersion(), Util.ByteArrayToString(nus.getKey()));
					Progress childProgress = new Progress();					
					progress.add(childProgress);
					nusa.downloadEncryptedFiles(progress);
							
					return true;
				}				
			}));
		}
		for(ForkJoinTask<Boolean> task : list){
			try {
				task.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public static AtomicInteger finished = new AtomicInteger(); 

	public static void downloadEncryptedAllVersions(List<NUSTitleInformation> output_, final Progress progress) {
		ForkJoinPool pool = new ForkJoinPool(25);

		List<ForkJoinTask<Boolean>> list = new ArrayList<>();
		final int outputsize = output_.size();
		for(final NUSTitleInformation nus : output_){
			final long tID = nus.getTitleID();
			list.add(pool.submit(new Callable<Boolean>(){
				@Override
				public Boolean call() throws Exception {
					int count = 1;
					for(Integer i : nus.getAllVersions()){
						NUSTitle nusa  = new NUSTitle(tID,i, Util.ByteArrayToString(nus.getKey()));
						Progress childProgress = new Progress();
						progress.add(childProgress);			
						nusa.downloadEncryptedFiles(progress);
						System.out.println("Update download progress " + "(" + nus.getLongnameEN() + ") version "+ i + " complete! This was " + count  + " of " + nus.getAllVersions().size() + "!");
						count++;
					}	
					System.out.println("Update download complete " + "(" + nus.getLongnameEN() +")" +"! Loaded updates for " +  nus.getAllVersions().size() + " version. Now are " + finished.incrementAndGet() + " of " + outputsize + " done! ");
					return true;
				}				
			}));
		}
		
		for(ForkJoinTask<Boolean> task : list){
			try {
				task.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}