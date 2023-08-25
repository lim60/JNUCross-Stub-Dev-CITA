package com.webank.wecross.stub.cita.contract;

import java.io.Serializable;

public class ContractParam implements Serializable {
    private String type;

    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ContractParam{" + "type='" + type + '\'' + ", value='" + value + '\'' + '}';
    }
}
