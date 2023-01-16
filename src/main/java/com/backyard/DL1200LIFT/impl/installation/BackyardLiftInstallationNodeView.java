package com.backyard.DL1200LIFT.impl.installation;

import com.backyard.DL1200LIFT.impl.Style;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JComboBox;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BackyardLiftInstallationNodeView implements SwingInstallationNodeView<BackyardLiftInstallationNodeContribution> {

    private final Style style;
    private JTextField ipField;
    private static final String logoFilePath = "/logo/logo_backyard.jpg";

    private JLabel stateMessage;
    private JLabel ipLabel;
    private JButton connectBtn;
    private JButton bUp;
    private JButton bDown;
    private JButton zeroCalibBtn;
    private Box modeBox;
    private JLabel controlModel;
    private JButton disconnectBtn;
    private JButton stopBtn;
    private BackyardLiftInstallationNodeContribution contribution;

    public BackyardLiftInstallationNodeView(Style style) {
        this.style = style;
    }


    @Override
    public void buildUI(JPanel panel, BackyardLiftInstallationNodeContribution contribution) {
        this.contribution = contribution;

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createLogo());
        panel.add(style.createVerticalSpacing());

        /*panel.add(createDaemonControl(contribution));
        panel.add(style.createVerticalSpacing());*/

        panel.add(createInput(contribution));
        panel.add(style.createVerticalSpacing());

        panel.add(createCombo(contribution));
        panel.add(style.createVerticalSpacing());

        panel.add(createButton(contribution));
    }

    //	logo box
    private Box createLogo() {
        Box logoBox = Box.createHorizontalBox();
        logoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        ImageIcon icon = new ImageIcon(getScaledImage(getImage(logoFilePath), 400, 100));
        JLabel labelLogo = new JLabel();
        labelLogo.setIcon(icon);
        logoBox.add(labelLogo);
        return logoBox;
    }

    private Box createCombo(final BackyardLiftInstallationNodeContribution installationNode) {
        //Object[] items = {"Jog Panel Control", "Robot Remote Control"};
        Object[] items = {installationNode.getTextResource().jogMode(), installationNode.getTextResource().remoteMode()};
        JComboBox modeComboBox = new JComboBox(items);
        modeComboBox.setPreferredSize(new Dimension(200, 30));
        modeComboBox.setMaximumSize(new Dimension(200, 30));
        modeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox comboBox1 = (JComboBox) event.getSource();
                Object selected = comboBox1.getSelectedItem();
                System.out.println("select control mode: " + selected);
                installationNode.setMode((String) selected);

                //0: jog    1: remote
                if ((String) selected == installationNode.getTextResource().jogMode()) {
                    installationNode.getXmlRpcMyDaemonInterface().switch_mode(0);
                } else {
                    installationNode.getXmlRpcMyDaemonInterface().switch_mode(1);
                }
            }
        });

        modeBox = Box.createHorizontalBox();
        modeBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        controlModel = new JLabel("Control Mode");


        modeBox.add(controlModel);
        modeBox.add(modeComboBox);

        modeBox.setVisible(false);
        return modeBox;
    }

    private Box createDaemonControl(final BackyardLiftInstallationNodeContribution installationNode) {
        Box daemonControlBox = Box.createHorizontalBox();
        daemonControlBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton start_daemon = new JButton("Start Daemon");
        start_daemon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                installationNode.startDaemon();
            }
        });
        JButton stop_daemon = new JButton("Stop Daemon");
        stop_daemon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                installationNode.stopDaemon();
            }
        });
        daemonControlBox.add(start_daemon);
        daemonControlBox.add(this.style.createHorizontalSpacing(5));
        daemonControlBox.add(stop_daemon);

        return daemonControlBox;
    }

    private Box createInput(final BackyardLiftInstallationNodeContribution installationNode) {
        Box inputBox = Box.createHorizontalBox();
        inputBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        ipLabel = new JLabel("IP");
        inputBox.add(ipLabel);
        inputBox.add(style.createHorizontalSpacing());

        //ip
        ipField = new JTextField();
        ipField.setFocusable(false);
        ipField.setPreferredSize(style.getInputfieldSize());
        ipField.setMaximumSize(ipField.getPreferredSize());
        ipField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                KeyboardTextInput keyboardInput = installationNode.getKeyboardFactory().createIPAddressKeyboardInput();
                keyboardInput.setInitialValue(installationNode.getIP());
                keyboardInput.show(ipField, new KeyboardInputCallback<String>() {
                    @Override
                    public void onOk(String value) {
                        ipField.setText(value);
                        installationNode.setIP(value);
                    }
                });
            }
        });
        inputBox.add(ipField);

        connectBtn = new JButton("Connect");
        connectBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Integer connect = installationNode.getXmlRpcDaemonInterface().connect(ipField.getText());
                if (null != connect && connect >= 0) {
                    System.out.println("Connect Lift Success");
                    modeBox.setVisible(true);
                    setConnectStatusLabel(installationNode.getTextResource().connected());
                } else {

                }
            }
        });
        inputBox.add(style.createHorizontalSpacing());
        inputBox.add(connectBtn);
        disconnectBtn = new JButton("Disconnect");
        disconnectBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Integer result = installationNode.getXmlRpcDaemonInterface().disconnect();
                modeBox.setVisible(false);
                if (null != result && result >= 0) {
                    //stateMessage.setText("Connected");
                    setConnectStatusLabel(installationNode.getTextResource().No_Connection());
                }
            }
        });
        inputBox.add(style.createHorizontalSpacing());
        inputBox.add(disconnectBtn);

        stateMessage = new JLabel("No_Connection");
        inputBox.add(style.createHorizontalSpacing());
        inputBox.add(stateMessage);

        return inputBox;
    }


    private Box createButton(final BackyardLiftInstallationNodeContribution installationNode) {
        Box box = Box.createHorizontalBox();
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        bUp = new JButton("Up");
        bUp.setVerticalTextPosition(AbstractButton.CENTER);
        bUp.setHorizontalTextPosition(AbstractButton.LEADING);
        bUp.setBounds(100, 60, 100, 40);

        bUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                installationNode.moveUp(true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                installationNode.moveUp(false);
            }
        });
        bDown = new JButton("Down");
        bDown.setVerticalTextPosition(AbstractButton.CENTER);
        bDown.setHorizontalTextPosition(AbstractButton.LEADING);
        bDown.setBounds(100, 60, 100, 40);
        bDown.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                installationNode.moveDown(true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                installationNode.moveDown(false);
            }
        });

        zeroCalibBtn = new JButton("Zero_Calibration");
        zeroCalibBtn.setVerticalTextPosition(AbstractButton.CENTER);
        zeroCalibBtn.setHorizontalTextPosition(AbstractButton.LEADING);
        zeroCalibBtn.setBounds(100, 60, 100, 40);
        zeroCalibBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                installationNode.calibrate();
            }
        });

        stopBtn = new JButton("Stop");
        stopBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                installationNode.stopLift();
            }
        });

        box.add(style.createHorizontalIndent());
        box.add(bUp);
        box.add(style.createHorizontalIndent());
        box.add(bDown);
        box.add(style.createHorizontalIndent());
        box.add(zeroCalibBtn);
        box.add(style.createHorizontalIndent());
        box.add(stopBtn);
        return box;
    }

    public void showIP(String text) {
        ipField.setText(text);
    }

    //  -------------------------- get Image from file ----------------------------------------------------------------
    private Image getImage(String filaPath) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResource(filaPath));

            return image;
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception while loading icon.", e);
        }
    }

    //    ---------------------------Resize image Method----------------------------------------------------------------
    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    public void setConnectBtn(String connectString) {
        connectBtn.setText(connectString);
    }

    public void setDisconnectBtn(String disconnectString) {
        disconnectBtn.setText(disconnectString);
    }

    public void setUpBtn(String up) {
        bUp.setText(up);
    }

    public void setDownBtn(String down) {
        bDown.setText(down);
    }

    public void setZeroCalibBtn(String zeroCalibration) {
        zeroCalibBtn.setText(zeroCalibration);
    }

    public void setIpLabel(String ipString) {
        ipLabel.setText(ipString);
    }

    public void setConnectStatusLabel(String text) {
        stateMessage.setText(text);
    }

    public void setControlModeLabel(String s) {
        controlModel.setText(s);
    }

    public void setDisconnect(Integer result, BackyardLiftInstallationNodeContribution installationNode) {
        modeBox.setVisible(false);
        System.out.println("Lift Connection Miss");
        setConnectStatusLabel(this.contribution.getTextResource().No_Connection());
    }

    public void setStopBtn(String stop) {
        stopBtn.setText(stop);
    }
}
