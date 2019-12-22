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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class PollSessionRepositoryImpl implements PollSessionRepository {
	private static final Logger log = LoggerFactory.getLogger(PollSessionRepositoryImpl.class);
	private Path path;
	private ObjectMapper objectMapper;
	private static final Pattern uuid =
			Pattern.compile("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})");

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
	public Page<PollSession> getPageByPid(Long pid, Pageable pageable) {
		Path pathToPoll = pidToPath(pid);
		Files.createDirectories(pathToPoll);
		Sort.Order order = pageable.getSort().getOrderFor("recordedAt");
		Comparator<Path> pathComparator = (order != null && order.getDirection().isAscending()) ?
				Comparator.naturalOrder() : Comparator.reverseOrder();

		return new PageImpl<>(Files.list(pathToPoll)
				.filter(p -> uuid.matcher(p.getFileName().toString()).matches())
				.sorted(Comparator.comparingLong(p -> p.toFile().lastModified()))
				.sorted(pathComparator)
				.skip(pageable.getOffset())
				.limit(pageable.getPageSize())
				.map(p -> {
					try {
						return objectMapper.readValue(p.toFile(), PollSession.class);
					} catch (IOException e) {
						log.info(String.format("Path %s at pid %d not found or IO error", p, pid), e);
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList())
		);
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

	@Override
	public void delete(Long pid, String sid) {
		if (!uuid.matcher(sid).matches()) {
			return;
		}
		Path pathToPoll = pidToPath(pid);
		Path pathToFile = pathToPoll.resolve(sid);
		try {
			Files.deleteIfExists(pathToFile);
		} catch (IOException e) {
			log.error(String.format("Delete poll error, pid:%d sid:%s", pid, sid), e);
		}
	}
}
