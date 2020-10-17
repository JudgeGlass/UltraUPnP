package net.zicron.ultraupnp;

import java.io.IOException;
import java.net.*;

public class Main {
    public static void main(String args[]){
        new Main();
    }

    public Main(){
        FindRouter findRouter = new FindRouter();
        try {
            findRouter.search();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void discovery () throws IOException {
        // SSDP port
        final int SSDP_PORT = 1900;
        final int SSDP_SEARCH_PORT = 1901;
        // Broadcast address for finding routers.
        final String SSDP_IP = "239.255.255.250";
        // Time out of the connection.
        int TIMEOUT = 5000;
        // Localhost address.
        InetAddress localhost = InetAddress.getLocalHost();

        // Send from localhost:1901
        InetSocketAddress srcAddress = new InetSocketAddress(localhost, SSDP_SEARCH_PORT);
        // Send to 239.255.255.250:1900
        InetSocketAddress dstAddress = new InetSocketAddress(InetAddress.getByName(SSDP_IP), SSDP_PORT);

        // ----------------------------------------- //
        //       Construct the request packet.       //
        // ----------------------------------------- //
        StringBuffer discoveryMessage = new StringBuffer();
        discoveryMessage.append("M-SEARCH * HTTP/1.1\r\n");
        discoveryMessage.append("HOST: " + SSDP_IP + ":" + SSDP_PORT + "\r\n");
        discoveryMessage.append("ST: urn:schemas-upnp-org:device:InternetGatewayDevice:1\r\n");
        // ST: urn:schemas-upnp-org:service:WANIPConnection:1\r\n
        discoveryMessage.append("MAN: \"ssdp:discover\"\r\n");
        discoveryMessage.append("MX: 2\r\n");
        discoveryMessage.append("\r\n");
        System.out.println("Request: " + discoveryMessage.toString() + "\n");
        byte[] discoveryMessageBytes = discoveryMessage.toString().getBytes();
        DatagramPacket discoveryPacket = new DatagramPacket(discoveryMessageBytes, discoveryMessageBytes.length, dstAddress);

        // ----------------------------------- //
        //       Send multi-cast packet.       //
        // ----------------------------------- //
        MulticastSocket multicast = null;
        try {
            multicast = new MulticastSocket(null);
            multicast.bind(srcAddress);
            multicast.setTimeToLive(4);
            System.out.println("Send multicast request.");
            // ----- Sending multi-cast packet ----- //
            multicast.send(discoveryPacket);
        } finally {
            System.out.println("Multicast ends. Close connection.");
            multicast.disconnect();
            multicast.close();
        }

        // -------------------------------------------------- //
        //       Listening to response from the router.       //
        // -------------------------------------------------- //
        DatagramSocket wildSocket = null;
        DatagramPacket receivePacket = null;
        try {
            wildSocket = new DatagramSocket(SSDP_SEARCH_PORT);
            wildSocket.setSoTimeout(TIMEOUT);
            // ----- Sending datagram packet ----- //
            System.out.println("Send datagram packet.");
            wildSocket.send(discoveryPacket);

            while (true) {
                try {
                    System.out.println("Receive ssdp.");
                    receivePacket = new DatagramPacket(new byte[1536], 1536);
                    wildSocket.receive(receivePacket);
                    String message = new String(receivePacket.getData());
                    System.out.println("Recieved messages:");
                    System.out.println(message);
                } catch (SocketTimeoutException e) {
                    System.err.print("Time out.");
                    break;
                }
            }
        } finally {
            if (wildSocket != null) {
                wildSocket.disconnect();
                wildSocket.close();
            }
        }
    }
}
