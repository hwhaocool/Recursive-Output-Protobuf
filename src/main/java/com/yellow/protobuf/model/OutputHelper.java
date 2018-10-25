package com.yellow.protobuf.model;

import java.util.Hashtable;
import java.util.List;

import sun.util.logging.resources.logging;

public class OutputHelper {

    /**
     * <br>当前的 变量列表
     */
    private Hashtable<String, List<String>> currentVarList;
    
    /**
     * <br>当前的导入列表
     */
    private List<String> currentImportList;
    
    /**
     * <br>当前的文件名
     */
    private String currentFileName;
    
    public OutputHelper() {
        
    }

    /**
     * @return 得到 currentVarList
     */
    public Hashtable<String, List<String>> getCurrentVarList() {
        return currentVarList;
    }

    /**
     * @param currentVarList 设置 currentVarList
     */
    public void setCurrentVarList(Hashtable<String, List<String>> currentVarList) {
        this.currentVarList = currentVarList;
    }

    /**
     * @return 得到 currentImportList
     */
    public List<String> getCurrentImportList() {
        return currentImportList;
    }

    /**
     * @param currentImportList 设置 currentImportList
     */
    public void setCurrentImportList(List<String> currentImportList) {
        this.currentImportList = currentImportList;
    }

    /**
     * @return 得到 currentFileName
     */
    public String getCurrentFileName() {
        return currentFileName;
    }

    /**
     * @param currentFileName 设置 currentFileName
     */
    public void setCurrentFileName(String currentFileName) {
        this.currentFileName = currentFileName;
    }
    
    public static void log(String format, Object... args) {
        System.out.println(String.format(format, args));
    }
    
    @Override
    public String toString() {
        return "OutputModel [currentVarList=" + currentVarList + ", currentImportList=" + currentImportList
                + ", currentFileName=" + currentFileName + "]";
    }
}
