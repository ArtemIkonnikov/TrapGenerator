class TrapProperty {

    String ip
    String version
    String trapOid
    String type
    List<String> oids = new ArrayList<>()
    List<String> finalOids = new ArrayList<>()
    List<String> values = new ArrayList<>()

<<<<<<< HEAD
    TrapProperty(String ip, String version, String trapOid, List<String> oids, List<String> finalOids, List<String> values, String type) {
=======
    TrapProperty(String ip, String version, String trapOid, List<String> oids, List<String> values ,String type) {
>>>>>>> 6b2e1c148aba66dda23bd572b2f15cffe54c42ac
        this.ip = ip
        this.version = version
        this.trapOid = trapOid
        this.oids = oids
        this.finalOids = finalOids
        this.values = values
        this.type = type
    }

    String getIp() {
        return ip
    }

    String getVersion() {
        return version
    }

    List<String> getOids() {
        return oids
    }

    List<String> getValues() {
        return values
    }

    String getTrapOid() {
        return trapOid
    }

    String getType() {
        return type
    }

<<<<<<< HEAD
    List<String> getFinalOids() {
        return finalOids
    }

=======
>>>>>>> 6b2e1c148aba66dda23bd572b2f15cffe54c42ac

    @Override
    public String toString() {
        return "TrapProperty{" +
                "ip='" + ip + '\'' +
                ", version='" + version + '\'' +
                ", trapOid='" + trapOid + '\'' +
                ", type='" + type + '\'' +
                ", oids=" + oids +
                ", finalOids=" + finalOids +
                ", values=" + values +
                '}';
    }
}
