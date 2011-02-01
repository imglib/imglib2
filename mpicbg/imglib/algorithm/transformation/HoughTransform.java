package mpicbg.imglib.algorithm.transformation;

import mpicbg.imglib.algorithm.Algorithm;
import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.container.PositionableContainerSampler;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public abstract class HoughTransform<S extends RealType<S>, T extends RealType<T>>
implements Algorithm, Benchmark
{
	protected long pTime;
	private String errorMsg;
	private final Image<T> image;
	private Image<S> voteSpace;
	private PositionableContainerSampler<S> voteCursor;	
	
	protected HoughTransform(final Image<T> inputImage, final int[] voteSize)
	{
		image = inputImage;
		voteCursor = null;
		pTime = 0;
		voteSpace = null;				
	}
	
	protected boolean placeVote(final int[] loc, S vote)
	{
		if (voteSpace != null)
		{
			if (voteCursor == null)
			{
				voteCursor = voteSpace.createPositionableRasterSampler();
			}
			voteCursor.setPosition(loc);
			
			voteCursor.get().add(vote);
			
			return true;
		}
		else
		{
			errorMsg = "Uninitialized Vote Space";
			return false;
		}		
	}
	
	protected void setVoteSpace(final Image<S> image)
	{
		voteSpace = image;
	}
	
	@Override
	public boolean checkInput() {
		if (voteSpace == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public String getErrorMessage() {
		return errorMsg;
	}
	
	@Override
	public long getProcessingTime() {		
		return pTime;
	}
	
	public final Image<T> getImage()
	{
		return image;
	}

}
