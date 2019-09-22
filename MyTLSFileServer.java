import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.naming.ldap.*;
import javax.net.*;
import java.io.*;


public class MyTLSFileServer{
	public static void main(String[] args){

		try{

		//if the wrong number of arguments is given, display a usage message and exit the program
		if(args.length != 1){
			System.out.println("Usage: port number");
			System.exit(0);
		}
		
		//set a variable port as the command line argument
		int port = Integer.parseInt(args[0]);		

		SSLContext ctx = SSLContext.getInstance("TLS");
		
		KeyStore ks = KeyStore.getInstance("JKS");

		char[] passphrase = "silson".toCharArray();
		
		ks.load(new FileInputStream("server.jks"), passphrase);

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");

		kmf.init(ks, passphrase);

		ctx.init(kmf.getKeyManagers(), null, null);

		ServerSocketFactory ssf = ctx.getServerSocketFactory();

		SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(port);

		String EnabledProtocols[] = {"TLSv1.2", "TLSv1.1"};
		ss.setEnabledProtocols(EnabledProtocols);

		while(true){
			System.out.println("Waiting for connection...");
			SSLSocket s = (SSLSocket)ss.accept();
			System.out.println("Connection made");
			
			//gets the input stream from the socket that has been connected
			InputStream input = s.getInputStream();
			//creates a buffered reader to read in the filename from the client 
			BufferedReader fileRead = new BufferedReader(new InputStreamReader(input));
			//read the name of the requested file			
			String fileName = fileRead.readLine();
			//creates a new buffered reader to read through the requested file
			BufferedReader contentRead = new BufferedReader(new FileReader(fileName));
			
			//gets the output stream of the client
			OutputStream output = s.getOutputStream();
			//creates a print writer which will write to the client's output stream
			PrintWriter pwrite = new PrintWriter(output, true);
			
			String str;
			
			//while there are lines of the file to be read
			while((str = contentRead.readLine()) != null){
				//print the line of the file to the client's output stream
				pwrite.println(str);
			}

			//close the sockets, printwriter, and readers
			s.close();
			ss.close();
			pwrite.close();
			fileRead.close();
			contentRead.close();
			System.exit(0);
						
		}

		}
		catch(Exception e){
			System.out.println(e);
		}
	}
}
			
