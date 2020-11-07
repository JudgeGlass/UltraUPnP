package net.zicron.ultraupnp.gui;

public class PortMapping {
    private String hostname;

    private String internalPort;
    private String externalPort;
    private String protocol;
    private String description;

    public PortMapping(String hostname, String internalPort, String externalPort, String protocol, String description){
        this.hostname = hostname;
        this.internalPort = internalPort;
        this.externalPort = externalPort;
        this.protocol = protocol;
        this.description = description;
    }

    public String getHostname() {
        return hostname;
    }

    public String getInternalPort() {
        return internalPort;
    }

    public String getExternalPort() {
        return externalPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getDescription() {
        return description;
    }
}
