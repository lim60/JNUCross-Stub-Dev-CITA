package com.webank.wecross.stub.cita.contract;

public class ContractCall {
    private String contract;

    private String data;

    private String sender = "0x0000000000000000000000000000000000000000";

    public ContractCall() {}

    public ContractCall(String contract, String data) {
        this.contract = contract;
        this.data = data;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
