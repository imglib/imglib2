/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import java.io.IOException;

import ij.ImageJ;
import loci.formats.FormatException;
import net.imglib2.algorithm.gauss.GaussianConvolution;
import net.imglib2.algorithm.transformation.ImageTransform;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImgLib2Display;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.io.ImgOpener;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;
import net.imglib2.type.numeric.real.FloatType;
import mpicbg.models.AffineModel3D;

/**
 * TODO
 *
 */
public class OpenDisplayGaussTransformBehaviour
{	
	public static void test( ImgFactory<FloatType> factory )
	{
		//Img<FloatType> img = LOCI.openLOCIFloatType( "D:/Temp/Truman/MoreTiles/73.tif",  factory );
		
		Img<FloatType> img = null;
		
		try
		{
			img = new ImgOpener().openImg( "D:/Temp/Truman/MoreTiles/73_8bit.tif", factory, new FloatType() );
		}
		catch ( Exception e )
		{
			System.out.println( "Cannot open file: " + e.getMessage() );
			return;
		}
		
		//ImgCursor<FloatType> c = img.localizingCursor();
		//System.out.println( Util.printCoordinates(c ) + ": " + ((FloatType)c.get()).i );		
		//System.exit( 0 );
		
		ImgLib2Display.copyToImagePlus( img, new int[] {2, 0, 1} ).show();
		
		// compute a gaussian convolution with sigma = 3
		GaussianConvolution<FloatType> gauss = new GaussianConvolution<FloatType>( img, new OutOfBoundsMirrorFactory<FloatType, Img<FloatType>>( Boundary.SINGLE ), 2 );
		
		if ( !gauss.checkInput() || !gauss.process() )
		{
			System.out.println( gauss.getErrorMessage() );
			return;
		}
		
		ImgLib2Display.copyToImagePlus( gauss.getResult() ).show();

		// Affine Model rotates 45 around X, 45 around Z and scales by 0.5		
		AffineModel3D model = new AffineModel3D();
		model.set( 0.35355338f, -0.35355338f, 0.0f, 0.0f, 0.25f, 0.25f, -0.35355338f, 0.0f, 0.25f, 0.25f, 0.35355338f, 0.0f );

		OutOfBoundsFactory<FloatType, Img<FloatType>> oob = new OutOfBoundsConstantValueFactory<FloatType, Img<FloatType>>( new FloatType( 255) );
		NearestNeighborInterpolatorFactory< FloatType > interpolatorFactory = new NearestNeighborInterpolatorFactory< FloatType >( oob );
		ImageTransform< FloatType > transform = new ImageTransform<FloatType>( img, model, interpolatorFactory );
		
		if ( !transform.checkInput() || !transform.process() )
		{
			System.out.println( transform.getErrorMessage() );
			return;
		}
		
		ImgLib2Display.copyToImagePlus( transform.getResult() ).show();
		
	}
	
	public static void main( String[] args )
	{
		new ImageJ();
		
		test( new ArrayImgFactory<FloatType>() );
	}
}
