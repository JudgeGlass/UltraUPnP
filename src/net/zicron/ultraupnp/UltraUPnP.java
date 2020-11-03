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
    public static final String VERSION = "1.0.0";
    public static final boolean IS_BETA = true;

    public static void main(String[] args){
        if(args.length <= 0){
            System.out.println("Usage: UltraUPnP.jar -externalPort <INT> -internalPort <INT> -host <STRING> -proto <String: UDP|TCP>");
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

    public void addPortMapping(String args[], Router router) throws IOException{
        CommandParser.currentParser.add(args);
        Log.info("Attempting: " + router.getExternalIPAddress() + ":" + CommandParser.currentParser.externalPort + " --> " + InetAddress.getLocalHost().toString() + ":" + CommandParser.currentParser.internalPort);
        int internalPort = CommandParser.currentParser.internalPort;
        int externalPort = CommandParser.currentParser.externalPort;
        String host = CommandParser.currentParser.host;
        String proto = CommandParser.currentParser.protocol;
        router.portForward(internalPort, externalPort, host, proto);
    }

    public void removePortMapping(String args[], Router router) throws IOException{
        CommandParser.currentParser.remove(args);
        router.removeMapping(CommandParser.currentParser.externalPort, CommandParser.currentParser.host, CommandParser.currentParser.protocol);
    }

    public String getExternalIPAddress(Router router) throws IOException{
        return router.getExternalIPAddress();
    }


    private void handleCommand(String[] args, Router router) throws IOException{
        switch (args[0]){
            case "-add":
                addPortMapping(args, router);
                break;
            case "-remove":
                removePortMapping(args, router);
                break;
            case "-externalAddress":
                Log.info("External IP: " + getExternalIPAddress(router));
                break;
            default:
                Log.error("Unknown Argument: " + args[0]);
                return;

        }


        //router.portForward(internPort, externPort, host, proto);
        //router.removeMapping(7979, "192.168.86.54", Router.TCP);

        List<Router.RouterArgument> routerArgumentList = router.getPortMappings(1);
        for(Router.RouterArgument ra: routerArgumentList){
            Log.info("RESPONSE: <" + ra.getArgName() + ">" + ra.getArgValue() + "</" + ra.getArgName() + ">");
        }
    }
}
