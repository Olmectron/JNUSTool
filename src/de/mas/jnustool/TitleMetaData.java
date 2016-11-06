package de.mas.jnustool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import de.mas.jnustool.util.Util;
/**
 * The TitleMetaData (TMD) stores information of the NUSTitle. [...]
 * 
 * 
 * Thanks to crediar for the offsets in CDecrypt
 * @author Maschell
 *
 */
public class TitleMetaData {
	//TODO: cleanup
	int				signatureType;									// 0x000
	byte[]			signature 			= 	new byte[0x100];		// 0x004
	byte[]			issuer 				=	new byte[0x40];			// 0x140
	byte			version;										// 0x180
	byte			CACRLVersion;									// 0x181
	byte			signerCRLVersion;								// 0x182
	long			systemVersion;									// 0x184
	long			titleID;										// 0x18C 	
	int				titleType;										// 0x194 	
	short			groupID;										// 0x198 
	byte[]			reserved 			= 	new byte[62];			// 0x19A 	
	int				accessRights;									// 0x1D8	
	short			titleVersion;									// 0x1DC 
	short			contentCount;									// 0x1DE 
	short 			bootIndex;										// 0x1E0	
	byte[]			SHA2 				= 	new byte[32];			// 0x1E4
	ContentInfo[] 	contentInfos		= 	new ContentInfo[64];	// 0x1E4
	Content[] 		contents;										// 0x1E4 
	byte[]          cert1                 =  new byte[0x400];
	byte[]          cert2                 =  new byte[0x300];

	private NUSTitle nus;
	
	private long totalContentSize;
	
	public TitleMetaData(File tmd) throws IOException {
		parse(tmd);
		setTotalContentSize();
	}

	public TitleMetaData(byte[] downloadTMDToByteArray) throws IOException  {
		if(downloadTMDToByteArray != null){			
			File tempFile;
			tempFile = File.createTempFile("jnustool",".tmp");				
			FileOutputStream fos = new FileOutputStream(tempFile);
			fos.write(downloadTMDToByteArray);
			fos.close();
			parse(tempFile);
			setTotalContentSize();
			
		}else{
			Logger.log("Invalid TMD");
			throw new IllegalArgumentException("Invalid TMD");
		}
	}

