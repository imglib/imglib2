package net.imglib2.algorithm.mser;

import java.util.ArrayList;

import net.imglib2.Localizable;
import net.imglib2.Location;
import net.imglib2.type.Type;

public class ComponentInfo< T extends Type< T > >
{
 	ArrayList< Localizable > locations;
	
	T value;

	public ComponentInfo()
	{
	}
	
	public ComponentInfo( final T value )
	{
		this.locations = new ArrayList< Localizable >();
		this.value = value.copy();
	}
	
	public void addPosition( final Localizable position )
	{
		locations.add( new Location( position ) );
	}
	
	public T get()
	{
		return value;
	}
	
	public void setValue( final T value )
	{
		this.value.set( value );
	}
	
	public void merge( final ComponentInfo< T > component )
	{
		for ( Localizable l : component.locations )
		{
			addPosition( l );
		}
	}

	@Override
	public String toString()
	{
		String s = "{" + value.toString() + " : ";
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
		return s;
	}
}
