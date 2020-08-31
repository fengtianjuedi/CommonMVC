package com.wufeng.commonmvc.entity;

public class CategoryNode {
    private String id; //品种id
    private String name; //品种名
    private String parentId; //父品种id
    private int level; //节点级别
    private boolean endNode; //是否是终节点
    private boolean expand; //是否展开
    private boolean loadChild; //是否加载子节点

    public String getId(){return id;}
    public void setId(String value){id = value;}

    public String getName(){return name;}
    public void setName(String value){name = value;}

    public String getParentId(){return parentId;}
    public void setParentId(String value){parentId = value;}

    public int getLevel(){return level;}
    public void setLevel(int value){level = value;}

    public boolean isEndNode(){return endNode;}
    public void setEndNode(boolean value){endNode = value;}

    public boolean isExpand(){return expand;}
    public void setExpand(boolean value){expand = value;}

    public boolean isLoadChild(){return loadChild;}
    public void setLoadChild(boolean value){loadChild = value;}
}
