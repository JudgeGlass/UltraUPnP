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
}
