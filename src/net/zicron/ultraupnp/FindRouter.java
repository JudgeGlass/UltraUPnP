package net.zicron.ultraupnp;

import java.io.IOException;
import java.net.*;

public class FindRouter {
    private final int SSDP_PORT = 1900;
    private final int SSDP_SEARCH_PORT = 1901;
    private final int SSDP_RESPONSE_DELAY = 2; // Seconds

    private final String SSDP_IP = "239.255.255.250";

    public FindRouter(){

    }

    public void search() throws IOException {
        StringBuilder SSDPMessage = new StringBuilder();
        SSDPMessage.append("M-SEARCH * HTTP/1.1\r\n");
        SSDPMessage.append("HOST: " + SSDP_IP + ":" + SSDP_PORT + "\r\n");
        SSDPMessage.append("MAN: \"ssdp:discover\"\r\n");
        SSDPMessage.append("MX: " + SSDP_RESPONSE_DELAY + "\r\n");
        SSDPMessage.append("ST: urn:schemas-upnp-org:device:InternetGatewayDevice:1\r\n");

        byte[] messageArray = SSDPMessage.toString().getBytes();
        InetSocketAddress messageSocketAddress = new InetSocketAddress(InetAddress.getByName(SSDP_IP), SSDP_PORT);
        DatagramPacket messagePacket = new DatagramPacket(messageArray, messageArray.length, messageSocketAddress);


        InetSocketAddress hostSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), SSDP_SEARCH_PORT);
        MulticastSocket multicastSocket = new MulticastSocket(hostSocketAddress);
        multicastSocket.setTimeToLive(4);
        multicastSocket.send(messagePacket);
        multicastSocket.disconnect();
        multicastSocket.close();


        DatagramSocket captureSocket = new DatagramSocket(SSDP_SEARCH_PORT);
        byte[] routerResponseArray = new byte[1024];
        DatagramPacket routerResponsePacket = new DatagramPacket(routerResponseArray, routerResponseArray.length);
        captureSocket.receive(routerResponsePacket);
        
        System.out.println("ROUTER RESPONSE");
        System.out.println(new String(routerResponseArray));
    }
}
