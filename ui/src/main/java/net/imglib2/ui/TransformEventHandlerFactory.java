package net.imglib2.ui;

/**
 * Factory for {@link TransformEventHandler}.
 *
 * @param <A>
 *            type of transformation.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface TransformEventHandlerFactory< A >
{
	/**
	 * Create a new {@link TransformEventHandler} which reports to the given
	 * {@link TransformListener}.
	 *
	 * @param transformListener
	 *            will receive transformation updates.
	 */
	public TransformEventHandler< A > create( TransformListener< A > transformListener );
}
