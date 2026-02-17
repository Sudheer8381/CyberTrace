package com.myProject.CyberTrace.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.myProject.CyberTrace.Model.Complaint;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class SendEmail {

	@Autowired
	private JavaMailSender mailSender;

	public void SendComplaintSuccessMail(Complaint complaint) {
		String subject = "Complaint Submission Successfully - Check Your Status";

		String userName = complaint.getName();
		String complaintId = complaint.getComplaintId();
		String getWhatsappNo = complaint.getWhatsappNo();
		String trackLink = "#";

		/*
		 * String message = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n" + "<head>\n" +
		 * "<meta charset=\"UTF-8\">\n" +
		 * "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
		 * +
		 * "<title>Complaint Registration Successful <br> Hello ! This Email from CyberTrace</title>\n"
		 * + "<style>\n" +
		 * "    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f6f8; margin: 0; padding: 0; }\n"
		 * +
		 * "    .container { width: 100%; max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.5); overflow: hidden; border:1px solid grey; }\n"
		 * +
		 * "    .header { background-color: #4CAF50; color: #ffffff; text-align: center; padding: 20px; }\n"
		 * + "    .header h1 { margin: 0; font-size: 24px; }\n" +
		 * "    .content { padding: 30px; color: #333333; line-height: 1.6; }\n" +
		 * "    .complaint-card { background-color: #f1f8e9; border-left: 6px solid #4CAF50; padding: 15px 20px; margin: 20px 0; border-radius: 5px; display: flex; align-items: center; gap: 15px; }\n"
		 * + "    .complaint-card img { width: 40px; height: 40px; }\n" +
		 * "    .button { display: inline-block; padding: 12px 20px; background-color: #4CAF50; color: #ffffff; text-decoration: none; border-radius: 5px; margin-top: 20px; font-weight: bold; }\n"
		 * +
		 * "    .footer { text-align: center; color: #888888; font-size: 12px; padding: 15px; }\n"
		 * + "</style>\n" + "</head>\n" + "<body>\n" + "<div class=\"container\">\n" +
		 * "    <div class=\"header\">\n" +
		 * "        <h1>Complaint Registration Successful</h1>\n" + "    </div>\n" +
		 * "    <div class=\"content\">\n" + "        <p>Dear <strong>" + userName +
		 * "</strong>,</p>\n" +
		 * "        <p>Your complaint has been successfully registered in our system. Please find your complaint details below:</p>\n"
		 * + "        <div class=\"complaint-card\">\n" +
		 * "            <img src=\"https://img.icons8.com/color/48/000000/ticket.png\" alt=\"Complaint ID\">\n"
		 * + "            <div>\n" +
		 * "                <strong>WhatssApp Number:</strong> <span>" + getWhatsappNo +
		 * "</span><br>\n" + "                <strong>Complaint ID:</strong> <span>" +
		 * complaintId + "</span><br>\n" +
		 * "                You can check the status or progress of your complaint on Official Website of CyberTrace.\n"
		 * + "            </div>\n" + "        </div>\n" +
		 * "        <p>Click the button below to track your complaint:</p>\n" +
		 * "        <a href=\"" + trackLink +
		 * "\" class=\"button\">Track Complaint</a>\n" +
		 * "        <p>Thank you for reaching out to us. We will resolve your complaint as soon as possible.</p>\n"
		 * + "        <p>Regards,<br>Customer Support Team</p>\n" + "    </div>\n" +
		 * "    <div class=\"footer\">\n" +
		 * "        &copy; 2025 Your Company Name. All Rights Reserved.\n" +
		 * "    </div>\n" + "</div></body></html>";
		 */

		String message = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n" + "<head>\n" + "<meta charset=\"UTF-8\">\n"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
				+ "<title>Complaint Registration Successful <br> Hello ! This Email from CyberTrace</title>\n"
				+ "<style>\n"
				+ "    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #FF0000; margin: 0; padding: 0; }\n"
				+ "    .container { width: 100%; max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.5); overflow: hidden; border:1px solid grey; }\n"
				+ "    .header { background-color: #4CAF50; color: #ffffff; text-align: center; padding: 20px; }\n"
				+ "    .header h1 { margin: 0; font-size: 24px; }\n"
				+ "    .content { padding: 30px; color: #333333; line-height: 1.6; }\n"
				+ "    .complaint-card { background-color: #f1f8e9; border-left: 6px solid #4CAF50; padding: 15px 20px; margin: 20px 0; border-radius: 5px; display: flex; align-items: center; gap: 15px; }\n"
				+ "    .complaint-card img { width: 40px; height: 40px; }\n"
				+ "    .button { display: inline-block; padding: 12px 20px; background-color: #4CAF50; color: #ffffff; text-decoration: none; border-radius: 5px; margin-top: 20px; font-weight: bold; }\n"
				+ "    .footer { text-align: center; color: #888888; font-size: 12px; padding: 15px; }\n" + "</style>\n"
				
				+ "    </head>\n" + "<body>\n" + "<div class=\"container\">\n" + "    <div class=\"header\">\n"
				+ "        <h1>Complaint Registration Successful</h1>\n" + "    </div>\n"
				+ "    <div class=\"content\">\n" + "        <p>Dear <strong>" + userName + "</strong>,</p>\n"
				+ "        <p>This email is sent by [CyberTrace / Cyber Cell ] as part of\r\n"
				+ "		  an authorized cybersecurity review inquery.  Our cyber monitoring systems have identified a potential digital security\r\n"
				+ "		  concern related to your email or online account activity. No immediate action is required at this stage. Inform you of the detected\r\n"
				+ "		  activity</p>\n"
				+ "       <p>Authorized By: CyberTrace Lucknow cybertraceofficial0001@gmail.com</p>\n"
				+ "       <h5>Contact No. </h5> <p> 43784****8 / www.cybertrace.com</p>\n"
				+ "       <p>Regards, Cybersecurity / CyberTrace & Incident Response Team</p>\n"
				+ "        <div class=\"complaint-card\">\n"
				+ "            <img src=\"https://img.icons8.com/color/48/000000/ticket.png\" alt=\"Complaint ID\">\n"
				+ "            <div>\n" + "                <strong>WhatssApp Number:</strong> <span>" + getWhatsappNo
				+ "</span><br>\n" + "                <strong>Complaint ID:</strong> <span>" + complaintId
				+ "</span><br>\n"
				+ "                You can check the status or progress of your complaint on Official Website of CyberTrace.\n"
				+ "            </div>\n" + "        </div>\n"
				+ "        <p>Click the button below to track your complaint:</p>\n" + "        <a href=\"" + trackLink
				+ "\" class=\"button\">Track Complaint</a>\n"
				+ "        <p>Thank you for reaching out to us. We will resolve your complaint as soon as possible.</p>\n"
				+ "        <p>Regards,<br>Customer Support Team</p>\n" + "    </div>\n" + "    <div class=\"footer\">\n"
				+ "        &copy; 2025 Your Company Name. All Rights Reserved.\n" + "    </div>\n"
				+ "</div></body></html>";

		/*
		 * 
		 * Dear [Recipient Name],
		 * 
		 * This email is sent by [Organization / Cyber Cell / Company Name] as part of
		 * an authorized cybersecurity review.
		 * 
		 * Our cyber monitoring systems have identified a potential digital security
		 * concern related to your email or online account activity.
		 * 
		 * No immediate action is required at this stage. Inform you of the detected
		 * activity
		 * 
		 * Provide guidance on cybersecurity best practices
		 * 
		 * Offer official support if you believe your account may be affected
		 * 
		 * Do not click on unknown links or share personal data If you wish to verify
		 * this communication, please contact us through our official website or
		 * helpline listed below.
		 * 
		 * Authorized By: [Name & Designation] [Organization Name] [Official Email
		 * Domain] [Contact Number / Website]
		 * 
		 * Regards, Cybersecurity & Incident Response Team
		 */

		// helper.setFrom(null);
		try {

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

			helper.setTo(complaint.getEmail());
			helper.setSubject(subject);
			helper.setText(message, true);

			mailSender.send(mimeMessage);
			System.err.println("Mail Send to : " + complaint.getEmail());

		} catch (MessagingException e) {
			e.printStackTrace();
			System.err.println("Error :" + e.getMessage());
		}
	}
}
