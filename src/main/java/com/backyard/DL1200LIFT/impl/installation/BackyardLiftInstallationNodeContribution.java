package com.backyard.DL1200LIFT.impl.installation;

import com.backyard.DL1200LIFT.impl.LiftDaemonService;
import com.backyard.DL1200LIFT.impl.i18n.CommandNamesResource;
import com.backyard.DL1200LIFT.impl.i18n.LanguagePack;
import com.backyard.DL1200LIFT.impl.i18n.TextResource;
import com.backyard.DL1200LIFT.impl.i18n.UnitsResource;
import com.backyard.DL1200LIFT.impl.util.XmlRpcMyDaemonInterface;
import com.ur.urcap.api.contribution.DaemonContribution;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import scriptCommunicator.ScriptCommand;
import scriptCommunicator.ScriptExporter;
import scriptCommunicator.ScriptSender;

public class BackyardLiftInstallationNodeContribution implements InstallationNodeContribution {

    private static final String IP_KEY = "inputIP";
    private static final String DEFAULT_IP = "192.168.10.11";
    private final BackyardLiftInstallationNodeView view;
    private final KeyboardInputFactory keyboardFactory;
    private DataModel model;

    //communication scripter
    private final ScriptSender sender;
    // Instance of ScriptExporter
    // Used to extract values from URScript
    private final ScriptExporter exporter;
    public static final int PORT = 9999;
    private XmlRpcMyDaemonInterface xmlRpcDaemonInterface;
    private final LiftDaemonService daemonService;

    private final LanguagePack languagePack;
    private boolean isViewOpen = false;
    private boolean isConnected = false;

    public BackyardLiftInstallationNodeContribution(InstallationAPIProvider apiProvider, DataModel model, BackyardLiftInstallationNodeView view, LiftDaemonService daemonService) {
        this.keyboardFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
        this.model = model;
        this.view = view;
        this.sender = new ScriptSender();
        this.exporter = new ScriptExporter();

        this.daemonService = daemonService;
        xmlRpcDaemonInterface = new XmlRpcMyDaemonInterface("127.0.0.1", PORT);

        languagePack = new LanguagePack(apiProvider.getSystemAPI().getSystemSettings().getLocalization());
    }

    public TextResource getTextResource() {
        return languagePack.getTextResource();
    }

    private CommandNamesResource getCommandNamesResource() {
        return languagePack.getCommandNamesResource();
    }

    private UnitsResource getUnitsResource() {
        return languagePack.getUnitsResource();
    }


    @Override
    public void openView() {
        view.setIpLabel(createIPString());
        view.setConnectBtn(createConnectString());
        view.setDisconnectBtn(createDisconnectString());
        view.setConnectStatusLabel(getTextResource().No_Connection());
        view.setControlModeLabel(getTextResource().controlMode() + ":");
        view.setUpBtn(getTextResource().up());
        view.setDownBtn(getTextResource().down());
        view.setZeroCalibBtn(getTextResource().zeroCalibration());
        view.setStopBtn(getTextResource().Stop());

        view.showIP(getIP());

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (daemonService.getDaemon().getState() != DaemonContribution.State.RUNNING)
                        BackyardLiftInstallationNodeContribution.this.awaitDaemonRunning(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();*/


        //monitor connection
        isViewOpen = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isViewOpen) {
                    if (isConnected) {

                    }
                    Integer current_pos = getXmlRpcMyDaemonInterface().get_current_pos();
                    if (current_pos == null || current_pos == -1) {
                        view.setDisconnect(-1, null);
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    private String createDisconnectString() {
        return getTextResource().disconnect();
    }

    private String createIPString() {
        return getTextResource().ip() + ":";
    }

    private String createConnectString() {
        return getTextResource().connect();
    }

    @Override
    public void closeView() {
        isViewOpen = false;
    }

    public boolean isDefined() {
        return !getIP().isEmpty();
    }

    @Override
    public void generateScript(ScriptWriter writer) {
    }

    //	get ip address
    public String getIP() {
        return model.get(IP_KEY, DEFAULT_IP);
    }

    public void setIP(String msg) {
        model.set(IP_KEY, msg);
    }

    public void moveUp(boolean b) {
        xmlRpcDaemonInterface.lift_up(b);
    }

    public void moveDown(boolean b) {
        ScriptCommand sendMoveDownCommand = new ScriptCommand("moveUp");
        xmlRpcDaemonInterface.lift_down(b);
    }

    public void calibrate() {
        xmlRpcDaemonInterface.calibrate();
    }

    public XmlRpcMyDaemonInterface getXmlRpcDaemonInterface() {
        return xmlRpcDaemonInterface;
    }

    public void awaitDaemonRunning(long timeOutMilliSeconds) throws InterruptedException {
        System.out.println("Start Daemon Before" + daemonService.getDaemon().getState());
        daemonService.getDaemon().start();
        long endTime = System.nanoTime() + timeOutMilliSeconds * 1000L * 1000L;
        while (System.nanoTime() < endTime && (daemonService.getDaemon().getState() != DaemonContribution.State.RUNNING)) {
            System.out.println("wait for daemon start....");
            //ERROR
            //System.out.println(daemonService.getDaemon().getState());
            Thread.sleep(100);
        }
        System.out.println("Start Daemon After: " + daemonService.getDaemon().getState());
    }

    public void setMode(String selected) {
        this.model.set("MODE", selected);
    }

    public XmlRpcMyDaemonInterface getXmlRpcMyDaemonInterface() {
        return this.xmlRpcDaemonInterface;
    }

    public KeyboardInputFactory getKeyboardFactory() {
        return this.keyboardFactory;
    }


    public void disConnect() {
        Integer result = getXmlRpcDaemonInterface().disconnect();
        if (result >= 0) {
            this.view.setDisconnect(result, this);
        }
    }

    public void startDaemon() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (daemonService.getDaemon().getState() != DaemonContribution.State.RUNNING)
                        BackyardLiftInstallationNodeContribution.this.awaitDaemonRunning(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stopDaemon() {
        disConnect();
        System.out.println("Stop Daemon Status Before: " + daemonService.getDaemon().getState());
        daemonService.getDaemon().stop();
        System.out.println("Stop Daemon Status After: " + daemonService.getDaemon().getState());
    }

    public void stopLift() {
        getXmlRpcDaemonInterface().stop();
    }
}
