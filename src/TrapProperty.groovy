class TrapProperty {

    String ip
    String version
    String trapOid
    String type
    List<String> oids = new ArrayList<>()
    List<String> values = new ArrayList<>()

    TrapProperty(String ip, String version, String trapOid, List<String> oids, List<String> values ,String type) {
        this.ip = ip
        this.version = version
        this.trapOid = trapOid
        this.oids = oids
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


    @Override
    public String toString() {
        return "TrapProperty{" +
                "ip='" + ip + '\'' +
                ", version='" + version + '\'' +
                ", trapOid='" + trapOid + '\'' +
                ", type='" + type + '\'' +
                ", oids=" + oids +
                ", values=" + values +
                '}';
    }
}
