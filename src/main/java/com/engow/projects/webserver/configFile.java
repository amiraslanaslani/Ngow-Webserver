/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.engow.projects.webserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author mrse
 */
public class configFile {
    private File file;
    private HashMap<String,String> data = new HashMap<>();
    private HashMap<String, ArrayList<String> > extentions = new HashMap<String, ArrayList<String>>();
    
    public configFile(String fileName) throws FileNotFoundException {
        file = new File(fileName);
        
        Scanner fileScanner = new Scanner(file);
        while(fileScanner.hasNext()){
            String key = fileScanner.next();
            if(key.charAt(0) == '#'){
                fileScanner.nextLine();
                continue;
            }
            if(key.equals("ExtentionDefine")){
                ArrayList<String> ext = new ArrayList<>();
                String Extention = fileScanner.next(); // Extention
                ext.add(fileScanner.next()); // Content Type
                ext.add(fileScanner.nextLine().trim()); // Command
                extentions.put("." + Extention, ext);
            }
            else
                data.put(key, fileScanner.nextLine().trim());
        }
    }
    
    public String[] getFolderIndexes(){
        if(!data.containsKey("FolderIndex"))
            return new String[0];
        return data.get("FolderIndex").trim().split(" ");
    }
    
    public String getData(String label) throws ConfigFileException{
        if(!hasData(label))
            throw new ConfigFileException(label);
        return data.get(label);
    }
    
    public boolean hasData(String label){
        return data.containsKey(label);
    }
    
    public ArrayList<String> getExtention(String label){
        return extentions.get(label);
    }
    
    public boolean hasExtention(String label){
        return extentions.containsKey(label);
    }
}
