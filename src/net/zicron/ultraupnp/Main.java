package net.zicron.ultraupnp;

import net.zicron.ultraupnp.gui.MainWindow;

public class Main {
    public static void main(String[] args){
        if(args.length > 0){
            new UltraUPnP(args);
        }else{
            UltraUPnP.isConsole = false;
            new MainWindow().run(args);
        }
    }
}

