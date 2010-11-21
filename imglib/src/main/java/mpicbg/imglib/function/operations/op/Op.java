package mpicbg.imglib.function.operations.op;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;

public interface Op< A extends NumericType<A> > {
	public void fwd();
	public void compute(A output);
	public void getImages(Set<Image<A>> images);
	public void init(A ref);
}