package com.myProject.CyberTrace.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.myProject.CyberTrace.Model.Complaint;
import com.myProject.CyberTrace.Model.Complaint.ComplaintStatus;
import com.myProject.CyberTrace.Model.Users;

public interface ComplaintRepository extends JpaRepository<Complaint, Long>{

	List<Complaint> findAllByStatus(ComplaintStatus pending);


	List<Complaint> findAllByStatusOrStatus(ComplaintStatus proccessing, ComplaintStatus resolved);


	Complaint findByComplaintId(String cid);


	boolean existsBycomplaintId(String cid);


	List<Complaint> findAllByStatusAndAssignedTo(ComplaintStatus proccessing, Users investigator);


  
	//  Monthly Complaint Counts (Native SQL)
    @Query(value = "SELECT MONTH(c.reg_date_time) AS month, COUNT(*) AS total " +
                   "FROM complaint c " +
                   "GROUP BY MONTH(c.reg_date_time) " +
                   "ORDER BY month", nativeQuery = true)
    List<Object[]> getMonthlyComplaintStats();

}
