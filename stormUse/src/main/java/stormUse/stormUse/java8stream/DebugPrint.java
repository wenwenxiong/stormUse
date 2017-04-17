package stormUse.stormUse.java8stream;

public class DebugPrint
{
	Boolean debug;
	
	public DebugPrint(Boolean debug)
	{
		this.debug = debug;
	}
	
	<T> void print(Boolean debug, T Obj)
	{
		if (debug)
		{
			System.out.println(Obj);
		}
	}

	public Boolean getDebug() {
		return debug;
	}

	public void setDebug(Boolean debug) {
		this.debug = debug;
	}

}
