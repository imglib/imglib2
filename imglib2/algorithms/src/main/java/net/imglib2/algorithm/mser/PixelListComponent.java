package net.imglib2.algorithm.mser;

import java.util.Iterator;

import net.imglib2.Localizable;
import net.imglib2.type.Type;

public class PixelListComponent< T extends Type< T > > implements Component< T >, Iterable< Localizable >
{
	private static int idGen = 0;
	
	final int id;
	
	T value;
	
	PixelList pixelList;

	public PixelListComponent( final T value, final PixelListComponentGenerator< T > generator )
	{
		id = idGen++;
		pixelList = new PixelList( generator.linkedList.randomAccess(), generator.dimensions );
		this.value = value.copy();
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
		final PixelListComponent< T > c = ( PixelListComponent< T > ) component;
		pixelList.merge( c.pixelList );
	}

	@Override
	public String toString()
	{
		String s = "{" + value.toString() + " : id=" + id + " : ";
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
