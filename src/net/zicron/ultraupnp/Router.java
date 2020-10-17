package net.zicron.ultraupnp;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Router {

    private class RouterArgument{
        private String argName;
        private String argValue;

        public RouterArgument(String argName, String argValue){
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


    private String UPNPUrl;

    private String controlUrl;
    private String serviceType;

    public Router(String UPNPUrl){
        this.UPNPUrl = UPNPUrl;

        try {
            getControlURL();

            System.out.println("SERVICE TYPE: " + serviceType);
            System.out.println("CONTORL URL: " + controlUrl);

            List<RouterArgument> routerArguments = new ArrayList<>();
            routerArguments.add(new RouterArgument("NewRemoteHost", ""));
            routerArguments.add(new RouterArgument("NewProtocol", "TCP"));
            routerArguments.add(new RouterArgument("NewInternalClient", "127.0.0.1"));
            routerArguments.add(new RouterArgument("NewExternalPort", "7879"));
            routerArguments.add(new RouterArgument("NewInternalPort", "7879"));
            routerArguments.add(new RouterArgument("NewPortMappingDescription", "UltraUPNP"));
            routerArguments.add(new RouterArgument("NewLeaseDuration", "0"));

            sendCommand("AddPortMapping", routerArguments);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
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

    public void sendCommand(String action, List<RouterArgument> routerArguments) throws IOException, ParserConfigurationException, SAXException {
        String SOAPData = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n" +
                "<s:Body>\r\n" +
                "<m:" + action + " xmlns:m=\"" + serviceType + "\">\r\n";

        for(RouterArgument routerArgument: routerArguments) {
            SOAPData += "<" + routerArgument.argName + ">" + routerArgument.argValue + "</" + routerArgument.argName + ">\r\n";
        }

        SOAPData += "</m:" + action + ">\r\n" +
                "</s:Body>\r\n" +
                "</s:Envelope>\r\n";

        System.out.println(SOAPData);

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
        System.out.println("Router Response: " + code + " " + response);

        connection.disconnect();
    }
}
