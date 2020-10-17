package net.zicron.ultraupnp;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Router {

    private static class RouterArgument{
        private final String argName;
        private final String argValue;

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

    public static final boolean UDP = true;
    public static final boolean TCP = false;
    private final String UPNPUrl;

    private String controlUrl;
    private String serviceType;

    public Router(String UPNPUrl){
        this.UPNPUrl = UPNPUrl;

        try {
            getControlURL();

            Log.info("SERVICE TYPE: " + serviceType);
            Log.info("CONTORL URL: " + controlUrl);
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

            if(serviceType.toLowerCase().contains(":wanipconnection:")){
                this.serviceType = serviceType;
                this.controlUrl = UPNPUrl.substring(0, UPNPUrl.indexOf("/", 7)) + controlUrl;
            }
        }
    }

    public void portForward(int internalPort, int externalPort, String host, boolean isUDP) throws IOException {
        portForward(internalPort, externalPort, host, isUDP, "UltraUPnP");
    }

    public void portForward(int internalPort, int externalPort, String host, boolean isUDP, String description) throws IOException {
        List<RouterArgument> routerArguments = new ArrayList<>();
        routerArguments.add(new RouterArgument("NewRemoteHost", ""));
        routerArguments.add(new RouterArgument("NewProtocol", isUDP ? "UDP" : "TCP"));
        routerArguments.add(new RouterArgument("NewInternalClient", host));
        routerArguments.add(new RouterArgument("NewExternalPort", Integer.toString(externalPort)));
        routerArguments.add(new RouterArgument("NewInternalPort", Integer.toString(internalPort)));
        routerArguments.add(new RouterArgument("NewPortMappingDescription", description));
        routerArguments.add(new RouterArgument("NewLeaseDuration", "0"));

        sendCommand("AddPortMapping", routerArguments);
    }

    private void sendCommand(String action, List<RouterArgument> routerArguments) throws IOException {
        String SOAPData = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" +
                "<s:Body>\r\n" +
                "<m:" + action + " xmlns:m=\"" + serviceType + "\">\r\n";

        for(RouterArgument routerArgument: routerArguments) {
            SOAPData += "<" + routerArgument.getArgName() + ">" + routerArgument.getArgValue() + "</" + routerArgument.getArgName() + ">\r\n";
        }

        SOAPData += "</m:" + action + ">\r\n" +
                "</s:Body>\r\n" +
                "</s:Envelope>\r\n";

        Log.info("SOAP DATA: \n" + SOAPData);

        HttpURLConnection connection = (HttpURLConnection) new URL(controlUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "text/xml");
        connection.setRequestProperty("SOAPAction", "\"" + serviceType + "#" + action + "\"");
        connection.setRequestProperty("Connection", "Close");
        connection.setRequestProperty("Content-Length", "" + routerArguments.size());
        connection.getOutputStream().write(SOAPData.getBytes());
        //connection.getOutputStream().flush();

        int code = connection.getResponseCode();
        String response = connection.getResponseMessage();
        Log.info("Router Response: " + code + " " + response);

        connection.disconnect();
    }
}
