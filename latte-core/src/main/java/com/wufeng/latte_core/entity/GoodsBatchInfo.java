package com.wufeng.latte_core.entity;

import java.util.Date;

//品种批次信息
public class GoodsBatchInfo {
    private String batchNo; //批次号
    private Date createTime; //创建时间

    public String getBatchNo(){return batchNo;}
    public void setBatchNo(String value){this.batchNo = value;}

    public Date getCreateTime(){return createTime;}
    public void setCreateTime(Date value){this.createTime = value;}
}
