package net.imglib2.ops.sandbox.types;

public interface Real<T> extends Number<T>, Ordered<T> {

	void toRational(Rational result);

	/**
	 * From Functions.
	 */
	<Z> void realToFrac(Fractional<Z> result);
}

