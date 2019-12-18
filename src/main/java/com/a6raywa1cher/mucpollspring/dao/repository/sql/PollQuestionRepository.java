package com.a6raywa1cher.mucpollspring.dao.repository.sql;

import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.PollQuestion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PollQuestionRepository extends CrudRepository<PollQuestion, Long> {
	@Query("select max(pq.index) from PollQuestion as pq where pq.poll = ?1 group by pq.index")
	Optional<Integer> getMaxIndex(Poll poll);
}
