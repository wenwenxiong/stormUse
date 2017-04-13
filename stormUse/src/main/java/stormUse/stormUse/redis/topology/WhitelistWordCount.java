package stormUse.stormUse.redis.topology;

import java.util.Map;
import java.util.Random;

import org.apache.storm.Config;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhitelistWordCount {
	private static final String WORD_SPOUT = "WORD_SPOUT";
    private static final String WHITELIST_BOLT = "WHITELIST_BOLT";
    private static final String COUNT_BOLT = "COUNT_BOLT";
    private static final String PRINT_BOLT = "PRINT_BOLT";

    private static final String TEST_REDIS_HOST = "127.0.0.1";
    private static final int TEST_REDIS_PORT = 6379;
    
    public static class PrintWordTotalCountBolt extends BaseRichBolt {
        private static final Logger LOG = LoggerFactory.getLogger(PrintWordTotalCountBolt.class);
        private static final Random RANDOM = new Random();
        private OutputCollector collector;

        @SuppressWarnings("rawtypes")
		@Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            this.collector = collector;
        }

        @Override
        public void execute(Tuple input) {
            String wordName = input.getStringByField("word");
            String countStr = input.getStringByField("count");

            // print lookup result with low probability
            if(RANDOM.nextInt(1000) > 995) {
                int count = 0;
                if (countStr != null) {
                    count = Integer.parseInt(countStr);
                }
                LOG.info("Count result - word : " + wordName + " / count : " + count);
            }

            collector.ack(input);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
        }
    }
    
    public static void main(String[] args) throws Exception {
        Config config = new Config();

        String host = TEST_REDIS_HOST;
        int port = TEST_REDIS_PORT;

        if (args.length >= 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }
        
        JedisPoolConfig poolConfig = new JedisPoolConfig.Builder()
        .setHost(host).setPort(port).build();
        
    }

}
