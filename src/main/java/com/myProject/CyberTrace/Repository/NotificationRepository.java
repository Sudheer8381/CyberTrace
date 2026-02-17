package com.myProject.CyberTrace.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myProject.CyberTrace.Model.Notification;
import com.myProject.CyberTrace.Model.Notification.NotificationStatus;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{

	List<Notification> findAllByStatus(NotificationStatus running);

}
