package kafkaUse.kafkaUse.withStorm;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class CountBolt implements IRichBolt
{
	Map<String, Integer> counters;
	private OutputCollector collector;
	
	@Override
	public void cleanup() 
	{
		 for(Map.Entry<String, Integer> entry:counters.entrySet())
		 {
	         System.out.println(entry.getKey()+" : " + entry.getValue());
	      }
	}
	@Override
	public void execute(Tuple input) 
	{
		String str = input.getString(0);
		Integer c = 0;
	      if(!counters.containsKey(str)){
	    	 c = 1; 
	         counters.put(str, c);
	      }else {
	         c = counters.get(str) +1;
	         counters.put(str, c);
	      }
	   
	     // collector.ack(input);
	    String value = str + " : " + String.valueOf(c);  
	    collector.emit(new Values(str, value));
		
	}
	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector collector) 
	{
		this.counters = new HashMap<String, Integer>();
	    this.collector = collector;
		
	}
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer)
	{
		declarer.declare(new Fields("word", "count"));
	}
	@Override
	public Map<String, Object> getComponentConfiguration()
	{
		return null;
	}
	
	
	
}
