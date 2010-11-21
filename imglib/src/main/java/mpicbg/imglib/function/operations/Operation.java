package mpicbg.imglib.function.operations;

import mpicbg.imglib.function.Function;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;

import java.util.Set;

public interface Operation< A extends NumericType<A> > extends Function< A, A, A > {
	public void fwd();
	public void compute(A output);
	public void getImages(Set<Image<A>> images);
	public void init(A ref);
}