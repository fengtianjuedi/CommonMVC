package com.wufeng.latte_core.entity;

public class CategoryInfo {
    private String id;
    private String name;
    private String batchNo;

    public CategoryInfo(){}

    public CategoryInfo(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId(){return id;}
    public void setId(String value) {this.id = value;}

    public String getName(){return name;}
    public void setName(String value) {this.name = value;}

    public String getBatchNo(){return batchNo;}
    public void setBatchNo(String value){this.batchNo = value;}
}
