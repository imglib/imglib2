package mpicbg.imglib.algorithm.math.operation;

import java.util.List;

import mpicbg.imglib.type.numeric.ComplexType;

/**
 * Take the norm of several {@link Type}s as a vector, in the L2-norm sense.
 * @author Larry Lindsey
 *
 * @param <S> output Type
 * @param <T> input Type
 */
public class TypeNorm<S extends ComplexType<S>, T extends ComplexType<T>> implements TypeOperation<S, T>
{
	private final S outType;
	
	public TypeNorm(S out)
	{
		outType = out;
	}
	
	@Override
	public int maxArgs() {		
		return -1;
	}

	@Override
	public int minArgs() {
		return 1;
	}

	@Override
	public S operate(List<T> typeList) {
		final S accum = outType.clone(), prod = outType.clone(), conj = outType.clone();
		
		accum.setZero();
		
		for (T t : typeList)
		{
			prod.setComplex(t.getComplexDouble());
			prod.setReal(t.getRealDouble());
			
			conj.set(prod.clone());
			conj.complexConjugate();
			
			prod.mul(conj);
			
			accum.add(prod);
		}
		
		//accum should contain a value that is strictly positive real.
		
		accum.setComplex(0);
		accum.setReal(Math.sqrt(accum.getRealDouble()));
		
		return accum;
	}

	@Override
	public boolean ready() {
		return true;
	}

	@Override
	public void reset() {
	}

}
