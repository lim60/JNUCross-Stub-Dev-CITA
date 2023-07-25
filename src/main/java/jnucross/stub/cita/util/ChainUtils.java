package jnucross.stub.cita.util;

import com.citahub.cita.protobuf.ConvertStrByte;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameterName;
import com.citahub.cita.protocol.core.methods.response.AppMetaData;
import com.citahub.cita.utils.Numeric;

import java.math.BigInteger;
import java.util.Random;

public class ChainUtils {
    private static int version = 2;
    private static BigInteger chainId = BigInteger.valueOf(1L);
    private static volatile BigInteger currentHeight = new BigInteger("0");
    public static final String ABI_ADDRESS = "ffffffffffffffffffffffffffffffffff010001";
    private static final Random RANDOM = new Random();

    public static byte[] convertHexToBytes(String hex) {
        String clearedStr = Numeric.cleanHexPrefix(hex);
        return ConvertStrByte.hexStringToBytes(clearedStr);
    }

    public static void initChainDatas(int chain_version, BigInteger chain_id) {
        version = chain_version;
        chainId = chain_id;
    }

    public static int getVersion() {
        return version;
    }

    public static BigInteger getChainId() {
        return chainId;
    }

    public static String getChainIdV1(CITAj service) {
        AppMetaData appMetaData = null;

        try {
            appMetaData =
                    (AppMetaData) service.appMetaData(DefaultBlockParameterName.PENDING).send();
        } catch (Exception var3) {
            throw new RuntimeException("获取链ID错误", var3);
        }

        return appMetaData.getAppMetaDataResult().getChainIdV1();
    }

    public static String getNonce() {
        return System.nanoTime() + String.valueOf(RANDOM.nextInt(100000) + 900000);
    }

    public static BigInteger getCurrentHeight(CITAj service) {
        return getCurrentHeight(service, 3);
    }

    public static void setCurrentHeight(BigInteger height) {
        currentHeight = height;
    }

    public static BigInteger getCurrentHeight() {
        return currentHeight;
    }

    private static BigInteger getCurrentHeight(CITAj service, int retry) {
        int count = 0;

        long height;
        for (height = -1L; count < retry; ++count) {
            try {
                height = service.appBlockNumber().send().getBlockNumber().longValue();
            } catch (Exception var8) {
                height = -1L;
                System.out.println("getBlockNumber failed retry ..");

                try {
                    Thread.sleep(2000L);
                } catch (Exception var7) {
                    System.out.println("failed to get block number, Exception: " + var7);
                }
            }
        }

        if (height == -1L) {
            System.out.println("Failed to get block number after " + count + " times.");
        }

        return BigInteger.valueOf(height);
    }

    public static BigInteger getValidUtilBlock(int validUntilBlock) {
        return getCurrentHeight().add(BigInteger.valueOf(validUntilBlock));
    }

    public static BigInteger getValidUtilBlock(CITAj service, int validUntilBlock) {
        return getCurrentHeight(service).add(BigInteger.valueOf(validUntilBlock));
    }
}
