package com.myProject.CyberTrace.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myProject.CyberTrace.Model.Enquiry;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {

	List<Enquiry> findTop3ByOrderByEnquiryDateDesc();


}
