package javaclass;

public class IpConfig {
    private String ipPath;
    private String root;

    public IpConfig(){
        ipPath = "http://10.253.221.78:81/Enotebook_server/";
        root = "http://10.253.221.78:81/";
    }

    public String getIpPath(){
        return ipPath;
    }

    public String getRoot(){
        return root;
    }
}
