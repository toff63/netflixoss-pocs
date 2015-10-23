package com.github.diegopacheco.sandbox.java.kundera.domain.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.github.diegopacheco.sandbox.java.kundera.domain.model.User;

public class KunderaExampleService {
	public static void main(String[] args) {
		User user = new User();
		user.setUserId("0001");
		user.setFirstName("John");
		user.setLastName("Smith");
		user.setCity("London");

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("cassandra_pu");
		EntityManager em = emf.createEntityManager();

		em.persist(user);
		em.close();
		emf.close();
		
		queryBenchmark(300);
		
	}
	
	private static void queryBenchmark(Integer numberOfTimes){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("cassandra_pu");
		EntityManager em = emf.createEntityManager();
		
		for(int i = 0; i<= numberOfTimes; i++){
			User result = em.find(User.class,"0001" + i);
			System.out.println("result: " + result);
		}
		
		em.close();
		emf.close();
	}
	
}
