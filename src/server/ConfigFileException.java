package server;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mrse
 */

public class ConfigFileException extends Exception {
   public String missing;
   
   public ConfigFileException(String missing) {
      this.missing = missing;
   }
   
   public String getMissingName() {
      return missing;
   }
}