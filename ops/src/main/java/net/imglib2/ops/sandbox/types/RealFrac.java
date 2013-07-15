package net.imglib2.ops.sandbox.types;

public interface RealFrac<T> extends Real<T>, Fractional<T>
{

	<Z extends Integral<Z>> void properFraction(Integral<Z> intResult,
		T fracResult);

	<Z extends Integral<Z>> void truncate(Integral<Z> result);

	<Z extends Integral<Z>> void round(Integral<Z> result);

	<Z extends Integral<Z>> void ceiling(Integral<Z> result);

	<Z extends Integral<Z>> void floor(Integral<Z> result);
}

