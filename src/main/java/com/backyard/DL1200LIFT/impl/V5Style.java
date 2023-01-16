package com.backyard.DL1200LIFT.impl;

import java.awt.*;

public class V5Style extends Style {
	private static final int HORIZONTAL_SPACING = 10;
	private static final int VERTICAL_SPACING =15;
	private static final int HORIZONTAL_INDENT = 20;
	private static final Dimension INPUTFIELD_SIZE = new Dimension(200, 30);
	private static final Dimension INPUTSHORTFIELD_SIZE = new Dimension(80, 30);
	@Override
	public Dimension getInputfieldSize()
	{
		return INPUTFIELD_SIZE;
	}
	
	@Override
	public Dimension getInputShortFieldSize()
	{
		return INPUTSHORTFIELD_SIZE;
	}


	@Override
	protected int getHorizontalSpacing()
	{
		return HORIZONTAL_SPACING;
	}

	@Override
	protected int getVertivcalSpacing()
	{
		return VERTICAL_SPACING;
	}

	@Override
	protected int getHorizontalIndent()
	{
		return HORIZONTAL_INDENT;
	}
}
