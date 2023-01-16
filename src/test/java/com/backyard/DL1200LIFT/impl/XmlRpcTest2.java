package com.backyard.DL1200LIFT.impl;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

import java.net.URL;

/**
 * aXMLRPC
 */
public class XmlRpcTest2 {
    public static void main(String[] args) {
        try {
            XMLRPCClient client = new XMLRPCClient(new URL("http://127.0.0.1/"));

            Integer i = (Integer) client.call("add", 5, 10);
            System.out.println(i);
        } catch (XMLRPCServerException ex) {
            // The server throw an error.
        } catch (XMLRPCException ex) {
            // An error occured in the client.
        } catch (Exception ex) {
            // Any other exception
        }
    }
}
