package tests;

import ij.IJ;
import ij.ImgJ;
import ij.ImgPlus;

import java.io.File;

import javax.swing.JFileChooser;

import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.display.imagej.ImgJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * A very simple imglib test that squares an image.
 * Displays both input and output images onscreen using ImgJ.
 * 
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SquareTest {

  /** Executes the test. */
  public void execute() {
    File file = chooseFile();
    if (file == null) return;
    Img<FloatType> inImg = LOCI.openLOCIFloatType(file.getPath(),
      new ArrayImgFactory());
    Img<FloatType> outImg = square(inImg);

    // show ImgJ control panel window
    if (IJ.getInstance() == null) new ImgJ();
    display(inImg, file.getName());
    display(outImg, "Squared");
  }

  /** Computes the square of a numeric image. */
  public <T extends RealType<T>> Img<T> square(Img<T> inputImg) {
    //ImgFactory<T> factory = new ImgFactory<T>(inputImg.createType(),
    //  new ArrayContainerFactory());
    //Img<T> outputImg = factory.createImg(new int[] {512, 512});
    Img<T> outputImg = inputImg.createNewImg();

    ImgCursor<T> inputCursor = inputImg.createRasterIterator();
    ImgCursor<T> outputCursor = outputImg.createRasterIterator();
    while (inputCursor.hasNext()) {
      inputCursor.fwd();
      outputCursor.fwd();
      float value = inputCursor.get().getRealFloat();
      outputCursor.get().setReal(value * value);
    }
    inputCursor.close();
    outputCursor.close();
    return outputImg;
  }

  /** Prompts the user to choose a file on disk. */
  public File chooseFile() { 
    JFileChooser jc = new JFileChooser();
    int result = jc.showOpenDialog(null);
    if (result != JFileChooser.APPROVE_OPTION) return null;
    return jc.getSelectedFile();
  }

  /** Displays the given imglib image as an ImgPlus. */
  public static <T extends RealType<T>> void display(Img<T> img,
    String title)
  {
    ImgPlus imp = null;
    Img<T> c = img.getContainer();
    if (c instanceof ImgPlusContainer<?, ?>) {
      ImgPlusContainer<T, ?> ipc = (ImgPlusContainer<T, ?>) c;
      try {
        imp = ipc.getImgPlus();
      }
      catch (ImgLibException exc) {
        IJ.log("Warning: " + exc.getMessage());
      }
    }
    if (imp == null) {
      imp = ImageJFunctions.showFloat(img);
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
