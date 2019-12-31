package com.a6raywa1cher.mucpollspring.dao.repository.sql;

import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends PagingAndSortingRepository<Poll, Long> {
	List<Poll> getAllByCreatorId(Long creator_id);

	List<Poll> getAllByCreatorId(Long creator_id, Pageable pageable);

	@Modifying
	@Query("update Poll p set p.launchedCount = p.launchedCount + :#{#delta} where p.id = :#{#poll.id}")
	void incrementLaunchedCount(@Param("poll") Poll poll, @Param("delta") int delta);
}
