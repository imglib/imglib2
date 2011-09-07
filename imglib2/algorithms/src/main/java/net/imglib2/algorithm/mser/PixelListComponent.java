package net.imglib2.algorithm.mser;

import java.util.ArrayList;

import net.imglib2.Localizable;
import net.imglib2.Location;
import net.imglib2.type.Type;

public class PixelListComponent< T extends Type< T > > implements Component< T >
{
	private static int idGen = 0;
	
	final int id;
	
 	ArrayList< Localizable > locations;
	
	T value;

	public PixelListComponent()
	{
		id = idGen++;
	}
	
	public PixelListComponent( final T value )
	{
		this();
		this.locations = new ArrayList< Localizable >();
		this.value = value.copy();
	}
	
	/* (non-Javadoc)
	 * @see net.imglib2.algorithm.mser.Component#addPosition(net.imglib2.Localizable)
	 */
	@Override
	public void addPosition( final Localizable position )
	{
		locations.add( new Location( position ) );
	}
	
	/* (non-Javadoc)
	 * @see net.imglib2.algorithm.mser.Component#get()
	 */
	@Override
	public T getValue()
	{
		return value;
	}
	
	/* (non-Javadoc)
	 * @see net.imglib2.algorithm.mser.Component#setValue(T)
	 */
	@Override
	public void setValue( final T value )
	{
		this.value.set( value );
	}
	
	/* (non-Javadoc)
	 * @see net.imglib2.algorithm.mser.Component#merge(net.imglib2.algorithm.mser.ComponentInfo)
	 */
	@Override
	public void merge( final Component< T > component )
	{
		final PixelListComponent< T > c = ( PixelListComponent< T > ) component;

		for ( Localizable l : c.locations )
		{
			addPosition( l );
		}
	}

	@Override
	public String toString()
	{
		String s = "{" + value.toString() + " : id=" + id + " : ";
		boolean first = true;
		for ( Localizable l : locations )
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
