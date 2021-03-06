package stormUse.stormUse.mysql.topology;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.jdbc.bolt.JdbcInsertBolt;
import org.apache.storm.jdbc.bolt.JdbcLookupBolt;
import org.apache.storm.topology.TopologyBuilder;

public class UserPersistanceTopology extends AbstractUserTopology
{
	private static final String USER_SPOUT = "USER_SPOUT";
    private static final String LOOKUP_BOLT = "LOOKUP_BOLT";
    private static final String PERSISTANCE_BOLT = "PERSISTANCE_BOLT";
    
    public static void main(String[] args) throws Exception
    {
        new UserPersistanceTopology().execute(args);
    }

	@Override
	public StormTopology getTopology() 
	{
		JdbcLookupBolt departmentLookupBolt = new JdbcLookupBolt(connectionProvider, SELECT_QUERY, this.jdbcLookupMapper);
		
		
        JdbcInsertBolt userPersistanceBolt = new JdbcInsertBolt(connectionProvider, this.jdbcMapper)
                .withInsertQuery("insert into user (create_date, dept_name, user_id, user_name) values (?,?,?,?)");

        // userSpout ==> jdbcBolt
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(USER_SPOUT, this.kafkaSpout, 1);
        builder.setBolt(LOOKUP_BOLT, departmentLookupBolt, 1).shuffleGrouping(USER_SPOUT);
        builder.setBolt(PERSISTANCE_BOLT, userPersistanceBolt, 1).shuffleGrouping(LOOKUP_BOLT);
        return builder.createTopology();

	}
    
}
