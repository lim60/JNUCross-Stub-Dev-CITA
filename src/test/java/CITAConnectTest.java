import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.methods.response.AppBlock;
import com.citahub.cita.protocol.core.methods.response.AppBlockNumber;
import com.citahub.cita.protocol.core.methods.response.AppGetBalance;
import com.citahub.cita.protocol.http.HttpService;
import jnucross.stub.cita.CITAConnection;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @author SDKany
 * @ClassName CITAConnectTest
 * @Date 2023/7/25 17:45
 * @Version V1.0
 * @Description
 */
public class CITAConnectTest {
    @Test
    public void ConnectTest() throws IOException {
        //CITAConnection connection = new CITAConnection("http://81.71.46.41:6018");

        CITAj service = CITAj.build(new HttpService("http://10.154.24.5:1337"));
        AppBlockNumber result = service.appBlockNumber().send();
        BigInteger blockNumber = result.getBlockNumber();
        System.out.println(blockNumber);

        //AppBlock appBlock = service.appGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(1)), true).send();
        //System.out.println(appBlock.getResult().getHash());

        AppBlock appBlock2 = service.appGetBlockByHash("0x0acd3bc4968597056a3c624066e7901b0887d14a4d5e625979ea89a6d67df968", true).send();
        System.out.println(appBlock2.getResult().getHash());

    }
}
