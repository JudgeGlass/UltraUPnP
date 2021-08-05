# UltraUPnP
UltraUPnP is a standalone jar / library that can be used to forward ports on a router without logging into the router.

# Requirements
- UPnP enabled on your router.
- UPnP compatible router.
- Internet Connection required
- Java 8+


# Usage
You must have Java 8+
```bash
java -jar UltraUPnP1.0.0.jar -add -externalPort <INT> -internalPort <INT> -host <STRING> -proto <String: UDP|TCP>
```

# Issues
- This program may not work on some routers. If you have a solution to your router feel free to contribute it.

Known to work:
- Google Nest Mesh v2 - Fully working
- Linksys EA7500 - Fully working
- Netgear Nighthawk RAX70 - Fully working, except GetExternalIPAddress due to MiniUPnPv1.2.0RC0 bug
- Ubiquiti ERLite-3 - Fully working
- TP-Link Deco X20 Mesh - Fully working

# Resources Used
http://upnp.org/specs/gw/UPnP-gw-WANIPConnection-v2-Service.pdf
http://upnp.org/resources/documents/UPnP_UDA_tutorial_July2014.pdf
