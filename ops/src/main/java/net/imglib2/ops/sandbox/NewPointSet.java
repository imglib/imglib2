package net.imglib2.ops.sandbox;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.type.logic.BitType;

public interface NewPointSet extends Positionable, Localizable, IterableInterval<BitType> {
	boolean contains(long[] point);
	<T> Cursor< T > bind( final RandomAccess< T > randomAccess );
}
