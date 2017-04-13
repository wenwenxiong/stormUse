package stormUse.stormUse.hdfs;

import java.util.Map;

import org.apache.storm.hdfs.spout.HdfsSpout;
import org.apache.storm.hdfs.spout.TextFileReader;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HdfsSpoutTopology 
{
	public static final String SPOUT_ID = "hdfsspout";
	public static final String BOLT_ID = "constbolt";
	
	public static class ConstBolt extends BaseRichBolt 
	{
		private static final long serialVersionUID = -5313598399155365865L;
	    public static final String FIELDS = "message";
	    private OutputCollector collector;
	    private static final Logger log = LoggerFactory.getLogger(ConstBolt.class);
	    int count =0;
	    
	    public ConstBolt() 
	    {
	    }

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector)
		{
			this.collector = collector;
		}

		@Override
		public void execute(Tuple tuple) 
		{
			log.info("Received tuple : {}", tuple.getValue(0));
			count++;
			if (count == 3)
			{
				collector.fail(tuple);
			} else 
			{
				collector.ack(tuple);
			}
	
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer)
		{
			declarer.declare(new Fields(FIELDS));
		}
	    
	}
	
	 /** Copies text file content from sourceDir to destinationDir. Moves source files into sourceDir after its done consuming */
	  public static void main(String[] args) throws Exception 
	  {
		
		// 0 - validate args
		if (args.length < 7) {
			System.err.println("Please check command line arguments.");
			System.err.println("Usage :");
			System.err
					.println(HdfsSpoutTopology.class.toString()
							+ " topologyName hdfsUri fileFormat sourceDir sourceArchiveDir badDir destinationDir.");
			System.err.println(" topologyName - topology name.");
			System.err.println(" hdfsUri - hdfs name node URI");
			System.err
					.println(" fileFormat -  Set to 'TEXT' for reading text files or 'SEQ' for sequence files.");
			System.err
					.println(" sourceDir  - read files from this HDFS dir using HdfsSpout.");
			System.err
					.println(" archiveDir - after a file in sourceDir is read completely, it is moved to this HDFS location.");
			System.err
					.println(" badDir - files that cannot be read properly will be moved to this HDFS location.");
			System.err.println(" spoutCount - Num of spout instances.");
			System.err.println();
			System.exit(-1);
		}
		 
		// 1 - parse cmd line args
		String topologyName = args[0];
		String hdfsUri = args[1];
		String fileFormat = args[2];
		String sourceDir = args[3];
		String archiveDir = args[4];
		String badDir = args[5];
		int spoutNum = Integer.parseInt(args[6]);
		
		// 2 - create and configure spout and bolt
	    ConstBolt bolt = new ConstBolt();
	  }
}


