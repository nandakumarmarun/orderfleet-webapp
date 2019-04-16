package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.File;

/**
 * Spring Data JPA repository for the File entity.
 * 
 * @author Shaheer
 * @since August 01, 2016
 */
public interface FileRepository extends JpaRepository<File, Long> {

	Optional<File> findOneByPid(String pid);

	Optional<File> findOneByFileName(String filename);

	List<File> findByFileNameAndUploadedDateAfter(String filename, LocalDateTime after);

	List<File> findByPidAndUploadedDateAfter(String pid, LocalDateTime after);

	@Transactional
	void deleteByPid(String pid);

}
