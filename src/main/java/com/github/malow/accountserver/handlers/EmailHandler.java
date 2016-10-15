package com.github.malow.accountserver.handlers;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.github.malow.malowlib.MaloWLogger;

public class EmailHandler
{
  private static Properties props = null;
  private static String emailAddress;
  private static String emailUser;
  private static String emailPassword;

  private EmailHandler()
  {

  }

  public static void init(String username, String password)
  {
    props = System.getProperties();
    props.put("mail.smtp.starttls.enable", true);
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.user", username);
    props.put("mail.smtp.password", password);
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.auth", true);

    emailAddress = username + "@gmail.com";
    emailUser = username;
    emailPassword = password;
  }

  public static boolean sendMail(String to, String subject, String msg)
  {
    Session session = Session.getInstance(props, null);
    MimeMessage message = new MimeMessage(session);
    try
    {
      InternetAddress from = new InternetAddress(emailAddress);
      message.setSubject(subject);
      message.setFrom(from);
      message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

      // Create a multi-part to combine the parts
      Multipart multipart = new MimeMultipart("alternative");

      // Create your text message part
      BodyPart messageBodyPart = new MimeBodyPart();
      messageBodyPart.setText(msg);

      // Add the text part to the multipart
      multipart.addBodyPart(messageBodyPart);

      // Create the html part
      messageBodyPart = new MimeBodyPart();
      String htmlMessage = msg;
      messageBodyPart.setContent(htmlMessage, "text/html");

      // Add html part to multi part
      multipart.addBodyPart(messageBodyPart);

      // Associate multi-part with message
      message.setContent(multipart);

      // Send message
      Transport transport = session.getTransport("smtp");
      transport.connect("smtp.gmail.com", emailUser, emailPassword);
      transport.sendMessage(message, message.getAllRecipients());
      return true;
    }
    catch (Exception e)
    {
      MaloWLogger.error("Exception when trying to send email", e);
      return false;
    }
  }
}