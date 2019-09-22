import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import javax.naming.ldap.*;
import javax.net.*;
import java.io.*;


public class MyTLSFileClient{
	public static void main(String[] args){

		try{
			
			//checks the number of arguments are correct
			if(args.length != 3){
				System.out.println("Usage: hostname, port, file");
				System.exit(0);	
			}
			
			//set the command line arguments to variables
			String host = args[0];
			int port = Integer.parseInt(args[1]);
			String file = args[2];

			//creates a SSL socket to connect with the server
			SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
			
			//sets the parameters
			SSLParameters params = new SSLParameters();
			params.setEndpointIdentificationAlgorithm("HTTPS");
			socket.setSSLParameters(params);
			//start a handshake with the server
			socket.startHandshake();
			
			//creates a new file to write to - the original file name with a _ added to the start
			File writeFile = new File("_" + file);
			//gets the outputstream of the socket
			OutputStream out = socket.getOutputStream();
			//sets the print writer to write to the socket's output
			PrintWriter pwrite = new PrintWriter(out, true);
			//send the name of the file to the server
			pwrite.println(file);
			
			//get the input stream of the socket - to read the file from the server
			InputStream in = socket.getInputStream();
			//creates a buffered reader which reads the lines of the file from the server
			BufferedReader socketRead = new BufferedReader(new InputStreamReader(in));
			//creates a print writer to write to the file
			PrintWriter fileWrite = new PrintWriter(writeFile);
			
			String str;
			//while there are still lines of the file to be written
			while((str = socketRead.readLine()) != null){
				//write the line to the file
				fileWrite.println(str);
			}
			//flush the writer
			fileWrite.flush();
			//close the writers, and socket reader
			fileWrite.close();
			pwrite.close();
			socketRead.close();

			//get the session and find the certificate
			SSLSession sesh = socket.getSession();
			X509Certificate cert = (X509Certificate)sesh.getPeerCertificates()[0];
			System.out.println(getCommonName(cert));

			//close the socket
			socket.close();
			//exit the program
			System.exit(0);
			
		}
		catch(Exception e){
			System.err.println(e);
		}
	}

	public static String getCommonName(X509Certificate cert){
		String cn = null;
		try{
			String name = cert.getSubjectX500Principal().getName();
			LdapName ln = new LdapName(name);
			for(Rdn rdn : ln.getRdns()){
				if("CN".equalsIgnoreCase(rdn.getType()))
					cn = rdn.getValue().toString();
			}
			
		}
		catch(Exception e){
			System.err.println(e);
		}
		return cn;
	}

}
		
