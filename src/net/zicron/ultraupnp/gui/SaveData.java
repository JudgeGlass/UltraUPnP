package net.zicron.ultraupnp.gui;

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


import net.zicron.ultraupnp.Log;

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
        Log.info("Writing save...");
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.print(txt);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
