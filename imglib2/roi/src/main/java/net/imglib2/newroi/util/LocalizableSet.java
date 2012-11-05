package net.imglib2.newroi.util;

import net.imglib2.EuclideanSpace;
import net.imglib2.Localizable;

public interface LocalizableSet extends EuclideanSpace
{
	boolean contains( final Localizable p );
}