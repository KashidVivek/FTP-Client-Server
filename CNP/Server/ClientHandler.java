//package Server;
import java.nio.file.Paths;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class ClientHandler extends Thread  
{ 

    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
    String message;    //message received from the client
	String MESSAGE;    //uppercase message send to the client
	ObjectOutputStream out;  //stream write to the socket
	ObjectInputStream in; 
	File directory;   //stream read from the socket
	/*String path = Paths.get("").toAbsolutePath().toString();
	File directory = new File("/ServerFiles");
	boolean successful=directory.mkdirs();*/
	//File directory = new File("C:\\Users\\Vivek\\Desktop\\Server");
	// Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos)  
    { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
    } 
  
    @Override
	public
    void run() 
	{
		try{
			String path = Paths.get("").toAbsolutePath().toString();
			directory = new File(path+"\\ServerFiles");
			boolean su =directory.mkdir(); 
			//System.out.println(directory);
			//File directoryL = new File("C:\\Users\\Vivek\\Desktop\\Server");
			out = new ObjectOutputStream(s.getOutputStream());
			out.flush();
			new PrintWriter( s.getOutputStream() );
			in = new ObjectInputStream(s.getInputStream());
			int flag = 0;
			while(true) {
				message = (String)in.readObject();
				if(message.equals("vivek")|| message.equals("vivek")) {
					flag = 1;
				}
				MESSAGE = "Enter Password: ";
				sendMessage(MESSAGE);
				message = (String)in.readObject();
				if(message.equals("12345") && flag==1) {
					sendMessage("Access Granted");
					break;
				}
				else {
					sendMessage("Incorrect Username or Password. Try Again !");
				}
			}
		

			try{
				while(true)
				{
					message = (String)in.readObject();
					String[] separated = message.split("\\ ");
					//System.out.println("Receive message: " + message);
					if(message.equals("dir")) {
						System.out.println("ClientHandler Dir");
						sendDir();
					}
					if(separated[0].equals("get") && !separated[1].equals("")) {
						sendFile(separated[1]);
					}
					label: if(separated[0].equals("upload") && !separated[1].equals("")) {
						message = (String)in.readObject();
						if(message.equals("NaN")) {
							System.out.println("File does exist");
							break label;
						}
						FileOutputStream output = new FileOutputStream(directory+"\\"+separated[1]);
						InputStream is = s.getInputStream();
						DataInputStream clientData = new DataInputStream(is);
			            long size = clientData.readLong();
			            long real_size= size;
			            byte[] buffer = new byte[1024];
			            int bytesRead = 0;

			            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
			                output.write(buffer, 0, bytesRead);
			                size -= bytesRead;
			            }
			            System.out.println("File " + separated[1] +" uploaded of size " +real_size);
			            output.close();
					}
					if(!separated[0].equals("get") && !separated[0].equals("dir") && !separated[0].equals("upload"))
					{
						sendMessage("Invalid Command. Try again!!");
					}
					if((separated[0].equals("upload") && separated[1].equals("") )|| (separated[0].equals("upload") && separated[1].equals(""))) {
						sendMessage("Invalid Command. Try again!!");
					}
					
				}
			}
			catch(Exception classnot){
					System.err.println("Data received in unknown format");
				}
		}
		catch(IOException | ClassNotFoundException ioException){
			ioException.printStackTrace();
		}
		finally{
			try{
				in.close();
				out.close();
				s.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}

	void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			//System.out.println("Send message: " + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	void sendDir() throws Exception {
     String[] fileList = directory.list();
     System.out.println("DIR: "+directory);
     for (int i = 0; i < fileList.length; i++) {
        out.writeObject(fileList[i]);
        //System.out.println(fileList[i]);
     }
     out.writeObject("DONE");
     out.flush();
  }
	
	void sendFile(String filename) throws Exception {
		
		File file = new File(directory+"\\"+filename);
		System.out.println(file);
		byte[] fileLength = new byte[(int) file.length()];  
		if(!file.exists()) {
			sendMessage("File Does not exist on Server!");
			return;
		}
		else {
			sendMessage("Server Sending...");
		}
        FileInputStream fis = new FileInputStream(file);  
        BufferedInputStream bis = new BufferedInputStream(fis);
        //message = (String)in.readObject();
        //if(message.equals("NaN")) return;
        System.out.println("Reached here");
        DataInputStream dis = new DataInputStream(bis);     
        dis.readFully(fileLength, 0, fileLength.length);  

        OutputStream os = s.getOutputStream();  

        //Sending size of file.
        DataOutputStream dos = new DataOutputStream(os);   
        dos.writeLong(fileLength.length);
        dos.write(fileLength, 0, fileLength.length);     
        dos.flush();  
        //System.out.println("File " + filename +" sent of size " +fileLength.length);
        
  }
} 