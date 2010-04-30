package mpicbg.imglib.algorithm.math.operation;

import java.util.List;

import mpicbg.imglib.type.Type;

public interface TypeOperation<S extends Type<S>, T extends Type<T>>  {

	public S operate(List<T> typeList);
	
	public int maxArgs();
	
	public int minArgs();
	
	public boolean ready();
	
	public void reset();
	
}
