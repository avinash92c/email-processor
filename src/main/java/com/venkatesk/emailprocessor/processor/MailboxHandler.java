package com.venkatesk.emailprocessor.processor;

import java.io.IOException;
import java.util.Properties;

import com.sun.mail.imap.IMAPFolder;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.event.MessageCountAdapter;
import jakarta.mail.event.MessageCountEvent;

public class MailboxHandler implements Runnable{
	private EmailConfig emailConfig;
	private Session session;

	public MailboxHandler(EmailConfig emailConfig) {
		super();
		this.emailConfig = emailConfig;
	}

	public void connect() {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imaps.host", emailConfig.getEmailHost());
        props.setProperty("mail.imaps.port", emailConfig.getEmailPort());

        // Create a new session with the properties
        Session session = Session.getInstance(props);
        session.setDebug(true); // Enable debug mode for troubleshooting
        this.session = session;
//        return session;
    }
	
	public void startListening() throws MessagingException, InterruptedException, IOException {
        Store store = session.getStore("imaps");
        store.connect(emailConfig.getEmailUsername(), emailConfig.getEmailPassword());

        IMAPFolder inbox = (IMAPFolder)store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        // Create a new thread to keep the connection alive
        //TODO ADD A UNIQUE NAME TO IDENTIFY MAILBOX
        Thread keepAliveThread = new Thread(new KeepAliveRunnable(inbox), "IdleConnectionKeepAlive");
        keepAliveThread.start();

        inbox.addMessageCountListener(new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent event) {
                // Process the newly added messages
                Message[] messages = event.getMessages();
                for (Message message : messages) {
                    try {
                        // Implement your email processing logic here
                        System.out.println("New email received: " + message.getSubject());
                        
                        //TODO WHEN MESSAGE RECEIVED WRITE TO DISK INTO A PENDING PROCESSING FOLDER OR SOMETHING
                        //MAIL READER THREAD WILL LISTEN FOR NEW FILES ADDED AND PICK UP FOR PROCESSING
                        //MAIL PROCESSOR THREADS WILL PROCESS THE FILES AND MOVE TO BACKUP FOLDER OR SOMETHING
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Start the IDLE Loop
        while (!Thread.interrupted()) {
            try {
                System.out.println("Starting IDLE");
                inbox.idle();
            } catch (MessagingException e) {
                System.out.println("Messaging exception during IDLE");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        // Interrupt and shutdown the keep-alive thread
        if (keepAliveThread.isAlive()) {
            keepAliveThread.interrupt();
        }
    }

	@Override
	public void run() {
		try {
			startListening();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
