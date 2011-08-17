package net.imglib2.ops.function.general;

import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;


public class NullFunction<N extends Neighborhood<?>, T> implements Function<N,T> {

	@Override
	public void evaluate(N input, T output) { // TODO : Could set to NaN?
		// do nothing
	}

	@Override
	public T createVariable() {
		return null;
		// TODO - returning null is sort of a problem. Though it makes sense.
		//  However if we only pass NullFunctions at outermost loop maybe we can avoid
		//  this method ever being called.
		//  What good is a null function if outermost loop can count on its own? Null
		//  function idea originally came about as a way to collect stats without
		//  destroying existing data. That need may now be obsolete. Investigate.
	}

}
