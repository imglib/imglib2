package net.imglib2.ops.features;

import java.util.Iterator;

import net.imglib2.Localizable;

public interface PositionIterator extends Iterator< Localizable >
{
	// Simple marker interface
	int numDimensions();
}
