package stormUse.stormUse;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class SenqueceBolt extends BaseBasicBolt
{

	public void execute(Tuple input, BasicOutputCollector collector) 
	{
        // TODO Auto-generated method stub
         String word = (String) input.getValue(0);  
         String out = "I'm " + word +  "!";  
         System.out.println("out=" + out);
         collector.emit(new Values(out));
    }
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) 
	{
        declarer.declare(new Fields("message"));
    }
}
