package stormUse.stormUse.mysql.topology;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.apache.storm.jdbc.common.JdbcClient;
import org.apache.storm.jdbc.mapper.JdbcLookupMapper;
import org.apache.storm.jdbc.mapper.JdbcMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcLookupMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcMapper;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

import stormUse.stormUse.mysql.spout.UserScheme;
import stormUse.stormUse.mysql.spout.UserSpout;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class AbstractUserTopology 
{
	private static final List<String> setupSqls = Lists.newArrayList
			(
            "drop table if exists user",
            "drop table if exists department",
            "drop table if exists user_department",
            "create table if not exists user (user_id integer, user_name varchar(100), dept_name varchar(100), create_date date)",
            "create table if not exists department (dept_id integer, dept_name varchar(100))",
            "create table if not exists user_department (user_id integer, dept_id integer)",
            "insert into department values (1, 'R&D')",
            "insert into department values (2, 'Finance')",
            "insert into department values (3, 'HR')",
            "insert into department values (4, 'Sales')",
            "insert into user_department values (1, 1)",
            "insert into user_department values (2, 2)",
            "insert into user_department values (3, 3)",
            "insert into user_department values (4, 4)"
    );
	
	protected UserSpout userSpout;
	protected KafkaSpout kafkaSpout;
	protected JdbcMapper jdbcMapper;
	protected JdbcLookupMapper jdbcLookupMapper;
	protected ConnectionProvider connectionProvider;
	
	protected static final String TABLE_NAME = "user";
    protected static final String JDBC_CONF = "jdbc.conf";
    protected static final String SELECT_QUERY = "select dept_name from department, user_department where department.dept_id = user_department.dept_id" +
            " and user_department.user_id = ?";
    
    public void execute(String[] args) throws Exception 
    {
    	if (args.length != 4 && args.length != 5) {
            System.out.println("Usage: " + this.getClass().getSimpleName() + " <dataSourceClassName> <dataSource.url> "
                    + "<user> <password> [topology name]");
            System.exit(-1);
        }
        Map map = Maps.newHashMap();
        map.put("dataSourceClassName", args[0]);//com.mysql.jdbc.jdbc2.optional.MysqlDataSource
        map.put("dataSource.url", args[1]);//jdbc:mysql://localhost/test
        map.put("dataSource.user", args[2]);//root

        map.put("dataSource.password", args[3]);//password

        Config config = new Config();
        config.put(JDBC_CONF, map);
        
        ConnectionProvider connectionProvider = new HikariCPConnectionProvider(map);
        connectionProvider.prepare();
        int queryTimeoutSecs = 60;
        JdbcClient jdbcClient = new JdbcClient(connectionProvider, queryTimeoutSecs);
        for (String sql : setupSqls) {
            jdbcClient.executeSql(sql);
        }

        this.userSpout = new UserSpout();
        connectionProvider.cleanup();
        
        Fields outputFields = new Fields("user_id", "user_name", "dept_name", "create_date");
        List<Column> queryParamColumns = Lists.newArrayList(new Column("user_id", Types.INTEGER));
        this.jdbcLookupMapper = new SimpleJdbcLookupMapper(outputFields, queryParamColumns);
        
        //must specify column schema when providing custom query.
        List<Column> schemaColumns = Lists.newArrayList(new Column("create_date", Types.DATE),
                new Column("dept_name", Types.VARCHAR), new Column("user_id", Types.INTEGER), new Column("user_name", Types.VARCHAR));
        jdbcMapper = new SimpleJdbcMapper(schemaColumns);
        
        this.connectionProvider = new HikariCPConnectionProvider(map);
        
        String zkConnString = "192.168.153.233:2181";
		String topic = "mysql";
		BrokerHosts hosts = new ZkHosts(zkConnString);

		SpoutConfig kafkaSpoutConfig = new SpoutConfig(hosts, topic, "/"
				+ topic, UUID.randomUUID().toString());
		kafkaSpoutConfig.bufferSizeBytes = 1024 * 1024 * 4;
		kafkaSpoutConfig.fetchSizeBytes = 1024 * 1024 * 4;
		// kafkaSpoutConfig.forceFromStart = true;
		kafkaSpoutConfig.scheme = new SchemeAsMultiScheme(new UserScheme());
		this.kafkaSpout = new KafkaSpout(kafkaSpoutConfig);
        
        
        if (args.length == 4) 
        {
            LocalCluster cluster = new LocalCluster();  
            cluster.submitTopology("test", config, getTopology());
            Utils.sleep(100000);  
            cluster.killTopology("test");  
            cluster.shutdown();  
        } 
        else
        {
            StormSubmitter.submitTopology(args[4], config, getTopology());
        }
    }
    
    public abstract StormTopology getTopology();


}
