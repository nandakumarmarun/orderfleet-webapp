package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orderfleet.webapp.domain.PostDatedVoucherAllocation;

@Repository
public interface PostDatedVoucherAllocationRepository extends JpaRepository<PostDatedVoucherAllocation, Long> {

}
