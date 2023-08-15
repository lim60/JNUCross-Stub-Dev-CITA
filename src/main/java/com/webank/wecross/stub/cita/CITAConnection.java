package com.webank.wecross.stub.cita;

import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameterName;
import com.citahub.cita.protocol.core.DefaultBlockParameterNumber;
import com.citahub.cita.protocol.core.methods.request.Call;
import com.citahub.cita.protocol.core.methods.response.AppBlock;
import com.citahub.cita.protocol.core.methods.response.AppBlockNumber;
import com.citahub.cita.protocol.core.methods.response.AppGetTransactionReceipt;
import com.citahub.cita.protocol.core.methods.response.AppSendTransaction;
import com.citahub.cita.protocol.http.HttpService;
import com.citahub.cita.utils.Numeric;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moandjiezana.toml.Toml;
import com.webank.wecross.stub.Connection;
import com.webank.wecross.stub.Request;
import com.webank.wecross.stub.Response;
import com.webank.wecross.stub.cita.constant.TransactionConstant;
import com.webank.wecross.stub.cita.contract.ContractCall;
import com.webank.wecross.stub.cita.util.InternalBlock;
import com.webank.wecross.stub.cita.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class CITAConnection implements Connection {

    private static final Logger logger = LoggerFactory.getLogger(CITAConnection.class);

    private CITAj citAj;

    private ConnectionEventHandler eventHandler = null;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public CITAConnection(){

    }

    public CITAConnection(String chainUrl) {
        this.citAj = CITAj.build(new HttpService(chainUrl));
    }

    public static CITAConnection build(String path) throws IOException {
        CITAConnection connection = new CITAConnection();
        Toml toml = new Toml();
        String confFilePath = path + "/stub.toml";//"/home/jnu-03/crosschain/wecross-demo/routers-payment/127.0.0.1-8250-25500/conf/chains/cita/stub.toml";
        //logger.error(confFilePath);
        //File confFile = new File(confFilePath);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(confFilePath);
        toml =toml.read(resource.getInputStream());
        //toml = toml.read(confFile);
        Map<String, Object> stubConfig = toml.toMap();

        Map<String, Object> channelServiceConfigValue =
                (Map<String, Object>) stubConfig.get("channelService");

        String url = ((ArrayList<String>)channelServiceConfigValue.get("connectionsStr")).get(0);
        connection.citAj = CITAj.build(new HttpService(url));
        return connection;
    }


    public CITAConnection(Map<String, Object> properties) throws Exception {
        Map<String, Object> common = (Map<String, Object>) properties.get("common");
        if (common == null) {
            throw new Exception("[common] item not found");
        }

        String chainUrl = (String) common.get("chainUrl");
        if (chainUrl == null) {
            throw new Exception("\"chainUrl\" item not found");
        }

        this.citAj = CITAj.build(new HttpService(chainUrl));
    }

    @Override
    public void asyncSend(Request request, Callback callback) {
        int type = request.getType();
        byte[] data = request.getData();
        switch (type) {
            case TransactionConstant.Type.SEND_TRANSACTION:
            {
                try {
                    AppSendTransaction appSendTransaction =
                            citAj.appSendRawTransaction(new String(data)).send();
                    if (appSendTransaction.getError() != null) {
                        String message = appSendTransaction.getError().getMessage();
                        Response response = new Response();
                        response.setErrorCode(TransactionConstant.Result.ERROR);
                        response.setErrorMessage(message);
                        response.setData(null);
                        callback.onResponse(response);
                    } else {
                        Response response = new Response();
                        response.setErrorCode(TransactionConstant.Result.SUCCESS);
                        response.setErrorMessage("Success");
                        response.setData(appSendTransaction
                                .getSendTransactionResult()
                                .getHash()
                                .getBytes());
                        callback.onResponse(response);
                    }
                } catch (IOException e) {
                    Response response = new Response();
                    response.setErrorCode(TransactionConstant.Result.ERROR);
                    response.setErrorMessage(e.getMessage());
                    response.setData(null);
                    callback.onResponse(response);
                }
                break;
            }

            case TransactionConstant.Type.CALL_TRANSACTION:
            {
                try {
                    ContractCall contractCall =
                            OBJECT_MAPPER.readValue(data, ContractCall.class);
                    Call call =
                            new Call(
                                    contractCall.getSender(),
                                    contractCall.getContract(),
                                    contractCall.getData());
                    String result =
                            citAj.appCall(call, DefaultBlockParameterName.PENDING)
                                    .send()
                                    .getValue();
                    Response response = new Response();
                    response.setErrorCode(TransactionConstant.Result.SUCCESS);
                    response.setErrorMessage("Success");
                    response.setData(result.getBytes());
                    callback.onResponse(response);
                } catch (IOException e) {
                    Response response = new Response();
                    response.setErrorCode(TransactionConstant.Result.ERROR);
                    response.setErrorMessage("call failed,error" + e.getMessage());
                    response.setData(null);
                    callback.onResponse(response);
                }
                break;
            }

            case TransactionConstant.Type.GET_TRANSACTION_RECEIPT:
            {
                try {
                    String txHash = new String(data);
                    if(!txHash.substring(0,2).equals("0x")){
                        txHash = "0x" + txHash;
                    }
                    AppGetTransactionReceipt receipt =
                            citAj.appGetTransactionReceipt(txHash).send();
                    if (receipt.hasError()) {
                        Response response = new Response();
                        response.setErrorCode(TransactionConstant.Result.ERROR);
                        response.setErrorMessage(receipt.getRawResponse());
                        response.setData(null);
                        callback.onResponse(response);
                        return;
                    }
                    Response response = new Response();
                    response.setErrorCode(TransactionConstant.Result.SUCCESS);
                    response.setErrorMessage("Success");
                    response.setData(OBJECT_MAPPER.writeValueAsBytes(receipt.getTransactionReceipt()));
                    callback.onResponse(response);
                } catch (IOException e) {
                    Response response = new Response();
                    response.setErrorCode(TransactionConstant.Result.ERROR);
                    response.setErrorMessage(e.getMessage());
                    response.setData(null);
                    callback.onResponse(response);
                }
                break;
            }
            case TransactionConstant.Type.GET_ABI:
            {
                try {
                    String path = request.getPath();
                    if (!path.substring(0,2).equals("0x")) path = "0x" + path;
                    String abi = citAj.appGetAbi(path, DefaultBlockParameterName.PENDING)
                            .send()
                            .getAbi();
                    Response response = new Response();
                    response.setErrorCode(TransactionConstant.Result.SUCCESS);
                    response.setErrorMessage("Success");
                    response.setData(abi.getBytes(StandardCharsets.UTF_8));
                    callback.onResponse(response);
                } catch (IOException e) {
                    Response response = new Response();
                    response.setErrorCode(TransactionConstant.Result.ERROR);
                    response.setErrorMessage(e.getMessage());
                    response.setData(null);
                    callback.onResponse(response);
                }
                break;
            }
            case TransactionConstant.Type.GET_BLOCK_NUMBER:
            {
                try {
                    AppBlockNumber ret = citAj.appBlockNumber().send();
                    long blockNumber = ret.getBlockNumber().longValue();
                    Response response = new Response();
                    response.setErrorCode(TransactionConstant.Result.SUCCESS);
                    response.setErrorMessage("Success");
                    response.setData(Utils.longToBytes(blockNumber));
                    callback.onResponse(response);
                } catch (IOException e) {
                    Response response = new Response();
                    response.setErrorCode(TransactionConstant.Result.ERROR);
                    response.setErrorMessage(e.getMessage());
                    response.setData(null);
                    callback.onResponse(response);
                }
                break;
            }
            case TransactionConstant.Type.GET_BLOCK_BY_NUMBER:
            {
                long blockNumber = Utils.bytesToLong(data);
                try {
                    AppBlock appBlock =
                            citAj.appGetBlockByNumber(new DefaultBlockParameterNumber(blockNumber), false)
                                    .send();
                    if (appBlock.isEmpty()) {
                        Response response = new Response();
                        response.setErrorCode(TransactionConstant.STATUS.INTERNAL_ERROR);
                        response.setErrorMessage("Block is empty");
                        response.setData(null);
                        callback.onResponse(response);
                        return;
                    }
                    InternalBlock blk = new InternalBlock(appBlock);
                    Response response = new Response();
                    response.setErrorCode(TransactionConstant.STATUS.OK);
                    response.setErrorMessage("Success");
                    response.setData(Utils.toByteArray(blk));
                    callback.onResponse(response);
                } catch (IOException e) {
                    Response response = new Response();
                    response.setErrorCode(TransactionConstant.STATUS.CONNECTION_EXCEPTION);
                    response.setErrorMessage(e.getMessage());
                    response.setData(null);
                    callback.onResponse(response);
                }
                break;
            }
            case TransactionConstant.Type.GET_BLOCK_BY_HASH:
            {
                try {
                    AppBlock appBlock = citAj.appGetBlockByHash("0x" + Numeric.toHexString(data), false).send();
                    if (appBlock.isEmpty()) {
                        Response response = new Response();
                        response.setErrorCode(TransactionConstant.STATUS.INTERNAL_ERROR);
                        response.setErrorMessage("Block is empty");
                        response.setData(null);
                        callback.onResponse(response);
                        return;
                    }
                    InternalBlock blk = new InternalBlock(appBlock);
                    Response response = new Response();
                    response.setErrorCode(TransactionConstant.STATUS.OK);
                    response.setErrorMessage("Success");
                    response.setData(Utils.toByteArray(blk));
                    callback.onResponse(response);
                } catch (IOException e) {
                    Response response = new Response();
                    response.setErrorCode(TransactionConstant.STATUS.CONNECTION_EXCEPTION);
                    response.setErrorMessage(e.getMessage());
                    response.setData(null);
                    callback.onResponse(response);
                }
                break;
            }
            default:
            {
                Response response = new Response();
                response.setErrorCode(TransactionConstant.Result.ERROR);
                response.setErrorMessage("Unrecognized type of " + type);
                response.setData(null);
                callback.onResponse(response);
                break;
            }
        }
    }

    @Override
    public void setConnectionEventHandler(ConnectionEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public Map<String, String> getProperties() {
        logger.error("@@@getProperties was called but returns nothing!");

        return null;
    }

    public CITAj getCitAj() {
        return citAj;
    }
}
