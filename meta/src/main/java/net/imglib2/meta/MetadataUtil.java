package net.imglib2.meta;

import net.imglib2.Interval;

/**
 * Utility class that allows to copy Metadata. It is the responsibility of the
 * user to ensure that the source and target spaces have the correct
 * dimensionality.
 * 
 * @author horn, dietz, zinsmaier
 */
public class MetadataUtil
{

	private MetadataUtil()
	{
		// helper class
	}

	/**
	 * copies the source attribute from in to out
	 * 
	 * @param in
	 * @param out
	 * @return returns out
	 */
	public static Sourced copySource( Sourced in, Sourced out )
	{
		out.setSource( in.getSource() );
		return out;
	}

	/**
	 * copies the name from in to out
	 * 
	 * @param in
	 * @param out
	 * @return returns out
	 */
	public static Named copyName( Named in, Named out )
	{
		out.setName( in.getName() );
		return out;
	}

	/**
	 * copies the ImageMetadata from in to out
	 * 
	 * @param in
	 * @param out
	 * @return returns out
	 */
	public static ImageMetadata copyImageMetadata( ImageMetadata in, ImageMetadata out )
	{
		out.setValidBits( in.getValidBits() );
		out.setCompositeChannelCount( in.getCompositeChannelCount() );

		for ( int c = 0; c < out.getCompositeChannelCount(); c++ )
		{
			out.setChannelMinimum( c, in.getChannelMinimum( c ) );
			out.setChannelMaximum( c, in.getChannelMaximum( c ) );
		}

		out.initializeColorTables( in.getColorTableCount() );
		for ( int n = 0; n < in.getColorTableCount(); n++ )
		{
			out.setColorTable( in.getColorTable( n ), n );
		}
		
		return out;
	}

	/**
	 * copies the TypedSpace attributes from in to out. Both spaces must have
	 * the same dimensionality.
	 * 
	 * @param in
	 * @param out
	 * @return returns out
	 */
	public static < T extends TypedAxis > TypedSpace< T > copyTypedSpace( TypedSpace< T > in, TypedSpace< T > out )
	{
		copyTypedSpace( null, in, out );
		return out;
	}

	/**
	 * copies the CalibratedSpace attributes from in to out. Both spaces must
	 * have the same dimensionality.
	 * 
	 * @param in
	 * @param out
	 * @return returns out
	 */
	public static < C extends CalibratedAxis > CalibratedSpace< C > copyTypedSpace( CalibratedSpace< C > in, CalibratedSpace< C > out )
	{
		copyTypedSpace( null, in, out );
		return out;
	}

	/**
	 * copies all ImgPlus metadata {@link Metadata} from in to out. The
	 * {@link CalibratedSpace} of in and out must have the same dimensionality.
	 * 
	 * @param in
	 * @param out
	 * @return returns out
	 */
	public static Metadata copyImgPlusMetadata( Metadata in, Metadata out )
	{
		copyName( in, out );
		copySource( in, out );
		copyImageMetadata( in, out );
		copyTypedSpace( in, out );
		return out;
	}

	// with cleaning

	/**
	 * copies the TypedSpace attributes from in to out. Attributes for
	 * dimensions of size 1 are removed during copying. The dimensionality of
	 * the out space must be dimensionality(inSpace) - #removed dims
	 * 
	 * @param inInterval
	 *            provides dimensionality information for the in space
	 * @param in
	 * @param out
	 * @return returns out
	 */
	public static < T extends TypedAxis > TypedSpace< T > copyAndCleanTypedSpace( Interval inInterval, TypedSpace< T > in, TypedSpace< T > out )
	{
		copyTypedSpace( inInterval, in, out );
		return out;
	}

	/**
	 * copies the CalibratedAxis attributes from in to out. Attributes for
	 * dimensions of size 1 are removed during copying. The dimensionality of
	 * the out space must be dimensionality(inSpace) - #removed dims
	 * 
	 * @param inInterval
	 *            provides dimensionality information for the in space
	 * @param in
	 * @param out
	 * @return returns out
	 */
	public static < C extends CalibratedAxis > CalibratedSpace< C > copyAndCleanTypedSpace( Interval inInterval, CalibratedSpace< C > in, CalibratedSpace< C > out )
	{
		copyTypedSpace( inInterval, in, out );
		return out;
	}

	/**
	 * copies all ImgPlus metadata {@link Metadata} from in to out.
	 * CalibratedSpace attributes for dimensions of size 1 are removed during
	 * copying. The dimensionality of the out space must be
	 * dimensionality(inSpace) - #removed dims
	 * 
	 * @param inInterval
	 *            provides dimensionality information for the in space
	 * @param in
	 * @param out
	 * @return returns out
	 */
	public static Metadata copyAndCleanImgPlusMetadata( Interval inInterval, Metadata in, Metadata out )
	{
		copyName( in, out );
		copySource( in, out );
		copyImageMetadata( in, out );
		copyAndCleanTypedSpace( inInterval, in, out );
		return out;
	}

	// PRIVATE HELPERS

	private static < T extends TypedAxis > void copyTypedSpace( Interval inInterval, TypedSpace< T > in, TypedSpace< T > out )
	{

		int offset = 0;
		for ( int d = 0; d < in.numDimensions(); d++ )
		{
			if ( inInterval != null && inInterval.dimension( d ) == 1 )
			{
				offset++;
			}
			else
			{
				out.setAxis( ( T ) in.axis( d ).copy(), d - offset );
			}
		}
	}

	private static < C extends CalibratedAxis > void copyTypedSpace( Interval inInterval, CalibratedSpace< C > in, CalibratedSpace< C > out )
	{

		int offset = 0;
		for ( int d = 0; d < in.numDimensions(); d++ )
		{
			if ( inInterval != null && inInterval.dimension( d ) == 1 )
			{
				offset++;
			}
			else
			{
				out.setAxis( (C) in.axis( d ).copy(), d- offset );
				out.setCalibration( in.calibration( d ), d - offset );
				out.setUnit( in.unit( d ), d - offset );
			}
		}
	}

}
