import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.methods.response.AppBlockNumber;
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
    }
}
