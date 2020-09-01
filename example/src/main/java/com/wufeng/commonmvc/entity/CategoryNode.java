package com.wufeng.commonmvc.entity;

import androidx.annotation.NonNull;

public class CategoryNode {
    @NonNull
    private String nodeId; //节点id 必须唯一且不能为空
    private String id; //品种id
    private String name; //品种名
    private int level; //节点级别
    private boolean endNode; //是否是终节点
    private boolean expand; //是否展开

    public String getNodeId(){return nodeId;}
    public void setNodeId(String value){nodeId = value;}

    public String getId(){return id;}
    public void setId(String value){id = value;}

    public String getName(){return name;}
    public void setName(String value){name = value;}

    public int getLevel(){return level;}
    public void setLevel(int value){level = value;}

    public boolean isEndNode(){return endNode;}
    public void setEndNode(boolean value){endNode = value;}

    public boolean isExpand(){return expand;}
    public void setExpand(boolean value){expand = value;}
}
