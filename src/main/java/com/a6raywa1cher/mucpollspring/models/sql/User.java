package com.a6raywa1cher.mucpollspring.models.sql;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
public class User {
	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@JsonIgnore
	@Column(nullable = false)
	private String password;

	@Column
	@Enumerated(EnumType.ORDINAL)
	private UserStatus status;

	@JsonBackReference
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
