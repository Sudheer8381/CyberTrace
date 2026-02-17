package com.myProject.CyberTrace.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Notification {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false, length = 500)
	    private String message;

	    @Enumerated(EnumType.STRING)
	    private NotificationStatus status;
	    
	    public enum NotificationStatus {
	        RUNNING,
	        ENDED
	    }

	    @Column(nullable = false)
	    private LocalDateTime publishAt;

	    

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getMessage() {
	        return message;
	    }

	    public void setMessage(String message) {
	        this.message = message;
	    }

	    public NotificationStatus getStatus() {
	        return status;
	    }

	    public void setStatus(NotificationStatus status) {
	        this.status = status;
	    }

	    public LocalDateTime getPublishAt() {
	        return publishAt;
	    }

	    public void setPublishAt(LocalDateTime publishAt) {
	        this.publishAt = publishAt;
	    }
}


