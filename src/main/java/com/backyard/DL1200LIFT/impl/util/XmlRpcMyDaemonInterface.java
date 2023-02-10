package com.backyard.DL1200LIFT.impl.util;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class XmlRpcMyDaemonInterface {
    private final XmlRpcClient client;

    public XmlRpcMyDaemonInterface(String host, int port) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setEnabledForExtensions(true);
        config.setConnectionTimeout(3);
        try {
            config.setServerURL(new URL("http://" + host + ":" + port + "/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        config.setConnectionTimeout(1000); //1s
        client = new XmlRpcClient();
        client.setConfig(config);
    }

    public Integer connect(String ip) {
        ArrayList<String> args = new ArrayList<String>();
        args.add(ip);
        Object result = null;
        try {
            result = client.execute("connect", args);
        } catch (XmlRpcException e) {
            e.printStackTrace();
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return (Integer) result;
    }

    public Integer disconnect() {
        ArrayList<String> args = new ArrayList<String>();
        Object result = null;
        try {
            result = client.execute("disconnect", args);
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return (Integer) result;
    }

    public Integer switch_mode(int mode) {
        ArrayList<Integer> args = new ArrayList<Integer>();
        args.add(mode);
        Object result = null;
        try {
            result = client.execute("switch_mode", args);
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return (Integer) result;
    }

    public Integer lift_up(boolean b) {
        ArrayList<Boolean> args = new ArrayList<Boolean>();
        args.add(b);
        Object result = null;
        try {
            result = client.execute("lift_up", args);
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
        return (Integer) result;
    }

    public Integer lift_down(boolean b) {
        ArrayList<Boolean> args = new ArrayList<Boolean>();
        args.add(b);
        Object result = null;
        try {
            result = client.execute("lift_down", args);
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
        return (Integer) result;
    }

    public Integer set_target_pos(Integer value) {
        ArrayList<Integer> args = new ArrayList<Integer>();
        args.add(value);
        Object result = null;
        try {
            result = client.execute("set_target_pos", args);
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
        return (Integer) result;
    }

    public void calibrate() {
        ArrayList<Integer> args = new ArrayList<Integer>();
        Object result = null;
        try {
            result = client.execute("calibrate", args);
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
    }

    public Integer get_target_pos() {
        ArrayList<Integer> args = new ArrayList<Integer>();
        Object result = null;
        try {
            result = client.execute("get_target_pos", args);
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
        return (Integer) result;
    }

    public Integer get_mode() {
        ArrayList<Integer> args = new ArrayList<Integer>();
        Object result = null;
        try {
            result = client.execute("get_mode", args);
        } catch (XmlRpcException e) {
            throw new RuntimeException(e);
        }
        return (Integer) result;
    }

    public Integer get_current_pos() {
        ArrayList<Integer> args = new ArrayList<Integer>();
        Object result = null;
        try {
            result = client.execute("get_current_pos", args);
        } catch (Exception e) {
            System.out.println("[ERROR] XmlRpcMyDaemonInterface get_current_pos");
            //e.printStackTrace();
        }
        return (Integer) result;
    }

    public Integer get_running_status() {
        ArrayList<Integer> args = new ArrayList<Integer>();
        Object result = null;
        try {
            result = client.execute("get_running_status", args);
        } catch (XmlRpcException e) {
            e.printStackTrace();
            return -1;
        }
        return (Integer) result;
    }

    public Integer get_calibration_status() {
        ArrayList<Integer> args = new ArrayList<Integer>();
        Object result = null;
        try {
            result = client.execute("get_calibration_status", args);
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return (Integer) result;
    }

    public Integer stop() {
        ArrayList<Integer> args = new ArrayList<Integer>();
        Object result = null;
        try {
            result = client.execute("stop", args);
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return (Integer) result;
    }

    public Integer cancelStop() {
        ArrayList<Integer> args = new ArrayList<Integer>();
        Object result = null;
        try {
            result = client.execute("cancel_stop", args);
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return (Integer) result;
    }
}
