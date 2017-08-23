package server;

import java.io.FileNotFoundException;

public class Server {
    public static void main(String args[]) {
        try {
            configFile config = new configFile("Ngow.config");
            //System.out.println(config.getData("Port") + "\n" + config.getData("RootPath"));
            
            Responser rsp = new Responser(config);
            rsp.start();
        } catch (FileNotFoundException ex) {
            System.err.println("Config File Not Found! This is not exists or not readable. (Ngow.config)");
        } catch (ConfigFileException mis) {
            System.err.println("Config file has not \"" + mis.missing + "\" key.");
        }
    }
}