/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.HashMap;

/**
 *
 * @author mrse
 */
public class FileStatus {

    public FileStatus(int status,String contentType,String data,String stat) {
        this.status = status;
        this.contentType = contentType;
        this.data = data;
        this.stat = stat;
    }
    
    int status;
    String contentType;
    String data;
    String stat;
}
