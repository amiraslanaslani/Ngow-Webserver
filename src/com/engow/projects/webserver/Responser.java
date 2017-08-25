package com.engow.projects.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author mrse
 */
public class Responser {
    int port;
    String rootPath;
    configFile config;
    FileExtentions fileExtentions;

    public Responser(configFile config) throws ConfigFileException {
        this.port = Integer.parseInt(config.getData("Port"));
        this.rootPath = config.getData("RootPath");
        this.config = config;
        fileExtentions = new FileExtentions(config);
    }
    
    void start(){
        try {
            // Get the port to listen on
            // Create a ServerSocket to listen on that port.
            ServerSocket ss = new ServerSocket(port);
            // Now enter an infinite loop, waiting for & handling connections.
            while(true) {
                HashMap<String,String> RequestData = new HashMap<>();
                // Wait for a client to connect. The method will block;
                // when it returns the socket will be connected to the client
                Socket client = ss.accept();

                // Get input and output streams to talk to the client
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream());

                // Collect Client Data
                Scanner clientInputScanner = new Scanner(client.getInputStream());
                int counter = 0;
                CollectClientData:
                while(clientInputScanner.hasNextLine()){
                    counter ++;
                    String line;
                    line = clientInputScanner.nextLine();
                    if(line.length() == 0)
                        break CollectClientData;
                    Scanner clientInputLineReader = new Scanner( line );
                    
                    if(counter == 1){
                        RequestData.put("RequestType", clientInputLineReader.next());
                        RequestData.put("RequestedPath", clientInputLineReader.next());
                        RequestData.put("ConnectionProtocol", clientInputLineReader.next());
                    }
                    else{
                        RequestData.put("Request" + clientInputLineReader.next(), clientInputLineReader.nextLine().trim());
                    }
                }
                
                
                System.out.println(RequestData);
                FileStatus page = openFile( RequestData.get("RequestedPath") );
                
//                for(String t : RequestData.keySet())
//                    System.out.println(t + ":[" + RequestData.get(t) + "]");

                // Start sending our reply, using the HTTP 1.1 protocol
                out.print("HTTP/1.1 " + page.status + " \r\n"); // Version & status code
                if(! page.contentType.equals("SELF_HEADER")){ // If program don't have header
                    out.print("Status: " + page.stat);
                    out.print("Content-Type: " + page.contentType + "; charset=UTF-8\r\n");
                    out.print("Connection: close\r\n"); // Will close stream
                    out.print("\r\n"); 
                }
                // End of headers

                // Write Main Content
                out.print(page.data);

                // Close socket, breaking the connection to the client, and
                // closing the input and output streams
                out.close(); // Flush and close the output stream
                in.close(); // Close the input stream
                client.close(); // Close the socket itself
            } // Now loop again, waiting for the next connection
        }
        // If anything goes wrong, print an error message
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    FileStatus openFile( String Path ) throws IOException, InterruptedException, ConfigFileException, Exception{
        String[] tree = FileExtentions.seprateFileName(Path).get(0).split("/");
        System.out.println(":: " + Arrays.toString(tree));
        
        File file = new File(rootPath + FileExtentions.seprateFileName(Path).get(0) + FileExtentions.seprateFileName(Path).get(1));
        System.out.println(rootPath + FileExtentions.seprateFileName(Path).get(0) + FileExtentions.seprateFileName(Path).get(1));
        
        // Set FolderIndex
        if(file.isDirectory()){
            System.out.println("Dir");
            boolean indexFind = false;
            for(String indexName : config.getFolderIndexes()){
                File index = new File(rootPath + FileExtentions.seprateFileName(Path).get(0) + FileExtentions.seprateFileName(Path).get(1) + "/" + indexName);
                if(index.exists()){
                    Path = FileExtentions.seprateFileName(Path).get(0) + FileExtentions.seprateFileName(Path).get(1) + "/" + indexName;
                    file = index;
                    indexFind = true;
                    break;
                }
            }
            if(!indexFind && config.getData("DirectoryListing").equals("Yes"))
                return getDirectoryList(Path);
        }
        System.out.println(Path);
        
        if(!file.exists())
            return getError(Path,404);
        if(!file.canRead())
            return getError(Path,403);
        
        return new FileStatus(200,fileExtentions.getContentType(Path),fileExtentions.getData(Path),"200 OK");
    }
    
    FileStatus getError(String Path, int ErrorNumber){
        String Title = "Error " + ErrorNumber,
               Status = "";
        switch(ErrorNumber){
            case 404:
                Title = "Page Not Found 404!";
                Status = "404 Not Found";
                break;
            case 403:
                Title = "Permision Denied 403!";
                Status = "403 Forbidden";
                break;
        }
        String data = "<h1>" + Title + "</h1><p>" + ErrorNumber + " for file at " + Path + "</p><hr>Ngow HTTP Server ;)";
        return new FileStatus(ErrorNumber,"text/html",data,Status);
    }
    
    FileStatus getDirectoryList(String Path){
        return new FileStatus(200,"text/html","<h1>Directory Index</h1>","200 OK");
    }
}
