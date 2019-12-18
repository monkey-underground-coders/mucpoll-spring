package com.a6raywa1cher.mucpollspring.models.sql;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class User {
	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column
	@Enumerated(EnumType.ORDINAL)
	private UserStatus status;

	@OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
	private List<Poll> pollList;

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				", status=" + status +
				'}';
	}
}
