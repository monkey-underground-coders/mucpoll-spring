package com.a6raywa1cher.mucpollspring.dao.repository.sql;

import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PollQuestionRepository extends CrudRepository<PollQuestion, Long> {
	@Query(value = "select max(index) from poll_question where poll_id = ?1", nativeQuery = true)
	Optional<Integer> getMaxIndex(Poll poll);
}
