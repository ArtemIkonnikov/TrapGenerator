import com.fasterxml.jackson.core.JsonGenerationException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.SourceURI

import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Matcher
import java.util.regex.Pattern

class TrapGenerator {

    static String batFilePath = " "
    static File batFile

    public static void main(String[] args) {

        String trapsReceivedPath
        String jsonRulesFilePath

         Map<String, String> mapVarbinds = new HashMap<>()

        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("-d")) {
                trapsReceivedPath = args[i + 1]
            } else if (args[i].equals("-r")) {
                jsonRulesFilePath = args[i + 1]
            } else if (args[i].equals("-o")) {
                batFilePath = args[i + 1]
            }
        }

        @SourceURI
        URI sourceUri
        Path scriptLocation = Paths.get(sourceUri)

        if (batFilePath == null | batFilePath == " ") {
            batFilePath = scriptLocation
            batFilePath = batFilePath.substring(0, batFilePath.length() - 20)
        }

        Map<String, Object> rawMapVarbinds

        try {
            ObjectMapper mapper = new ObjectMapper()
            rawMapVarbinds = mapper.readValue(
                    new File(jsonRulesFilePath),
                    new TypeReference<Map<String, Object>>() {
                    })
        } catch (JsonGenerationException e) {
            e.printStackTrace()
        } catch (JsonMappingException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        for (Map.Entry<String, String> entry : rawMapVarbinds.entrySet()) {
            String key = entry.getKey()
            String value = entry.getValue()
            if (value.equals("int")) {
                mapVarbinds.put(key, "i")
            } else {
                mapVarbinds.put(key, "s")
            }
        }

        File trapsReceivedFile = new File(trapsReceivedPath)
        String readableStr
        BufferedReader bufferedReader

        try  {
            bufferedReader = new BufferedReader(new FileReader(trapsReceivedFile) )
            while (bufferedReader.ready()) {
                readableStr = bufferedReader.readLine()
                String completeStr = trapBuilder(readableStr,mapVarbinds)

                BufferedWriter bufferedWriter
                try {
                    bufferedWriter = new BufferedWriter(new FileWriter(batFile, true))
                    bufferedWriter.write(completeStr + "\n")
                } catch (IOException e ) { e.printStackTrace() }
                finally {
                    try {
                        bufferedWriter.close()
                    } catch (Exception e){e.printStackTrace()
                    }
                }

            }
        } catch (IOException e ) {
            e.printStackTrace()
        }
    }
    public static String trapBuilder (String receivedStr , Map<String, String> mapVarbinds ){

        List<String> values = new ArrayList<>()
        List<String> oids = new ArrayList<>()
        String ip
        String trapVer
        String trapOid

        for (String oid : mapVarbinds.keySet()) {
            if (receivedStr.contains(oid)) {

                Pattern pattern1 = Pattern.compile("(?<= - ).+?(?=\\/)|(?<= - ).+?(?= : )")
                Matcher matcher1 = pattern1.matcher(receivedStr)

                while (matcher1.find())
                    ip = matcher1.group()

                if (receivedStr.contains("V1TRAP")) {
                    trapVer = "v1"
                } else {
                    trapVer = "v2"
                }

                Pattern pattern2 = Pattern.compile("(?<=; |VBS\\[)1.3.6.1\\..+?(?= )( = )(.+?)(?=;|\\])")
                Matcher matcher2 = pattern2.matcher(receivedStr)

                while (matcher2.find()){

                    if ((matcher2.group(0)).contains(oid)){
                        String oidForList = oid
                        String value = matcher2.group(2)
                        values.add(value)
                        oids.add(oidForList)
                    }
                }

                Pattern pattern = Pattern.compile("(?<=enterprise=).+?(?=,)|(?<=1.3.6.1.6.3.1.1.4.1.0 = ).+?(?=;)")
                Matcher matcher = pattern.matcher(receivedStr)

                while (matcher.find()) {
                    trapOid = matcher.group()
                }
            }
        }

        TrapProperty trap
        if (ip != null && trapOid != null && oids != null && values != null) {
             trap = new TrapProperty(ip, trapVer, trapOid, oids, values)
        }

        StringBuilder stringBuilder = new StringBuilder()
        String batFileName = trap.ip + "_" + trap.version + "_traps.bat"
        batFile = new File(batFilePath, batFileName)

        if (trap.version.equals("v1")) {
            stringBuilder.append("SET NETSNMP_PATH=C:\\usr\\bin\n" +
                    "%NETSNMP_PATH%\\snmptrap -v 1 -c public ")
            stringBuilder.append(trap.ip + " ")
            stringBuilder.append(trap.trapOid + " ")
            stringBuilder.append(trap.ip + " 6 0 '55' ")
        } else if (trap.version.equals("v2")) {
            stringBuilder.append("SET NETSNMP_PATH=C:\\usr\\bin\n" +
                    "%NETSNMP_PATH%\\snmptrap -v 2c -c public ")
            stringBuilder.append(trap.ip + " \"\" ")
            stringBuilder.append(trap.trapOid + " ")
        }
        for (int i = 0; i < trap.oids.size(); i++) {
            stringBuilder.append(trap.oids[i] + " ")
            stringBuilder.append(mapVarbinds.get(trap.oids[i]) + " ")
            stringBuilder.append(trap.values[i] + " ")
        }

        return stringBuilder
    }
}