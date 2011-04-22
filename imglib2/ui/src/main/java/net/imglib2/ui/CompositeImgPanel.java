package net.imglib2.ui;

import java.awt.Adjustable;
import java.awt.Color;
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

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgIOUtils;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.ui.lut.Lut;
import net.imglib2.ui.lut.LutBuilder;

public class CompositeImgPanel extends JPanel {

	protected Color[] createRampLUT(int c) {
		int numcolors = 256;
		Color[] colorTable = new Color[numcolors];
		for (int i = 0; i < numcolors; i++) {
			if (c == 0) {
				colorTable[i] = new Color(i, 0, 0);
			} else if (c == 1) {
				colorTable[i] = new Color(0, i, 0);
			} else if (c == 2) {
				colorTable[i] = new Color(0, 0, i);
			}

		}
		return colorTable;
	}

		public class CompositeSliderPanel extends JPanel {

			/*
			 * CompositeSliderPanel
			 * If there is a channel dimension is displayed as a composite, 
			 * a slider for that dim should not be added.
			 */
			
			public CompositeSliderPanel(final CompositeImgData<?> imgData) {
				setBorder(new TitledBorder(imgData.name));
				setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				// add one slider per dimension beyond the first two
				for (int d = 3; d < imgData.img.numDimensions(); d++) {
					
					final int dimLength = (int) imgData.img.dimension(d);
					final JScrollBar bar = new JScrollBar(Adjustable.HORIZONTAL, 0, 1, 0, dimLength);
					final int dim = d;
					bar.addAdjustmentListener(new AdjustmentListener() {

						@Override
						public void adjustmentValueChanged(AdjustmentEvent e) {
							final int value = bar.getValue();
							imgData.projector.setPosition(value, dim);
							imgData.projector.map();
							System.out.println("dim #" + dim + ": value->" + value);//TEMP
							imgData.owner.repaint();
						}

					});
					add(bar);
				}
			}

		}
		protected List<CompositeImgData<?>> images = new ArrayList<CompositeImgData<?>>();
		protected int maxWidth = 0, maxHeight = 0;

		public CompositeImgPanel() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(new JPanel() { // image canvas

				@Override
				public void paint(Graphics g) {
					for (final CompositeImgData<?> imgData : images) {
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

		public <T extends RealType<T> & NativeType<T>> void addImage(final String name,
			final Img<T> img)
		{
			// create an RGB 3-channel CompositeImgData, with channel on axis 2
			ArrayList<Lut> lutList = new ArrayList<Lut>();
			lutList.add(LutBuilder.getInstance().createLUT("red"));
			lutList.add(LutBuilder.getInstance().createLUT("green"));
			lutList.add(LutBuilder.getInstance().createLUT("blue"));
			int channelDimIndex = 2;
			final CompositeImgData<T> imgData = new CompositeImgData<T>(name, img, channelDimIndex, lutList, this);
			//
			images.add(imgData);
			if (imgData.width > maxWidth) {
				maxWidth = imgData.width;
			}
			if (imgData.height > maxHeight) {
				maxHeight = imgData.height;
			}
			add(new CompositeSliderPanel(imgData));
		}

		public static final <T extends RealType<T> & NativeType<T>> void main(final String[] args) {
			final String[] urls = {
				"file:///C:/TestImages/TestImages/MyoblastCells.tif"
				//"http://loci.wisc.edu/files/software/data/mitosis-test.zip"
					//,				"http://loci.wisc.edu/files/software/ome-tiff/z-series.zip"
			};
			final JFrame frame = new JFrame("ImgPanel Test Frame");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			final CompositeImgPanel imgPanel = new CompositeImgPanel();
			for (String url : urls) {
				final ImgPlus<T> img = loadImage(url);
				//img.getAxes(Metadata.)
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
