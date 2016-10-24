/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.olmectron.jnustoolmod.gui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.olmectron.material.utils.XMLString;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 *
 * @author Edgar
 */
public class Hex {
    public static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is,"UTF-8").useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
    }
   public static void renameFile(File f, String newName){
        try {
            // File (or directory) with old name
            File file=f;
            
// File (or directory) with new name
String a=newName;
if(f.getParent()!=null){
    a=f.getParentFile().getAbsolutePath();
    if(a.endsWith("/")){
        a=a+newName;
    }
    else{
        a=a+"/"+newName;
    }
}
File file2 = new File(a);

if (file2.exists())
    throw new java.io.IOException("file exists");

// Rename file (or directory)
boolean success = file.renameTo(file2);

if (!success) {
    // File was not successfully renamed
}       } catch (IOException ex) {
            Logger.getLogger(Hex.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
    private static final int polynom   = 0x8005;
private static final int initialValue = 0xFFFF;
public static int[] byteToInt(byte[] var){
            int[] arreglo=new int[var.length];
            for(int i=0;i<arreglo.length;i++){
                int c=var[i];
                if(c<0){
                    c = c & 0xff;
                }
                arreglo[i]=c; 
            }
            return arreglo;
        }

   
    
    public static final int NTR=0;
    public static final int TWL=1;
    private static byte[] NTR_TEMPLATE_BYTES;
    private static byte[] TWL_TEMPLATE_BYTES;
    private static byte[] AB_PATH_SETTER_BYTES;
    public static String getHexString(byte[] values){
        String total="";
        for(int i=0;i<values.length;i++){
            total+=getHexPair(values[i]);
        }
        return total;
    }
    
    public static String getHexPair(int code){
        if(code<0){
            code=code & 0xff;
        }
        if(code<0x10){
            return "0"+Integer.toHexString(code).toUpperCase();
        }
        else{
            return Integer.toHexString(code).toUpperCase();
        }
    }
    public static String getHexPairs(int code){
        String par=getHexPair(code);
        while(par.length()%2!=0){
            par="0"+par;
        }
        return par;
    }
    public static byte[] getHexBytes(int code){
        String bytes=getHexPairs(code);
        int counter=0;
        byte[] data=new byte[bytes.length()/2];
        for(int i=0;i<bytes.length();i+=2){
            data[counter]=(byte)Integer.decode("0x"+bytes.substring(i,i+2)).intValue();
            counter++;
        }
        return data;
    }
    public static int getHexNumber(byte[] bytes){
        String hex="";
        for(int i=0;i<bytes.length;i++){
            hex=hex+getHexPair(bytes[i]);
        }
        return Integer.decode("0x"+hex);
       
    }
    public static byte[] getBytesFromWord(String word){
        byte[] b = word.getBytes(StandardCharsets.UTF_8);
        return b;
    }
    public static String getWordFromPairBytes(byte[] bytes){
         String result="";
         try{
             
         
               for(int i=0;i<bytes.length;i+=2){
				String newHex=getHexPair(bytes[i+1])+getHexPair(bytes[i]);
                               
				result+=Character.toString((char)(Integer.decode("0x"+newHex).intValue()));
			}
         }
         catch(ArrayIndexOutOfBoundsException ex){
             System.err.println("Check your array length");
         }
                return result;
    }
    public static String getWordFromBytes(byte[] bytes){
         String result="";
                for(int i=0;i<bytes.length;i++){
                    
                    
                        int newChar=(int)bytes[i];
                        if(newChar<0){
                            newChar= newChar & 0xff;
                        }
                        result=result+Character.toString((char)(newChar));
                    
                }
                return result;
    }
    private int lastNTRType=0;
    
    
    public static byte[] reverse(byte[] original){
        byte[] reversed=new byte[original.length];
        int counter=0;
        for(int i=original.length-1;i>=0;i--){
            reversed[counter]=original[i];
            counter++;
        }
        return reversed;
    }
    
    //Copy the first 12 bytes from ROM to template
   
   
    public static byte[] getInternalFile(String internalPath){
        if(!internalPath.startsWith("file:") && !internalPath.startsWith("config:")){
        try {
            String path;
            InputStream inputStream;
            
                inputStream=Hex.class.getResourceAsStream(internalPath);
            
            
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            
            int nRead;
            byte[] data = new byte[16384];
            
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            
            buffer.flush();
            
            return buffer.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(Hex.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        }
        else{
            if(internalPath.startsWith("file:"))
            internalPath=internalPath.substring(5);
            else if(internalPath.startsWith("config:"))
                internalPath=internalPath.substring(7);
            try {
                return Files.readAllBytes(new File(internalPath).toPath());
            } catch (IOException ex) {
                Logger.getLogger(Hex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;    
    }
}

