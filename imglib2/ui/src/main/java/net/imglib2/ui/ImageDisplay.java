package net.imglib2.ui;

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


import net.imglib2.img.Axes;
import net.imglib2.img.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.ui.lut.Lut;
import net.imglib2.ui.lut.LutBuilder;

public class ImageDisplay extends JPanel {

	protected List<ImgProjector<?>> images = new ArrayList<ImgProjector<?>>();
	protected int maxWidth = 0, maxHeight = 0;

	public ImageDisplay() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new JPanel() { // image canvas

			@Override
			public void paint(Graphics g) {
				for (final ImgProjector<?> imgData : images) {
					final Image image = imgData.getScreenImage().image();
					g.drawImage(image, 0, 0, this);
				}
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(maxWidth, maxHeight);
			}

		});
	}

	public class DimensionSliderPanel extends JPanel {
		/*
		 * CompositeSliderPanel
		 * If there is a channel dimension is displayed as a composite, 
		 * a slider for that dim should not be added.
		 */

		public DimensionSliderPanel(final ImgProjector<?> imgProj) {
			setBorder(new TitledBorder(imgProj.getImg().getName()));
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			// add one slider per dimension beyond the first two
			for (int d = 3; d < imgProj.getImg().numDimensions(); d++) {
				final int dim = d;
				final int dimLength = (int) imgProj.getImg().dimension(d);
				final JScrollBar bar = new JScrollBar(Adjustable.HORIZONTAL, 0, 1, 0, dimLength);
				bar.addAdjustmentListener(new AdjustmentListener() {

					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						final int value = bar.getValue();
						imgProj.getProjector().setPosition(value, dim);
						imgProj.getProjector().map();
						System.out.println("dim #" + dim + ": value->" + value);//TEMP
						imgProj.getOwner().repaint();
					}

				});
				bar.setPreferredSize(new Dimension(200, 18));
				add(bar);
			}
		}

	}

	/*		public class SliderPanel extends JPanel {
	
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
	System.out.println("dim #" + dim + ": value->" + value);// TEMP
	imgData.owner.repaint();
	}
	});
	add(bar);
	}
	}
	}
	 */
	public <T extends RealType<T> & NativeType<T>> void addCompositeRGBImage(final String name,
		final ImgPlus<T> img) {
		// create an RGB 3-channel CompositeImgData, with channel on axis 2
		ArrayList<Lut> lutList = new ArrayList<Lut>();
		lutList.add(LutBuilder.getInstance().createLUT("red"));
		lutList.add(LutBuilder.getInstance().createLUT("green"));
		lutList.add(LutBuilder.getInstance().createLUT("blue"));
		int channelDimIndex = img.getAxisIndex(Axes.CHANNEL);
		if (channelDimIndex < 0) {
			//"No Channel dimension."
		}
		final ImgProjector<T> imgData = new ImgProjector<T>(name, img, channelDimIndex, lutList, this);
		images.add(imgData);
		setMaxDimension(imgData);
		add(new DimensionSliderPanel(imgData));
	}

	
	/*
	 * Add a 
	 */
	public <T extends RealType<T> & NativeType<T>> void addImage(final String name, final ImgPlus<T> img) {
		final ImgProjector<T> imgData = new ImgProjector<T>(name, img, -1, null, this);
		images.add(imgData);
		setMaxDimension(imgData);
		add(new DimensionSliderPanel(imgData));
	}

	
	
	private void setMaxDimension(ImgProjector<?> imgData) {
		int width = (int) imgData.getImg().dimension(0);
		int height = (int) imgData.getImg().dimension(1);
		if (width > maxWidth) {
			maxWidth = width;
		}
		if (height > maxHeight) {
			maxHeight = height;
		}
	}

	private static void center(final Window win) {
		final Dimension size = win.getSize();
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final int w = (screen.width - size.width) / 2;
		final int h = (screen.height - size.height) / 2;
		win.setLocation(w, h);
	}

	public static final <T extends RealType<T> & NativeType<T>> void main(final String[] args) {
		final String[] urls = {
			//"file:///C:/TestImages/TestImages/MyoblastCells.tif"
			//"http://loci.wisc.edu/files/software/data/mitosis-test.zip"
			"http://loci.wisc.edu/files/software/ome-tiff/z-series.zip"
		};
		final JFrame frame = new JFrame("ImgPanel Test Frame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final ImageDisplay imgPanel = new ImageDisplay();
		//
		for (String url : urls) {
			final ImgPlus<T> img = Util.loadImage(url);
			//img.getAxes(Metadata
			// is it multichannel ??
			if (img.getAxisIndex(Axes.CHANNEL) > 0) {
				if (img.dimension(img.getAxisIndex(Axes.CHANNEL)) == 3) {
					// If 3 channels, probably an RGB image
					imgPanel.addCompositeRGBImage(url, img);
				} else {
					// more than 3 channels
				}
			} else {
				imgPanel.addImage(url, img);
			}
		}
		frame.setContentPane(imgPanel);
		frame.pack();
		center(frame);
		frame.setVisible(true);
	}

}
