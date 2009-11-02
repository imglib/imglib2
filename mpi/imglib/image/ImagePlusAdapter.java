package mpi.imglib.image;

import ij.ImagePlus;
import mpi.imglib.container.imageplus.ByteImagePlus;
import mpi.imglib.container.imageplus.FloatImagePlus;
import mpi.imglib.container.imageplus.ImagePlusContainerFactory;
import mpi.imglib.container.imageplus.IntImagePlus;
import mpi.imglib.container.imageplus.ShortImagePlus;
import mpi.imglib.cursor.Cursor;
import mpi.imglib.image.Image;
import mpi.imglib.type.NumericType;
import mpi.imglib.type.Type;
import mpi.imglib.type.TypeConverter;
import mpi.imglib.type.numeric.ByteType;
import mpi.imglib.type.numeric.FloatType;
import mpi.imglib.type.numeric.RGBALegacyType;
import mpi.imglib.type.numeric.ShortType;

public class ImagePlusAdapter
{
	@SuppressWarnings("unchecked")
	public static <T extends NumericType<T>> Image< T > wrap( final ImagePlus imp )
	{
		return (Image<T>) wrapLocal(imp);
	}
	
	protected static Image<?> wrapLocal( final ImagePlus imp )
	{
		switch( imp.getType() )
		{		
			case ImagePlus.GRAY8 : 
			{
				return wrapByte( imp );
			}
			case ImagePlus.GRAY16 : 
			{
				return wrapShort( imp );
			}
			case ImagePlus.GRAY32 : 
			{
				return wrapFloat( imp );
			}
			case ImagePlus.COLOR_RGB : 
			{
				return wrapRGBA( imp );
			}
			default :
			{
				System.out.println( "mpi.imglib.container.imageplus.ImagePlusAdapter(): Cannot handle type " + imp.getType() );
				return null;
			}
		}
	}
	
	public static Image<ByteType> wrapByte( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY8)
			return null;
		
		ImagePlusContainerFactory containerFactory = new ImagePlusContainerFactory();
		ByteImagePlus<ByteType> container = new ByteImagePlus<ByteType>( imp,  containerFactory );
		ImageFactory<ByteType> imageFactory = new ImageFactory<ByteType>( new ByteType(), containerFactory );				
		Image<ByteType> image = new Image<ByteType>( container, imageFactory, imp.getTitle() );
		
		return image;		
	}
	
	public static Image<ShortType> wrapShort( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY16)
			return null;

		ImagePlusContainerFactory containerFactory = new ImagePlusContainerFactory();
		ShortImagePlus<ShortType> container = new ShortImagePlus<ShortType>( imp,  containerFactory );
		ImageFactory<ShortType> imageFactory = new ImageFactory<ShortType>( new ShortType(), containerFactory );				
		Image<ShortType> image = new Image<ShortType>( container, imageFactory, imp.getTitle() );
		
		return image;						
	}

	public static Image<RGBALegacyType> wrapRGBA( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.COLOR_RGB)
			return null;

		ImagePlusContainerFactory containerFactory = new ImagePlusContainerFactory();
		IntImagePlus<RGBALegacyType> container = new IntImagePlus<RGBALegacyType>( imp,  containerFactory );
		ImageFactory<RGBALegacyType> imageFactory = new ImageFactory<RGBALegacyType>( new RGBALegacyType(), containerFactory );				
		Image<RGBALegacyType> image = new Image<RGBALegacyType>( container, imageFactory, imp.getTitle() );
		
		return image;				
	}	
	
	public static Image<FloatType> wrapFloat( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY32)
			return null;

		ImagePlusContainerFactory containerFactory = new ImagePlusContainerFactory();
		FloatImagePlus<FloatType> container = new FloatImagePlus<FloatType>( imp,  containerFactory );
		ImageFactory<FloatType> imageFactory = new ImageFactory<FloatType>( new FloatType(), containerFactory );				
		Image<FloatType> image = new Image<FloatType>( container, imageFactory, imp.getTitle() );
		
		return image;				
	}	
	
	public static Image<FloatType> convertFloat( final ImagePlus imp )
	{
		if ( imp.getType() != ImagePlus.GRAY32)
		{
			Image<?> img = wrapLocal( imp );
			
			if ( img == null )
				return null;				
			
			return convertToFloat( img );
			
		}
		else
		{
			return wrapFloat( imp );
		}
	}
	
	protected static <T extends Type<T> > Image<FloatType> convertToFloat( Image<T> input )
	{		
		ImageFactory<FloatType> factory = new ImageFactory<FloatType>( new FloatType(), new ImagePlusContainerFactory() );
		Image<FloatType> output = factory.createImage( input.getDimensions(), input.getName() );
	
		Cursor<T> in = input.createCursor();
		Cursor<FloatType> out = output.createCursor();
		
		TypeConverter tc = TypeConverter.getTypeConverter( in.getType(), out.getType() );
		
		if ( tc == null )
		{
			System.out.println( "Cannot convert from " + in.getType().getClass() + " to " + out.getType().getClass() );
			output.close();
			return null;
		}
		
		while ( in.hasNext() )
		{
			in.fwd();
			out.fwd();
			
			tc.convert();			
		}
		
		return output;
	}
}
