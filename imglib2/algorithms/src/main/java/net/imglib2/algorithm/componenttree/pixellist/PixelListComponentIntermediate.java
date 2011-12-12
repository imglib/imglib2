package net.imglib2.algorithm.componenttree.pixellist;

import java.util.ArrayList;

import net.imglib2.Localizable;
import net.imglib2.algorithm.componenttree.Component;
import net.imglib2.type.Type;

/**
 * Implementation of {@link Component} that stores a list of associated pixels
 * in a {@link PixelList}.
 *
 * @author Tobias Pietzsch
 *
 * @param <T>
 *            value type of the input image.
 */
final class PixelListComponentIntermediate< T extends Type< T > > implements Component< T >
{
	/**
	 * Threshold value of the connected component.
	 */
	private final T value;

	/**
	 * Pixels in the component.
	 */
	final PixelList pixelList;

	/**
	 * A list of PixelListComponentIntermediate merged into this one since it
	 * was last emitted. (For building up component tree.)
	 */
	final ArrayList< PixelListComponentIntermediate< T > > children;

	/**
	 * The PixelListComponent assigned to this PixelListComponentIntermediate
	 * when it was last emitted. (For building up component tree.)
	 */
	PixelListComponent< T > emittedComponent;

	/**
	 * Create new empty component.
	 *
	 * @param value
	 *            (initial) threshold value {@see #getValue()}.
	 * @param generator
	 *            the {@link PixelListComponentGenerator#linkedList} is used to
	 *            store the {@link #pixelList}.
	 */
	PixelListComponentIntermediate( final T value, final PixelListComponentGenerator< T > generator )
	{
		pixelList = new PixelList( generator.linkedList.randomAccess(), generator.dimensions );
		this.value = value.copy();
		children = new ArrayList< PixelListComponentIntermediate< T > >();
		emittedComponent = null;
	}

	@Override
	public void addPosition( final Localizable position )
	{
		pixelList.addPosition( position );
	}

	@Override
	public T getValue()
	{
		return value;
	}

	@Override
	public void setValue( final T value )
	{
		this.value.set( value );
	}

	@Override
	public void merge( final Component< T > component )
	{
		final PixelListComponentIntermediate< T > c = (PixelListComponentIntermediate< T > ) component;
		pixelList.merge( c.pixelList );
		children.add( c );
	}

	@Override
	public String toString()
	{
		String s = "{" + value.toString() + " : ";
		boolean first = true;
		for ( Localizable l : pixelList )
		{
			if ( first )
			{
				first = false;
			}
			else
			{
				s += ", ";
			}
			s += l.toString();
		}
		return s + "}";
	}
}
