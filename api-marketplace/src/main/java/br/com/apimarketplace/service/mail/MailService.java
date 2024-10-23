package br.com.apimarketplace.service.mail;

import br.com.apimarketplace.model.ResetToken;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;


    @Value("${spring.mail.username}")
    private String sender;

    @Value("${reset.end.point}")
    private String endPoint;

    public void sendEmailText(String receiver, ResetToken token) throws MessagingException {
        if (receiver.isEmpty()){
            throw new RuntimeException(String.valueOf(HttpStatus.BAD_REQUEST));
        }
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message,true);

        Map<String,String> emailMessageTextSubject = new HashMap<>();
        emailMessageTextSubject = textResetEmail(receiver,token.getToken());

        try{
            helper.setFrom(sender);
            helper.setTo(receiver);
            helper.setSubject(emailMessageTextSubject.get("subject"));
            helper.setText(emailMessageTextSubject.get("emailText"),true);
            javaMailSender.send(message);
        }catch (Exception e ){
            e.getMessage();
        }
    }

    private Map<String,String> textResetEmail(String recipient, String token){
        Map<String,String> textMap = new HashMap<>();
      String emailText = "<html>"
              +
              "<body style='font-family: Arial, sans-serif;'>"
              +
              "<h1>Recuperação de senha</h1>"
              +
              "<p>Olá, tudo bem?</p>"
              +
              "<p>Você solicitou a redefinição de senha do e-mail cadastrado em nosso sistema. Clique no link abaixo para prosseguir:</p>"
              +
              "<p style='background-color: #24a0ed; padding: 10px; color: white; border-radius: 5px; display: inline-block;'><a href="
              +endPoint
              + recipient + "/"
              + token + "' style='color: white; text-decoration: none;'>Redefinir senha</a></p>"
              +
              "<p>Atenciosamente,</p>"
              +
              "<p>Time Api MarketPlace.</p>"
              +
              "</body>"
              +
              "</html>";
        String subject = "API de redefinição de senha";

        textMap.put("subject",subject);
        textMap.put("emailText",emailText);

        return textMap;

    }

}
