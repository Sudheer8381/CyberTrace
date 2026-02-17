package com.myProject.CyberTrace.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myProject.CyberTrace.Model.Complaint;
import com.myProject.CyberTrace.Model.Complaint.ComplaintStatus;
import com.myProject.CyberTrace.Model.Users;
import com.myProject.CyberTrace.Model.Users.UserRole;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>{

	boolean existsByEmail(String email);

	Users findByEmail(String email);

	List<Users> findAllByRole(UserRole investigator);


}
