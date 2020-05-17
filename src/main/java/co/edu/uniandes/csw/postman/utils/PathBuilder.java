/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.postman.utils;


import co.edu.uniandes.csw.postman.interfaces.IPostmanDir;
import java.io.File;

/**
 *
 * @author Asistente
 */
public final class PathBuilder implements IPostmanDir{
    private  File pub;
    private String PATH =  System.getProperty("user.dir").concat(File.separator).concat("collections");

    public PathBuilder() {
        this.pub = new File( getPATH());
        
    }

    @Override
    public boolean validateDir() {
               return this.getPub().isDirectory(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public File[] getFiles() {
        return getPub().listFiles();
         //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int countFiles() {
        return getPub().list().length; //To change body of generated methods, choose Tools | Templates.
    }

   

    /**
     * @return the pub
     */
    public File getPub() {
        return pub;
    }

    /**
     * @param pub the pub to set
     */
    public void setPub(File pub) {
        this.pub = pub;
    }

    /**
     * @return the PATH
     */
    public String getPATH() {
        return PATH;
    }

    /**
     * @param PATH the PATH to set
     */
    public void setPATH(String PATH) {
        this.PATH = PATH;
    }
    
    
}
