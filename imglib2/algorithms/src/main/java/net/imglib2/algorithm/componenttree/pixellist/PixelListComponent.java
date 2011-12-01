package net.imglib2.algorithm.componenttree.pixellist;

import java.util.ArrayList;
import java.util.Iterator;

import net.imglib2.Localizable;
import net.imglib2.type.Type;

/**
 * A connected component of the image thresholded at {@link #value()}. The set
 * of pixels can be accessed by iterating ({@ling #iterator()}) the component.
 *
 * This is a node in a {@link PixelListComponentTree}. The child and parent
 * nodes can be accessed by {@link #getChildren()} and {@link #getParent()}.
 *
 * @author Tobias Pietzsch
 *
 * @param <T>
 *            value type of the input image.
 */
public final class PixelListComponent< T extends Type< T > > implements Iterable< Localizable >
{
	/**
	 * child nodes in the {@link PixelListComponentTree}.
	 */
	private final ArrayList< PixelListComponent< T > > children;

	/**
	 * parent node in the {@link PixelListComponentTree}.
	 */
	private PixelListComponent< T > parent;

	/**
	 * Threshold value of the connected component.
	 */
	private final T value;

	/**
	 * Pixels in the component.
	 */
	private final PixelList pixelList;

	PixelListComponent( PixelListComponentIntermediate< T > intermediate )
	{
		children = new ArrayList< PixelListComponent< T > >();
		parent = null;
		value = intermediate.getValue().copy();
		pixelList = new PixelList( intermediate.pixelList );
		for ( PixelListComponentIntermediate< T > c : intermediate.children )
		{
			children.add( c.emittedComponent );
			c.emittedComponent.parent = this;
		}
		intermediate.emittedComponent = this;
		intermediate.children.clear();
	}

	/**
	 * Get the image threshold that created the extremal region.
	 *
	 * @return the image threshold that created the extremal region.
	 */
	public T value()
	{
		return value;
	}

	/**
	 * Get the number of pixels in the extremal region.
	 *
	 * @return number of pixels in the extremal region.
	 */
	public long size()
	{
		return pixelList.size();
	}

	/**
	 * Get an iterator over the pixel locations ({@link Localizable}) in this
	 * connected component.
	 *
	 * @return iterator over locations.
	 */
	@Override
	public Iterator< Localizable > iterator()
	{
		return pixelList.iterator();
	}

	/**
	 * Get the children of this node in the {@link PixelListComponentTree}.
	 *
	 * @return the children of this node in the {@link PixelListComponentTree}.
	 */
	public ArrayList< PixelListComponent< T > > getChildren()
	{
		return children;
	}

	/**
	 * Get the parent of this node in the {@link PixelListComponentTree}.
	 *
	 * @return the parent of this node in the {@link PixelListComponentTree}.
	 */
	public PixelListComponent< T > getParent()
	{
		return parent;
	}
}
