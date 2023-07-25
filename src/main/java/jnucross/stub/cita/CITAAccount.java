package jnucross.stub.cita;

import com.citahub.cita.crypto.ECKeyPair;
import com.citahub.cita.crypto.Keys;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.tx.RawTransactionManager;
import com.citahub.cita.utils.Numeric;
import com.webank.wecross.stub.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Map;

public class CITAAccount implements Account {
    private static final Logger logger = LoggerFactory.getLogger(CITAAccount.class);

    private static final String ABI_ADDRESS = "ffffffffffffffffffffffffffffffffff010001";
    private RawTransactionManager transactionManager;
    private CITAj service;
    private String abi;
    private String name;
    private String type;
    private String identity;
    private int keyID;
    private BigInteger publicKey;
    private ECKeyPair ecKeyPair;

    public CITAAccount(String name, String type, ECKeyPair ecKeyPair) {
        this.name = name;
        this.type = type;
        this.publicKey = ecKeyPair.getPublicKey();
        this.ecKeyPair = ecKeyPair;
        this.identity = Keys.getAddress(publicKey);
    }

    public CITAAccount(Map<String, Object> properties) {
        String name = (String) properties.get("name");
        String address = (String) properties.get("address");
        String pubKeyStr = (String) properties.get("publicKey");
        String priKeyStr = (String) properties.get("privateKey");
        String type = (String) properties.get("type");
        if (name == null || name.length() == 0) {
            logger.error("name has not given");
            return;
        }

        if (address == null || address.length() == 0) {
            logger.error("address has not given");
            return;
        }

        if (pubKeyStr == null || pubKeyStr.length() == 0) {
            logger.error("publicKey has not given");
            return;
        }

        if (priKeyStr == null || priKeyStr.length() == 0) {
            logger.error("privateKey has not given");
            return;
        }

        if (type == null || type.length() == 0) {
            logger.error("type has not given");
            return;
        }

        try {
            BigInteger publicKey = Numeric.toBigInt(pubKeyStr);
            BigInteger privateKey = Numeric.toBigInt(priKeyStr);
            logger.info("New account: {} type:{}", name, type);
            ECKeyPair ecKeyPair = new ECKeyPair(privateKey, publicKey);

            this.identity = address;
            this.name = name;
            this.type = type;
            this.publicKey = publicKey;
            this.ecKeyPair = ecKeyPair;
        } catch (Exception e) {
            logger.error("EthereumAccount exception: " + e.getMessage());
            return;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getIdentity() {
        return this.identity;
    }

    @Override
    public int getKeyID() {
        return this.keyID;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    public ECKeyPair getEcKeyPair() {
        return ecKeyPair;
    }
}
