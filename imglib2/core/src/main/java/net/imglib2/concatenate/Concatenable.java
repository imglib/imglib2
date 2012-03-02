package net.imglib2.concatenate;

/**
 * If T implements {@link Concatenable}< A > that means you can concatenate it
 * with an A, usually resulting in another T. The conventional meaning for
 * concatenating transformations is the following: Let
 * <em>ba = b.concatenate(a)</em>. Applying <em>ba</em> to <em>x</em> is
 * equivalent to first applying <em>a</em> to <em>x</em> and then applying
 * <em>b</em> to the result.
 *
 * @author Tobias Pietzsch, Stephan Saalfeld
 */
public interface Concatenable< A >
{
	/**
	 * Concatenate this object with <em>a</em>. The result will be an object
	 * that can be concatenated with another <em>A</em>. The conventional
	 * meaning for concatenating transformations is the following: Let
	 * <em>ba = b.concatenate(a)</em>. Applying <em>ba</em> to <em>x</em> is
	 * equivalent to first applying <em>a</em> to <em>x</em> and then applying
	 * <em>b</em> to the result.
	 */
	public Concatenable< A > concatenate( A a );

	public Class< A > getConcatenableClass();
}
