package net.imglib2.concatenate;

/**
 * If T implements {@link PreConcatenable}< A > that means you can
 * pre-concatenate it with an A, usually resulting in another T. The
 * conventional meaning for concatenating transformations is the following: Let
 * <em>ba = a.preConcatenate(b)</em>. Applying <em>ba</em> to <em>x</em> is
 * equivalent to first applying <em>a</em> to <em>x</em> and then applying
 * <em>b</em> to the result.
 *
 * @author Tobias Pietzsch, Stephan Saalfeld
 */
public interface PreConcatenable< A >
{
	/**
	 * Pre-concatenate this object with <em>a</em>. The result will be an object
	 * that can be pre-concatenated with another <em>A</em>. The conventional
	 * meaning for concatenating transformations is the following: Let
	 * <em>ba = a.preConcatenate(b)</em>. Applying <em>ba</em> to <em>x</em> is
	 * equivalent to first applying <em>a</em> to <em>x</em> and then applying
	 * <em>b</em> to the result.
	 */
	public PreConcatenable< A > preConcatenate( A a );

	public Class< A > getPreConcatenableClass();
}
