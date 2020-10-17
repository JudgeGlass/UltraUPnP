package net.zicron.ultraupnp;

/**
 * UltraUPNP
 *
 *
 * **/


import java.io.IOException;
import java.net.InetAddress;

public class UltraUPnP {
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
        boolean udp = false;

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
        router.portForward(internPort, externPort, host, udp);
    }
}
