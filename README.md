# Recursive-Output-Protobuf
Recursive Output Protobuf

## 功能
因为在写`protobuf`的时候，可以依赖其他对象，其他对象又可以一直依赖下去  
有时在公司协同合作的时候，需要把这些对象全部列出来  
人工操作太繁琐，且容易遗漏  
所以写了这个工具  

## 使用方法
输入一个存放`protobuf`文件的文件夹路径  
然后点击分析  
两个树形控件就可以显示出`文件列表`和`变量列表`  
点击某个想看的变量，最右侧用来输出的文本域就可以显示啦  

## 编译打包
`mvn clean package`

## 运行
`java -jar recusive-output-protobuf-1.0.0.jar`

## 界面

![截图]( https://github.com/hwhaocool/Recursive-Output-Protobuf/raw/master/picture/example.png )

## 已知BUG

`变量列表`点快了，程序会没有输出
`变量列表` 有的变量，点了之后，程序其实找到了结果，给文本域设置的时候，不知道为什么会不生效

## 待实现功能
目前界面上的搜索框功能是没有实现的  
后面有空想实现一下

