package com.a6raywa1cher.mucpollspring.rest.mirror;

import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.Tag;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TagMirror {
	private Long id;

	private Long creator_id;

	private List<Long> pidList;

	private String name;

	public static TagMirror convert(Tag tag, boolean includePids) {
		TagMirror mirror = new TagMirror();
		mirror.setId(tag.getId());
		mirror.setCreator_id(tag.getCreator().getId());
		if (includePids) {
			mirror.setPidList(tag.getPollList().stream()
					.map(Poll::getId)
					.collect(Collectors.toList()));
		} else {
			mirror.setPidList(null);
		}
		mirror.setName(tag.getName());
		return mirror;
	}
}
