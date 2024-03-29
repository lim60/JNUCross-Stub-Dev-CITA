package com.webank.wecross.stub.cita.constant;

public interface TransactionConstant {

    interface Type {
        int SEND_TRANSACTION = 101;
        int CALL_TRANSACTION = 102;
        int GET_TRANSACTION_RECEIPT = 103;
        int GET_ABI = 104;
        int GET_BLOCK_NUMBER = 105;
        int GET_BLOCK_BY_HASH = 106;
        int GET_BLOCK_BY_NUMBER = 107;
    }

    interface Result {
        int SUCCESS = 0;
        int ERROR = -1;
    }

    interface Event {
        int EVENT_NEW_BLOCK = 201;
        int EVENT_RESOURCES_CHANGED = 202;
    }
    interface STATUS {
        public static final int OK = 0;
        public static final int INTERNAL_ERROR = 100; // driver internal error
        public static final int CONNECTION_EXCEPTION = 200; // query connection exception
        public static final int ROUTER_EXCEPTION = 300; // router exception
        public static final int ACCOUNT_MANAGER_EXCEPTION = 400;
    }

}
