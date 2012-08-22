package net.imglib2.ops.operation.randomaccessibleinterval.regiongrowing;

public class ThreadSafeLabelNumbers
{

	// Current labelnumber
	private int m_labelNumber;

	public ThreadSafeLabelNumbers()
	{
		m_labelNumber = 1;
	}

	public final synchronized int aquireNewLabelNumber()
	{
		return m_labelNumber++;
	}
}
