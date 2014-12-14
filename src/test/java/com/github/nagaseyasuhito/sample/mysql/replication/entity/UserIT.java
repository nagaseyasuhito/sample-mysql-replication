package com.github.nagaseyasuhito.sample.mysql.replication.entity;

import java.sql.Connection;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import lombok.Cleanup;
import lombok.extern.java.Log;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;

@Log
public class UserIT {

	@Test
	public void persistSuccess() throws Exception {
		Map<String, String> properties = ImmutableMap.of("javax.persistence.jdbc.url", System.getProperty("javax.persistence.jdbc.url"));

		@Cleanup
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("sample-mysql-replication", properties);
		@Cleanup
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		entityManager.getTransaction().begin();
		User user = new User();
		user.setName("name");
		user.setPassword("password");
		entityManager.persist(user);
		entityManager.getTransaction().commit();

		// from slave
		entityManager.getTransaction().begin();
		// for EclipseLink
		entityManager.unwrap(Connection.class).setReadOnly(true);
		// for Hibernate
		// entityManager.unwrap(SessionImplementor.class).connection().setReadOnly(true);
		assertThat(entityManager.createQuery("from User u where u.name = 'name'", User.class).getSingleResult(), is(user));
		entityManager.getTransaction().commit();
	}
}
