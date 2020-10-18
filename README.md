# UltraUPnP
UltraUPnP is a standalone jar / library that can be used to forward ports on a router without logging into the router.

# Requirements
- UPnP enabled on your router.
- UPnP compatible router.
- Java 8+


# Usage
You must have Java 8+
```bash
java -jar UltraUPnP1.0.0.jar -externalPort <INT> -internalPort <INT> -host <STRING> -proto <String: UDP|TCP>
```

# Issues
This program may not work on some routers. I have only tested them on two different routers. If you have a solution to your router
feel free to contribute it.

# Resources Used
http://upnp.org/specs/gw/UPnP-gw-WANIPConnection-v2-Service.pdf
http://upnp.org/resources/documents/UPnP_UDA_tutorial_July2014.pdf