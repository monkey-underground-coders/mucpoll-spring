package com.a6raywa1cher.mucpollspring.dao.repository.file;

import com.a6raywa1cher.mucpollspring.config.AppConfigProperties;
import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class PollSessionRepositoryImpl implements PollSessionRepository {
	private static final Logger log = LoggerFactory.getLogger(PollSessionRepositoryImpl.class);
	private Path path;
	private ObjectMapper objectMapper;

	@Autowired
	public PollSessionRepositoryImpl(AppConfigProperties appConfigProperties) {
		path = Path.of(appConfigProperties.getHistoryDirectory());
		objectMapper = new ObjectMapper()
				.registerModule(new JavaTimeModule());
	}

	private Path pidToPath(Long pid) {
		String pidString = pid + "_";
		StringBuilder sb = new StringBuilder();
		for (char c : pidString.toCharArray()) {
			if (sb.length() % 3 == 2) {
				sb.append('/');
			}
			sb.append(c);
		}
		sb.append('/');
		return path.resolve(sb.toString());
	}

	@PostConstruct
	public void createFolder() throws IOException {
		Files.createDirectories(path);
	}

	@Override
	@SneakyThrows
	public Stream<String> getAllSidsByPid(Long pid) {
		Path pathToPoll = pidToPath(pid);
		Files.createDirectories(pathToPoll);
		return Files.list(pathToPoll)
				.sorted(Comparator.comparingLong(p -> p.toFile().lastModified()))
				.sorted(Comparator.reverseOrder())
				.map(p -> p.getFileName().toString());
	}

	@Override
	public Optional<PollSession> getBySidAndPid(Long pid, String sid) {
		Path pathToPoll = pidToPath(pid);
		Path pathToFile = pathToPoll.resolve(sid);
		try {
			return Optional.of(objectMapper.readValue(pathToFile.toFile(), PollSession.class));
		} catch (IOException e) {
			log.info(String.format("Sid %s at pid %d not found or IO error", sid, pid), e);
			return Optional.empty();
		}
	}

	@Override
	@SneakyThrows
	public PollSession save(PollSession pollSession) {
		Path pathToPoll = pidToPath(pollSession.getPid());
		Files.createDirectories(pathToPoll);
		Path pathToFile = pathToPoll.resolve(pollSession.getSid());
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pathToFile.toFile()))) {
			objectMapper.writeValue(writer, pollSession);
		}
		return pollSession;
	}

	@Override
	public void deleteAllByPid(Long pid) {
		Path pathToPoll = pidToPath(pid);
		try {
			FileUtils.deleteDirectory(pathToPoll.toFile());
		} catch (IOException e) {
			log.error("Delete poll history unsuccessful", e);
		}
	}
}
