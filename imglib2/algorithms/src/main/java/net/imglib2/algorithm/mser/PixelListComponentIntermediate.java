package net.imglib2.algorithm.mser;

import java.util.ArrayList;
import java.util.Iterator;

import net.imglib2.Localizable;
import net.imglib2.type.Type;

public final class PixelListComponentIntermediate< T extends Type< T > > implements Component< T >, Iterable< Localizable >
{
	private T value;

	final PixelList pixelList;

	/**
	 * A list of PixelListComponentBuilder merged into this one since it was last emitted.
	 * (For building up component tree.)
	 */
	final ArrayList< PixelListComponentIntermediate< T > > ancestors;

	/**
	 * The PixelListComponent assigned to this PixelListComponentBuilder when it was last emitted.
	 * (For building up component tree.)
	 */
	PixelListComponent< T > emittedComponent;

	public PixelListComponentIntermediate( final T value, final PixelListComponentGenerator< T > generator )
	{
		pixelList = new PixelList( generator.linkedList.randomAccess(), generator.dimensions );
		this.value = value.copy();
		ancestors = new ArrayList< PixelListComponentIntermediate< T > >();
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
		final PixelListComponentIntermediate< T > c = ( PixelListComponentIntermediate< T > ) component;
		pixelList.merge( c.pixelList );
		ancestors.add( c );
	}

	@Override
	public String toString()
	{
		String s = "{" + value.toString() + " : ";
		boolean first = true;
		for ( Localizable l : this )
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

	@Override
	public Iterator< Localizable > iterator()
	{
		return pixelList.iterator();
	}
}
