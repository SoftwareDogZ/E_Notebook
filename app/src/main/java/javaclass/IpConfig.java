package javaclass;

public class IpConfig {
    private String ipPath;
    private String root;

    public IpConfig(){
        ipPath = "http://118.24.30.60/Enotebook_server/";
        root = "http://118.24.30.60/";
    }

    public String getIpPath(){
        return ipPath;
    }

    public String getRoot(){
        return root;
    }
}
