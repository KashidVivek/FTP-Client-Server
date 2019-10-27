//package Client;
import java.nio.file.Paths;
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class FTPClient2 {
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
 	ObjectInputStream in;          //stream read from the socket
	String message;                //message send to the server
	String MESSAGE;                //capitalized message read from the server
	//File directory = new File("C:\\Users\\Vivek\\Desktop\\Client2");
	String path = Paths.get("").toAbsolutePath().toString();
	File directory = new File(path+"\\Client2Files");
	boolean su =directory.mkdir(); 
	public final static int FILE_SIZE = 6022386;
	public static void main(String args[]) throws Exception
	{
		FTPClient2 client = new FTPClient2();
		client.run();
	}

	public void FTPClient2() {}

	void run() throws Exception
	{
		int PORT_NUMBER = 0;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		
    	while(true) {
    	String command = bufferedReader.readLine();
        String[] space = command.split("\\ ");
    	if(space[0].equals("ftpclient") && space[2].equals("8000") && space[1].equals("127.0.0.1")) {
    		PORT_NUMBER = Integer.parseInt(space[2]);
    		break;
    	}
    	else {
    		System.out.println("Enter Valid IP or PORT!");
    	}
    	}
		try{
			//create a socket to connect to the server
			//BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
	    	
			requestSocket = new Socket("127.0.0.1", 8000);
			System.out.println("Connected to localhost in port 8000");
			//initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			
			
			while(true) {
				System.out.print("Enter Username: ");
				message = bufferedReader.readLine();
				sendMessage(message);       //send username
				MESSAGE = (String) in.readObject();
				System.out.print(MESSAGE);    //enter password
				message = bufferedReader.readLine();
				sendMessage(message);       //send password  
				MESSAGE = (String) in.readObject();   //response from server
				System.out.println(MESSAGE);
				if(MESSAGE.equals("Access Granted")) {
					break;
				}
			}
			

			while(true)
			{
				System.out.print("Enter a command: ");
				message = bufferedReader.readLine();
				sendMessage(message);
				String[] separated = message.split("\\ ");
				
				if(message.equals("dir")) {
					while(true) {
						MESSAGE = (String)in.readObject();
						if(MESSAGE.equals("DONE")) break;
						System.out.println("["+ MESSAGE+ "]");
					}
					
				}
				label:
				if(separated[0].equals("get") && !separated[1].equals(" ")) {
					//FileOutputStream output = new FileOutputStream("C:\\Users\\Vivek\\eclipse-workspace\\CN\\src\\client\\"+separated[1]);
					/*File file = new File("C:\\Users\\Vivek\\eclipse-workspace\\CN\\src\\client\\"+separated[1]);
					if(file.exists()) {
						System.out.println("File already exits. Do you want to overwrite? (Y/N)");
						String ch = bufferedReader.readLine();
						if(ch.equals("N")) break label;
					}
					else {
						System.out.println("File does not exist");
					}*/
					//System.out.println("Here inside client get");
					MESSAGE = (String)in.readObject();
					if(MESSAGE.equals("File Does not exist on Server!")) {
						System.out.println(MESSAGE);
						break label;
					}
					FileOutputStream output = new FileOutputStream(directory+"\\"+separated[1]);
					InputStream is = requestSocket.getInputStream();
					DataInputStream clientData = new DataInputStream(is);
		            long size = clientData.readLong();
		            long real_size=size;
		            byte[] buffer = new byte[1024];
		            int bytesRead = 0;

		            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
		                output.write(buffer, 0, bytesRead);
		                size -= bytesRead;
		            }
		            output.close();
		            System.out.println("File " + separated[1] +" received of size " + real_size +" bytes");
				    }
				
				if(separated[0].equals("upload") && !separated[1].equals(" ")) {
					sendFile(separated[1]);
				
				}
				if(!separated[0].equals("get") && !separated[0].equals("dir") && !separated[0].equals("upload")) {
					MESSAGE = (String)in.readObject();
					System.out.println(MESSAGE);
				}
				
				if((separated[0].equals("upload") && separated[1].equals(" ") )|| (separated[0].equals("upload") && separated[1].equals(" "))) {
					MESSAGE = (String)in.readObject();
					System.out.println(MESSAGE);
				}	
				
			}
			
		}
		catch (ConnectException e) {
    			System.err.println("Connection refused. You need to initiate a server first.");
		} 
		catch ( ClassNotFoundException e ) {
            		System.err.println("Class not found");
        	} 
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	private void getUsernamePwd() {
		// TODO Auto-generated method stub
		
	}

	//send a message to the output stream
	void sendMessage(String msg)
	{
		try{
			//stream write the message
			out.writeObject(msg);
			out.flush();
			//System.out.println("Send message: " + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	void sendFile(String filename) throws Exception {
		File file = new File(directory+"\\"+filename);
		byte[] fileLength = new byte[(int) file.length()];  
		if(!file.exists()) {
			System.out.println("File Does not Exist!!");
			sendMessage("NaN");
			return;
		}
		sendMessage("Uploading....");
        FileInputStream fis = new FileInputStream(file);  
        BufferedInputStream bis = new BufferedInputStream(fis);

        DataInputStream dis = new DataInputStream(bis);     
        dis.readFully(fileLength, 0, fileLength.length);  

        OutputStream os = requestSocket.getOutputStream();  

        //Sending size of file.
        DataOutputStream dos = new DataOutputStream(os);   
        dos.writeLong(fileLength.length);
        dos.write(fileLength, 0, fileLength.length);     
        dos.flush();  
        System.out.println("File " + filename +" sent of size " +fileLength.length+" bytes");
		
  }
}

