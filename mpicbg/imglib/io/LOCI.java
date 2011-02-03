/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.io;

import java.io.IOException;

import loci.formats.ChannelSeparator;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.sampler.special.OrthoSliceIterator;
import mpicbg.imglib.type.numeric.real.FloatType;

public class LOCI
{
	public static Container<FloatType> openLOCIFloatType( final String fileName, final ContainerFactory factory )
	{				
		final IFormatReader r = new ChannelSeparator();

		final String id = fileName;
		
		try 
		{
			r.setId( id );
						
			final boolean isLittleEndian = r.isLittleEndian();			
			final int width = r.getSizeX();
			final int height = r.getSizeY();
			final int depth = r.getSizeZ();
			int timepoints = r.getSizeT();
			int channels = r.getSizeC();
			final int pixelType = r.getPixelType();
			final int bytesPerPixel = FormatTools.getBytesPerPixel(pixelType); 
			final String pixelTypeString = FormatTools.getPixelTypeString(pixelType);
			
			if ( timepoints > 1 )
			{
				System.out.println("LOCI.openLOCI(): More than one timepoint. Not implemented yet. Returning first timepoint");
				timepoints = 1;
			}
			
			if ( channels > 1 )
			{
				System.out.println("LOCI.openLOCI(): More than one channel. Image<FloatType> supports only 1 channel right now, returning the first channel.");
				channels = 1;
			}
			
			if (!(pixelType == FormatTools.UINT8 || pixelType == FormatTools.UINT16 || pixelType == FormatTools.UINT32 || pixelType == FormatTools.FLOAT))
			{
				System.out.println("LOCI.openLOCI(): PixelType " + pixelTypeString + " not supported by FloatType, returning. ");
				return null;
			}
			
			final Container<FloatType> container;
			
			if ( depth == 1 )
				container = factory.create( new long[] { width, height }, new FloatType() );
			else
				container = factory.create( new long[] { width, height, depth }, new FloatType() );
			
			if ( container  == null )
			{
				System.out.println("LOCI.openLOCI():  - Could not create image.");
				return null;
			}
			else
			{
				System.out.println( "Opening '" + fileName + "' [" + width + "x" + height + "x" + depth + " type=" + pixelTypeString + " image=Image<FloatType>]" );
			}
					
			final int t = 0;			
			final byte[][] b = new byte[channels][width * height * bytesPerPixel];
			
			final int[] planePos = new int[3];
			final int planeX = 0;
			final int planeY = 1;
									
			for (int z = 0; z < depth; z++)
			{	
				// set the z plane iterator to the current z plane
				planePos[ 2 ] = z ;
				final OrthoSliceIterator< FloatType > it = new OrthoSliceIterator<FloatType>( container, planeX, planeY, planePos ); 
				
				// read the data from LOCI
				for (int c = 0; c < channels; c++)
				{
					final int index = r.getIndex(z, c, t);
					r.openBytes(index, b[c]);	
				}
				
				// write data for that plane into the Image structure using the iterator
				if (channels == 1)
				{					
					if (pixelType == FormatTools.UINT8)
					{						
						while(it.hasNext())
						{
							it.fwd();
							it.get().set( b[ 0 ][ it.getIntPosition( planeX )+it.getIntPosition( planeY )*width ] & 0xff );
						}
						
					}	
					else if (pixelType == FormatTools.UINT16)
					{
						while(it.hasNext())
						{
							it.fwd();
							it.get().set( getShortValue( b[ 0 ], ( it.getIntPosition( planeX )+it.getIntPosition( planeY )*width ) * 2, isLittleEndian ) );
						}
					}						
					else if (pixelType == FormatTools.UINT32)
					{
						//TODO: Untested

						while(it.hasNext())
						{
							it.fwd();
							it.get().set( getIntValue( b[ 0 ], ( it.getIntPosition( planeX )+it.getIntPosition( planeY )*width )*4, isLittleEndian ) );
						}

					}
					else if (pixelType == FormatTools.FLOAT)
					{
						while(it.hasNext())
						{
							it.fwd();
							it.get().set( getFloatValue( b[ 0 ], ( it.getIntPosition( planeX )+it.getIntPosition( planeY )*width )*4, isLittleEndian ) );
						}

					}
				}
			}
			
			return container;			
			
		}
		catch (IOException exc) { System.out.println("LOCI.openLOCI(): Sorry, an error occurred: " + exc.getMessage()); return null;}
		catch (FormatException exc) {System.out.println("LOCI.openLOCI(): Sorry, an error occurred: " + exc.getMessage()); return null;}		
	}
	
	protected static String checkPath( String path )
	{
		if (path.length() > 1) 
		{
			path = path.replace('\\', '/');
			if (!path.endsWith("/"))
				path = path + "/";
		}
		
		return path;
	}

	private static final float getFloatValue( final byte[] b, final int i, final boolean isLittleEndian )
	{
		if ( isLittleEndian )
			return Float.intBitsToFloat( ((b[i+3] & 0xff) << 24)  + ((b[i+2] & 0xff) << 16)  +  ((b[i+1] & 0xff) << 8)  + (b[i] & 0xff) );
		else
			return Float.intBitsToFloat( ((b[i] & 0xff) << 24)  + ((b[i+1] & 0xff) << 16)  +  ((b[i+2] & 0xff) << 8)  + (b[i+3] & 0xff) );
	}

	private static final int getIntValue( final byte[] b, final int i, final boolean isLittleEndian )
	{
		// TODO: Untested
		if ( isLittleEndian )
			return ( ((b[i+3] & 0xff) << 24)  + ((b[i+2] & 0xff) << 16)  +  ((b[i+1] & 0xff) << 8)  + (b[i] & 0xff) );
		else
			return ( ((b[i] & 0xff) << 24)  + ((b[i+1] & 0xff) << 16)  +  ((b[i+2] & 0xff) << 8)  + (b[i+3] & 0xff) );
	}
	
	private static final short getShortValue( final byte[] b, final int i, final boolean isLittleEndian )
	{
		return (short)getShortValueInt( b, i, isLittleEndian );
	}

	private static final int getShortValueInt( final byte[] b, final int i, final boolean isLittleEndian )
	{
		if ( isLittleEndian )
			return ((((b[i+1] & 0xff) << 8)) + (b[i] & 0xff));
		else
			return ((((b[i] & 0xff) << 8)) + (b[i+1] & 0xff));
	}
	
}
