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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.TitledBorder;

import loci.formats.FormatException;
import net.imglib2.converter.Converter;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

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

	public class CompositeImgData<T extends RealType<T> & NativeType<T>> {

		public String name;
		public Img<T> img;
		public CompositeImgPanel owner;
		public int width, height;
		public ARGBScreenImage screenImage;
		ArrayList<Converter<T,ARGBType>> converters = new ArrayList<Converter<T,ARGBType>>();
		//public RealLUTConverter<T> converter = new RealLUTConverter<T>[3];
		public CompositeXYProjector<T, ARGBType> projector;

		public CompositeImgData(final String name, final Img<T> img, final CompositeImgPanel owner) {
			this.name = name;
			this.img = img;
			this.owner = owner;
			width = (int) img.dimension(0);
			height = (int) img.dimension(1);

			screenImage = new ARGBScreenImage(width, height);
			final int min = 0, max = 255;

			converters.add(new CompositeLUTConverter<T>(min, max, createRampLUT(0)));
			converters.add(new CompositeLUTConverter<T>(min, max, createRampLUT(1)));
			converters.add(new CompositeLUTConverter<T>(min, max, createRampLUT(2)));
			int channelDimIndex = 2;
			projector = new CompositeXYProjector<T, ARGBType>(img, screenImage, converters, channelDimIndex);
			projector.map();
		}
	}

		public class SliderPanel extends JPanel {

			public SliderPanel(final CompositeImgData<?> imgData) {
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
			final CompositeImgData<T> imgData = new CompositeImgData<T>(name, img, this);
			images.add(imgData);
			if (imgData.width > maxWidth) {
				maxWidth = imgData.width;
			}
			if (imgData.height > maxHeight) {
				maxHeight = imgData.height;
			}
			add(new SliderPanel(imgData));
		}

		public static final <T extends RealType<T> & NativeType<T>> void main(final String[] args) {
			final String[] paths = {
				"C:/TestImages/TestImages/MyoblastCells.tif"
			//"C:/TestImages/multi-channel-time-series.ome.tif"
			};
			final JFrame frame = new JFrame("ImgPanel Test Frame");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			final CompositeImgPanel imgPanel = new CompositeImgPanel();
			for (String path : paths) {
				final ImgPlus<T> img = loadImage(path);
				imgPanel.addImage(path, img.getImg());
			}
			frame.setContentPane(imgPanel);
			frame.pack();
			center(frame);
			frame.setVisible(true);
		}

		private static <T extends RealType<T> & NativeType<T>> ImgPlus<T> loadImage(String path) {
			try {
				return new ImgOpener().openImg(path);
			} catch (IncompatibleTypeException e) {
				e.printStackTrace();
			} catch (FormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
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
