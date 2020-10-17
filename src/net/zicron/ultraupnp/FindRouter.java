package net.zicron.ultraupnp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.TimeoutException;

public class FindRouter {
    private final int SSDP_PORT = 1900;
    private final int SSDP_SEARCH_PORT = 1901;
    private final int SSDP_RESPONSE_DELAY = 2; // Seconds

    private final String SSDP_IP = "239.255.255.250";

    private String UPNPUrl = "";

    public FindRouter(){

    }

    public boolean search() throws IOException {
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
        captureSocket.setSoTimeout(5000);
        while(true) {
            try {
                byte[] routerResponseArray = new byte[1024];
                DatagramPacket routerResponsePacket = new DatagramPacket(routerResponseArray, routerResponseArray.length);
                captureSocket.receive(routerResponsePacket);

                Log.info("ROUTER RESPONSE");
                String routerResponseMessage = new String(routerResponsePacket.getData());
                Log.info(routerResponseMessage);

                if(routerResponseMessage.contains("LOCATION")){
                    String url = routerResponseMessage.substring(routerResponseMessage.indexOf("http"),
                                                                 routerResponseMessage.indexOf("\n",
                                                                 routerResponseMessage.indexOf("http")));
                    Log.info("ROUTER URL: " + url);
                    UPNPUrl = url;
                }
            }catch (SocketTimeoutException e){
                Log.warn("TIMED OUT. Please wait...");
                break;
            }
        }

        captureSocket.disconnect();
        captureSocket.close();

        return !UPNPUrl.isEmpty();
    }

    public static String getPublicIP(){
        String systemipaddress = "";
        try
        {
            URL url_name = new URL("http://bot.whatismyipaddress.com");

            BufferedReader sc =
                    new BufferedReader(new InputStreamReader(url_name.openStream()));

            // reads system IPAddress
            systemipaddress = sc.readLine().trim();
        }
        catch (Exception e)
        {
            systemipaddress = "Cannot Execute Properly";
        }
        return systemipaddress;
    }

    public String getUPNPUrlDescriptor(){
        return UPNPUrl;
    }
}
