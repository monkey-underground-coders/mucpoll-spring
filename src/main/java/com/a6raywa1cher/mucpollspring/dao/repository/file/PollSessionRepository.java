package com.a6raywa1cher.mucpollspring.dao.repository.file;

import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.stream.Stream;

public interface PollSessionRepository {
	Stream<String> getAllSidsByPid(Long pid);

	Optional<PollSession> getBySidAndPid(Long pid, String sid);

	Page<PollSession> getPageByPid(Long pid, Pageable pageable);

	PollSession save(PollSession pollSession);

	void deleteAllByPid(Long pid);
}
