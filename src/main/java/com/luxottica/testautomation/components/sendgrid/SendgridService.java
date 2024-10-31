package com.luxottica.testautomation.components.sendgrid;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SendgridService {

    private static final Logger logger = LoggerFactory.getLogger(SendgridService.class);

    @Value("${sendgrid.api}")
    private String sendgridApi;

    @Value("${sendgrid.from.name}")
    private String fromName;
    @Value("${sendgrid.from.email}")
    private String fromEmail;
    @Value("${sendgrid.to.name}")
    private String toName;
    @Value("${sendgrid.to.email}")
    private String toEmail;

    @Value("${sendgrid.subject}")
    private String subject;

    @Value("${sendgrid.templateId}")
    private String templateId;

    @Value("${sendgrid.zipName}")
    private String zipName;

    public void sendMail(File report, File... attachments) {

        logger.info("Sending email with report");

        try {

            List<File> files = new ArrayList<>(Collections.singletonList(report));
            if (attachments != null && attachments.length > 0) {
                files.addAll(List.of(attachments));
            }

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");
            String finalZipName = zipName.replace("{datetime}", sdf.format(date));

            File zipFile = zipFiles(finalZipName, files.toArray(new File[0]));

            byte[] data = null;
            try {
                data = Files.readAllBytes(zipFile.toPath());
            } catch (IOException e) {
                logger.error("Error reading file", e);
            }

            Attachments attachments3 = new Attachments();
            String dataString = Base64.getEncoder().encodeToString(data);
            attachments3.setContent(dataString);
            attachments3.setFilename(finalZipName);
            attachments3.setType("application/zip");
            attachments3.setDisposition("attachment");
            attachments3.setContentId("report");

            SendGrid sg = new SendGrid(sendgridApi);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            Mail mail = craftMail();
            mail.addAttachments(attachments3);

            request.setBody(mail.build());
            Response response = sg.api(request);

            if (!HttpStatus.valueOf(response.getStatusCode()).is2xxSuccessful()) {
                logger.warn("Error sending email: {}", response.getBody());
                return;
            }

            logger.info("Email sent successfully!");
        } catch (IOException e) {
            logger.error("Error sending email", e);
        }
    }

    private File zipFiles(String zipName, File[] files) {

        logger.debug("Zipping {} files..", files.length);

        File zipFile = new File(zipName);

        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File file : files) {
                FileInputStream fis = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOut.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
            }

            logger.debug("Files zipped successfully!");
        } catch (IOException e) {
            logger.error("Error zipping files", e);
        }

        return zipFile;
    }

    private Mail craftMail() {

        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail, toName);

        Mail mail = new Mail();
        mail.setFrom(from);

        Personalization personalization = new Personalization();
        personalization.addDynamicTemplateData("subject", subject);
        personalization.addTo(to);
        mail.addPersonalization(personalization);
        mail.setTemplateId(templateId);

        return mail;
    }

}
