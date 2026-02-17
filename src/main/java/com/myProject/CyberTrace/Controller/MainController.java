package com.myProject.CyberTrace.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;              
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.myProject.CyberTrace.Model.Notification;
import com.myProject.CyberTrace.Model.Notification.NotificationStatus;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myProject.CyberTrace.API.SendEmail;
import com.myProject.CyberTrace.DTO.ComplaintDto;
import com.myProject.CyberTrace.DTO.EnquiryDto;
import com.myProject.CyberTrace.DTO.LoginDto;
import com.myProject.CyberTrace.Model.Complaint;
import com.myProject.CyberTrace.Model.Complaint.ComplaintStatus;
import com.myProject.CyberTrace.Model.Enquiry;
import com.myProject.CyberTrace.Model.Users;
import com.myProject.CyberTrace.Model.Users.UserRole;
import com.myProject.CyberTrace.Repository.ComplaintRepository;
import com.myProject.CyberTrace.Repository.EnquiryRepository;
import com.myProject.CyberTrace.Repository.NotificationRepository;
import com.myProject.CyberTrace.Repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EnquiryRepository enquiryRepo;

    @Autowired
    private ComplaintRepository complaintRepo;

    @Autowired
    private SendEmail sendEmail;
    
    @Autowired
    private NotificationRepository notificationRepo;
    
    @GetMapping("/")
    public String showIndex(Model model) {
        List<Notification> notifications = notificationRepo.findAllByStatus(NotificationStatus.RUNNING);
        model.addAttribute("notifications", notifications);
        return "index";
    }
   
    
    @GetMapping("/AboutUs")
    public String ShowAboutUsPage() {
        return "AboutUs";
    }

    @GetMapping("/Services")
    public String ShowServicesPage() {
        return "Services";
    }

    @GetMapping("/Login")
    public String ShowLoginPage(Model model) {
        model.addAttribute("dto", new LoginDto());
        return "Login";
    }
    
    
    

    @PostMapping("/Login")
    public String Login(@ModelAttribute("dto") LoginDto dto, 
                         RedirectAttributes attributes, HttpSession session) {

        try {
            if (!userRepo.existsByEmail(dto.getEmail())) {
                attributes.addFlashAttribute("msg", "User Not Found!");
                return "redirect:/Login";
            }

            Users user = userRepo.findByEmail(dto.getEmail());

            if (user.getPassword().equals(dto.getPassword())) {

                if (user.getRole().equals(UserRole.ADMIN)) {
                    session.setAttribute("loggedInAdmin", user);
                    return "redirect:/Admin/Dashboard";

                } else {
                    session.setAttribute("loggedInUser", user);
                    return "redirect:/Investigator/Dashboard";
                }

            } else {
                attributes.addFlashAttribute("msg", "Invalid Email or Password!");
            }

        } catch (Exception e) {
            attributes.addFlashAttribute("msg", e.getMessage());
        }

        return "redirect:/Login";
    }

    @GetMapping("/ContactUs")
    public String ShowContactUsPage(Model model) {
        model.addAttribute("dto", new EnquiryDto());
        return "ContactUs";
    }

    @PostMapping("/ContactUs")
    public String SubmitQuery(@ModelAttribute("dto") EnquiryDto dto, RedirectAttributes attributes) {

        Enquiry enquiry = new Enquiry();
        enquiry.setName(dto.getName());
        enquiry.setAddress(dto.getAddress());
        enquiry.setContactNo(dto.getContactNo());
        enquiry.setEmail(dto.getEmail());
        enquiry.setGender(dto.getGender());
        enquiry.setSubject(dto.getSubject());
        enquiry.setMessage(dto.getMessage());
        enquiry.setEnquiryDate(LocalDateTime.now());

        enquiryRepo.save(enquiry);

        attributes.addFlashAttribute("msg", "Query Successfully Submitted");
        return "redirect:/ContactUs";
    }

    @GetMapping("/ReportScam")
    public String ShowReportScan(Model model) {
        model.addAttribute("dto", new ComplaintDto());
        return "ReportScam";
    }

    @PostMapping("/ReportScam")
    public String ReportScam(@ModelAttribute("dto") ComplaintDto dto,
                             @RequestParam("evidenceImages") MultipartFile[] evidenceImages,
                             RedirectAttributes attributes) {

    	if (evidenceImages.length == 0 || evidenceImages[0].isEmpty()) {
    	    attributes.addFlashAttribute("msg", "You have to upload at least 1 image.");
    	    return "redirect:/ReportScam";
    	}


        if (evidenceImages.length > 5) {
            attributes.addFlashAttribute("msg", "You can Only upload maximum 5 images!");
            return "redirect:/ReportScam";
        }

        try {
            String uploadDir = "public/uploads";
            File folder = new File(uploadDir);

            if (!folder.exists()) {
                folder.mkdirs(); 
            }

            List<String> fileNames = new ArrayList<>();

            for (MultipartFile file : evidenceImages) {
                String storageFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                Path uploadPath = Paths.get(uploadDir).resolve(storageFileName);

                Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

                fileNames.add(storageFileName);
            }

            Complaint complaint = new Complaint();
            
            complaint.setComplaintId("CT-" + System.currentTimeMillis());
            complaint.setName(dto.getName());
            complaint.setContactNo(dto.getContactNo());
            complaint.setWhatsappNo(dto.getWhatsappNo());
            complaint.setAddress(dto.getAddress());
            complaint.setLostAmount(dto.getLostAmount());
            complaint.setPlatform(dto.getPlatform());
            complaint.setScammer(dto.getScammer());
            complaint.setTitle(dto.getTitle());
            complaint.setDescription(dto.getDescription());
            complaint.setTypeOfScam(dto.getTypeOfScam());
            complaint.setScamDateTime(null);
            complaint.setEmail(dto.getEmail());
            complaint.setRegDateTime(LocalDateTime.now());
            complaint.setStatus(ComplaintStatus.PENDING);
            complaint.setEvidence(fileNames);

            complaintRepo.save(complaint);
            
            // Email Sender 
            
            sendEmail.SendComplaintSuccessMail(complaint);
            attributes.addFlashAttribute("msg", "Your complaint is successfully registered!");

        } catch (Exception e) {
            attributes.addFlashAttribute("msg", e.getMessage());
        }

        return "redirect:/ReportScam";
    }

    
    @GetMapping("/TrackComplaint")
    public String ShowTrackComplaint()
    {
    	return "TrackComplaint";
    }
    
    @PostMapping("/TrackComplaint")
    public String TrackComplaint(@RequestParam("cid") String cid, Model model, RedirectAttributes attributes)
    {
    	if(!complaintRepo.existsBycomplaintId(cid)) {
    		attributes.addFlashAttribute("msg", "Invalid Compalaint Id.");
    		return "redirect:/TrackComplaint";    		
    	}
    	Complaint complaint = complaintRepo.findByComplaintId(cid);
    	
    	model.addAttribute("complaint", complaint);
    	return "TrackComplaint";
    }
    
    @GetMapping("/CyberInvestigator")
    public String ShowCyberInvestigatorPage() {
        return "CyberInvestigator";
    }
}
