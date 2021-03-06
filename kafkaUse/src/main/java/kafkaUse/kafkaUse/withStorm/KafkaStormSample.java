package kafkaUse.kafkaUse.withStorm;

import java.util.Properties;
import java.util.UUID;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

public class KafkaStormSample 
{
	public static void main(String[] args) throws Exception
 {
		Config config = new Config();
		config.setDebug(true);
		config.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
		
		String zkConnString = "192.168.153.233:2181";
		String topic = "test";
		BrokerHosts hosts = new ZkHosts(zkConnString);

		SpoutConfig kafkaSpoutConfig = new SpoutConfig(hosts, topic, "/"
				+ topic, UUID.randomUUID().toString());
		kafkaSpoutConfig.bufferSizeBytes = 1024 * 1024 * 4;
		kafkaSpoutConfig.fetchSizeBytes = 1024 * 1024 * 4;
		// kafkaSpoutConfig.forceFromStart = true;
		kafkaSpoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		
		Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.153.233:9092");
        props.put("acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaBolt<String, String> bolt = new KafkaBolt<String, String>()
                .withProducerProperties(props)
                .withTopicSelector(new DefaultTopicSelector("topic3"))
                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<String, String>("word", "count"));

		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("kafkaspout", new KafkaSpout(kafkaSpoutConfig));
		builder.setBolt("wordspitter", new SplitBolt()).shuffleGrouping(
				"kafkaspout");
		builder.setBolt("wordcounter", new CountBolt()).shuffleGrouping(
				"wordspitter");
		builder.setBolt("kafkabolt", bolt).shuffleGrouping("wordcounter");

		if (args != null && args.length > 0) {
			config.setNumWorkers(3);
			StormSubmitter.submitTopology(args[0], config,
					builder.createTopology());
		} else {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("Topo", config, builder.createTopology());
			Utils.sleep(100000);
			cluster.killTopology("Topo");
			cluster.shutdown();
		}

	}
}
