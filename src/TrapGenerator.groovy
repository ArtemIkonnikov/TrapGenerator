import com.fasterxml.jackson.core.JsonGenerationException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.SourceURI

import java.nio.file.Path
import java.nio.file.Paths

class TrapGenerator {
    public static void main(String[] args) {

        String trapsReceivedPath
        String jsonRulesFilePath
        String batFilePath

        List<TrapProperty> traps = new ArrayList<>()

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

        if (batFilePath == null) {
            batFilePath = scriptLocation
            batFilePath = batFilePath.substring(0, batFilePath.length() - 20)

        }

        File trapsReceivedFile = new File(trapsReceivedPath)

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

        Map<String, String> mapVarbinds = new HashMap<>()
        for (Map.Entry<String, String> entry : rawMapVarbinds.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.equals("int")) {
                mapVarbinds.put(key, "i")
            } else {
                mapVarbinds.put(key, "s")
            }
        }

        String readableStr
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(trapsReceivedFile))) {

            while (bufferedReader.ready()) {
                readableStr = bufferedReader.readLine()
                List<String> values = new ArrayList<>()
                List<String> oids = new ArrayList<>()
                String ip
                String trapVer
                String trapOid

                for (String oid : rawMapVarbinds.keySet()) {
                    if (readableStr.contains(oid)) {

                        String[] strMas1 = readableStr.split("-")
                        String[] strMas2 = strMas1[1].split(":")
                        String[] strMas3 = strMas2[1].split("\\[")
                        String[] strMas4 = readableStr.split("VBS\\[")
                        String[] strMas5 = strMas4[1].split(";")

                        ip = strMas2[0].trim()
                        if (ip.contains("/")) {
                            ip = (ip.split("/"))[0]
                        }

                        trapVer = strMas3[0].trim()
                        if (trapVer.contains("V1TRAP")) {
                            trapVer = "v1"
                        } else {
                            trapVer = "v2"
                        }

                        for (int i = 0; i < strMas5.size(); i++) {
                            if (strMas5[i].contains(oid)) {
                                String[] strMas6 = strMas5[i].split("=")
                                String oidForList = strMas6[0].trim()
                                String value = strMas6[1].trim()
                                if (value.endsWith("]]")) {
                                    value = (value.substring(0, value.length() - 2))
                                }
                                values.add(value)
                                oids.add(oidForList)
                            }
                        }
                        if (readableStr.contains("enterprise=")) {
                            String[] strArr = readableStr.split("enterprise=")
                            trapOid = (strArr[1].split(","))[0]
                        } else {
                            String[] strArr = readableStr.split("1.3.6.1.6.3.1.1.4.1.0 = ")
                            trapOid = (strArr[1].split(";"))[0]
                        }
                    }
                }
                if (ip != null && traps != null && oids != null && values != null)
                    traps.add(new TrapProperty(ip, trapVer, trapOid, oids, values))
            }
        } catch (IOException e) {
            e.printStackTrace()
        }

        for (TrapProperty trap : traps) {

            StringBuilder stringBuilder = new StringBuilder()
            String batFileName = trap.ip + "_" + trap.version + "_traps.bat"
            File batFile = new File(batFilePath, batFileName)

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

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(batFile, true))) {
                bufferedWriter.write(stringBuilder + "\n")
            } catch (IOException e) {
                e.printStackTrace()
            }
        }
    }
}