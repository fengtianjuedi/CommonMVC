package com.wufeng.latte_core.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "user_profile")
public class UserProfile {
    @Id(autoincrement = true)
    private Long id;
    private String customerId;
    private String customerName;
    @Generated(hash = 957928604)
    public UserProfile(Long id, String customerId, String customerName) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
    }
    @Generated(hash = 968487393)
    public UserProfile() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCustomerId() {
        return this.customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public String getCustomerName() {
        return this.customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
