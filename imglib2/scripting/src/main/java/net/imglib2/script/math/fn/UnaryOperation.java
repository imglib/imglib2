package net.imglib2.script.math.fn;


import java.util.Collection;

import net.imglib2.img.Img;
import net.imglib2.img.ImgCursor;
import net.imglib2.type.numeric.RealType;

/* An abstract class to facilitate implementing a function that takes one argument.
 * Subclasses must call one of the three constructors: for an {@link Image}, an {@link IFunction},
 * or for a {@link Number}.
 * 
 * Here is an example. Suppose you want a function that adds 42:
 * 
 <code>
 import net.imglib2.scripting.math.fn.IFunction;
 import net.imglib2.scripting.math.fn.UnaryOperation;
 import net.imglib2.image.Image;
 
 public class Add42 extends UnaryOperation {
     public Add42(Image<? extends RealType<?>> img) {
         super(img);
     }
     public Add42(IFunction fn) {
         super(fn);
     }
     public Add42(Number val) {
         super(val);
     }
     
     public final double eval() {
         return a() + 42;
     }
 }
 </code>
 *
 * The new Add42 function created above will interact with any other of the math functions,
 * or with any other class implementing IFunction.
 */
public abstract class UnaryOperation extends FloatImageOperation
{
	private final IFunction a;

	public UnaryOperation(final Img<? extends RealType<?>> img) {
		this.a = new ImageFunction(img);
	}

	public UnaryOperation(final IFunction fn) {
		this.a = fn;
	}

	public UnaryOperation(final Number val) {
		this.a = new NumberFunction(val);
	}

	@Override
	public final void findCursors(final Collection<ImgCursor<?>> cursors) {
		a.findCursors(cursors);
	}

	/** Call a().eval() to obtain the result as a double of the computation encapsulated by the @field a. 
	 *  @returns the IFunction @field a*/
	public final IFunction a() { return a; }
	
	public IFunction duplicate() throws Exception
	{
		return getClass().getConstructor(IFunction.class).newInstance(a.duplicate());
	}
}
