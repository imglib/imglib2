package net.imglib2.script.analysis.fn;

public interface ReduceFn
{
	/** Returns the value to start reducing with the first value of a sequence;
	 * if null, the first value of the sequence is used. */
	public Double initial();

	/** Processes the final result of the reduction of a sequence.
	 * @param r The final result of reducing a sequence.
	 * */
	public double end(double r);

	/** The reducing function that takes the result so far and the next value as arguments.
	 * 
	 * @param r The result so far.
	 * @param v The new value.
	 * @return The resulting value to reduce with the next value in the sequence.
	 */
	public double reduce(double r, double v);
}
