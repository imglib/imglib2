package net.imglib2.ops.sandbox;

import net.imglib2.Cursor;

public interface NewFunc<U,V> {
	void evaluate(NewIterableInterval<U> interval, V output);
	//void evaluate(Cursor<U> interval, V output);
	V createOutput();
	NewFunc<U,V> copy();
}
