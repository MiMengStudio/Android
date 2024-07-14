package com.mimeng.resourcepack;

public class ResourcePackInfo {
    private String name;
    private String resolution;
    private String type;
    private String version;

    // 无参构造函数
    public ResourcePackInfo() {
    }

    // 全参构造函数
    public ResourcePackInfo(String name, String resolution, String type, String version) {
        this.name = name;
        this.resolution = resolution;
        this.type = type;
        this.version = version;
    }

    // Getter和Setter方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    // toString方法，用于打印对象信息
    @Override
    public String toString() {
        return "ResourcePackInfo{" +
                "name='" + name + '\'' +
                ", resolution='" + resolution + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}