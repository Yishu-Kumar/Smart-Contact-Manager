package org.springboot.smartcontactmanager.entites;

import javax.persistence.*;

@Entity
@Table(name = "Orders")
public class MyOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "My_Order_Id")
    private long myOrderId;

    @Column(name = "Order_Id")
    private String orderId;

    @Column(name = "Amount")
    private String amount;

    @Column(name = "Receipt")
    private String receipt;

    @Column(name = "Status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "User_Id")
    private User user;

    @Column(name = "Payment_Id")
    private String paymentId;

    public MyOrder() {

    }

    public MyOrder(long myOrderId, String orderId, String amount, String receipt, String status, User user, String paymentId) {
        this.myOrderId = myOrderId;
        this.orderId = orderId;
        this.amount = amount;
        this.receipt = receipt;
        this.status = status;
        this.user = user;
        this.paymentId = paymentId;
    }

    public long getMyOrderId() {
        return myOrderId;
    }

    public void setMyOrderId(long myOrderId) {
        this.myOrderId = myOrderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public String toString() {
        return "MyOrder[myOrderId = " + myOrderId + ", orderId = " + orderId + ", amount = " + amount + ", receipt = " +
               receipt + ", status = " + status + ", paymentId = " + paymentId + "]";
    }
}
