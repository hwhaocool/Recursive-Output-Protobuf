package com.yellow.protobuf;

import java.util.Arrays;
import java.util.List;

public class Constant {

    public static final String TYPE_MESSAGE = "message";
    
    public static final String TYPE_ENUM = "enum";
    
    public static final String TYPE_IMPORT = "import";
    
    public static final String CHANGE_LINE = "\n";
    
    public static final List<String> BASE_TYPE = Arrays.asList(
            "string", "int32", "int64", "double");
}
