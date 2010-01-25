package mpicbg.imglib.algorithm.fft;

import mpicbg.imglib.algorithm.math.MathLib;

public class PhaseCorrelationPeak implements Comparable<PhaseCorrelationPeak>
{
	int[] position = null;
	int[] originalInvPCMPosition = null;
	float phaseCorrelationPeak = 0, crossCorrelationPeak = 0;
	boolean sortPhaseCorrelation = true;
	
	public PhaseCorrelationPeak( final int[] position, final float phaseCorrelationPeak, final float crossCorrelationPeak )
	{
		this.position = position.clone();
		this.phaseCorrelationPeak = phaseCorrelationPeak;
		this.crossCorrelationPeak = crossCorrelationPeak;
	}
	
	public PhaseCorrelationPeak( final int[] position, final float phaseCorrelationPeak )
	{
		this ( position, phaseCorrelationPeak, 0 );
	}

	public PhaseCorrelationPeak( final int[] position )
	{
		this ( position, 0, 0 );
	}
	
	public PhaseCorrelationPeak()
	{
		this ( null, 0, 0 );
	}
	
	public void setPosition( final int[] position ) { this.position = position.clone(); }
	public void setOriginalInvPCMPosition( final int[] originalInvPCMPosition ) { this.originalInvPCMPosition = originalInvPCMPosition; }
	public void setPhaseCorrelationPeak( final float phaseCorrelationPeak ) { this.phaseCorrelationPeak = phaseCorrelationPeak; }
	public void setCrossCorrelationPeak( final float crossCorrelationPeak ) { this.crossCorrelationPeak = crossCorrelationPeak; }
	public void setSortPhaseCorrelation( final boolean sortPhaseCorrelation ) { this.sortPhaseCorrelation = sortPhaseCorrelation; }
	
	public int[] getPosition() { return position.clone(); }
	public int[] getOriginalInvPCMPosition() { return originalInvPCMPosition; }
	public float getPhaseCorrelationPeak() { return phaseCorrelationPeak; }
	public float getCrossCorrelationPeak() { return crossCorrelationPeak; }
	public boolean getSortPhaseCorrelation() { return sortPhaseCorrelation; }

	@Override
	public int compareTo( final PhaseCorrelationPeak o )
	{
		if ( sortPhaseCorrelation )
		{
			if ( this.phaseCorrelationPeak > o.phaseCorrelationPeak )
				return 1;
			else if ( this.phaseCorrelationPeak == o.phaseCorrelationPeak )
				return 0;
			else
				return -1;
		}
		else
		{
			if ( this.crossCorrelationPeak > o.crossCorrelationPeak )
				return 1;
			else if ( this.crossCorrelationPeak == o.crossCorrelationPeak )
				return 0;
			else
				return -1;			
		}
	}
	
	@Override
	public String toString()
	{
		if ( originalInvPCMPosition == null)
			return MathLib.printCoordinates( position ) + ", phaseCorrelationPeak = " + phaseCorrelationPeak + ", crossCorrelationPeak = " + crossCorrelationPeak;
		else
			return MathLib.printCoordinates( position ) + " [" + MathLib.printCoordinates( originalInvPCMPosition ) + "], phaseCorrelationPeak = " + phaseCorrelationPeak + ", crossCorrelationPeak = " + crossCorrelationPeak; 
	}
}
