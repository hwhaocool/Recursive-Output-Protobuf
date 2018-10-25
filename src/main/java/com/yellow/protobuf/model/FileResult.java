package com.yellow.protobuf.model;

import java.util.Hashtable;
import java.util.List;

public class FileResult {

    private List<String> importList;
    
    private Hashtable<String, List<String>> varList;
    
    public FileResult() {
        
    }

    /**
     * @return 得到 importList
     */
    public List<String> getImportList() {
        return importList;
    }

    /**
     * @param importList 设置 importList
     */
    public void setImportList(List<String> importList) {
        this.importList = importList;
    }

    /**
     * @return 得到 varList
     */
    public Hashtable<String, List<String>> getVarList() {
        return varList;
    }

    /**
     * @param varList 设置 varList
     */
    public void setVarList(Hashtable<String, List<String>> varList) {
        this.varList = varList;
    }

    @Override
    public String toString() {
        return "FileResult [importList=" + importList + ", varList=" + varList + "]";
    }
    
    
}
