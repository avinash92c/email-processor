package com.venkatesk.emailprocessor.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class MailBoxOrchestrator {
	private Map<String,MailboxHandler> mailBoxes = new HashMap<>();
	private ExecutorService mailboxExecutor = Executors.newFixedThreadPool(10); //SPECIFY NO OF THREADS. ONE FOR EACH MAILBOX
	private ExecutorService mailProcessorExecutor = Executors.newFixedThreadPool(10); //SPECIFY NO OF THREADS. ONE FOR EACH BATCH OF MAILS
	private ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<String>(50);
	
	@PostConstruct
	private void init() {
		//TODO READ FROM DB MAILBOX CONFIGURATIONS
		
		//TODO CREATE MAILBOX HANDLERS
		MailboxHandler handler = new MailboxHandler(null);
		mailboxExecutor.submit(handler);
		
		//TODO SUBMIT TO EXECUTOR FOR STARTING LISTENING
		MailProcessor processor = new MailProcessor(messages);
		mailProcessorExecutor.submit(processor);
	}
}
