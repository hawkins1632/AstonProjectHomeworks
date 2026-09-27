package org.notification.e2e;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractGreenMailTest {

    protected static final String RECIPIENT = "to@test.local";
    protected static final String MAIL_FROM = "no-reply@localhost";

    static final int SMTP_PORT = 3025;

    static GreenMail greenMail;

    static {
        ServerSetup smtpSetup = new ServerSetup(SMTP_PORT, null, "smtp");
        greenMail = new GreenMail(smtpSetup);
        greenMail.setUser(RECIPIENT, "", "");
        greenMail.start();
        Runtime.getRuntime().addShutdownHook(new Thread(greenMail::stop));
    }

    @BeforeEach
    void purgeMailbox() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
    }
}