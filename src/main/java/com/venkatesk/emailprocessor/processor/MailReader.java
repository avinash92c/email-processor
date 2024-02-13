package com.venkatesk.emailprocessor.processor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.nio.file.StandardWatchEventKinds;

public class MailReader implements Runnable{

	private String mailFolder;
	private ArrayBlockingQueue<String> messages;
	
	public void setMessages(ArrayBlockingQueue<String> messages) {
		this.messages = messages;
	}

	public MailReader(ArrayBlockingQueue<String> messages) {
		super();
		this.messages = messages;
	}

	//	PICK MAILS FOLDER AND PICK FILES AND PASS TO WORKERS
	@Override
	public void run() {
		try {
			Path myDir = Paths.get(mailFolder);
			WatchService watcher = myDir.getFileSystem().newWatchService();
			myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

			WatchKey watckKey = watcher.take();

			List<WatchEvent<?>> events = watckKey.pollEvents();
			for (WatchEvent<?> event : events) {
			     if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
			         System.out.println("Created: " + event.context().toString());
//			         watckKey.watchable().
			         //TODO FIGURE OUT HOW TO GET ABSOLUTE FILE PATH
			         
			         //TODO PUSH PATH TO messages queue
			     }
			 }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
