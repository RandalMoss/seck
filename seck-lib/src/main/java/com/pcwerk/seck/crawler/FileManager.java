package com.pcwerk.seck.crawler;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import org.apache.tika.sax.Link;

import com.pcwerk.seck.extractor.Extractor;
import com.pcwerk.seck.extractor.ExtractorFactory;
import com.pcwerk.seck.store.WebDocument;

public class FileManager {
	public static File directory;
	public static File masterFile;
	public static Queue<Queue<String>> queueMaster = new LinkedList<Queue<String>>();
	public static File htmlFile;

	public static ArrayList<String> readFile(String fileName) 
	throws IOException {
		ArrayList<String> arrayOfStrings = new ArrayList<String>();
		try {		

			File fin = new File(fileName);

			Scanner scanner = new Scanner(fin);
			
			while (scanner.hasNext()) {
				arrayOfStrings.add(scanner.nextLine());
			}
			
			scanner.close();
			
		} catch (Exception e) {
			String msg = "File read error: filename = " + fileName;
			throw new IOException(msg);
		}

		return arrayOfStrings;
	}
	
	public static ArrayList<Integer> readMasterFile(String fileName) {
		ArrayList<Integer> hashes = new ArrayList<Integer>();
		try {		
			File fin = new File(fileName);

			Scanner scanner = new Scanner(fin);
			
			while (scanner.hasNext()) {
				hashes.add(Integer.parseInt(scanner.next()));
			}
			
			scanner.close();
			
		} catch (Exception e) {
			System.out.println(fileName + " does not exist.");
		}

		return hashes;
	}
	
