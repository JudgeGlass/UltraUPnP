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


public class PortMapping {
    private String hostname;

    private String internalPort;
    private String externalPort;
    private String protocol;
    private String description;

    public PortMapping(String hostname, String internalPort, String externalPort, String protocol, String description){
        this.hostname = hostname;
        this.internalPort = internalPort;
        this.externalPort = externalPort;
        this.protocol = protocol;
        this.description = description;
    }

    public String getHostname() {
        return hostname;
    }

    public String getInternalPort() {
        return internalPort;
    }

    public String getExternalPort() {
        return externalPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getDescription() {
        return description;
    }
}
