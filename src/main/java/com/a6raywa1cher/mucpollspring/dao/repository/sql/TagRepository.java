package com.a6raywa1cher.mucpollspring.dao.repository.sql;

import com.a6raywa1cher.mucpollspring.models.sql.Tag;
import com.a6raywa1cher.mucpollspring.models.sql.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {
	List<Tag> getAllByCreator(User creator);
}
