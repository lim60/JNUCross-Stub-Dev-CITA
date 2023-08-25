package com.webank.wecross.stub.cita;

import com.citahub.cita.abi.FunctionEncoder;
import com.citahub.cita.abi.datatypes.Function;
import com.citahub.cita.crypto.ECKeyPair;
import com.citahub.cita.crypto.Hash;
import com.citahub.cita.crypto.Keys;
import com.citahub.cita.crypto.Sign;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.utils.HexUtil;
import com.citahub.cita.utils.Numeric;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wecross.stub.*;
import com.webank.wecross.stub.cita.constant.TransactionConstant;
import com.webank.wecross.stub.cita.contract.ContractCall;
import com.webank.wecross.stub.cita.util.*;
import link.luyu.toolkit.abi.ContractABI;
import link.luyu.toolkit.abi.FunctionABI;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CITADriver implements Driver {
    private static final Logger logger = LoggerFactory.getLogger(CITADriver.class);

    private Connection connection;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public CITADriver(Connection connection, Map<String, Object> properties) throws Exception {
        this.connection = connection;
    }

    @Override
    public ImmutablePair<Boolean, TransactionRequest> decodeTransactionRequest(Request request) {

        int requestType = request.getType();
        logger.error("Trace - decodeTransactionRequest called!");
        return null;
    }

    @Override
    public List<ResourceInfo> getResources(Connection connection) {
        logger.error("@@@getResources was called but returns empty!");
        //A dumb Recource list:
        List<ResourceInfo> ResourceInfoList = new ArrayList<>();
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setName("Nonsense CITA Resource");
        resourceInfo.setStubType("CITAStub");
        resourceInfo.setProperties(null);
        resourceInfo.setChecksum(null);

        ResourceInfoList.add(resourceInfo);

        return ResourceInfoList;
        //return new ArrayList<>();
        /*return null;*/
    }

    @Override
    public void asyncCall(TransactionContext context, TransactionRequest request, boolean byProxy, Connection connection, Callback callback) {
        Path path = context.getPath();
        String name = path.getResource();
        connection.asyncSend(newRequest(Utils.getResourceName(name), TransactionConstant.Type.GET_ABI, null, null), response -> {
            if(response.getErrorCode() != TransactionConstant.STATUS.OK) {
                callback.onTransactionResponse(new TransactionException(response.getErrorCode(), response.getErrorMessage()), null);
            } else {
                String raw_abi = new String(response.getData(), StandardCharsets.UTF_8);
                String abi = Utils.hexStr2Str(Utils.hexRemove0x(raw_abi));
                ContractABI ctAbi = new ContractABI(abi);
                FunctionABI funAbi;
                try {
                    funAbi = ctAbi.getFunctions(request.getMethod()).get(0);
                } catch (IndexOutOfBoundsException ioe) {
                    callback.onTransactionResponse(new TransactionException(TransactionConstant.STATUS.INTERNAL_ERROR,
                            "no method found"),
                            null);
                    return;
                }
                Function function = Utils.convertFunction(abi, request.getMethod(), request.getArgs());
                ContractCall call = new ContractCall(Utils.getResourceName(name), FunctionEncoder.encode(function));
                BigInteger pk = ((CITAAccount)context.getAccount()).getEcKeyPair().getPublicKey();
                BigInteger sk = ((CITAAccount)context.getAccount()).getEcKeyPair().getPrivateKey();
                byte[] pubKey = Numeric.toBytesPadded(pk, 64);
                byte[] secKey = Numeric.toBytesPadded(sk, 32);

                String sender = Keys.getAddress(Numeric.toHexStringWithPrefixZeroPadded(new BigInteger(1, pubKey), 128));
                call.setSender("0x" + sender);
                try {
                    byte[] data = objectMapper.writeValueAsBytes(call);
                    connection.asyncSend(
                            newRequest(name,
                                    TransactionConstant.Type.CALL_TRANSACTION,
                                    data, null),
                            response1 -> {
                                TransactionResponse transactionResponse = new TransactionResponse();
                                if (response1.getData() != null) {
                                    String resp = new String(response1.getData());
                                    if (!resp.equals("0x")) {
                                        transactionResponse.setResult(funAbi.decodeOutput(resp));
                                    }
                                }
                                transactionResponse.setErrorCode(0); // original receipt status
                                transactionResponse.setMessage("Success");
                                //transactionResponse.setHash();
                                //transactionResponse.setBlockNumber();
                                callback.onTransactionResponse(null, transactionResponse);
                            });
                } catch (JsonProcessingException e) {
                    callback.onTransactionResponse(
                            new TransactionException(TransactionConstant.STATUS.INTERNAL_ERROR, "serialize failed,error:" + e.getMessage()), null);
                    throw new RuntimeException("serialize failed");
                }
            }
        });
    }

    public static byte[] join(byte[]... params) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] res = null;
        try {
            for (int i = 0; i < params.length; i++) {
                baos.write(params[i]);
            }
            res = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public void asyncSendTransaction(TransactionContext context, TransactionRequest request, boolean byProxy, Connection connection, Callback callback) {
        Path path = context.getPath();
        String name = path.getResource();
        connection.asyncSend(newRequest(Utils.getResourceName(name), TransactionConstant.Type.GET_ABI, null, null), response -> {
            if(response.getErrorCode() != TransactionConstant.STATUS.OK) {
                callback.onTransactionResponse(new TransactionException(response.getErrorCode(), response.getErrorMessage()), null);
            } else {
                String raw_abi = new String(response.getData(), StandardCharsets.UTF_8);
                String abi = Utils.hexStr2Str(Utils.hexRemove0x(raw_abi));

                connection.asyncSend(newRequest("", TransactionConstant.Type.GET_BLOCK_NUMBER, null, null), response1 -> {
                    if(response1.getErrorCode() != TransactionConstant.STATUS.OK) {
                        callback.onTransactionResponse(new TransactionException(response1.getErrorCode(), response1.getErrorMessage()), null);
                    } else {
                        long blockNumber = Utils.bytesToLong(response1.getData());
                        com.citahub.cita.protocol.core.methods.request.Transaction tx =
                                new com.citahub.cita.protocol.core.methods.request.Transaction(
                                        Utils.getResourceName(name),
                                        ChainUtils.getNonce(),
                                        10000000L,
                                        blockNumber + 88,
                                        ChainUtils.getVersion(),
                                        ChainUtils.getChainId(),
                                        "0",
                                        FunctionEncoder.encode(
                                                Utils.convertFunction(
                                                        abi, request.getMethod(), request.getArgs())));

                        try {
                            byte[] bsTx = tx.serializeRawTransaction(false);
                            Sign.SignatureData signatureData = Sign.signMessage(bsTx, ((CITAAccount)context.getAccount()).getEcKeyPair());
                            String raw_tx = tx.serializeUnverifiedTransaction(signatureData.get_signature(), bsTx);

                            connection.asyncSend(newRequest(
                                            name,
                                            TransactionConstant.Type.SEND_TRANSACTION,
                                            raw_tx.getBytes(StandardCharsets.UTF_8), null),
                                    response2 -> {
                                        // todo verify transaction on-chain proof
                                        if(response2.getErrorCode() != TransactionConstant.STATUS.OK) {
                                            callback.onTransactionResponse(new TransactionException(response2.getErrorCode(), response2.getErrorMessage()), null);
                                        } else {
                                            TransactionResponse receipt = new TransactionResponse();
                                            receipt.setBlockNumber(blockNumber);
                                            receipt.setErrorCode(0); // SUCCESS
                                            receipt.setMessage("Success");
                                            receipt.setHash(new String(response2.getData()));
                                            callback.onTransactionResponse(null, receipt);
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException("sign and send failed,error" + e.getMessage());
                        }
                    }
                });
            }
        });
    }

    @Override
    public void asyncGetBlockNumber(Connection connection, GetBlockNumberCallback callback) {
        CompletableFuture<byte[]> getBlockNumberFuture = new CompletableFuture<>();
        connection.asyncSend(newRequest(null, TransactionConstant.Type.GET_BLOCK_NUMBER, null, null), response -> {
                if (response.getErrorCode() != TransactionConstant.STATUS.OK) {
                    callback.onResponse(new Exception(response.getErrorMessage()), -1);
                } else {
                    callback.onResponse(null, Utils.bytesToLong(response.getData()));
                }
            });
    }

    @Override
    public void asyncGetBlock(long blockNumber, boolean onlyHeader, Connection connection, GetBlockCallback callback) {
        connection.asyncSend(newRequest(null, TransactionConstant.Type.GET_BLOCK_BY_NUMBER, Utils.longToBytes(blockNumber), null), response -> {
            if(response.getErrorCode() != TransactionConstant.STATUS.OK) {
                callback.onResponse(new Exception(response.getErrorMessage()), null);
            } else {
                InternalBlock block = (InternalBlock)Utils.toObject(response.getData());
                callback.onResponse(null, block.toBlock());
            }
        });
    }

    @Override
    public void asyncGetTransaction(String transactionHash, long blockNumber, BlockManager blockManager, boolean isVerified, Connection connection, GetTransactionCallback callback) {
        connection.asyncSend(newRequest(
                null,
                TransactionConstant.Type.GET_TRANSACTION_RECEIPT,
                transactionHash.getBytes(),
                null),
                response -> {
                    try {
                        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                        TransactionReceipt transactionReceipt =
                                objectMapper.readValue(response.getData(), TransactionReceipt.class);
                        Transaction transaction = new Transaction();
                        transaction.setAccountIdentity(transactionReceipt.getFrom());
                        transaction.getTransactionResponse().setBlockNumber(Long.parseLong(transactionReceipt.getBlockNumberRaw()));
                        transaction.getTransactionResponse().setHash(transactionReceipt.getTransactionHash());
                        transaction.getTransactionResponse().setMessage(transactionReceipt.getTransactionHash());
                        transaction.setReceiptBytes(response.getData());
                        transaction.getTransactionResponse().setErrorCode(0);

                        callback.onResponse(null, transaction);
                    } catch (IOException e) {
                        callback.onResponse(new Exception(
                                "deserialize failed,error:" + e.getMessage()),
                                null);
                    }
                });
    }

    @Override
    public void asyncCustomCommand(String command, Path path, Object[] args, Account account, BlockManager blockManager, Connection connection, CustomCommandCallback callback) {
        //TODO: 应当可以不实现？
    }

    @Override
    public byte[] accountSign(Account account, byte[] message) {
        ECKeyPair ecKeyPair = ((CITAAccount)account).getEcKeyPair();
        Sign.SignatureData signatureData = Sign.signMessage(message, ecKeyPair);
        return concatenateSignature(signatureData.getR(), signatureData.getS(), signatureData.getV());
    }
    public static byte[] concatenateSignature(byte[] r, byte[] s, byte v) {
        byte[] signature = new byte[65];
        System.arraycopy(r, 0, signature, 0, r.length);
        System.arraycopy(s, 0, signature, 32, s.length);
        signature[64] = v;
        return signature;
    }

    @Override
    public boolean accountVerify(String identity, byte[] signBytes, byte[] message) {
        byte[] hashMsg = Hash.sha3(message);
        byte[] r = Arrays.copyOfRange(signBytes, 0, 32);
        byte[] s = Arrays.copyOfRange(signBytes, 32, 64);
        byte v = signBytes[64];
        Sign.SignatureData sig = new Sign.SignatureData(v,r,s);
        BigInteger recoverPubKey = null;
        try {
            recoverPubKey = Sign.signedMessageToKey(message, sig);
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        String recoverAddress = Keys.getAddress(recoverPubKey);
        return Numeric.toBigInt(recoverAddress).equals(Numeric.toBigInt(identity));
    }

    public Request newRequest(String path, int type, byte[] data, ResourceInfo resourceInfo){
        Request request = new Request();
        request.setType(type);
        request.setData(data);
        request.setPath(path);
        request.setResourceInfo(resourceInfo);
        return request;
    }
}
