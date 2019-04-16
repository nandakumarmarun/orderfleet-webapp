package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.State;

public interface StateRepository extends JpaRepository<State, Long> {

	public State findByCode(String code);

	public State findByName(String name);

	public List<State> findAllByCountryId(Long id);
}
