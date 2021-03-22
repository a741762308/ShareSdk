package com.charles.sharesdk.plugin.extension;

public class ConfigExt {
    public boolean enable = true;
    public String appId = "";
    public String appSecret = "";
    public String url = "";
    public boolean onlyAuthCode = false;

    public ConfigExt() {
    }

    public ConfigExt(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "ConfigExt{" +
                "enable='" + enable +
                ", appId=" + appId +'\''+
                ", appSecret='" + appSecret + '\'' +
                ", url='" + url + '\'' +
                ", onlyAuthCode='" + onlyAuthCode +
                '}';
    }
}
