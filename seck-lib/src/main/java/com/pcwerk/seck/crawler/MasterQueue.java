package com.pcwerk.seck.crawler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MasterQueue{
	public int depth;
	public int tc;
	public String masterFile;
	
	public MasterQueue(int depth, int tc, String masterFile){
		this.depth = depth;
		this.tc = tc;
		this.masterFile = masterFile;
	}
	
	 public void runCrawl(){
		  ExecutorService ex = Executors.newFixedThreadPool(tc);
		  for(int i = 0; i < tc; i++){
			  ex.execute(new SlaveCrawler(i, depth, tc, masterFile));
			  try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		  }
		  ex.shutdownNow();
	  }

}
