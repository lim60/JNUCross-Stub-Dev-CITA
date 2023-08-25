package com.webank.wecross.stub.cita;

import com.citahub.cita.crypto.*;
import com.citahub.cita.protocol.core.CITA;
import com.webank.wecross.stub.*;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.util.Map;

@Stub("CITAStub")
public class CITAStubFactory implements StubFactory {

    private Logger logger = LoggerFactory.getLogger(CITAStubFactory.class);

    @Override
    public void init(WeCrossContext context) {

    }

    @Override
    public Driver newDriver() {
        Driver driver = null;
        try {
            driver = new CITADriver(new CITAConnection("http://10.154.24.5:1337"), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO, 初始化 Driver
        return driver;
    }

    @Override
    public Connection newConnection(String path) {
        Connection connection =new CITAConnection();
        try {
            connection = CITAConnection.build(path);

        } catch (Exception e){
            logger.error("CITA failed to build new connection"+e);
        }
        return connection;
        //TODO 需要测试connection的有效性

    }

    @Override
    public Account newAccount(Map<String, Object> properties) {
        Account account = new CITAAccount(properties);
        logger.error("@@@Am I here?");
        return account;
    }

    @Override
    public void generateAccount(String path, String[] args) {
        /** create KeyPair first */
        ECKeyPair ecKeyPair = null;
        try {
            ecKeyPair = Keys.createEcKeyPair();
        } catch (Exception ex) {
            logger.error("creat key pair exception :" + ex.getMessage());
            return;
        }

        String fileName = null;
        try {
            fileName = WalletUtils.generateWalletFile(
                    "123456", ecKeyPair, new File(path), true);
        } catch (Exception ex) {
            logger.error("generate WalletFile exception :" + ex.getMessage());
            return;
        }

        /** write private to file in pem format */

        String accountTemplate =
                "[account]\n"
                        + "    type='CITAStub"
                        + "'\n"
                        + "    accountFile='"
                        + fileName
                        + "'\n"
                        + "    password='"+ "123456" +"' # password is required"
                        + "\n"
                        + "    privateKey='0x"+ecKeyPair.getPrivateKey().toString(16)
                        + "'\n"
                        + "    publicKey='0x"+ecKeyPair.getPublicKey().toString(16)+"'";
        String confFilePath = path + "/account.toml";//"/home/jnu-03/crosschain/wecross-demo/routers-payment/127.0.0.1-8250-25500/conf/accounts/cita/account.toml";/*path + "/account.toml";*/
        File confFile = new File(confFilePath);
        FileWriter fileWriter = null;
        try {
            confFile.createNewFile();
            fileWriter = new FileWriter(confFile);
            fileWriter.write(accountTemplate);
        } catch (IOException ex) {
            logger.error("Conf file created error!" + ex.getMessage(), ex);
            ex.printStackTrace();
        }finally {
            try {
                if (fileWriter != null)
                    fileWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void generateConnection(String path, String[] args) {
        try {
            String chainName = new File(path).getName();

            String accountTemplate =
                    "[common]\n"
                            + "    name = '"
                            + chainName
                            + "'\n"
                            + "    type = 'CITAStub'"
                            + "\n"
                            + "[channelService]\n"
                            + "    wallFile = 'xxx.json'\n"
                            + "    connectionsStr = ['http://10.154.24.5:1337']\n"
                            + "\n";
            String confFilePath = path + "/stub.toml";//"/home/jnu-03/crosschain/wecross-demo/routers-payment/127.0.0.1-8250-25500/conf/chains/cita/stub.toml";/*path + "/stub.toml";*/
            File confFile = new File(confFilePath);
            if (!confFile.createNewFile()) {
                logger.error("Conf file exists! {}", confFile);
                return;
            }

            FileWriter fileWriter = new FileWriter(confFile);
            try {
                fileWriter.write(accountTemplate);
            } finally {
                fileWriter.close();
            }

            generateProxyContract(path);
            generateHubContract(path);

            generateAccount(path, null);

            System.out.println(
                    "SUCCESS: Chain \""
                            + chainName
                            + "\" config framework has been generated to \""
                            + path
                            + "\"");
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }
    }

    public void generateProxyContract(String path) {
        try {
            String proxyPath = "WeCrossProxy.sol";
            URL proxyDir = getClass().getResource(File.separator + proxyPath);
            File dest =
                    new File(path + File.separator + "WeCrossProxy" + File.separator + proxyPath);
            FileUtils.copyURLToFile(proxyDir, dest);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void generateHubContract(String path) {
        try {
            String hubPath = "WeCrossHub.sol";
            URL hubDir = getClass().getResource(File.separator + hubPath);
            File dest = new File(path + File.separator + "WeCrossHub" + File.separator + hubPath);
            FileUtils.copyURLToFile(hubDir, dest);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
