package com.yellow.protobuf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.yellow.protobuf.model.FileResult;
import com.yellow.protobuf.model.OutputHelper;

public class YellowPanel {
    
    private JPanel pathPanel;
    private JPanel searchPanel;
    private Container contentPane;
    
    private JTree fileTree;
    private JTree varTree;
    private JTextArea output;
    
    private AnalyseProtobuf analyseProtobuf;
    
    private RecursiveOutput rOutput;

    public static void main(String[] args) {
        
        new YellowPanel();

    }
    
    public YellowPanel() {
        genPathPanel();
        
        JFrame frame = new JFrame("Recursive Output Protobuf");
        this.contentPane = frame.getContentPane();
        contentPane.setBackground(Color.LIGHT_GRAY);
     
        contentPane.add(genInputPanel(), BorderLayout.NORTH);
      //添加树形控件
        contentPane.add(genTreePanel(), BorderLayout.CENTER);
//        
        
        frame.setSize(800,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(200, 200);
        frame.setVisible(true);
    }
    
    /**
     * <br>生成路径面板
     * @author YellowTail
     * @since 2018-12-26
     */
    private void genPathPanel() {
        
        JPanel pathPanel = new JPanel();
//        pathPanel.setBackground(Color.yellow);
        
        JLabel pathLabel = new JLabel("path");
        JTextField path = new JTextField(50);
        JButton aButton = new JButton("Analyse");
        
        pathPanel.add(pathLabel, BorderLayout.WEST);
        pathPanel.add(path, BorderLayout.CENTER);
        pathPanel.add(aButton, BorderLayout.EAST);
        
        aButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                String text = path.getText();
                if (null == text || text.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "path can't be empty");
                    return;
                }
                
                //path面板置灰，不允许点多次
                path.setEditable(false);
                aButton.setEnabled(false);
                
                //搜索面板 取消置灰
                searchPanel.getComponent(1).setEnabled(true);
                searchPanel.getComponent(2).setEnabled(true);
                
                analyseProtobuf = new AnalyseProtobuf(text);
                
                rOutput = new RecursiveOutput(analyseProtobuf);
                
                fillFileTreePanel();
            }
        });
        
        this.pathPanel = pathPanel;
    }
    
    private void genSearchPanel() {
        JPanel searchPanel = new JPanel();
        
        JLabel keyLabel = new JLabel("key");
        
        JTextField textField = new JTextField(20);
        textField.setText("the key which you want to search");
        textField.setEnabled(false);
     
        JButton button = new JButton("Search");
        button.setEnabled(false);
        
        searchPanel.add(keyLabel, BorderLayout.WEST);
        searchPanel.add(textField, BorderLayout.CENTER);
        searchPanel.add(button, BorderLayout.EAST);
        
        this.searchPanel = searchPanel;
    }
    
    /**
     * <br>得到输入面板
     * @return
     * @author YellowTail
     * @since 2018-12-26
     */
    private JPanel genInputPanel() {
        genPathPanel();
        genSearchPanel();
        
        JPanel inputPanel = new JPanel();
        BoxLayout lo = new BoxLayout(inputPanel, BoxLayout.Y_AXIS);
        inputPanel.setLayout(lo);
        
        inputPanel.add(this.pathPanel);
        inputPanel.add(this.searchPanel);
        
        return inputPanel;
    }
    
    /**
     * <br>生成树形面板
     * @return
     * @author YellowTail
     * @since 2018-12-26
     */
    private JPanel genTreePanel() {
        JPanel treePanel = new JPanel();
        
        GridLayout gridLayout = new GridLayout(1, 3);
        treePanel.setLayout(gridLayout);
        
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("Root");
        
        //左侧第一个，文件列表树
        this.fileTree = new JTree(node1);
        
        TitledBorder bFile = BorderFactory.createTitledBorder("File List");
        fileTree.setBorder(bFile);
        
        fileTree.addTreeSelectionListener(new TreeSelectionListener() {
            
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
                
                String fileName = (String) selectedNode.getUserObject();
                System.out.println("select file is " + fileName);
                
                fillVarTreePanel(fileName);
            }
        });
        
        //左侧第二个，变量列表树
        DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("Root");
        this.varTree = new JTree(node2);
        TitledBorder bVar = BorderFactory.createTitledBorder("Var List");
        varTree.setBorder(bVar);
        
        varTree.addTreeSelectionListener(new TreeSelectionListener() {
            
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) varTree.getLastSelectedPathComponent();
                String varName = (String) selectedNode.getUserObject();
                
                System.out.println("select var is " + varName);
                
                DefaultMutableTreeNode selectedFileNode = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
                
                String fileName = (String) selectedFileNode.getUserObject();
                
                fillOutput(fileName, varName);
            }
        });
        
        //左侧第三个，输出的文本域
        this.output = new JTextArea("xixi");
        TitledBorder bO = BorderFactory.createTitledBorder("Output");
        output.setBorder(bO);
        output.setEditable(false);
        
        //带滚动条的面板
        JScrollPane jScrollPane = new JScrollPane(output);
        
        treePanel.add(new JScrollPane(fileTree));
        treePanel.add(new JScrollPane(varTree));
        treePanel.add(jScrollPane);
        
        
        return treePanel;
    }
    
    private void fillFileTreePanel() {
        
        
        DefaultTreeModel model = (DefaultTreeModel) this.fileTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        
        Hashtable<String, FileResult> fileRootNode = analyseProtobuf.getFileRootNode();
        
        for(String key : fileRootNode.keySet()) {
            DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(key);
            model.insertNodeInto(tempNode, root, 0);
            
            fileTree.scrollPathToVisible(new TreePath(tempNode.getPath()) );
        }
        
    }
    
    private void fillVarTreePanel(String fileName) {
        
        
        DefaultTreeModel model = (DefaultTreeModel) this.varTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        
        Hashtable<String, FileResult> fileRootNode = analyseProtobuf.getFileRootNode();
        
        FileResult fileResult = fileRootNode.get(fileName);
        Hashtable<String, List<String>> varList = fileResult.getVarList();
        
        for(String key : varList.keySet()) {
            DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(key);
            model.insertNodeInto(tempNode, root, 0);
            
            varTree.scrollPathToVisible(new TreePath(tempNode.getPath()) );
        }
        
    }
    
    private void fillOutput(String fileName, String varName) {
        
        String outputResult = rOutput.output(fileName, varName);
        
        output.setText(outputResult);
        
    }

}

