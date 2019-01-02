package com.ncu.processors;
import com.ncu.validators.*;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.*;
import java.util.Properties;
import java.net.Socket;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class FileClient{

	String constantconfig = System.getProperty("user.dir")+ File.separator + "configs/constants/constants.properties";  
	String log4jConfigFile = System.getProperty("user.dir")+ File.separator + "configs/logger/logger.properties";
	public final static String CLIENT_DIRECTORY = System.getProperty("user.dir") + File.separator + "Storage/client/download/";
	public final static String FILE_TO_SEND = System.getProperty("user.dir") + File.separator + "Storage/client/upload/";
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	BufferedReader br=null;
	Socket sock = null;
	FileInputStream fis = null;
	BufferedInputStream bis = null;
	OutputStream os = null;
	DataInputStream dis=null;
	DataOutputStream dos=null;
	Logger logger = Logger.getLogger(FileClient.class);


	public void recieveFile() throws IOException{
		PropertyConfigurator.configure(log4jConfigFile);
		logger.info("Enter File Name :");
		this.br = new BufferedReader(new InputStreamReader(System.in));
		String fileName=this.br.readLine();
		this.dos.writeUTF(fileName);
		this.dis=new DataInputStream(this.sock.getInputStream());
		String msgFromServer = this.dis.readUTF();
		if(msgFromServer.compareTo("File Not Found")==0)
		{
			logger.info("File not found on Server ...");
			return;
		}
		else if(msgFromServer.compareTo("READY")==0) {
			try{
				String filepath = CLIENT_DIRECTORY + fileName;	
				File file=new File(filepath);
				if(file.exists())
				{
					String Option;
					logger.info("File Already Exists. Want to OverWrite (Y/N) ?");
					Option=br.readLine();            
					if( Option.equalsIgnoreCase("N"))    
						{ this.dos.writeUTF("Cancel");
					return;    
				}       
			}
			this.dos.writeUTF("Overwrite");
			FileOutputStream fos = new FileOutputStream(filepath);
			FileInputStream configObj = new FileInputStream(constantconfig);
			Properties propSetObject = new Properties();
			propSetObject.load(configObj);
			Integer FILE_SIZE = Integer.parseInt(propSetObject.getProperty("FILE_SIZE"));
			byte[] buffer = new byte[FILE_SIZE];
			int filesize = FILE_SIZE; 
			int read = 0;
			int totalRead = 0;
			int remaining = this.dis.available();

			while((read = this.dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
				totalRead += read;
				remaining -= read;
				System.out.print("read " + totalRead + " bytes." + remaining);
				fos.write(buffer, 0, read);
			}
			fos.close();
		// displayMenu();
		}catch(Exception e){}
	}
}

public void sendFile(){
	PropertyConfigurator.configure(log4jConfigFile);
	try{
		logger.info("Enter File Name :");
		this.br = new BufferedReader(new InputStreamReader(System.in));
		String fileName=this.br.readLine();
		String filepath = FILE_TO_SEND + fileName;
		File myFile = new File (filepath);
		if(!myFile.exists())
		{
			System.out.println("File not Exists...");
			this.dos.writeUTF("File not found");
			return;
		}
		this.dos.writeUTF(fileName);
		byte [] mybytearray  = new byte [(int)myFile.length()];
		// to read byte oriented data from file
		fis = new FileInputStream(myFile);
		bis = new BufferedInputStream(fis);
// It read the bytes from the specified byte-input stream
		bis.read(mybytearray,0,mybytearray.length);
		os = this.sock.getOutputStream();
		logger.info("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
		os.write(mybytearray,0,mybytearray.length);
		System.out.println("Done.");

	}catch(Exception e){System.out.print (e);}

}

public void displayMenu() throws Exception{ 

	while(true){  

		if(this.sock==null){
			FileInputStream configObj = new FileInputStream(constantconfig);
			Properties propSetObject = new Properties();
			propSetObject.load(configObj);
			String server = propSetObject.getProperty("SERVER");
			Integer port = Integer.parseInt(propSetObject.getProperty("SOCKET_PORT"));
			this.sock = new Socket(server, port);
			PropertyConfigurator.configure(log4jConfigFile);
			logger.info("Connection Started : " + this.sock);
		}
		this.dos = new DataOutputStream(this.sock.getOutputStream());
		System.out.println("[ MENU ]");
		System.out.println("1. Send File");
		System.out.println("2. Receive File");
		System.out.println("3. Exit");
		System.out.print("\nEnter Choice :");
		try{
			int choice=0;
			br =  new BufferedReader(new InputStreamReader(System.in)); 
			choice=Integer.parseInt(br.readLine());
			ClientValidator cv = new ClientValidator();
			if(cv.choiceValidator(choice)){

			if(choice==1)
			{ 
				this.dos.writeUTF("SEND");
				this.sendFile();
			}
			else if(choice==2)
			{
				this.dos.writeUTF("GET");
				this.recieveFile();
			}
			// else if(choice==3)
			// {
		 //   this.sock.close();		
			// }

		  }else{
      continue;
			}
		}
		catch(Exception e){System.out.print(e);}
	}

}

}