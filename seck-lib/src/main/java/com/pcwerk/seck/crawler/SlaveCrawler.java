package com.pcwerk.seck.crawler;

import java.util.LinkedList;
import java.util.Queue;

public class SlaveCrawler extends Crawler{
	
	public int id;
	public int depth;
	public int tc;
	public String masterFile;
	public Queue<String> queueLocal;
	
	public SlaveCrawler(int id, int depth, int tc, String masterFile){
		this.id = id;
		this.depth = depth;
		this.tc = tc;
		this.masterFile = masterFile;
	}

	public void run() {
		
		boolean nextIter = false;
		int currDepth = 0;
		boolean crawled = false;
		String root = "";
		queueLocal = new LinkedList<String>();
		
		synchronized (this){
			queueLocal.addAll(FileManager.getQueue());
		}
		
		while (currDepth != depth){
			if(nextIter){
				synchronized (this) {
					queueLocal.addAll(FileManager.getQueue());
				}
				nextIter = false;
				}
				if(queueLocal.isEmpty()){
					crawled = true;
					nextIter = true;
					currDepth++;
				}
				else{
					root = queueLocal.poll();
					synchronized (this) {
					crawled = FileManager.isCrawled(root, masterFile);
					}
				}
			
			if(!crawled){ 
				synchronized (this) {
				System.out.println(id + " " + root);
				FileManager.saveHash(root.hashCode());
				FileManager.populate(root, tc);
				}

			}
			else{
				System.out.println(id + " " + root + " has been crawled already! Skipping...");
			}
		}
	}

}
