package com.backyard.DL1200LIFT.impl.program;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import com.backyard.DL1200LIFT.impl.Style;
import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;


public class BackyardLiftProgramNodeView implements SwingProgramNodeView<BackyardLiftProgramNodeContribution> {

    private final Style style;
    private JSlider posSlider = new JSlider(JSlider.VERTICAL);
    private static final String filePath_logo = "/logo/logo_backyard.jpg";
    private JTextField inputPos;
    private JLabel labelTargetPos;
    private JLabel labelCurrentPos;
    private JLabel labelMovingStatus;
    private JButton performBtn;
    private JLabel targetPosLabel;
    private JLabel statusHeader;
    private JLabel argumentHeader;

    public BackyardLiftProgramNodeView(Style style) {
        this.style = style;
    }

    @Override
    public void buildUI(JPanel panel, ContributionProvider<BackyardLiftProgramNodeContribution> provider) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(createInfo());

        panel.add(createPosSlider(posSlider, 0, 300, provider));
        panel.add(style.createVerticalSpacing());
        panel.add(style.createVerticalSpacing());
        panel.add(createStatusBox(provider));
        panel.add(style.createVerticalSpacing());
        panel.add(style.createVerticalSpacing());
        Box imagebox = Box.createHorizontalBox();
        imagebox.setAlignmentX(Component.LEFT_ALIGNMENT);
        ImageIcon icon1 = new ImageIcon(getScaledImage(getImage(filePath_logo), 200, 96));
        JLabel labelLogo1 = new JLabel();
        labelLogo1.setIcon(icon1);
        imagebox.add(Box.createHorizontalGlue());
        imagebox.add(style.createHorizontalSpacing());
        imagebox.add(labelLogo1);
        panel.add(imagebox);
    }

    private Box createStatusBox(ContributionProvider<BackyardLiftProgramNodeContribution> provider) {
        Box box = Box.createVerticalBox();
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusHeader = new JLabel("Status");
        statusHeader.setFont(new Font("宋体bai", Font.BOLD, 15));
        statusHeader.setFont(statusHeader.getFont().deriveFont(Font.BOLD, style.getSmallHeaderFontSize()));

        box.add(statusHeader);
        labelCurrentPos = new JLabel("Current Position:");
        labelTargetPos = new JLabel("Target Position:");
        labelMovingStatus = new JLabel("Running Status:");
        box.add(style.createVerticalSpacing());
        box.add(labelCurrentPos);
        box.add(Box.createVerticalStrut(10));
        box.add(labelTargetPos);
        box.add(Box.createVerticalStrut(10));
        box.add(labelMovingStatus);
        box.add(Box.createVerticalStrut(10));

        return box;
    }


    private Box createInfo() {
        Box box = Box.createHorizontalBox();
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        //----------------------------------------------
        Box infoBox = Box.createVerticalBox();
        infoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        argumentHeader = new JLabel("Action Arguments");
        argumentHeader.setFont(new Font("宋体bai", Font.BOLD, 15));
        argumentHeader.setFont(argumentHeader.getFont().deriveFont(Font.BOLD, style.getSmallHeaderFontSize()));


        infoBox.add(argumentHeader);
        infoBox.add(Box.createVerticalStrut(10));
        Box logoBox = Box.createHorizontalBox();
        logoBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon icon1 = new ImageIcon(getScaledImage(getImage(filePath_logo), 200, 96));
        JLabel labelLogo1 = new JLabel();
        labelLogo1.setIcon(icon1);
        box.add(infoBox);
        return box;
    }


    private Box createPosSlider(final JSlider slider, int min, int max,
                                final ContributionProvider<BackyardLiftProgramNodeContribution> provider) {
        Box box = Box.createHorizontalBox();
        Box btnbox = Box.createHorizontalBox();
        Box returnBox = Box.createVerticalBox();
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnbox.setAlignmentX(Component.LEFT_ALIGNMENT);

        performBtn = new JButton("Perform immediately");
        performBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                provider.get().setTargetPos(Integer.parseInt(inputPos.getText()));
            }
        });

        //pos input field
        inputPos = new JTextField();
        //inputPos.setFocusable(false);
        inputPos.setPreferredSize(style.getInputShortFieldSize());
        inputPos.setMaximumSize(inputPos.getPreferredSize());
        inputPos.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                KeyboardNumberInput<Integer> keyboardInput = provider.get().getInputPosForTextField();
                keyboardInput.show(inputPos, new KeyboardInputCallback<Integer>() {
                    @Override
                    public void onOk(Integer value) {
                        inputPos.setText(value.toString());
                        provider.get().setPos(value);
                    }
                });
                keyboardInput.setErrorValidator(provider.get().getKeyboardInputValidationFactory().createIntegerRangeValidator(0, 500));
            }
        });

        targetPosLabel = new JLabel("TargetPosition:");
        box.add(targetPosLabel);
        box.add(style.createHorizontalSpacing(10));
        box.add(inputPos);
        box.add(style.createHorizontalSpacing(10));
        box.add(new JLabel("mm"));
        box.add(style.createHorizontalSpacing(15));
        btnbox.add(performBtn);
        returnBox.add(box);
        returnBox.add(style.createVerticalSpacing());
        returnBox.add(Box.createVerticalStrut(15));

        returnBox.add(btnbox);
        return returnBox;
    }

    public void updateTextField(int value) {
        inputPos.setText(Integer.toString(value));
    }

    public void setPosSlider(int value) {
        posSlider.setValue(value);
    }

    private Image getImage(String filaPath) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResource(filaPath));
            return image;
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception while loading icon.", e);
        }
    }

    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    public void showPos(int value) {
        inputPos.setText(Integer.toString(value));
    }

    public void setTargetPosLabel(String text) {
        labelTargetPos.setText(text);
    }

    public void setCurrentPosLabel(String text) {
        labelCurrentPos.setText(text);
    }

    public void setMovingStatus(String text) {
        labelMovingStatus.setText(text);
    }

    public void setPerformBtn(String perform) {
        performBtn.setText(perform);
    }

    public void setTargetPos(String text) {
        targetPosLabel.setText(text);
    }

    public void setStatusText(String status) {
        statusHeader.setText(status);
    }

    public void setArgumentText(String argument) {
        argumentHeader.setText(argument);
    }
}
