package com.prediction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.prediction.entity.StudentRecord;

import jakarta.transaction.Transactional;

public interface StudentRecordRepository extends JpaRepository<StudentRecord, Long> {

	StudentRecord findByStudentId(Long studentId);

	@Transactional
	@Modifying
	@Query("DELETE FROM StudentRecord s WHERE s.studentId = :studentId")
	void deleteByStudentId(String studentId);
//	void deleteByStudentId(String studentId);

	StudentRecord findByStudentId(String studentId);

}