	public static void writeFile(String fileName, String[] array) {
		File fout = new File(fileName);

		try {
			String websiteName = array[array.length - 1];
			
			PrintWriter out = new PrintWriter(fout);
			out.println(websiteName);
			
			for(int i = 0; i < array.length -1; i++){
				out.println("***" + array[i]);
			}
			
//			for (String line : array) {
//				out.println(line);
//			}
//			
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeFile(String fileName, int hashCode) {
		boolean alreadyThere = false;
		ArrayList<String> list = new ArrayList<String>();
		if (list.contains(hashCode + "")) alreadyThere = true;
		try {
			if(!alreadyThere){
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(masterFile, true)));
			    out.println(hashCode);
			    out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String[]	getInfo(){
		return null;
	}

	public static synchronized boolean isCrawled(String fileName, String masterFileLocation){
		
		ArrayList<Integer> hashes = new ArrayList<Integer>();
		hashes = FileManager.readMasterFile(masterFileLocation);
		
		if(hashes.contains(fileName.hashCode())) return true;
		else return false;

	}
	
	public static synchronized void saveHash(int root){
		//System.out.println(masterFile + " this is the masterFile toString");
		String stringFile = masterFile.toString().replace("\\", "/");
		//System.out.println(stringFile + " this is the stringFile");
		FileManager.writeFile(stringFile, root);
	}
	
	public static synchronized void saveData(String fileName, ArrayList<String> linksSet)	{
		//System.out.println(directory.getPath());
		String localName = directory.getPath().concat("/" + fileName.hashCode() + ".txt");
		//System.out.println(localName);

		String[] array = new String[linksSet.size() + 1];
		linksSet.toArray(array).toString();
		array[array.length - 1] = fileName;
		FileManager.writeFile(localName, array);
	}
	
	public static ArrayList<String> extractLinks(String root, File file){
		ExtractorFactory eFactory = new ExtractorFactory();
		Extractor ex = eFactory.getExtractor(file);
		WebDocument webDoc = new WebDocument();
		try {
			webDoc = ex.extract(new URL(root));
			Thread.sleep(20);
		} catch (MalformedURLException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Media/Website interrupted thread.");
		}
		HashSet<String> temp = new HashSet<String>();
		ArrayList<String> temp2 = new ArrayList<String>();
		if(webDoc.isHTML()){
			List<Link> links = webDoc.getLinks();
			for(Link link : links){
				//System.out.println(link.getUri());
				temp.add(link.getUri());
			}
			for (String line : temp){
				temp2.add(line);
			}
			String data = webDoc.getContent();
			temp2.add(data);
		}
		return temp2;
	}
	
	public static ArrayList<String> extractData(String root, File file){
		ExtractorFactory eFactory = new ExtractorFactory();
		Extractor ex = eFactory.getExtractor(file);
		WebDocument webDoc = new WebDocument();
		try {
			webDoc = ex.extract(new URL(root));
		} catch (MalformedURLException e) {

		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<String> data = new ArrayList<String>();
		if(webDoc.isHTML()){
			data.add("******");
			data.add(webDoc.getContent());
		}
		return data;
	}
	
	public static void setDirectory(String directoryIn){
		if(!directoryIn.contains("/")){
			System.out.println("Please add a directory to the \"file\" parameter " +
					"\n Example: file=data/masterFile.txt");
			System.exit(0);
		}
		String path = directoryIn;
		String[] pathArray = path.split("/");
		String newDirectory = new String();
		for(int i = 0; i < pathArray.length - 1; i++){
			newDirectory = newDirectory.concat(pathArray[i] + "/");
		}
		directory = new File(newDirectory);
		masterFile = new File(directoryIn);
		if(!directory.exists())
			directory.mkdirs();
		
		if(!masterFile.exists())
			try {
				//System.out.println(masterFile + " is the masterFile");
				masterFile.createNewFile();
			} catch (IOException e) {

				e.printStackTrace();
			}
	}
	
	public static void printCrawlInfo(String masterFilePath){
		ArrayList<Integer> urlFileNames = readMasterFile(masterFilePath);
		
		System.out.println("The number of files currently crawled is: " + urlFileNames.size());
		System.out.println("The websites that have been crawled are: ");
		
		
		String[] directoryArray = masterFilePath.split("/");
		String directory = new String();
		
		for(int i = 0; i < directoryArray.length - 1; i++){
			directory = directory.concat(directoryArray[i] + "/");
		}
		ArrayList<String> data = new ArrayList<String>();
		for(Integer line : urlFileNames){
			try {
			data = readFile(directory.concat(line + ".txt"));
			if(data.size() > 0)
				System.out.println(data.get(0));
			} catch (IOException e) {
				System.out.println("File" + line + "was an invalid url/could not connect");
			}
		
		}
	}
	
	public static synchronized void updateQueueMaster(ArrayList<String> linksSet, int tc){
		Queue<String> queue = new LinkedList<String>();
		String[] array = new String[linksSet.size()];
		linksSet.toArray(array).toString();

			//System.out.println(set.toArray());
			for(int i = 0; i < array.length; i++){
				queue.add(array[i]);
			}
		//set.addAll(queue);
		int length = linksSet.size();
		
		int mod = length % tc;

		for(int i = 0; i < tc; i++){
			Queue<String> newQueue = new LinkedList<String>();
			for(int j = (i) * (length / tc); j < (i + 1) * (length / tc) ; j++){
				//System.out.println(queue.peek());
				newQueue.add(queue.poll());
				
			}
			if(mod > 0){
				newQueue.add(queue.poll());
				mod--;
			}
			queueMaster.add(newQueue);
			System.out.println("new Queue size = " + newQueue.size());
		}
		//queueMaster.remove();
		System.out.println("Size of master is = " + queueMaster.size());

	}
	
	public static synchronized Queue<String> getQueue(){
		return queueMaster.poll();
	}
	
	public static void removeQueueHead(){
		queueMaster.remove();
	}
	
	public static void populate(String root, int tc){
		boolean invalidURL = false;
		URL website;
		String localName = directory.getPath().concat("/" + root.hashCode() + ".txt");
		try {
			website = new URL(root);
			InputStream inStr = website.openConnection().getInputStream();
		    BufferedInputStream bins = new BufferedInputStream(inStr);
		    FileOutputStream fos = new FileOutputStream(localName);
		    int c;
		    while((c = bins.read()) != -1){
		    	fos.write(c);
		    }
		    fos.close();
		    bins.close();
		    inStr.close();

		} catch (MalformedURLException e) {

		} catch (IOException e) {
			System.out.println("Unable to connect to: " + root);
			invalidURL = true;
			
		} 
		if(!invalidURL){
		    File file = new File(localName);
		    
		    ArrayList<String> linksSet = FileManager.extractLinks(root, file);
			FileManager.saveData(root, linksSet);
		    FileManager.updateQueueMaster(linksSet, tc);
		}
		else{
			ArrayList<String> linksSet = new ArrayList<String>();
			ArrayList<String> empty = new ArrayList<String>();
			FileManager.saveData(root, empty);
			linksSet.add(root);
			FileManager.updateQueueMaster(linksSet, tc);
		}
	}
	
}
