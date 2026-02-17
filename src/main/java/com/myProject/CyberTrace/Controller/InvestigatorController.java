package com.myProject.CyberTrace.Controller;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myProject.CyberTrace.Model.Complaint;
import com.myProject.CyberTrace.Model.Complaint.ComplaintStatus;
import com.myProject.CyberTrace.Model.Users;
import com.myProject.CyberTrace.Repository.ComplaintRepository;
import com.myProject.CyberTrace.Repository.UserRepository;

import jakarta.mail.Multipart;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Investigator")
public class InvestigatorController {

	
	@Autowired 
	private UserRepository userRepo;

	@Autowired
	private HttpSession session;
	
	@Autowired
	private ComplaintRepository complaintRepo;
		
	@GetMapping("/Dashboard")
	public String ShowDashboard(Model model)
	{
		if(session.getAttribute("loggedInUser")==null) {
			return"redirect:/Login";
		}
		
		Users investigator = (Users) session.getAttribute("loggedInUser");
		
		int processingComplaints = complaintRepo.findAllByStatusAndAssignedTo(ComplaintStatus.PROCCESSING, investigator).size();
		int terminatedComplaints = complaintRepo.findAllByStatusAndAssignedTo(ComplaintStatus.TERMINATED, investigator).size();
		int solvedComplaints = complaintRepo.findAllByStatusAndAssignedTo(ComplaintStatus.RESOLVED, investigator).size();
		
		model.addAttribute("processingComplaints", processingComplaints);
		model.addAttribute("terminatedComplaints", terminatedComplaints);
		model.addAttribute("solvedComplaints", solvedComplaints);
		return "Investigator/Dashboard";
	}
	
	@GetMapping("/Logout")
	public String Logout() {
		session.removeAttribute("loggedInUser");
		return "redirect:/Login";
	}
	
	@GetMapping("/AssignedComplaints")
    public String AssignedComplaints(Model model)
    {
		if(session.getAttribute("loggedInUser")==null) {
			return"redirect:/AssignedComplaints";
		}
		
		Users investigator = (Users) session.getAttribute("loggedInUser");    
		
		List<Complaint> complaints = complaintRepo.findAllByStatusAndAssignedTo(ComplaintStatus.PROCCESSING, investigator);
		model.addAttribute("complaints", complaints);
    	return"Investigator/AssignedComplaints";
    }
	
	@PostMapping("/CloseComplaint")
	public String CloseComplaint(@RequestParam("cid") long cid, @RequestParam("message") String message, RedirectAttributes attributes) {
		
		try {
			Complaint complaint = complaintRepo.findById(cid).get();
			
			complaint.setMessage(message);
			complaint.setStatus(ComplaintStatus.RESOLVED);
			complaint.setSolvedAt(LocalDateTime.now());
			complaintRepo.save(complaint);
			
			attributes.addFlashAttribute("msg", "Complaint Successfully Closed");
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}
		
		return"redirect:/Investigator/AssignedComplaints";
	}
	
	@GetMapping("/ClosedComplaints")
    public String ClosedComplaints(Model model)
    {
		if(session.getAttribute("loggedInUser")==null) {
			return"redirect:/ClosedComplaints";
		}
		
		Users investigator = (Users) session.getAttribute("loggedInUser");
		
		List<Complaint> complaints = complaintRepo.findAllByStatusAndAssignedTo(ComplaintStatus.RESOLVED, investigator);
		model.addAttribute("complaints",complaints);
		
    	return"Investigator/ClosedComplaints";
    }
	
