package com.backyard.DL1200LIFT.impl;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class XmlRpcTest {
    public static void main(String[] args) {

        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://127.0.0.1:4444"));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            Vector params = new Vector();

            params.addElement(new Integer(1));
            params.addElement(new Integer(2));

            /*Object result = client.execute("add", params);

            int sum = ((Integer) result).intValue();
            System.out.println("The sum is: " + sum);*/

            ArrayList<Integer> params1 = new ArrayList<Integer>();
            //Object[] params1 = new Object[0];
            params1.add(new Integer(500));

            Object result1 = client.execute("set_target_pos", params1);
            System.out.println("result1: " + result1);

        } catch (Exception exception) {
            exception.printStackTrace();
            System.err.println("JavaClient: " + exception);
        }
    }
}
