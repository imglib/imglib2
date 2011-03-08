package mpicbg.imglib.view;

import mpicbg.imglib.img.Img;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsMirrorFactory;
import mpicbg.imglib.util.Util;

public class Views
{
	public static < T > View< T > view( final Img< T > img, final OutOfBoundsFactory< T, Img< T > > factory )
	{
		return new ExtendableRandomAccessibleIntervalImp< T, Img< T > >( img, factory );
	}

	public static < T > View< T > view( final Img< T > img )
	{
		return view( img, new OutOfBoundsMirrorFactory< T, Img< T > >(OutOfBoundsMirrorFactory.Boundary.SINGLE) );
	}

	public static < T > View< T > view( final View< T > view, final OutOfBoundsFactory< T, View< T > > factory )
	{
		return new ExtendableRandomAccessibleIntervalImp< T, View< T > >( view, factory );
	}

	public static < T > View< T > view( final View< T > view )
	{
		return view( view, new OutOfBoundsMirrorFactory< T, View< T > >(OutOfBoundsMirrorFactory.Boundary.SINGLE) );
	}

	public static < T > View< T > superIntervalView( final View< T > view, long[] offset, long[] dimension )
	{
		final int n = view.numDimensions();
		ViewTransform t = new ViewTransform( n, n );
		t.setTranslation( offset );
		return new ViewTransformView< T >( view, t, dimension );
	}

	public static < T > View< T > flippedView( final View< T > view, final int d )
	{
		final int n = view.numDimensions();
		long[] tmp = new long[ n ];
		tmp[ d ] = view.max( d );
		int[] component = new int[ n ];
		boolean[] inv = new boolean[ n ];
		for ( int e = 0; e < n; ++e )
		{
			component[ e ] = e;
			inv[ e ] = (e == d);
		}
		ViewTransform t = new ViewTransform( n, n );
		t.setTranslation( tmp );
		t.setPermutation( component, inv );
		view.dimensions( tmp );
		System.out.println( Util.printCoordinates( tmp ) );
		return new ViewTransformView< T >( view, t, tmp );
	}
}
