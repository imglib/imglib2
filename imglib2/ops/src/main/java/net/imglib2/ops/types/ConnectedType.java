package net.imglib2.ops.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Neighborhood types.
 * 
 * @author dietzc, hornm
 */
public enum ConnectedType
{
	/**
	 * Touching voxels without diagonal neighbors.
	 */
	FOUR_CONNECTED( "Four-Connected" ),

	/**
	 * All touching voxels.
	 */
	EIGHT_CONNECTED( "Eight-Connected" );

	public static final List< String > NAMES = new ArrayList< String >();

	static
	{
		for ( ConnectedType e : ConnectedType.values() )
		{
			NAMES.add( e.toString() );
		}
	}

	public static ConnectedType value( final String name )
	{
		return values()[ NAMES.indexOf( name ) ];
	}

	private final String m_name;

	private ConnectedType( final String name )
	{
		m_name = name;
	}

	@Override
	public String toString()
	{
		return m_name;
	}
}
