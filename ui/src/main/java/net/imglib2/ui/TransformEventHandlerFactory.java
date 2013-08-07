package net.imglib2.ui;

/**
 * TODO
 *
 * @param <T>
 *            type of transformation.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface TransformEventHandlerFactory< T >
{
	/**
	 * TODO
	 *
	 * @param transformListener
	 * @return
	 */
	public TransformEventHandler< T > create( TransformListener< T > transformListener );
}