	@PostMapping("/RejectComplaint")
	public String RejectComplaint(@RequestParam("cid") long cid, @RequestParam("message" )String message, RedirectAttributes attributes) {
		
		try {
			Complaint complaint = complaintRepo.findById(cid).get();
			
			complaint.setMessage(message);
			complaint.setStatus(ComplaintStatus.TERMINATED);
			complaint.setSolvedAt(LocalDateTime.now());
			complaintRepo.save(complaint);
			
			attributes.addFlashAttribute("msg", "Complaint Succesfully Rejected.");
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}
		
		return "redirect:/Investigator/AssignedComplaints";
	}
	
	
	@GetMapping("/RejectedComplaints")
    public String RejectedComplaints(Model model)
    {
		if(session.getAttribute("loggedInUser")==null) {
			return"redirect:/RejectedComplaints";
		}
		
		Users investigator = (Users) session.getAttribute("loggedInUser");
		
		List<Complaint> complaints = complaintRepo.findAllByStatusAndAssignedTo(ComplaintStatus.TERMINATED, investigator);
		model.addAttribute("complaints", complaints);
		
    	return"Investigator/RejectedComplaints";
    }
	
	@GetMapping("/ViewProfile")
    public String ViewProfile()
    {
		if(session.getAttribute("loggedInUser")==null) {
			return"redirect:/ViewProfile";
		}
    	return"Investigator/ViewProfile";
    }
	
	@GetMapping("/UpdateProfilePic")
	public String UpdateProfilePic() {
		return"Investigator/UpdateProfilePic";
	}
	
	
	@PostMapping("/UpdateProfilePic")
	public String UpdateProfilePic(@RequestParam("profilePic") MultipartFile profilePic, RedirectAttributes attributes) {
	    try {
	        Users investigator = (Users) session.getAttribute("loggedInUser");

	        if (profilePic != null && !profilePic.isEmpty()) {
	            // Generate unique filename
	            String storageProfilePicName = System.currentTimeMillis() + "_" +
	                    profilePic.getOriginalFilename().replaceAll("\\s+", "_");

	            // Folder to save profile pics
	            String profileUploadDir = "Public/Profiles/";
	            File profileFolder = new File(profileUploadDir);
	            if (!profileFolder.exists()) profileFolder.mkdirs();

	            // Copy file to folder
	            Path profilePath = Paths.get(profileUploadDir, storageProfilePicName);
	            Files.copy(profilePic.getInputStream(), profilePath, StandardCopyOption.REPLACE_EXISTING);

	            // Update user in DB
	            investigator.setProfilePic(storageProfilePicName);
	            userRepo.save(investigator);

	            // Update session
	            session.setAttribute("loggedInUser", investigator);

	            attributes.addFlashAttribute("msg", "Profile Picture Updated Successfully!");
	        } else {
	            attributes.addFlashAttribute("msg", "Please select a valid image.");
	        }

	    } catch (Exception e) {
	        attributes.addFlashAttribute("msg", "Error: " + e.getMessage());
	    }

	    return "redirect:/Investigator/UpdateProfilePic";
	}
	

	@GetMapping("/ChangePassword")
	public String ShowChangePasswordPage() {
		return"Investigator/ChangePassword";
	}
	
	
	@PostMapping("/ChangePassword")
	public String ChangePassword(HttpServletRequest request, RedirectAttributes attributes) {
		
		String oldPass = request.getParameter("oldPass");
		String newPass = request.getParameter("newPass");
		String confirmPass = request.getParameter("confirmPass");
		
		
		Users investigator = (Users) session.getAttribute("loggedInUser"); 
		
		try {
			if(!newPass.equals(confirmPass)){
				attributes.addFlashAttribute("msg", "New Password and Confirm Paassword must be Same.");
				return"redirect:/Investigator/ChangePassword";
			}
			
			if (newPass.equals(investigator.getPassword())) {
				attributes.addFlashAttribute("msg", "Old Password Can't same New Password");
				return "redirect:/Investigator/ChangePassword";
			}
			
			if(oldPass.equals(investigator.getPassword())) {
				investigator.setPassword(confirmPass);
				
				userRepo.save(investigator);
				session.removeAttribute("loggedInUser");
				attributes.addFlashAttribute("msg", "Password Successfully Changed.");
			}
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}
		
		return "redirect:/Investigator/Dashboard";
	}
	
}
