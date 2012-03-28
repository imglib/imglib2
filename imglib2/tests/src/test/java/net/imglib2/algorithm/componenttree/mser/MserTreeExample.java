/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.algorithm.componenttree.mser;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.EllipseRoi;
import ij.gui.Overlay;
import ij.process.ByteProcessor;

import java.awt.Color;

import net.imglib2.Localizable;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * Example of computing and visualizing the {@link MserTree} of an image.
 *
 *
 * @author Tobias Pietzsch
 */
public class MserTreeExample< T extends IntegerType< T > >
{
	final ImagePlus imp;
	final Overlay ov;
	final ImageStack stack;
	final int w;
	final int h;

	public MserTreeExample( final ImagePlus imp, final ImageStack stack )
	{
		this.imp = imp;
		ov = new Overlay();
		imp.setOverlay( ov );
		this.stack = stack;
		this.w = imp.getWidth();
		this.h = imp.getHeight();
	}

	/**
	 * Visualise MSER. Add a 3sigma ellipse overlay to {@link #imp} in the given
	 * color. Add a slice to {@link #stack} showing binary mask of MSER region.
	 */
	public void visualise( final Mser< T > mser, final Color color )
	{
		final ByteProcessor byteProcessor = new ByteProcessor( w, h );
		final byte[] pixels = ( byte[] )byteProcessor.getPixels();
		for ( final Localizable l : mser )
		{
			final int x = l.getIntPosition( 0 );
			final int y = l.getIntPosition( 1 );
			pixels[ y * w + x ] = (byte)(255 & 0xff);
		}
		final String label = "" + mser.value();
		stack.addSlice( label, byteProcessor );

		final EllipseRoi ellipse = createEllipse( mser.mean(), mser.cov(), 3 );
		ellipse.setStrokeColor( color );
		ov.add( ellipse );
	}

	/**
	 * Visualize all MSER in a tree. {@see #visualise(Mser, Color)}.
	 */
	public void visualise( final MserTree< T > tree, final Color color )
	{
		for ( final Mser< T > mser : tree )
			visualise( mser, color );
	}

	/**
	 * Paint ellipse at nsigmas standard deviations
	 * of the given 2D Gaussian distribution.
	 *
	 * @param mean (x,y) components of mean vector
	 * @param cov (xx, xy, yy) components of covariance matrix
	 * @return ImageJ roi
	 */
	public static EllipseRoi createEllipse( final double[] mean, final double[] cov, final double nsigmas )
	{
        final double a = cov[0];
        final double b = cov[1];
        final double c = cov[2];
        final double d = Math.sqrt( a*a + 4*b*b - 2*a*c + c*c );
        final double scale1 = Math.sqrt( 0.5 * ( a+c+d ) ) * nsigmas;
        final double scale2 = Math.sqrt( 0.5 * ( a+c-d ) ) * nsigmas;
        final double theta = 0.5 * Math.atan2( (2*b), (a-c) );
        final double x = mean[ 0 ];
        final double y = mean[ 1 ];
        final double dx = scale1 * Math.cos( theta );
        final double dy = scale1 * Math.sin( theta );
        final EllipseRoi ellipse = new EllipseRoi( x-dx, y-dy, x+dx, y+dy, scale2 / scale1 );
		return ellipse;
	}

	public static void main( final String[] args )
	{
		final int delta = 15;
		final long minSize = 10;
		final long maxSize = 100*100;
		final double maxVar = 0.8;
		final double minDiversity = 0;

		final Img< UnsignedByteType > img;
		try
		{
			new ImageJ();
			IJ.run("Lena (68K)");
			IJ.run("8-bit");
			img = ImagePlusAdapter.wrapByte( IJ.getImage() );
		}
		catch ( final Exception e )
		{
			e.printStackTrace();
			return;
		}

		final ImagePlus impImg = IJ.getImage();
		final ImageStack stack = new ImageStack( (int) img.dimension( 0 ), (int) img.dimension( 1 ) );

		final MserTree< UnsignedByteType > treeDarkToBright = MserTree.buildMserTree( img, new UnsignedByteType( delta ), minSize, maxSize, maxVar, minDiversity, true );
		final MserTree< UnsignedByteType > treeBrightToDark = MserTree.buildMserTree( img, new UnsignedByteType( delta ), minSize, maxSize, maxVar, minDiversity, false );
		final MserTreeExample< UnsignedByteType > vis = new MserTreeExample< UnsignedByteType >( impImg, stack );
		vis.visualise( treeDarkToBright, Color.CYAN );
		vis.visualise( treeBrightToDark, Color.MAGENTA );

		final ImagePlus imp = new ImagePlus("components", stack);
		imp.show();
	}
}
