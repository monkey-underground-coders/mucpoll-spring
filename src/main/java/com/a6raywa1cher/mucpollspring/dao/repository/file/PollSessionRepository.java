package com.a6raywa1cher.mucpollspring.dao.repository.file;

import com.a6raywa1cher.mucpollspring.models.file.PollSession;

import java.util.Optional;
import java.util.stream.Stream;

public interface PollSessionRepository {
	Stream<String> getAllSidsByPid(Long pid);

	Optional<PollSession> getBySidAndPid(Long pid, String sid);

	PollSession save(PollSession pollSession);

	void deleteAllByPid(Long pid);
}
