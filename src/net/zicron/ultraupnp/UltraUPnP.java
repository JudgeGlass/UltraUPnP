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
 *
 *
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

    public static void main(String args[]){
        if(args.length <= 0){
            System.out.println("Usage: UltraUPnP.jar -externalPort <INT> -internalPort <INT> -host <STRING> -proto <String: UDP|TCP>");
            return;
        }
        new UltraUPnP(args);
    }

    public UltraUPnP(String[] args){
        FindRouter findRouter = new FindRouter();
        Router router = null;
        try {
            if(findRouter.search()){
                router = new Router(findRouter.getUPNPUrlDescriptor());
            }else{ return;}

            handleCommand(args, router);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCommand(String[] args, Router router) throws IOException{
        String host = "";
        int externPort = -1;
        int internPort = -1;
        String udp = "";

        int index = 0;
        while(index < args.length){
            String arg = args[index];
            switch (arg){
                case "-externalPort":
                    externPort = Integer.parseInt(args[index+1]);
                    index += 2;
                    break;
                case "-internalPort":
                    internPort = Integer.parseInt(args[index+1]);
                    index += 2;
                    break;
                case "-host":
                    host = args[index+1];
                    index+=2;
                    break;
                case "-proto":
                    if(args[index+1].equalsIgnoreCase("udp")){
                        udp = Router.UDP;
                    }else{
                        udp = Router.TCP;
                    }
                    index += 2;
                    break;
                default:
                    Log.error("Unknown Argument: " + arg);
                    return;
            }
        }

        Log.info("Attempting: " + FindRouter.getPublicIP() + ":" + externPort + " --> " + InetAddress.getLocalHost().toString() + ":" + internPort);
        //router.portForward(internPort, externPort, host, udp);
        //router.removeMapping(7979, "192.168.86.54", Router.TCP);
        //System.out.println("External IP: " + router.getExternalIPAddress());
    }
}
