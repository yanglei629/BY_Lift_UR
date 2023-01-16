package com.backyard.DL1200LIFT.impl;

import java.awt.*;

import javax.swing.Box;

public abstract class Style {
	private static final int VERTICAL_SPACING = 10;
	private static final int LARGE_VERTICAL_SPACING = 25;
    private static final int XLARGE_VERTICAL_SPACING = 50;
	private static final int SMALL_HEADER_FONT_SIZE = 16;
//	protected abstract int getHorizontalSpacing();
	protected abstract int getVertivcalSpacing();
	protected abstract int getHorizontalIndent();
	protected abstract int getHorizontalSpacing();
	
//	public int getHorizontalSpacing() {
//		return HORIZONTAL_SPACING;
//	}

	public int getVerticalSpacing() {
		return VERTICAL_SPACING;
	}

    public int getExtraLargeVerticalSpacing() {
        return XLARGE_VERTICAL_SPACING;
    }

	public int getLargeVerticalSpacing() {
		return LARGE_VERTICAL_SPACING;
	}

	public int getSmallHeaderFontSize() {
		return SMALL_HEADER_FONT_SIZE;
	}
	
	public Component createHorizontalSpacing() {
		return Box.createRigidArea(new Dimension(getHorizontalSpacing(), 0));
	}
	
	public Component createHorizontalSpacing(int n) {
		return Box.createRigidArea(new Dimension(n, 0));
	}
	
	public Component createHorizontalIndent() {
		return Box.createRigidArea(new Dimension(getHorizontalIndent(), 0));
	}
	
	public Component createVerticalSpacing() {
		return Box.createRigidArea(new Dimension(0, getVertivcalSpacing()));
	}

	public abstract Dimension getInputfieldSize();
	
	public abstract Dimension getInputShortFieldSize();
}
