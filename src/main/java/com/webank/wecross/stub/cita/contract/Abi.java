package com.webank.wecross.stub.cita.contract;

import java.util.List;

public class Abi {
    private String name;
    private List<AbiFunctionType> inputTypes;
    private List<AbiFunctionType> outputTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AbiFunctionType> getInputTypes() {
        return inputTypes;
    }

    public void setInputTypes(List<AbiFunctionType> inputTypes) {
        this.inputTypes = inputTypes;
    }

    public List<AbiFunctionType> getOutputTypes() {
        return outputTypes;
    }

    public void setOutputTypes(List<AbiFunctionType> outputTypes) {
        this.outputTypes = outputTypes;
    }
}
