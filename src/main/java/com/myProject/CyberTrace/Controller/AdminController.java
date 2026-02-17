package com.myProject.CyberTrace.Controller;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myProject.CyberTrace.DTO.ComplaintDto;
import com.myProject.CyberTrace.DTO.UserDto;
import com.myProject.CyberTrace.Model.Complaint;
import com.myProject.CyberTrace.Model.Complaint.ComplaintStatus;
import com.myProject.CyberTrace.Model.Enquiry;
import com.myProject.CyberTrace.Model.Notification;
import com.myProject.CyberTrace.Model.Notification.NotificationStatus;
import com.myProject.CyberTrace.Model.Users;
import com.myProject.CyberTrace.Model.Users.UserRole;
import com.myProject.CyberTrace.Model.Users.UserStatus;
import com.myProject.CyberTrace.Repository.ComplaintRepository;
import com.myProject.CyberTrace.Repository.EnquiryRepository;
import com.myProject.CyberTrace.Repository.NotificationRepository;
import com.myProject.CyberTrace.Repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Admin")
public class AdminController {

	@Autowired
	private HttpSession session;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private EnquiryRepository enquiryRepo;

	@Autowired
	private ComplaintRepository complaintRepo;

	@Autowired
	private NotificationRepository notificationRepo;
	
	
	@GetMapping("/Dashboard")
	public String ShowDashboard(Model model) {
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}
		
		long totalInvestigators = userRepo.findAllByRole(UserRole.INVESTIGATOR).size();
		long totalComplaints = complaintRepo.count();
		long pendingComplaints = complaintRepo.findAllByStatus(ComplaintStatus.PENDING).size();
		long rejectedComplaints = complaintRepo.findAllByStatus(ComplaintStatus.TERMINATED).size();
		long processingComplaints = complaintRepo.findAllByStatus(ComplaintStatus.PROCCESSING).size();
		long resolvedComplaints = complaintRepo.findAllByStatus(ComplaintStatus.RESOLVED).size();
		long totalEnqueries = enquiryRepo.count();
		
		model.addAttribute("totalInvestigators", totalInvestigators);
		model.addAttribute("totalComplaints", totalComplaints);
		model.addAttribute("pendingComplaints", pendingComplaints);
		model.addAttribute("rejectedComplaints", rejectedComplaints);
		model.addAttribute("processingComplaints", processingComplaints);
		model.addAttribute("resolvedComplaints", resolvedComplaints);
		model.addAttribute("totalEnqueries", totalEnqueries);
		
		List<Enquiry> enqueries = enquiryRepo.findTop3ByOrderByEnquiryDateDesc();
		
		model.addAttribute("enqueries", enqueries);
		
		
		// Orders data for Chart (monthly complaint)
        List<Object[]> stats = complaintRepo.getMonthlyComplaintStats();
        
        
        Map<Integer, Long> monthCountMap = new HashMap<>();
        for (Object[] row : stats) {
            int monthNumber = ((Number) row[0]).intValue();  // 1-12
            long count = ((Number) row[1]).longValue();
            System.err.println("Message : "+count);

            monthCountMap.put(monthNumber, count);
        }

        List<String> complaintMonths = new ArrayList<>();
        List<Long> complaintCounts = new ArrayList<>();

        String[] monthNames = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        for (int i = 1; i <= 12; i++) {
        	complaintMonths.add(monthNames[i - 1]);
        	complaintCounts.add(monthCountMap.getOrDefault(i, 0L)); // agar nahi mila to 0
        }        
        model.addAttribute("complaintMonths", complaintMonths);
        model.addAttribute("complaintCounts", complaintCounts);
        
