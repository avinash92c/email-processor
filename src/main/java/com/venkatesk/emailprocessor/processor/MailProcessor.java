package com.venkatesk.emailprocessor.processor;

import java.util.concurrent.ArrayBlockingQueue;

public class MailProcessor implements Runnable{
	private ArrayBlockingQueue<String> messages;
	
	public void setMessages(ArrayBlockingQueue<String> messages) {
		this.messages = messages;
	}

	public MailProcessor(ArrayBlockingQueue<String> messages) {
		super();
		this.messages = messages;
	}

//	FILL EMAIL READING AND PROCESSING LOGIC HERE <BUSINESS LOGIC>
	@Override
	public void run() {
		while (true) {
			try {
				while(true) {
					if(messages.peek()!=null) {
						String filePath = messages.poll();
						
						//TODO PERFORM YOUR BUSINESS LOGIC
					}
					Thread.sleep(200);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
	}
	
}
