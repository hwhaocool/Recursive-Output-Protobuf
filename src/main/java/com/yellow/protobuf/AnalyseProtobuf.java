package com.yellow.protobuf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import com.yellow.protobuf.model.FileResult;

public class AnalyseProtobuf {

    private Hashtable<String, FileResult> fileRootNode = new Hashtable<>();
    
    private Hashtable<String, Object> varRootNode = new Hashtable<>();
    
    private Pattern msgPattern = Pattern.compile("^\\s*message\\s+([a-zA-Z_0-9]+)\\s*.*$");
    
    public static Pattern enumPattern = Pattern.compile("^\\s*enum\\s+([a-zA-Z_0-9]+)\\s*.*$");
    
    private Pattern importPattern = Pattern.compile("^\\s*import\\s+\\\"(.+)\\\".+$");
    
    private Pattern endMarkPattern = Pattern.compile("^[^}]*}\\s*$");
    
    public AnalyseProtobuf(String path) {
        analysis(path);
    }
    
    /**
     * @return 得到 fileRootNode
     */
    public Hashtable<String, FileResult> getFileRootNode() {
        return fileRootNode;
    }

    /**
     * @param fileRootNode 设置 fileRootNode
     */
    public void setFileRootNode(Hashtable<String, FileResult> fileRootNode) {
        this.fileRootNode = fileRootNode;
    }

    /**
     * @return 得到 varRootNode
     */
    public Hashtable<String, Object> getVarRootNode() {
        return varRootNode;
    }

    /**
     * @param varRootNode 设置 varRootNode
     */
    public void setVarRootNode(Hashtable<String, Object> varRootNode) {
        this.varRootNode = varRootNode;
    }

    private void analysis(String path) {
        File file = new File(path);
        
        if (!file.exists()) {
            String errorMsg = "path is not exist : " + path;
            System.out.println(errorMsg);
            JOptionPane.showMessageDialog(null, errorMsg);
            return;
        }
        
        if ( ! file.isDirectory()) {
            String errorMsg = "path is not directory : " + path;
            System.out.println(errorMsg);
            JOptionPane.showMessageDialog(null, errorMsg);
            return;
        }
        
        for(File tempFile : file.listFiles()) {
            String name = tempFile.getName();
            System.out.println("start analysis file " + name);
            
            FileResult readFileByLines = readFileByLines(tempFile);
            
            fileRootNode.put(name, readFileByLines);
        }
        
    }
    
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    private FileResult readFileByLines(File file) {
        
        String name = file.getName();
        
        Hashtable<String, List<String>> varList = new Hashtable<>();
        List<String> importList = new ArrayList<>();
        
        BufferedReader reader = null;
        try {
            
            reader = new BufferedReader(new FileReader(file));
            
            String tempString = null;
            
            //行号
            int line = 1;
            
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                line++;
                
                tempString = tempString.trim();
                
                if (tempString.startsWith(Constant.TYPE_IMPORT)) {
                    String importName = getImportName(tempString);
                    importList.add(importName);
                    continue;
                } else if (tempString.startsWith(Constant.TYPE_MESSAGE)) {
                    String var = getVarName(tempString);
                    
                    System.out.println(var);
                    varList.put(var, findUntilEndMarkMatch(tempString, reader));
                    
                } else if (tempString.startsWith(Constant.TYPE_ENUM)) {
                    String enumName = getEnumName(tempString);
                    
                    System.out.println(enumName);
                    varList.put(enumName, findUntilEndMarkMatch(tempString, reader));
                }
                
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        
        FileResult fileResult = new FileResult();
        fileResult.setImportList(importList);
        fileResult.setVarList(varList);
        
        return fileResult;
    }
    
    private String getVarName(String tempString) {
        Matcher matcher = msgPattern.matcher(tempString);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    private String getEnumName(String tempString) {
        Matcher matcher = enumPattern.matcher(tempString);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    private String getImportName(String tempString) {
        Matcher matcher = importPattern.matcher(tempString);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    private List<String> findUntilEndMarkMatch(String tempString,  BufferedReader reader)
            throws IOException {
        
        List<String> messageContent = new ArrayList<>();
        //第一行
        messageContent.add(tempString);
        
        String msgString = null;
        while ((msgString = reader.readLine()) != null) {
            Matcher matcher = endMarkPattern.matcher(msgString);
            
            messageContent.add(msgString);
            
            if (matcher.matches()) {
                break;
            } 
        }
        
        return messageContent;
    }
}
