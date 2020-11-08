package net.zicron.ultraupnp;

/*
***********************************************************************
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
 *
 *******************************************************************
 *
 * Ref: http://upnp.org/specs/gw/UPnP-gw-WANIPConnection-v2-Service.pdf
 *      http://upnp.org/resources/documents/UPnP_UDA_tutorial_July2014.pdf
 */


import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class UltraUPnP {
    public static final String VERSION = "1.1.2";
    public static final boolean IS_BETA = false;

    public static void main(String[] args){
        if(args.length <= 0){
            System.out.println("Usage: UltraUPnP.jar\nAdd: -add -externalPort <INT> -internalPort <INT> -host <STRING> -proto <String: UDP|TCP>\n" +
                               "Remove: -remove -externalPort <INT> -host <STRING> -proto <String: UDP|TCP>");
            return;
        }
        Log.info("Starting UltraUPnP v" + VERSION + "...");
        new UltraUPnP(args);
    }

    public UltraUPnP(String[] args){
        RouterFinder routerFinder = new RouterFinder();
        Router router = null;
        try {
            if(routerFinder.search()){
                router = new Router(routerFinder.getUPNPUrlDescriptor());
            }else{ return; }

            handleCommand(args, router);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPortMapping(String[] args, Router router, CommandParser currentParser) throws IOException{
        currentParser.add(args);
        int internalPort = currentParser.internalPort;
        int externalPort = currentParser.externalPort;
        String host = currentParser.host;
        String proto = currentParser.protocol;
        String description = currentParser.description;
        router.portForward(internalPort, externalPort, host, proto, description);
    }

    public void removePortMapping(String[] args, Router router, CommandParser currentParser) throws IOException{
        currentParser.remove(args);
        router.removeMapping(currentParser.externalPort, currentParser.host, currentParser.protocol);
    }

    public String getExternalIPAddress(Router router) throws IOException{
        return router.getExternalIPAddress();
    }

    public void listMappings(Router router) throws IOException {
        List<Router.RouterArgument> routerArgumentList = router.getPortMappings();
        System.out.println("\tInternal Port\tExternal Port\tHost\t\tProtocol\tDescription");

        String host = "";
        String proto = "";
        String externalPort = "";
        String internalPort = "";
        String description = "";

        for(Router.RouterArgument ra: routerArgumentList){
            //Log.info("RESPONSE: <" + ra.getArgName() + ">" + ra.getArgValue() + "</" + ra.getArgName() + ">");

            switch (ra.getArgName()){
                case "NewInternalClient":
                    host = ra.getArgValue();
                    break;
                case "NewProtocol":
                    proto = ra.getArgValue();
                    break;
                case "NewInternalPort":
                    internalPort = ra.getArgValue();
                    break;
                case "NewExternalPort":
                    externalPort = ra.getArgValue();
                    break;
                case "NewPortMappingDescription":
                    description = ra.getArgValue();
                    break;
            }

            if(!proto.isEmpty() && !host.isEmpty() && !externalPort.isEmpty() && !internalPort.isEmpty() && !proto.isEmpty()) {
                System.out.println(String.format("Mapping: %s\t\t%s\t\t%s\t%s\t\t%s", internalPort, externalPort, host, proto, description));
                host = "";
                proto = "";
                externalPort = "";
                internalPort = "";
                description = "";
            }
        }
    }


    private void handleCommand(String[] args, Router router) throws IOException{
        switch (args[0]){
            case "-add":
                addPortMapping(args, router, CommandParser.currentParser);
                break;
            case "-remove":
                removePortMapping(args, router, CommandParser.currentParser);
                break;
            case "-externalAddress":
                Log.info("External IP: " + getExternalIPAddress(router));
                break;
            case "-list":
                listMappings(router);
                break;
            default:
                Log.error("Unknown Argument: " + args[0]);
                return;

        }


        //router.portForward(internPort, externPort, host, proto);
        //router.removeMapping(7979, "192.168.86.54", Router.TCP);


    }
}
