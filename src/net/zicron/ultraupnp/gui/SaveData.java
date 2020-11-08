package net.zicron.ultraupnp.gui;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaveData {

    public static List<PortMapping> getPortMappingsFromFile(String filename) throws IOException {
        if(!new File(filename).exists()){
            return null;
        }
        List<PortMapping> portMappings = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while((line = br.readLine()) != null){
            String[] lineData = line.split(":");
            if(lineData[0].equals("PORT-ENTRY")){
                String host = lineData[1];
                String proto = lineData[2];
                String externalPort = lineData[3];
                String internalPort = lineData[4];
                String description = lineData[5];
                portMappings.add(new PortMapping(host, internalPort, externalPort, proto, description));
            }
        }

        return portMappings;
    }

    public static void writeFile(String fileName, String txt) {
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.print(txt);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
