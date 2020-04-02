class TrapProperty {

    String ip
    String version
    String trapOid
    List<String> oids = new ArrayList<>()
    List<String> values = new ArrayList<>()

    TrapProperty(String ip, String version, String trapOid, List<String> oids, List<String> values) {
        this.ip = ip
        this.version = version
        this.trapOid = trapOid
        this.oids = oids
        this.values = values
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


    @Override
    public String toString() {
        return "TrapProperty{" +
                "ip='" + ip + '\'' +
                ", version='" + version + '\'' +
                ", trapOid='" + trapOid + '\'' +
                ", oids=" + oids +
                ", values=" + values +
                '}';
    }
}
