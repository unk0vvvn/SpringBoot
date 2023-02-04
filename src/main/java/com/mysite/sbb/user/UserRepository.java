package com.mysite.sbb.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<SiteUser,Integer>{
	Optional<SiteUser> findByUsername(String username);
	Optional<SiteUser> findByEmail(String email);

	@Query(value = "SELECT password FROM SITE_USER WHERE username = :username LIMIT 1", nativeQuery = true)
	String findPasswordByUsername(@Param("username") String username);
	
	@Transactional
	void deleteByUsername(String username);
	
	@Transactional
	@Modifying
	@Query("UPDATE SiteUser "
			+ "SET password = :password "
			+ "WHERE username = :username")
	void updatePassword(@Param("username")String username, @Param("password") String password);
	
	
	
}