		return "Admin/Dashboard";
	}

	@GetMapping("/Add-Investigator")
	public String ShowAddInvestigatorPage(Model model) {
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}
		UserDto dto = new UserDto();
		model.addAttribute("dto", dto);
		return "Admin/AddInvestigator";
	}

	@PostMapping("/Add-Investigator")
	public String ShowAddInvestigatorPage(@ModelAttribute("dto") UserDto dto, RedirectAttributes attributes) {
		try {

			String storageProfilePicName = System.currentTimeMillis() + "_"
					+ dto.getProfilePic().getOriginalFilename().replaceAll("\\s+", "_");

			String storageIdProofName = System.currentTimeMillis() + "_"
					+ dto.getGovtIdProof().getOriginalFilename().replaceAll("\\s+", "_");

			String profileUploadDir = "public/Profiles/";
			String idProofUploadDir = "public/IdProof/";

			File profileFolder = new File(profileUploadDir);
			File idProofFolder = new File(idProofUploadDir);

			if (!profileFolder.exists()) {
				profileFolder.mkdirs();
			}

			if (!idProofFolder.exists()) {
				idProofFolder.mkdirs();
			}

			Path profilePath = Paths.get(profileUploadDir, storageProfilePicName);
			Path idProofPath = Paths.get(idProofUploadDir, storageIdProofName);

			Files.copy(dto.getProfilePic().getInputStream(), profilePath, StandardCopyOption.REPLACE_EXISTING);
			Files.copy(dto.getGovtIdProof().getInputStream(), idProofPath, StandardCopyOption.REPLACE_EXISTING);

			// Setting Data Into DataBase

			Users investigator = new Users();

			investigator.setName(dto.getName());
			investigator.setGender(dto.getGender());
			investigator.setContactNo(dto.getContactNo());
			investigator.setEmail(dto.getEmail());
			investigator.setAddress(dto.getAddress());
			investigator.setPassword(dto.getPassword());
			investigator.setProfilePic(storageProfilePicName);
			investigator.setGovtIdProof(idProofUploadDir);
			investigator.setStatus(UserStatus.UNBLOCKED);
			investigator.setRole(UserRole.INVESTIGATOR);
			investigator.setRegDate(LocalDateTime.now());

			userRepo.save(investigator);

			attributes.addFlashAttribute("msg", "Data Successfully Added.");

		} catch (Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}

		return "redirect:/Admin/Add-Investigator";
	}

	@GetMapping("/Manage-Investigator")
	public String ShowManageInvestigatorPage(Model model, RedirectAttributes attributes) {
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}

		List<Users> investigators = userRepo.findAllByRole(UserRole.INVESTIGATOR);
		model.addAttribute("investigators", investigators);
		return "Admin/ManageInvestigator";
	}

	@GetMapping("/UpdateStatus/{id}")
	public String UpdateStatus(@PathVariable long id) {
		Users investigator = userRepo.findById(id).get();

		if (investigator.getStatus().equals(UserStatus.UNBLOCKED)) {
			investigator.setStatus(UserStatus.BLOCKED);
		} else if (investigator.getStatus().equals(UserStatus.BLOCKED)) {
			investigator.setStatus(UserStatus.UNBLOCKED);
		}
		userRepo.save(investigator);
		return "redirect:/Admin/Manage-Investigator";
	}

	@GetMapping("/PendingComplaint")
	public String ShowPendingComplaint(Model model) {
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}

		List<Complaint> complaints = complaintRepo.findAllByStatus(ComplaintStatus.PENDING);
		model.addAttribute("complaints", complaints);

		List<Users> investigators = userRepo.findAllByRole(UserRole.INVESTIGATOR);
		model.addAttribute("investigators", investigators);

		return "Admin/PendingComplaint";
	}

	@PostMapping("/AssignComplaint")
	public String AssignComplaint(@RequestParam("assignTo") long uid, @RequestParam("cid") long cid,
			RedirectAttributes attributes) {
		try {
			Users investigator = userRepo.findById(uid).get();
			Complaint complaint = complaintRepo.findById(cid).get();

			complaint.setAssignedTo(investigator);
			complaint.setStatus(ComplaintStatus.PROCCESSING);
			complaintRepo.save(complaint);
			attributes.addFlashAttribute("msg", "Successfully Assigned");

		} catch (Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());

		}
		return "redirect:/Admin/PendingComplaint";
	}
	
	@PostMapping("/RejectComplaint")
	public String RejectComplaint(@RequestParam("cid") long cid, @RequestParam("message") String message, RedirectAttributes attributes)
	{
		Complaint complaint = complaintRepo.findById(cid).get();
		complaint.setMessage(message);
		complaint.setStatus(ComplaintStatus.TERMINATED);
		attributes.addFlashAttribute("msg", "Complaint Successfully Rejected.");
		complaintRepo.save(complaint);
		
		return"redirect:/Admin/PendingComplaint";
	}
	
	@GetMapping("/ManageComplaint")
	public String ShowManageComplaint(Model model)
	{
		if(session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/Login";
		}
		List<Complaint> complaints = complaintRepo.findAllByStatusOrStatus(ComplaintStatus.PROCCESSING, ComplaintStatus.RESOLVED);
		model.addAttribute("complaints", complaints);
		return "Admin/ManageComplaint";
	}

	
	@GetMapping("/Enquiry")
	public String ShowEnquiry(Model model) {
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}

		List<Enquiry> enquiries = enquiryRepo.findAll();
		model.addAttribute("enquiries", enquiries);

		return "Admin/Enquiry";
	}

	@GetMapping("/ChangePassword")
	public String ShowChangePassword() {
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}
		return "Admin/ChangePassword";
	}

	@PostMapping("/ChangePassword")
	public String ChangePassword(HttpServletRequest request, RedirectAttributes attributes) {
		try {
			String oldPass = request.getParameter("oldPass");
			String newPass = request.getParameter("newPass");
			String confirmPass = request.getParameter("confirmPass");

			Users admin = (Users) session.getAttribute("loggedInAdmin");

			if (!newPass.equals(confirmPass)) {
				attributes.addFlashAttribute("msg", "New Password and Confirm Password must be Same.");
				return "redirect:/Admin/ChangePassword";
			}

			if (newPass.equals(admin.getPassword())) {
				attributes.addFlashAttribute("msg", "Old Password And New Password Can`t be same.");
				return "redirect:/Admin/ChangePassword";
			}

			if (oldPass.equals(admin.getPassword())) {
				admin.setPassword(confirmPass);
				userRepo.save(admin);
				session.removeAttribute("loggedInAdmin");
				attributes.addFlashAttribute("msg", "Password Changed Successfully.");
			} else {
				attributes.addFlashAttribute("msg", "Invalid Old Password.");
			}

		} catch (Exception e) {
			attributes.addAttribute("msg", e.getMessage());
		}
		return "redirect:/Admin/ChangePassword";
	}

	@GetMapping("/Logout")
	public String LogOut(RedirectAttributes attributes) {
		session.removeAttribute("loggedInAdmin");
		attributes.addFlashAttribute("msg", "Logout Successfully.");
		return "redirect:/Login";
	}
	
	
	@GetMapping("/AddNotification")
	public String ShowAddNotification(Model model)
	{
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}
		
		List<Notification> notifications = notificationRepo.findAll();
		model.addAttribute("notifications", notifications);
	      return"Admin/AddNotification";	
	}
	
	
	
	@PostMapping("/AddNotification")
	public String AddNotification(Model model, @RequestParam("message") String message,
	                              RedirectAttributes attributes) {

	    if (session.getAttribute("loggedInAdmin") == null) {
	        return "redirect:/AddNotification";
	    }

	    try {
	        Notification notification = new Notification();
	        notification.setMessage(message);
	        notification.setStatus(Notification.NotificationStatus.RUNNING);
	        notification.setPublishAt(LocalDateTime.now());

	        notificationRepo.save(notification);
	        attributes.addFlashAttribute("msg", "Notification Successfully Published.");
	    } catch (Exception e) {
	        attributes.addFlashAttribute("msg", e.getMessage());
	    }

	    return "redirect:/Admin/AddNotification";
	}

	
	@GetMapping("/EndNotification/{id}")
	public String EndNotification(@PathVariable long id, RedirectAttributes attributes){
		
		try {
			Notification notification = notificationRepo.findById(id).get();
			notification.setStatus(NotificationStatus.ENDED);
			notificationRepo.save(notification);
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}
		
		return "redirect:/Admin/AddNotification";
	}
	
	@GetMapping("/DeleteNotification/{id}")
	public String DeleteNotification(@PathVariable long id){
		
		if(notificationRepo.existsById(id)) {
			notificationRepo.deleteById(id);
		}
		return "redirect:/Admin/AddNotification";
	}
	
	@GetMapping("/UpdateProfilePic")
	public String UpdateProfilePic(){
		return"Admin/UpdateProfilePic";
	}
	
	
	@PostMapping("/UpdateProfilePic")
	public String UpdateProfilePic(@RequestParam("profilePic") MultipartFile profilePic, RedirectAttributes attributes) {
	    try {
	        Users admin = (Users) session.getAttribute("loggedInAdmin");

	        if (profilePic != null && !profilePic.isEmpty()) {
	            // Generate unique filename
	            String storageProfilePicName = System.currentTimeMillis() + "_" +
	                    profilePic.getOriginalFilename().replaceAll("\\s+", "_");

	            // Folder to save profile pics
	            String profileUploadDir = "Public/Profile/";
	            File profileFolder = new File(profileUploadDir);
	            if (!profileFolder.exists()) profileFolder.mkdirs();

	            // Copy file to folder
	            Path profilePath = Paths.get(profileUploadDir, storageProfilePicName);
	            Files.copy(profilePic.getInputStream(), profilePath, StandardCopyOption.REPLACE_EXISTING);

	            // Update user in DB
	            admin.setProfilePic(storageProfilePicName);
	            userRepo.save(admin);

	            // Update session
	            session.setAttribute("loggedInAdmin", admin);

	            attributes.addFlashAttribute("msg", "Profile Picture Updated Successfully!");
	        } else {
	            attributes.addFlashAttribute("msg", "Please select a valid image.");
	        }

	    } catch (Exception e) {
	        attributes.addFlashAttribute("msg", "Error: " + e.getMessage());
	    }
	    return "redirect:/Admin/UpdateProfilePic";
	}
	
	
	@GetMapping("/EditInvestigatorInfo")
	public String EditUserInfo(HttpServletRequest request, RedirectAttributes attributes ){
		
//		String newName = request.getParameter("newName");
//		String gender = request.getParameter("gender");
//		String email = request.getParameter("email");
//		String contactNo = request.getParameter("contactno");
//		String 
		return "Admin/EditInvestigatorInfo";
	}
	
}
