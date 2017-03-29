package stormUse.stormUse;

import java.util.Properties;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

public class StormKafkaTopo 
{

	public static void main(String[] args) throws Exception 
	{ 
		// 配置Zookeeper地址
        BrokerHosts brokerHosts = new ZkHosts("192.168.153.233:2181");
        // 配置Kafka订阅的Topic，以及zookeeper中数据节点目录和名字
        SpoutConfig spoutConfig = new SpoutConfig(brokerHosts, "test", "/test" , "kafkaspout");
       
        // 配置KafkaBolt中的kafka.broker.properties
        Config conf = new Config();  
        
        //set producer properties.
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.153.233:9092");
        props.put("acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaBolt<String, String> bolt = new KafkaBolt<String, String>()
                .withProducerProperties(props)
                .withTopicSelector(new DefaultTopicSelector("topic2"))
                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<String, String>());
       
        
        spoutConfig.scheme = new SchemeAsMultiScheme(new MessageScheme());  
        TopologyBuilder builder = new TopologyBuilder();   
        builder.setSpout("spout", new KafkaSpout(spoutConfig));  
        builder.setBolt("bolt", new SenqueceBolt()).shuffleGrouping("spout"); 
        //builder.setBolt("kafkabolt", new KafkaBolt<String, Integer>()).shuffleGrouping("bolt");
        builder.setBolt("kafkabolt", bolt).shuffleGrouping("bolt");
        
        if (args != null && args.length > 0) 
        {  
            conf.setNumWorkers(3);  
            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());  
        } else 
        {  
            LocalCluster cluster = new LocalCluster();  
            cluster.submitTopology("Topo", conf, builder.createTopology());  
            Utils.sleep(100000);  
            cluster.killTopology("Topo");  
            cluster.shutdown();  
        }  
	}
}
