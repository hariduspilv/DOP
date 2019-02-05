package ee.hm.dop.service;

import ee.hm.dop.model.CustomerSupport;
import ee.hm.dop.model.UserEmail;
import ee.hm.dop.utils.DateUtils;
import org.apache.commons.configuration2.Configuration;
import org.simplejavamail.MailException;
import org.simplejavamail.email.AttachmentResource;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ee.hm.dop.utils.ConfigurationProperties.*;
import static org.apache.commons.codec.binary.Base64.decodeBase64;

@Service
@Transactional
public class SendMailService {

    @Inject
    private Configuration configuration;

    private static final Logger logger = LoggerFactory.getLogger(SendMailService.class);
    private static final String BREAK = "<br/>";

    public Email composeEmailToUser(CustomerSupport customerSupport) {
        return EmailBuilder.startingBlank()
                .from("e-Koolikott", configuration.getString(EMAIL_NO_REPLY_ADDRESS))
                .to(customerSupport.getName(), customerSupport.getEmail())
                .withSubject("e-Koolikott, küsimuse kinnitus")
                .withHTMLText("<b>Teema:</b> " + customerSupport.getSubject() + BREAK +
                        "<b>Küsimus:</b> " + customerSupport.getMessage())
                .buildEmail();
    }

    public Email composeEmailToSupport(CustomerSupport customerSupport) {

        List<AttachmentResource> collect = customerSupport.getFiles().stream()
                .map(a -> {
                    try {
                        return new AttachmentResource(a.getName(), new ByteArrayDataSource(decodeBase64(a.getContent().substring(a.getContent().indexOf(',')+1)), "image/*"));
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return EmailBuilder.startingBlank()
                .from("e-Koolikott", customerSupport.getEmail())
                .to("HITSA Support", configuration.getString(EMAIL_ADDRESS))
                .withSubject("e-Koolikott: " + customerSupport.getSubject())
                .withHTMLText("<b>Küsimus:</b> " + customerSupport.getMessage() + BREAK +
                        "<b>Küsija kontakt:</b> " + customerSupport.getName() + ", " + customerSupport.getEmail())
                .withAttachments(collect)
                .buildEmail();
    }

    public Email composeEmailToSupportWhenSendFailed(CustomerSupport customerSupport) {
        return EmailBuilder.startingBlank()
                .from("e-Koolikott", customerSupport.getEmail())
                .to("HITSA Support", configuration.getString(EMAIL_ADDRESS))
                .withSubject("Kasutaja ebaõnnestunud pöördumine")
                .withHTMLText("Kasutaja: " + customerSupport.getName() + ", " + customerSupport.getEmail() + BREAK
                        + "On saatnud pöördumise teemaga: " + customerSupport.getSubject() + BREAK
                        + "Sisuga: " + customerSupport.getMessage() + BREAK
                        + "Pöördumine saadeti: " + DateUtils.toStringWithoutMillis(customerSupport.getSentAt()))
                .buildEmail();
    }

    public boolean sendEmail(Email email) {
        try {
            MailerBuilder
                    .withSMTPServer(configuration.getString(EMAIL_HOST), configuration.getInt(EMAIL_PORT), configuration.getString(EMAIL_USERNAME), configuration.getString(EMAIL_PASSWORD))
                    .withTransportStrategy(TransportStrategy.valueOf(configuration.getString(EMAIL_TRANSPORT_STRATEGY)))
                    .buildMailer()
                    .sendMail(email);

            return true;

        } catch (MailException e) {
            logger.info("Failed to send e-mail", e);
            return false;
        }
    }

    public Email sendPinToUser(UserEmail userEmail, UserEmail email) {
        return EmailBuilder.startingBlank()
                .from("e-Koolikott", configuration.getString(EMAIL_NO_REPLY_ADDRESS))
                .to(userEmail.getUser().getFullName(), email.getEmail())
                .withSubject("e-Koolikotis e-posti kinnitamine")
                .withHTMLText("Tere, " + userEmail.getUser().getFullName()+ BREAK +
                        "Aitäh, et oled e-Koolikoti kasutaja!" + BREAK +
                        "Palun trüki allolev kood e-posti aadressi kinnitamise aknasse." + BREAK +
                        BREAK +
                        "<span style=\"font-size: 18px\"><b>" + userEmail.getPin() + "</b></span>" + BREAK + BREAK +
                        "Pane tähele, et kood on personaalne, ära saada seda teistele kasutajatele edasi." + BREAK +
                        "Küsimuste korral kirjuta: e-koolikott@hitsa.ee")
                .buildEmail();
    }

    public Email sendPinToUser(UserEmail userEmail) {
        return EmailBuilder.startingBlank()
                .from("e-Koolikott", configuration.getString(EMAIL_NO_REPLY_ADDRESS))
                .to(userEmail.getUser().getFullName(), userEmail.getEmail())
                .withSubject("e-Koolikotis e-posti kinnitamine")
                .withHTMLText("Tere, " + userEmail.getUser().getFullName()+ BREAK +
                        "Aitäh, et oled e-Koolikoti kasutaja!" + BREAK +
                        "Palun trüki allolev kood e-posti aadressi kinnitamise aknasse." + BREAK +
                        BREAK +
                        "<span style=\"font-size: 18px\"><b>" + userEmail.getPin() + "</b></span>" + BREAK + BREAK +
                        "Pane tähele, et kood on personaalne, ära saada seda teistele kasutajatele edasi." + BREAK +
                        "Küsimuste korral kirjuta: e-koolikott@hitsa.ee")
                .buildEmail();
    }
}