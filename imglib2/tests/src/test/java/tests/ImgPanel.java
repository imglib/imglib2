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

package tests;

import java.awt.Adjustable;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.TitledBorder;

import loci.common.StatusEvent;
import loci.common.StatusListener;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYProjector;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgIOUtils;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

/**
 * A simple UI that demonstrates display of {@link Img}s.
 *
 * @author Curtis Rueden
 */
public class ImgPanel extends JPanel {

	public class ImgData<T extends RealType<T> & NativeType<T>> {

		public String name;
		public ImgPlus<T> imgPlus;
		public ImgPanel owner;
		public int width, height;
		public ARGBScreenImage screenImage;
		public RealARGBConverter<T> converter;
		public XYProjector<T, ARGBType> projector;

		public ImgData(final String name, final ImgPlus<T> imgPlus,
			final ImgPanel owner)
		{
			this.name = name;
			this.imgPlus = imgPlus;
			this.owner = owner;
			width = (int) imgPlus.dimension(0);
			height = (int) imgPlus.dimension(1);
			screenImage = new ARGBScreenImage(width, height);
			final int min = 0, max = 255;
			converter = new RealARGBConverter<T>(min, max);
			projector =
				new XYProjector<T, ARGBType>(imgPlus, screenImage, converter);
			projector.map();
		}
	}

	public class SliderPanel extends JPanel {

		public SliderPanel(final ImgData<?> imgData) {
			setBorder(new TitledBorder(imgData.name));
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			// add one slider per dimension beyond the first two
			for (int d = 2; d < imgData.imgPlus.numDimensions(); d++) {
				final int dimLength = (int) imgData.imgPlus.dimension(d);
				final JScrollBar bar =
					new JScrollBar(Adjustable.HORIZONTAL, 0, 1, 0, dimLength);
				final int dim = d;
				bar.addAdjustmentListener(new AdjustmentListener() {

					@Override
					public void adjustmentValueChanged(final AdjustmentEvent e) {
						final int value = bar.getValue();
						imgData.projector.setPosition(value, dim);
						imgData.projector.map();
						imgData.owner.repaint();
					}
				});
				add(bar);
			}
		}
	}

	protected List<ImgData<?>> images = new ArrayList<ImgData<?>>();
	protected int maxWidth = 0, maxHeight = 0;

	public ImgPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new JPanel() { // image canvas

			@Override
			public void paint(final Graphics g) {
				for (final ImgData<?> imgData : images) {
					final Image image = imgData.screenImage.image();
					g.drawImage(image, 0, 0, this);
				}
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(maxWidth, maxHeight);
			}
		});
	}

	public <T extends RealType<T> & NativeType<T>> void addImage(
		final String name, final ImgPlus<T> img)
	{
		final ImgData<T> imgData = new ImgData<T>(name, img, this);
		images.add(imgData);
		if (imgData.width > maxWidth) maxWidth = imgData.width;
		if (imgData.height > maxHeight) maxHeight = imgData.height;
		add(new SliderPanel(imgData));
	}

	public static final <T extends RealType<T> & NativeType<T>> void main(
		final String[] args)
	{
		final String[] urls = {
			"http://loci.wisc.edu/files/software/ome-tiff/z-series.zip"
		};
		final JFrame frame = new JFrame("ImgPanel Test Frame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final ImgPanel imgPanel = new ImgPanel();
		for (final String url : urls) {
			final ImgPlus<T> img = loadImage(url);
			imgPanel.addImage(url, img);
		}
		frame.setContentPane(imgPanel);
		frame.pack();
		center(frame);
		frame.setVisible(true);
	}

	private static <T extends RealType<T> & NativeType<T>> ImgPlus<T> loadImage(
		final String url)
	{
		try {
			System.out.println("Downloading " + url);
			final String id = ImgIOUtils.cacheId(url);
			System.out.println("Opening " + id);
			final ImgOpener imgOpener = new ImgOpener();
			imgOpener.addStatusListener(new StatusListener() {
				@Override
				public void statusUpdated(StatusEvent e) {
					System.out.println(e.getStatusMessage());
				}
			});
			return imgOpener.openImg(id);
		}
		catch (final IncompatibleTypeException e) {
			e.printStackTrace();
		}
		catch (final ImgIOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void center(final Window win) {
		final Dimension size = win.getSize();
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final int w = (screen.width - size.width) / 2;
		final int h = (screen.height - size.height) / 2;
		win.setLocation(w, h);
	}

}
