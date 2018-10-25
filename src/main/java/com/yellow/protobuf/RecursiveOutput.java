package com.yellow.protobuf;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yellow.protobuf.model.FileResult;
import com.yellow.protobuf.model.OutputHelper;

public class RecursiveOutput {

    private Hashtable<String, FileResult> fileRootNode;
    
    /**
     * <br>缓存，fileName--varName--output
     */
    private HashMap<String, HashMap<String, String> > cache = new HashMap<>();
    
    
    private Pattern typePattern = Pattern.compile("^\\s*([a-zA-Z_0-9]+)\\s+.+$");
    
    private Pattern repeatedTypePattern = Pattern.compile("^\\s*repeated\\s+([a-zA-Z_0-9]+)\\s+.+$");
    
    public RecursiveOutput(AnalyseProtobuf analyseProtobuf) {
        fileRootNode = analyseProtobuf.getFileRootNode();
    }
    
    public String output(String fileName, String varName) {
        OutputHelper.log("output, file is %s, var is %s", fileName, varName);
        
        if (!cache.containsKey(fileName)) {
            cache.put(fileName, new HashMap<>());
        }
        
        if (cache.get(fileName).containsKey(varName)) {
            return cache.get(fileName).get(varName);
        }
        
        FileResult fileResult = fileRootNode.get(fileName);
        
        Hashtable<String, List<String>> currentVarList = fileResult.getVarList();
        
        if (! currentVarList.containsKey(varName)) {
            //当前文件不存在该 var
            return "";
        } else {
            //避免死循环
            cache.get(fileName).put(varName, "");
        }
        
        List<String> list = currentVarList.get(varName);
        
        //判断一下当前对象是什么类型的
        boolean isEnum = isEnum(list.get(0));
        
        //自己的信息
        StringBuilder sb = new StringBuilder();
        
        //嵌套的其他对象的信息
        StringBuilder sb2 = new StringBuilder();
        
        //生成递归过程中的临时对象
        OutputHelper outputModel = new OutputHelper();
        outputModel.setCurrentFileName(fileName);
        outputModel.setCurrentImportList(fileResult.getImportList());
        outputModel.setCurrentVarList(currentVarList);
        
        for(String tempString: list) {
            
            sb.append(tempString).append(Constant.CHANGE_LINE);
            
            if (isEnum) {
                continue;
            }
            
            //现在是message
            if (! tempString.startsWith(Constant.TYPE_MESSAGE)) {
                //不是开头，开始要判断了
                Matcher matcher = repeatedTypePattern.matcher(tempString.trim());
                if (matcher.matches()) {
                    String currentType = matcher.group(1);
                    
                    output(tempString, sb2, currentType, outputModel);
                    
                } else {
                    matcher = typePattern.matcher(tempString.trim());
                    if (matcher.matches()) {
                        String currentType = matcher.group(1);
                        
                        output(tempString, sb2, currentType, outputModel);
                    } else {
                        //无法解析
                    }
                }
            }
        }
        
        return sb.append(sb2).toString();
    }
    
    private void output(String tempString, StringBuilder sb2, String currentType, OutputHelper outputModel) {
        
        String currentFileName = outputModel.getCurrentFileName();
        Hashtable<String, List<String>> currentVarList = outputModel.getCurrentVarList();
        List<String> currentImportList = outputModel.getCurrentImportList();
        
        if ( Constant.BASE_TYPE.contains(currentType.toLowerCase())) {
            //基本类型 不用递归查找
            return;
        } 
        
        //recursive find
        
        //先在当前文件找
        Set<String> keySet = currentVarList.keySet();
        if (keySet.contains(currentType)) {
            //当前文件有匹配的
            if (isCacheMatch(currentFileName, currentType)) {
                sb2.append(cache.get(currentFileName).get(currentType));
            } else {
                //没有缓存过
                sb2.append(output(currentFileName, currentType));
            }
        } else {
            //到import 文件找
            if (null == currentImportList || currentImportList.isEmpty()) {
                //没有import，置空
                System.out.println(String.format(
                        "find %s in file %s, while this file don't contains and the file have no import",
                        currentType, currentFileName));
                
            } else {
                
                for(String importFileName: currentImportList) {
                    String output = output(importFileName, currentType);
                    if (!output.isEmpty()) {
                        //find
                        sb2.append(output);
                        return;
                    }
                }
            }
            
        }
        
    }
    
    /**
     * <br>是否已缓存
     * @param varName
     * @return
     * @author YellowTail
     * @since 2018-10-25
     */
    private boolean isCacheMatch(String fileName, String varName) {
        if (!cache.containsKey(fileName)) {
            return false;
        }
        
        if (cache.get(fileName).containsKey(varName)) {
            return true;
        }
        
        return false;
    }
    
    private boolean isEnum(String tempString) {
        Matcher matcher = AnalyseProtobuf.enumPattern.matcher(tempString);
        return matcher.matches();
    }
    
    
}
