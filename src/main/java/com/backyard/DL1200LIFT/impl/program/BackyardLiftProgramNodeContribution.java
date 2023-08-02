package com.backyard.DL1200LIFT.impl.program;

import java.util.Timer;

import com.backyard.DL1200LIFT.impl.i18n.LanguagePack;
import com.backyard.DL1200LIFT.impl.i18n.TextResource;
import com.backyard.DL1200LIFT.impl.installation.BackyardLiftInstallationNodeContribution;
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;
import com.ur.urcap.api.domain.undoredo.UndoableChanges;
import com.ur.urcap.api.domain.userinteraction.inputvalidation.InputValidationFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;

import scriptCommunicator.ScriptExporter;
import scriptCommunicator.ScriptSender;

public class BackyardLiftProgramNodeContribution implements ProgramNodeContribution {

    private final ProgramAPI programAPI;
    private final UndoRedoManager undoRedoManager;
    private static final String POSKEY = "pos";
    private static final int DEFAULT_POS = 0;

    private static final String DISPOSKEY = "DISPOSKEY";

    // Used to send a URScript for execution
    private final ScriptSender sender;
    // Instance of ScriptExporter
    // Used to extract values from URScript
    private final ScriptExporter exporter;

    private final BackyardLiftProgramNodeView view;
    private final DataModel model;

    private final KeyboardInputFactory keyboardFactory;
    private final InputValidationFactory keyboardInputValidationFactory;
    private Timer uiTimer;        //UI updates from non-GUI threads must use EventQueue.invokeLater (or SwingUtilities.invokeLater)
    private boolean isViewOpen = false;

    private final LanguagePack languagePack;

    public BackyardLiftProgramNodeContribution(ProgramAPIProvider apiProvider, BackyardLiftProgramNodeView view, DataModel model) {
        this.programAPI = apiProvider.getProgramAPI();
        this.undoRedoManager = apiProvider.getProgramAPI().getUndoRedoManager();
        this.keyboardFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();

        keyboardInputValidationFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getInputValidationFactory();

        this.view = view;
        this.model = model;
        this.sender = new ScriptSender();
        this.exporter = new ScriptExporter();

        languagePack = new LanguagePack(apiProvider.getSystemAPI().getSystemSettings().getLocalization());

        setPos(10);
    }

    public InputValidationFactory getKeyboardInputValidationFactory() {
        return keyboardInputValidationFactory;
    }

    private TextResource getTextResource() {
        return languagePack.getTextResource();
    }

    @Override
    public void openView() {
        //international
        view.setArgumentText(getTextResource().argument());
        view.setStatusText(getTextResource().status());
        view.setTargetPos(getTextResource().targetPos() + ":");

        view.setPerformBtn(getTextResource().perform());

        view.setTargetPosLabel(getTextResource().targetPos() + ":");

        view.setCurrentPosLabel(getTextResource().currentPos() + ":");

        view.setMovingStatus(getTextResource().status() + ":");

        view.setConnectionStatus(getTextResource().connectionStatus() + ":");

        view.setAutoConnection(getTextResource().autoActivation());

        view.setStopBtn(getTextResource().stop());

        //refresh status
        view.showPos(getPos());
        view.showAutoActivate(getInstalltion().getAutoActivation());


        isViewOpen = true;
        //refresh status
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isViewOpen) {
                    updateUI();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }


    @Override
    public void closeView() {
        if (uiTimer != null) {
            uiTimer.cancel();
        }

        isViewOpen = false;
    }

    @Override
    public String getTitle() {
        return "BYLift: Pos : " + (model.isSet(POSKEY) ? getPos() : "");
        //return "BYLift: Pos : " + getPos();
    }

    @Override
    public boolean isDefined() {
        return true;
    }

    @Override
    public void generateScript(ScriptWriter scriptWriter) {
        //build rpc client
        scriptWriter.appendLine("BY_lift=rpc_factory(\"xmlrpc\",\"http://127.0.0.1:9999/\")");
        scriptWriter.appendLine("BY_lift.cancel_stop()");
        scriptWriter.appendLine("BY_lift.set_target_pos(" + getPos() + ")");


        scriptWriter.appendLine("while BY_lift.get_current_pos() != " + getPos() + ":");
        scriptWriter.appendLine("    sleep(1)");
        scriptWriter.appendLine("end");
    }

    public int getPos() {
        return model.get(POSKEY, DEFAULT_POS);
    }

    public void onPosSelection(final int pos) {
        undoRedoManager.recordChanges(new UndoableChanges() {
            @Override
            public void executeChanges() {
                model.set(POSKEY, pos);
            }
        });
    }

    public void setTargetPos(int value) {
        view.showPos(getPos());
        getInstallation().getXmlRpcDaemonInterface().set_target_pos(value);
    }

    public int getTargetPos() {
        int returnValue = getInstallation().getXmlRpcDaemonInterface().get_target_pos();
        return returnValue;
    }

    public int getCurrentPos() {
        int returnValue = getInstallation().getXmlRpcDaemonInterface().get_current_pos();
        return returnValue;
    }


    public int getMovingStatus() {
        int returnValue = getInstallation().getXmlRpcDaemonInterface().get_running_status();
        return returnValue;
    }

    private void updateUI() {
        System.out.println("refresh state........................");
        int targetPos = getTargetPos();
        view.setTargetPosLabel(getTextResource().targetPos() + ":" + targetPos + "mm");

        int currentPos = getCurrentPos();
        view.setCurrentPosLabel(getTextResource().currentPos() + ":" + currentPos + "mm");

        int value = getMovingStatus();
        if (value == 1) {
            view.setMovingStatus(getTextResource().status() + ":" + getTextResource().moving());
        }
        if (value == 0) {
            if (targetPos != currentPos) {
                view.setMovingStatus(getTextResource().status() + ":" + getTextResource().unAchievable());
            } else {
                view.setMovingStatus(getTextResource().status() + ":" + getTextResource().stopped());
            }
        }

        if (value == -1) {
            view.setConnectionStatus(getTextResource().connectionStatus() + ":" + getTextResource().disconnected());
        } else {
            view.setConnectionStatus(getTextResource().connectionStatus() + ":" + getTextResource().connected());
        }
    }

    private BackyardLiftInstallationNodeContribution getInstallation() {
        return programAPI.getInstallationNode(BackyardLiftInstallationNodeContribution.class);
    }

    public KeyboardNumberInput<Integer> getInputPosForTextField() {
        KeyboardNumberInput<Integer> keyboardInput = keyboardFactory.createIntegerKeypadInput();
        keyboardInput.setInitialValue(getPos());
        return keyboardInput;
    }

    public void setPos(Integer pos) {
        model.set(POSKEY, pos);
    }

    public BackyardLiftInstallationNodeContribution getInstalltion() {
        return this.programAPI.getInstallationNode(BackyardLiftInstallationNodeContribution.class);
    }
}
