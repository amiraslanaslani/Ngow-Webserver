/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author mrse
 */
public class FileExtentions {
    configFile config;

    public FileExtentions(configFile config) {
        this.config = config;
    }
    
    public String getContentType(String label) throws Exception{
        String ext = seprateFileName(label).get(1);
        if(! config.hasExtention(ext))
            return "text/plain";
        return config.getExtention(ext).get(0);
    }
    
    public String getCommand(String label) throws Exception{
        return config.getExtention(seprateFileName(label).get(1)).get(1);
    }
    
    public String getData(String path) throws IOException, InterruptedException, ConfigFileException, Exception{
        String Data = "",
        ext = seprateFileName(path).get(1),
        filePath = config.getData("RootPath") + seprateFileName(path).get(0) + seprateFileName(path).get(1);
        if(! config.hasExtention(ext)){
            Data = getSystemOutput("cat " + filePath);
        }
        else{
            String command = getCommand(path);
            command = command.replaceFirst("<FILE>", filePath);
            command = command.replaceFirst("<ARGS>", seprateFileName(path).get(2));
            
            System.out.println(command);
            Data = getSystemOutput(command);
            System.out.println("DT: " + Data);
        }
        return Data;
    }
    
    public static ArrayList<String> seprateFileName(String path) throws Exception{
        // [1] name | [2] extention | [3] args
        String[] fileN1 = new String[2];
        if(path == null)
            throw new Exception("Path is Null!!");
        if(!path.contains("?")){
            fileN1[0] = path;
            fileN1[1] = "";
        }
        else{
            fileN1 = path.split("\\?");
            if(fileN1.length < 2){
                fileN1 = new String[2];
                fileN1[0] = path.split("\\?")[0];
                fileN1[1] = "";
            }
        }
        String[] fileN2 = fileN1[0].split("\\.");
        ArrayList<String> fileName = new ArrayList<>();
        
        //System.out.println(fileN2[0]);
        if(fileN2.length == 1){ // Get Index File
            fileName.add(fileN2[0]);
            fileName.add("");
        }else{
            fileName.add(fileN2[0]);
            fileName.add("." + fileN2[1]);
        }
        fileName.add(fileN1[1]);
        return fileName;
    }
    
    private String getSystemOutput(String command) throws InterruptedException, IOException, ConfigFileException{
        String Data = "";
        Runtime runtime = Runtime.getRuntime();
        Process p = runtime.exec(command);
        p.waitFor(Integer.parseInt(config.getData("TimeOut")), TimeUnit.SECONDS);
        BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line;
        while ((line = b.readLine()) != null) {
            Data += line + "\n";
        }

        b.close();
        return Data;
    }
}
