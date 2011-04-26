package tests;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;

import javax.swing.JFileChooser;

import net.imglib2.container.imageplus.ImagePlusContainer;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Image;
import net.imglib2.img.Img;
import net.imglib2.img.ImgCursor;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.LOCI;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * A very simple imglib test that squares an image.
 * Displays both input and output images onscreen using ImageJ.
 * 
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SquareTest {

  /** Executes the test. */
  public void execute() {
    File file = chooseFile();
    if (file == null) return;
    Image<FloatType> inImg = LOCI.openLOCIFloatType(file.getPath(),
      new ArrayImgFactory());
    Image<FloatType> outImg = square(inImg);

    // show ImageJ control panel window
    if (IJ.getInstance() == null) new ImageJ();
    display(inImg, file.getName());
    display(outImg, "Squared");
  }

  /** Computes the square of a numeric image. */
  public <T extends RealType<T>> Image<T> square(Image<T> inputImage) {
    //ImageFactory<T> factory = new ImageFactory<T>(inputImage.createType(),
    //  new ArrayContainerFactory());
    //Image<T> outputImage = factory.createImage(new int[] {512, 512});
    Image<T> outputImage = inputImage.createNewImage();

    ImgCursor<T> inputCursor = inputImage.createRasterIterator();
    ImgCursor<T> outputCursor = outputImage.createRasterIterator();
    while (inputCursor.hasNext()) {
      inputCursor.fwd();
      outputCursor.fwd();
      float value = inputCursor.get().getRealFloat();
      outputCursor.get().setReal(value * value);
    }
    inputCursor.close();
    outputCursor.close();
    return outputImage;
  }

  /** Prompts the user to choose a file on disk. */
  public File chooseFile() { 
    JFileChooser jc = new JFileChooser();
    int result = jc.showOpenDialog(null);
    if (result != JFileChooser.APPROVE_OPTION) return null;
    return jc.getSelectedFile();
  }

  /** Displays the given imglib image as an ImagePlus. */
  public static <T extends RealType<T>> void display(Image<T> img,
    String title)
  {
    ImagePlus imp = null;
    Img<T> c = img.getContainer();
    if (c instanceof ImagePlusContainer<?, ?>) {
      ImagePlusContainer<T, ?> ipc = (ImagePlusContainer<T, ?>) c;
      try {
        imp = ipc.getImagePlus();
      }
      catch (ImgLibException exc) {
        IJ.log("Warning: " + exc.getMessage());
      }
    }
    if (imp == null) {
      imp = ImageJFunctions.copyToImagePlus(img);
    }
    if (title != null) imp.setTitle(title);
    img.getDisplay().setMinMax();
    imp.getProcessor().setMinAndMax( img.getDisplay().getMin(), img.getDisplay().getMax() );
    imp.show();
  }
  
  public static void main(String[] args) {
    SquareTest test = new SquareTest();
    test.execute();
  }

}
