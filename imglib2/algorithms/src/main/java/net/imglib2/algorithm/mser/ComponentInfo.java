package net.imglib2.algorithm.mser;

import java.util.ArrayList;

import net.imglib2.Localizable;
import net.imglib2.Location;
import net.imglib2.type.Type;

public class ComponentInfo< T extends Type< T > >
{
	private static int idGen = 0;
	
	final int id;
	
 	ArrayList< Localizable > locations;
	
	T value;

	public ComponentInfo()
	{
		id = idGen++;
	}
	
	public ComponentInfo( final T value )
	{
		this();
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
		System.out.println( "merge " + component.id + " into " + id );
		for ( Localizable l : component.locations )
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
