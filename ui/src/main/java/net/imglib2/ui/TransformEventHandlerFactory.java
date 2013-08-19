package net.imglib2.ui;

/**
 * TODO
 *
 * @param <A>
 *            type of transformation.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface TransformEventHandlerFactory< A >
{
	/**
	 * TODO
	 *
	 * @param transformListener
	 * @return
	 */
	public TransformEventHandler< A > create( TransformListener< A > transformListener );
}
