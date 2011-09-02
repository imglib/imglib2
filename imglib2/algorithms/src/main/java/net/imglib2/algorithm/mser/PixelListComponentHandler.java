package net.imglib2.algorithm.mser;

import net.imglib2.Localizable;
import net.imglib2.type.Type;

public class PixelListComponentHandler< T extends Type< T > > implements ComponentHandler< PixelListComponent< T > >
{
	final long[] dimensions;

	public PixelListComponentHandler( final long[] inputDimensions )
	{
		assert inputDimensions.length == 2;
		dimensions = inputDimensions.clone();
	}

	@Override
	public void emit( PixelListComponent< T > component )
	{
		System.out.println( "emit " + component );
		
		for ( int r = 0; r < dimensions[1]; ++r )
		{
			System.out.print("| ");
			for ( int c = 0; c < dimensions[0]; ++c )
			{
				boolean set = false;
				for ( Localizable l : component.locations )
					if( l.getIntPosition( 0 ) == c && l.getIntPosition( 1 ) == r )
						set = true;
				System.out.print( set ? "x " : ". " );
			}
			System.out.println("|");
		}
		
		System.out.println();
	}
	
}