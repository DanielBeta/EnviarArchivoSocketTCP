package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 *
 * @author danielbeta
 */
public class Cliente {
    
    public static void main (String args[]) 
	{
		Socket s = null;

		Scanner sc = new Scanner(System.in);

		try
		{
			int serverPort = 2317;

			System.out.println("CLIENT: connecting to server");

			s = new Socket("127.0.0.1", serverPort);

			System.out.println("CLIENT: extracting I/O streams");

			DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream());

			while(true)
			{
				System.out.print("CLIENT: enter text: ");

				String input = sc.nextLine().trim();

				System.out.println("CLIENT: sending data to server");

				out.write((" " + input).getBytes());

				out.flush();

				if(input.equals("bye"))
				{
					System.out.println("RESPONSE: goodbye!");

					break;
				}

				System.out.println("CLIENT: receiving data from server");

				byte[] bytes = new byte[in.read()];

                                /* Almacena los bytes de la peticion del cliente */
                                in.read(bytes);               

                                String peticion = "";
                                
                                /* Construye la peticion del cliente*/
                                for(byte b : bytes)
                                    peticion += (char)b;
                    

				System.out.println("RESPONSE: " + peticion);
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Use: java TCPHashClient <server address>");
		}
		catch (UnknownHostException e)
		{
			System.out.println("Sock:" + e.getMessage());
		} 
		catch (EOFException e)
		{
			System.out.println("EOF:" + e.getMessage());
		} 
		catch (IOException e)
		{	
			System.out.println("IO:" + e.getMessage());
		} 
		finally 
		{
			if(s != null) 
				try 
				{
					System.out.println("CLIENT: closing socket");

					s.close();
				}
				catch (IOException e)
				{
					/*close failed*/
				}
		}
	}
}
