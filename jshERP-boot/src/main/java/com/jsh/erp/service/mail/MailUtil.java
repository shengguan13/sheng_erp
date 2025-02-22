package com.jsh.erp.service.mail;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;

public class MailUtil {
    /**
     *
     * @param email     接收者邮箱
     * @param subject   邮件主题
     * @param emailMsg  邮件内容
     * @throws AddressException
     * @throws MessagingException
     */
    public static void sendMail(String email, String subject,String emailMsg)
            throws AddressException, MessagingException, GeneralSecurityException {
        final Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.qq.com");
        //使用SSL加密SMTP通过465端口进行邮件发送
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable","true");
        props.put("mail.smtp.ssl.socketFactory",sf);
        //你自己的邮箱
        props.put("mail.user", "807060446@qq.com");
        //你开启pop3/smtp时的验证码
        props.put("mail.password", "smdixjsvjmypbgab");
        //此时将端口设置为465
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.starttls.enable", "true");
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        // 设置发件人
        String username = props.getProperty("mail.user");
        InternetAddress form = new InternetAddress(username);
        message.setFrom(form);
        InternetAddress to = new InternetAddress(email);
        message.setRecipient(Message.RecipientType.TO, to);
        // 设置邮件标题
        message.setSubject(subject);
        // 设置邮件的内容体
        message.setContent(emailMsg, "text/plain;charset=UTF-8");
        // 发送邮件
        Transport.send(message);
    }
}

