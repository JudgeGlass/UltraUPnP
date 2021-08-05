package net.zicron.ultraupnp;

/*
 * Copyright 2020 Hunter Wilcox
 * Copyright 2020 Zicron-Technologies
 *
 * This file is part of UltraUPNP.
 *
 * UltraUPNP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraUPNP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraUPNP.  If not, see <http://www.gnu.org/licenses/>.
 */



import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Router {

    public static class RouterArgument{
        private final String argName;
        private String argValue;

        public RouterArgument(final String argName, final String argValue){
            this.argName = argName;
            this.argValue = argValue;
        }

        public String getArgName(){
            return argName;
        }

        public String getArgValue(){
            return argValue;
        }
    }

    public static final String UDP = "UDP";
    public static final String TCP = "TCP";
    private final String UPNPUrl;

    private String controlUrl;
    private String serviceType;

    private boolean isConnected = false;

    public Router(String UPNPUrl){
        this.UPNPUrl = UPNPUrl;

        try {
            getControlURL();

            Log.info("SERVICE TYPE: " + serviceType);
            Log.info("CONTROL URL: " + controlUrl);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    private void getControlURL() throws ParserConfigurationException, IOException, SAXException {
        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(UPNPUrl);
        NodeList routerServices = d.getElementsByTagName("service");

        String controlUrl = "";
        String serviceType = "";

        for(int i = 0; i < routerServices.getLength(); i++){
            Node service = routerServices.item(i);
            NodeList childrenNodes = service.getChildNodes();
            for(int j = 0; j < childrenNodes.getLength(); j++){
                Node item = childrenNodes.item(j);
                if(item.getNodeName().equals("serviceType")){
                    serviceType = item.getFirstChild().getNodeValue();
                }

                if(item.getNodeName().equals("controlURL")){
                    controlUrl = item.getFirstChild().getNodeValue();
                }
            }

            if(serviceType.toLowerCase().contains(":wanipconnection:") || serviceType.toLowerCase().contains(":wanpppcontion:")){
                this.serviceType = serviceType;
                this.controlUrl = UPNPUrl.substring(0, UPNPUrl.indexOf("/", 7)) + controlUrl;
                isConnected = true;
            }
        }
    }

    public void portForward(int internalPort, int externalPort, String host, String proto) throws IOException {
        portForward(internalPort, externalPort, host, proto, "UltraUPnP");
    }

    public List<RouterArgument> portForward(int internalPort, int externalPort, String host, String proto, String description) throws IOException {
        Log.info("Attempting: " + getExternalIPAddress() + ":" + externalPort + " --> " + getInternalAddress() + ":" + internalPort);
        List<RouterArgument> routerArguments = new ArrayList<>();
        routerArguments.add(new RouterArgument("NewRemoteHost", ""));
        routerArguments.add(new RouterArgument("NewProtocol", proto));
        routerArguments.add(new RouterArgument("NewInternalClient", host));
        routerArguments.add(new RouterArgument("NewExternalPort", Integer.toString(externalPort)));
        routerArguments.add(new RouterArgument("NewInternalPort", Integer.toString(internalPort)));
        routerArguments.add(new RouterArgument("NewPortMappingDescription", description));
        routerArguments.add(new RouterArgument("NewLeaseDuration", "0"));

        List<RouterArgument> resp = sendCommand("AddPortMapping", routerArguments);
        routerArguments.clear();

        return resp;
    }

    public void removeMapping(int externalPort, String host, String proto) throws IOException {
        Log.debug("Removing mapping...");
        List<RouterArgument> routerArguments = new ArrayList<>();
        routerArguments.add(new RouterArgument("NewRemoteHost", host));
        routerArguments.add(new RouterArgument("NewExternalPort", Integer.toString(externalPort)));
        routerArguments.add(new RouterArgument("NewProtocol", proto));

        sendCommand("DeletePortMapping", routerArguments);
        routerArguments.clear();
    }

    public String getExternalIPAddress() throws IOException {
        Log.debug("Getting external IP Address");
        List<RouterArgument> routerArguments = new ArrayList<>();
        //routerArguments.add(new RouterArgument("NewExternalIPAddress", "ExternalIPAddress"));

        List<RouterArgument> response = sendCommand("GetExternalIPAddress", routerArguments);
        routerArguments.clear();

        if(response == null){ // If the router does not support this feature
            Log.error("Could not get external address from router! Attempting third-party...");
            URL url_name = new URL("http://bot.whatismyipaddress.com");
            BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream()));

            // reads system IPAddress
            return sc.readLine().trim();
        }


        String ip = "";
        for(RouterArgument routerArgument: response){
            if(routerArgument.getArgName().equals("NewExternalIPAddress")){
                ip = routerArgument.getArgValue();
            }
        }

        if(response.size() == 0){
            ip = "ERROR";
        }

        response.clear();

        return ip;
    }

    public List<RouterArgument> getPortMappings() throws IOException {
        List<RouterArgument> routerArguments = new ArrayList<>();
        routerArguments.add(new RouterArgument("NewPortMappingIndex", Integer.toString(0)));

        List<RouterArgument> response;
        List<RouterArgument> mappings = new ArrayList<>();
        int counter = 1;

        // Kind of a bad way of doing it, but it works?
        while((response = sendCommand("GetGenericPortMappingEntry", routerArguments)) != null){
            routerArguments.get(0).argValue = Integer.toString(counter);
            mappings.addAll(response);
            counter++;
        }

        for(int i = 0; i < mappings.size(); i++){
            Log.debug("COUNT: " + i +"<"+mappings.get(i).argName+">" + mappings.get(i).argValue + "</" + mappings.get(i).argName + ">");
        }

        routerArguments.clear();
        return mappings;
    }

    private List<RouterArgument> sendCommand(String action, List<RouterArgument> routerArguments) throws IOException {
        String SOAPData = "<?xml version=\"1.0\"?>\r\n<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" +
                "<s:Body>\r\n" +
                "<m:" + action + " xmlns:m=\"" + serviceType + "\">\r\n";

        for(RouterArgument routerArgument: routerArguments) {
            SOAPData += "<" + routerArgument.getArgName() + ">" + routerArgument.getArgValue() + "</" + routerArgument.getArgName() + ">\r\n";
        }

        SOAPData += "</m:" + action + ">\r\n" +
                "</s:Body>\r\n" +
                "</s:Envelope>\r\n";

        Log.debug("SOAP DATA: \n" + SOAPData);

        HttpURLConnection connection = (HttpURLConnection) new URL(controlUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "text/xml");
        connection.setRequestProperty("SOAPAction", "\"" + serviceType + "#" + action + "\"");
        connection.setRequestProperty("Connection", "Close");
        connection.setRequestProperty("Content-Length", "" + SOAPData.length());
        connection.getOutputStream().write(SOAPData.getBytes());
        //connection.getOutputStream().flush();

        int code = connection.getResponseCode();
        String response = connection.getResponseMessage();
        Log.info("Router Response: " + code + " " + response);

        List<RouterArgument> routerResponse = new ArrayList<>();
        if(code != 200){
            Log.error("There was an error processing your request! Router response: " + code + " (This is normal for GetPortMappings list)");
            connection.disconnect();
            return null;
        }


        try {
            Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(connection.getInputStream());
            String tagName = "u:" + action + "Response";
            NodeList list = d.getElementsByTagName(tagName);
            Log.debug("LEN: " + list.getLength());
            for(int i = 0; i < list.getLength(); i++){
                NodeList l = list.item(i).getChildNodes();
                for(int j = 0; j < l.getLength(); j++){
                    Node n = l.item(j);
                    routerResponse.add(new RouterArgument(n.getNodeName(), n.getTextContent()));
                }

            }
        } catch (SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        connection.disconnect();

        return routerResponse;
    }

    public static String getInternalAddress(){
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        }catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }

        return "";
    }
}
