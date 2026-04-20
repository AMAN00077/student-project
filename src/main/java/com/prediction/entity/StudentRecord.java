package com.prediction.entity;

import jakarta.persistence.Column;
//package com.project.studentml.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "student_record", uniqueConstraints = @UniqueConstraint(columnNames = "studentId"))
public class StudentRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true, nullable = false)

	private Long studentId;

	private int attendance;
	private int internalMarks;
	private int assignmentScore;
	private int quizScore;
	private double studyHours;
	private double previousCgpa;
	private int backlogs;
	private String classParticipation;

	private String submissionRegular;

	// getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public int getAttendance() {
		return attendance;
	}

	public void setAttendance(int attendance) {
		this.attendance = attendance;
	}

	public int getInternalMarks() {
		return internalMarks;
	}

	public void setInternalMarks(int internalMarks) {
		this.internalMarks = internalMarks;
	}

	public int getAssignmentScore() {
		return assignmentScore;
	}

	public void setAssignmentScore(int assignmentScore) {
		this.assignmentScore = assignmentScore;
	}

	public int getQuizScore() {
		return quizScore;
	}

	public void setQuizScore(int quizScore) {
		this.quizScore = quizScore;
	}

	public double getStudyHours() {
		return studyHours;
	}

	public void setStudyHours(double studyHours) {
		this.studyHours = studyHours;
	}

	public double getPreviousCgpa() {
		return previousCgpa;
	}

	public void setPreviousCgpa(double previousCgpa) {
		this.previousCgpa = previousCgpa;
	}

	public int getBacklogs() {
		return backlogs;
	}

	public void setBacklogs(int backlogs) {
		this.backlogs = backlogs;
	}

	public String getClassParticipation() {
		return classParticipation;
	}

	public void setClassParticipation(String classParticipation) {
		this.classParticipation = classParticipation;
	}

	public String getSubmissionRegular() {
		return submissionRegular;
	}

	public void setSubmissionRegular(String submissionRegular) {
		this.submissionRegular = submissionRegular;
	}

}
