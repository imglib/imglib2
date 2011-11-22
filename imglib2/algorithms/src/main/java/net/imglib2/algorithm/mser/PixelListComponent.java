package net.imglib2.algorithm.mser;

import java.util.ArrayList;
import java.util.Iterator;

import net.imglib2.Localizable;
import net.imglib2.type.Type;

public class PixelListComponent< T extends Type< T > > implements Iterable< Localizable >
{
	private final ArrayList< PixelListComponent< T > > ancestors;

	private PixelListComponent< T > successor;

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
		ancestors = new ArrayList< PixelListComponent< T > >();
		successor = null;
		value = intermediate.getValue().copy();
		pixelList = new PixelList( intermediate.pixelList );
		for ( PixelListComponentIntermediate< T > c : intermediate.ancestors )
		{
			ancestors.add( c.emittedComponent );
			c.emittedComponent.successor = this;
		}
		intermediate.emittedComponent = this;
		intermediate.ancestors.clear();
	}

	/**
	 * @return the image threshold that created the extremal region.
	 */
	public T value()
	{
		return value;
	}

	/**
	 * @return number of pixels the extremal region.
	 */
	public long size()
	{
		return pixelList.size();
	}

	@Override
	public Iterator< Localizable > iterator()
	{
		return pixelList.iterator();
	}

	public ArrayList< PixelListComponent< T > > getAncestors()
	{
		return ancestors;
	}
	
	public PixelListComponent< T > getSuccessor()
	{
		return successor;
	}
}