import com.citahub.cita.abi.FunctionEncoder;
import com.citahub.cita.crypto.CipherException;
import com.citahub.cita.crypto.Credentials;
import com.citahub.cita.crypto.Sign;
import com.citahub.cita.crypto.WalletUtils;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.CITAjService;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.methods.request.Transaction;
import com.citahub.cita.protocol.core.methods.response.AppGetBalance;
import com.citahub.cita.protocol.core.methods.response.AppSendTransaction;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.protocol.exceptions.TransactionException;
import com.citahub.cita.protocol.http.HttpService;
import com.citahub.cita.tx.TransactionManager;
import com.citahub.cita.tx.response.PollingTransactionReceiptProcessor;
import com.citahub.cita.tx.response.TransactionReceiptProcessor;
import com.citahub.cita.utils.Numeric;
import com.webank.wecross.stub.cita.util.ChainUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @author SDKany
 * @ClassName TransferTest
 * @Date 2023/8/7 22:26
 * @Version V1.0
 * @Description
 */
public class TransferTest {
    static CITAj service = CITAj.build(new HttpService("http://10.154.24.5:1337"));

    @Test
    public void Transfer() throws IOException, TransactionException, InvalidAlgorithmParameterException, CipherException, NoSuchAlgorithmException, NoSuchProviderException {
        Credentials credentials = Credentials.create("0xbc24c93fe64c3228515322fbf235b97615688240483cce89fc5bf347ab8aab32");
        Credentials credentials1 = WalletUtils.loadCredentials("123456", "./src/test/resources/UTC--2023-08-08T10-36-33.190000000Z--da8b813b49161d9a25d91bdcc4397887cc70325e.json");
        //System.out.println(credentials1.getAddress());
        System.out.println("address = " + credentials.getAddress());
        System.out.println("privateKey = " + credentials.getEcKeyPair().getPrivateKey());
        System.out.println("publicKey = " + credentials.getEcKeyPair().getPublicKey());
        CITAQueryBalance(credentials.getAddress());
        String to = credentials1.getAddress();
        System.out.println("to :" + to);
        CITAQueryBalance(to);

        Long blockNumber = Numeric.toBigInt(service.appGetBlockByNumber(DefaultBlockParameter.valueOf("latest"), true).send().getBlock().getHeader().getNumber()).longValue();
        //System.out.println("blockNumber:" + blockNumber);
//
        Transaction tx =
                new Transaction(
                        to,
                        ChainUtils.getNonce(),
                        10000000L,
                        blockNumber + 88,
                        ChainUtils.getVersion(),
                        ChainUtils.getChainId(),
                        "1000000000", "0x00");
        byte[] bsTx = tx.serializeRawTransaction(false);
        Sign.SignatureData signatureData = Sign.signMessage(bsTx, credentials.getEcKeyPair());
        String raw_tx = tx.serializeUnverifiedTransaction(signatureData.get_signature(), bsTx);

        AppSendTransaction appSendTransaction =
                service.appSendRawTransaction(raw_tx).send();
        //System.out.println(appSendTransaction.getError());

        TransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(service, TransactionManager.DEFAULT_POLLING_FREQUENCY, TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
        TransactionReceipt txReceipt = transactionReceiptProcessor.waitForTransactionReceipt(appSendTransaction.getSendTransactionResult().getHash());
        System.out.println("success");
        System.out.println(txReceipt.getTransactionHash());
        System.out.println(txReceipt.getBlockNumber());

        System.out.println("------------");
        CITAQueryBalance(credentials.getAddress());
        CITAQueryBalance(to);
        System.out.println("------------");
//
//        System.out.println(ChainUtils.getChainIdV1(service));
//        System.out.println(ChainUtils.getChainId());
//        System.out.println(ChainUtils.getVersion());
    }

    public static void CITAQueryBalance(String address) throws IOException {
        AppGetBalance balance = service.appGetBalance(address, DefaultBlockParameter.valueOf("latest")).send();
        //System.out.println(balance.getError());
        System.out.println(address + "\t balance: " + balance.getBalance());
    }

    @Test
    public void WalletFileTest() throws Exception, NoSuchAlgorithmException, IOException, NoSuchProviderException {
        //String file = WalletUtils.generateFullNewWalletFile("123456", new File("./src/test/resources"));
        //System.out.println(file);
        Credentials credentials1 = WalletUtils.loadCredentials("123456", "./src/test/resources/UTC--2023-08-08T10-36-33.190000000Z--da8b813b49161d9a25d91bdcc4397887cc70325e.json");
        System.out.println(credentials1.getAddress());
    }
}
