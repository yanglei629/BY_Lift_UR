package com.backyard.DL1200LIFT.impl;

import helma.xmlrpc.XmlRpcClient;
import helma.xmlrpc.XmlRpcException;

import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

public class XmlRpcTest4 {
    public static void main(String[] args) {
        try {
            // Create an object to represent our server.
            XmlRpcClient server = new XmlRpcClient(new URL("http://127.0.0.1:4444"));
            // Build our parameter list.
            Vector params = new Vector();
            params.addElement(new Integer(5));
            params.addElement(new Integer(3));

            // Call the server, and get our result.
            Hashtable result =
                    (Hashtable) server.execute("sample.sumAndDifference", params);
            int sum = ((Integer) result.get("sum")).intValue();
            int difference = ((Integer) result.get("difference")).intValue();

            // Print out our result.
            System.out.println("Sum: " + Integer.toString(sum) +
                    ", Difference: " +
                    Integer.toString(difference));

        } catch (XmlRpcException exception) {
            System.err.println("JavaClient: XML-RPC Fault #" +
                    Integer.toString(exception.code) + ": " +
                    exception.toString());
        } catch (Exception exception) {
            System.err.println("JavaClient: " + exception.toString());
        }
    }
}
