package com.github.diegopacheco.java.sanbox.cassandradatastax.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.ProtocolOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;

public class MainApp {
	
	private static final Logger logger   = LoggerFactory.getLogger("com.github.diegopacheco.java.sanbox.cassandradatastax.main.MainApp");
	private static final Cluster cluster = clusterConnect();
	private static final Session session = cluster.connect("datastax_mapper_test");
	private static final MappingManager manager  = new MappingManager(session);
	private static final Mapper<User> userMapper = manager.mapper(User.class);
	private static final UserAccessor userAccessor = manager.createAccessor(UserAccessor.class);
	private static final Integer TOTAL_RUNS = 200; 
	
	private static void startup(){
		logger.info("Connected to Cassandra Cluster: " + cluster + " on Session: " + session);
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		startup();
		
		final Result<User> users = null;
		
		for(int i=0;i<=TOTAL_RUNS;i++){
		 	new Thread(new Runnable() {
				@Override
				public void run() {
					User u1 = new User("Diego", "diegoSQN@gmail.com",1984);
					userMapper.save(u1);
					userAccessor.getAll();
				}
			}).start();
			 
		}
		logger.info("Users from accessor: " + users);
		
		if (users!=null){
			for(User u : users){
				System.out.println(u);
			}
		}
	}

	private static Cluster clusterConnect() {
		Builder builder = Cluster.builder();
		builder.addContactPoint("127.0.0.1");

		PoolingOptions pool  = new PoolingOptions();
		pool.setMaxSimultaneousRequestsPerConnectionThreshold(HostDistance.LOCAL, 128);
		pool.setMaxSimultaneousRequestsPerConnectionThreshold(HostDistance.REMOTE, 128);
		pool.setCoreConnectionsPerHost(HostDistance.LOCAL, 2);
		pool.setCoreConnectionsPerHost(HostDistance.REMOTE, 2);
		pool.setMaxConnectionsPerHost(HostDistance.LOCAL, 10);
		pool.setMaxConnectionsPerHost(HostDistance.REMOTE, 10);
		
		Cluster cluster =  builder
						  .withPort(9042)	
						  .withPoolingOptions(pool)
						  .withSocketOptions(new SocketOptions().setTcpNoDelay(true))
						  .build();
		
		cluster.getConfiguration().
		getProtocolOptions().
		setCompression(ProtocolOptions.Compression.SNAPPY);
		
		return cluster;
	}
}
