import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrapGeneratorTest {

    private Map<String, String> mapVarbinds = new HashMap<String, String>() {
        {
            put("1.3.6.1.4.1.1418.1.6.1.8","i");
            put("1.3.6.1.4.1.1418.1.8.1.3","i");
            put("1.3.6.1.4.1.1418.1.13","i");
            put("1.3.6.1.4.1.1418.1.6.1.9","i");
            put("1.3.6.1.4.1.1418.1.6.1.6","i");
            put("1.3.6.1.4.1.1418.1.11","i");
            put("1.3.6.1.4.1.1418.1.6.1.7","i");
            put("1.3.6.1.4.1.1418.1.8.1.7","i");
            put("1.3.6.1.4.1.1418.1.8.1.6","i");
            put("1.3.6.1.4.1.1418.1.8.1.5","i");
            put("1.3.6.1.4.1.1418.1.8.1.4","i");
            put("1.3.6.1.4.1.1418.1.9.1.2","i");
            put("1.3.6.1.4.1.1418.1.9.1.1","i");
            put("1.3.6.1.4.1.1418.1.7.1.2","i");
            put("1.3.6.1.4.1.1418.1.7.1.3","i");
            put("1.3.6.1.4.1.1418.1.6.1.1","i");
        }
    };

    @Test
    void trapBuilderV1Test() {
        TrapGenerator trapGenerator = new TrapGenerator();

        String actual = trapGenerator.trapBuilder("10 мар 2020 17:52:20,296 - 127.0.0.1 : V1TRAP[reqestID=0," +
                        "timestamp=0:00:00.00,enterprise=1.3.6.1.4.1.1418.1.0.1,genericTrap=6,specificTrap=0, " +
                        "VBS[1.3.6.1.4.1.1418.1.6.1.1 = 1; 1.3.6.1.4.1.1418.1.6.1.6 = 2; 1.3.6.1.4.1.1418.1.6.1.7 = 3]] \n",
                        mapVarbinds);
        String expected = "SET NETSNMP_PATH=C:\\usr\\bin\n" +
                         "%NETSNMP_PATH%\\snmptrap -v 1 -c public 127.0.0.1 1.3.6.1.4.1.1418.1.0.1 127.0.0.1 6 0 '55' " +
                         "1.3.6.1.4.1.1418.1.6.1.6 i 2 1.3.6.1.4.1.1418.1.6.1.7 i 3 1.3.6.1.4.1.1418.1.6.1.1 i 1 ";

        assertEquals(expected,actual);
    }

    @Test
    void trapBuilderV2Test() {
        TrapGenerator trapGenerator = new TrapGenerator();

        String actual = trapGenerator.trapBuilder("10 мар 2020 17:53:42,055 - 127.0.0.1/64835 : " +
                        "TRAP[requestID=21771, errorStatus=Success(0), errorIndex=0, VBS[1.3.6.1.2.1.1.3.0 = 495 days," +
                        " 2:13:42.61; 1.3.6.1.6.3.1.1.4.1.0 = 1.3.6.1.4.1.1418.1.0.1; 1.3.6.1.4.1.1418.1.6.1.1 = 1; " +
                        "1.3.6.1.4.1.1418.1.6.1.6 = 2; 1.3.6.1.4.1.1418.1.6.1.7 = 3]]",
                mapVarbinds);
        String expected = "SET NETSNMP_PATH=C:\\usr\\bin\n" +
                       "%NETSNMP_PATH%\\snmptrap -v 2c -c public 127.0.0.1 \"\" 1.3.6.1.4.1.1418.1.0.1 " +
                       "1.3.6.1.4.1.1418.1.6.1.6 i 2 1.3.6.1.4.1.1418.1.6.1.7 i 3 1.3.6.1.4.1.1418.1.6.1.1 i 1 ";

        assertEquals(expected,actual);
    }

    @Test
    void trapBuilderINFORMTest() {
        TrapGenerator trapGenerator = new TrapGenerator();

        String actual = trapGenerator.trapBuilder("10 мар 2020 17:53:42,055 - 127.0.0.1/64835 : " +
                        "INFORM[requestID=21771, errorStatus=Success(0), errorIndex=0, VBS[1.3.6.1.2.1.1.3.0 = 495 days," +
                        " 2:13:42.61; 1.3.6.1.6.3.1.1.4.1.0 = 1.3.6.1.4.1.1418.1.0.1; 1.3.6.1.4.1.1418.1.6.1.1 = 1; " +
                        "1.3.6.1.4.1.1418.1.6.1.6 = 2; 1.3.6.1.4.1.1418.1.6.1.7 = 3]]",
                mapVarbinds);
        String expected = "SET NETSNMP_PATH=C:\\usr\\bin\n" +
                "%NETSNMP_PATH%\\snmpinform -v 2c -c public 127.0.0.1 \"\" 1.3.6.1.4.1.1418.1.0.1 " +
                "1.3.6.1.4.1.1418.1.6.1.6 i 2 1.3.6.1.4.1.1418.1.6.1.7 i 3 1.3.6.1.4.1.1418.1.6.1.1 i 1 ";

        assertEquals(expected,actual);
    }
}