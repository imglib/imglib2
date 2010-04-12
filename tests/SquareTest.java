package tests;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;

import javax.swing.JFileChooser;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.container.imageplus.ImagePlusContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.exception.ImgLibException;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.numeric.FloatType;

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
      new ArrayContainerFactory());
    Image<FloatType> outImg = square(inImg);

    // show ImageJ control panel window
    if (IJ.getInstance() == null) new ImageJ();
    display(inImg, file.getName());
    display(outImg, "Squared");
  }

  /** Computes the square of a numeric image. */
  public <T extends NumericType<T>> Image<T> square(Image<T> inputImage) {
    //ImageFactory<T> factory = new ImageFactory<T>(inputImage.createType(),
    //  new ArrayContainerFactory());
    //Image<T> outputImage = factory.createImage(new int[] {512, 512});
    Image<T> outputImage = inputImage.createNewImage();

    Cursor<T> inputCursor = inputImage.createCursor();
    Cursor<T> outputCursor = outputImage.createCursor();
    while (inputCursor.hasNext()) {
      inputCursor.fwd();
      outputCursor.fwd();
      float value = inputCursor.getType().getReal();
      outputCursor.getType().setReal(value * value);
    }
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
  public static <T extends NumericType<T>> void display(Image<T> img,
    String title)
  {
    ImagePlus imp = null;
    Container<T, ?> c = img.getContainer();
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
    imp.show();
  }
  
  public static void main(String[] args) {
    SquareTest test = new SquareTest();
    test.execute();
  }

}