	/**
	 * Parses a TMD file
	 * @param tmd 
	 * @throws IOException
	 */
	private void parse(File tmd) throws IOException {
		
		RandomAccessFile f = new RandomAccessFile(tmd, "r");		
		f.seek(0);
		this.signatureType = f.readInt();
		
		f.read(signature, 0, 0x100);
		f.seek(0x140);
		f.read(issuer, 0, 0x40);
		
		f.seek(0x180);
		this.version = f.readByte();
		this.CACRLVersion = f.readByte();
		this.signerCRLVersion = f.readByte();
		f.seek(0x184);
		this.systemVersion = f.readLong();
		this.titleID = f.readLong();
		this.titleType = f.readInt();
		this.groupID = f.readShort();
		f.seek(0x19A);
		f.read(reserved, 0, 62);
		f.seek(0x1D8);
		this.accessRights = f.readInt();
		this.titleVersion = f.readShort();
		this.contentCount = f.readShort();
		this.bootIndex = f.readShort();
		f.seek(0x1E4);
		f.read(SHA2, 0, 32);
		f.seek(0x204);
				
		short indexOffset;
		short commandCount;
		
		for(int i =0;i<64;i++){
			f.seek(0x204+(0x24*i));
			indexOffset =f.readShort();
			commandCount =f.readShort();
			byte[] buffer = new byte[0x20];	//  16    0xB14
			f.read(buffer, 0, 0x20);
			this.contentInfos[i] = new ContentInfo(indexOffset,commandCount,buffer);
		}
		this.contents = new Content[contentCount];
		
		int 	ID;						//	0	 0xB04
		short	index;					//	4    0xB08
		short 	type;					//	6	 0xB0A
		long	size;					//	8	 0xB0C
		
		
		for(int i =0;i<contentCount;i++){
			f.seek(0xB04+(0x30*i));
			ID = f.readInt();
			index = f.readShort();
			type = f.readShort();
			size = f.readLong();
			byte[] buffer = new byte[0x20];	//  16    0xB14
			f.read(buffer,0, 0x20);
			
			this.contents[i] = new Content(ID,index,type,size,buffer,this);
		}
		
		if(f.read(cert2,0, 0x300) != 0x300){
            Logger.log("Error reading TMD cert2");
        }
		if(f.read(cert1,0, 0x400) != 0x400){
            Logger.log("Error reading TMD cert1");
		}
		f.close();
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("signatureType:		"	+ signatureType +"\n");
		sb.append("signature:		"	+ Util.ByteArrayToString(signature) +"\n");
		sb.append("issuer:			"  	+ Util.ByteArrayToString(issuer) +"\n");
		sb.append("version:		"  	+ version +"\n");
		sb.append("CACRLVersion:		" 	+ CACRLVersion +"\n");
		sb.append("signerCRLVersion:	" 	+ signerCRLVersion +"\n");
		sb.append("systemVersion:		"  	+ String.format("%8X",systemVersion) +"\n");
		sb.append("titleID:		"  	+ String.format("%8X",titleID) +"\n");
		sb.append("titleType:		"  	+ titleType +"\n");
		sb.append("groupID:		"  	+ groupID +"\n");
		sb.append("reserved:		"  	+ Util.ByteArrayToString(reserved) +"\n");
		sb.append("accessRights:		"  	+ accessRights +"\n");
		sb.append("titleVersion:		"  	+ titleVersion +"\n");
		sb.append("contentCount:		"  	+ contentCount +"\n");
		sb.append("bootIndex:		"  	+ bootIndex +"\n");
		sb.append("SHA2:			"  	+ Util.ByteArrayToString(SHA2) +"\n");
		sb.append("cert1:			"  	+ Util.ByteArrayToString(cert1) +"\n");
		sb.append("cert2:           "   + Util.ByteArrayToString(cert2) +"\n");
		sb.append("contentInfos:		\n");
		
		for(int i = 0; i<contents.length-1;i++){
			sb.append("		" + contentInfos[i] +"\n");
		}
		sb.append("contents:			\n");
		for(int i = 0; i<contents.length-1;i++){
			sb.append("		" + contents[i] +"\n");
		}
		return sb.toString();		
	}
	
	/**
	 * Calculates the total size of the encrypted content file
	 */
	public void setTotalContentSize(){
		this.totalContentSize = 0;
		for(int i = 0; i <contents.length-1;i++){
			this.totalContentSize += contents[i].size;
		}
	}	
	
	/**
	 *  Size of all encrypted contents (Without tmd,cetk and h3 files)
	 * @return Size of contents
	 */
	public long getTotalContentSize() {
		return totalContentSize;
	}
	
	/**
	 * Checks of the title is an update
	 * @return true if its an update
	 */
	public boolean isUpdate() {
		return (titleID & 0x5000E00000000L)  == 0x5000E00000000L;
	}
	
	/**
	 * Downloads all content files (encrypted) 
	 * @param progress: A progress object can be used to get informations of the progress. Will be ignored when null is used. 
	 * @throws IOException
	 */	
        public void stopDownload(){
            if(pool!=null){
                pool.shutdownNow();
            }
        }
        private ForkJoinPool pool;
	public void downloadContents(Progress progress) throws IOException{
		
		String tmpPath = getContentPath();
		File f = new File(tmpPath);
		if(!f.exists())f.mkdir();
		
		pool = ForkJoinPool.commonPool();
                
		List<ContentDownloader> dlList = new ArrayList<>();
		for(Content c : contents){
                    
			dlList.add(new ContentDownloader(c,progress));			
		}
		pool.invokeAll(dlList);
		Logger.log("Done!");
			
	}

	/**
	 * Returns the path of the folder where the encrypted content files are stored
	 * @return path of contents
	 */
	public String getContentPath() {		
		return nus.getContentPath();
	}

	/**
	 * Returns the instance of the NUSTitle of this TMD
	 * @return NUSTitle
	 */
	public NUSTitle getNUSTitle() {
		return nus;
	}

	/**
	 * Sets the NUSTitle of this TMD
	 * @param nus The NUStitle that will be set
	 */
	public void setNUSTitle(NUSTitle nus) {
		this.nus = nus;
	}

}