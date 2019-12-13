package com.a6raywa1cher.mucpollspring.dao.repository;

import com.a6raywa1cher.mucpollspring.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	Optional<User> getByUsername(String username);
}
