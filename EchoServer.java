// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  public ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI)
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient (Object msg, ConnectionToClient client) {
	  String message = (String)msg;
	  char[] chrArray = message.toCharArray();
	  if (chrArray[0] == '#') {
		  String[] splitStr = message.split(" ");
		  if (splitStr.length == 2){
			  switch(splitStr[0]) {
			  	case "#login":
			  		if (client.getInfo("loginID") == null) {
			  			client.setInfo("loginID", splitStr[1]);
			  		}
			  		else {
			  			try {
			  				client.sendToClient("Error, user already logged in");
			  				client.close();
			  			}catch(IOException e){
			  				serverUI.display("Error, couldn't close");
			  			}
			  		}
			  		break;
			  	default:
			  		serverUI.display("Invalid command");
			  }
		  } else {
			  serverUI.display("Invalid command");
		  }
	  }
	  serverUI.display("Message received: " + msg + " from " + client + " with ID of " + client.getInfo("loginID"));
	  this.sendToAllClients(client.getInfo("loginID") + " > " + msg);
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
	  serverUI.display("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
	  serverUI.display("Server has stopped listening for connections.");
  }
  
  //Class methods ***************************************************
  
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  /*
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    ServerConsole c = new ServerConsole(port);
    EchoServer sv = new EchoServer(port, c);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
      sv.serverUI.display(ex.getStackTrace().toString());
    }
  }*/
  
  /**
   * Called whenever a client connects to the server
   * Prints a message announcing the client's connection to the server
   * @param client Client that connected to the server
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  serverUI.display("A client: " + client + " has connected to the server.");
  }
  
  /**
   * Called when a client disconnects from the server
   * Prints a message announcing the client's disconnection to the server
   * @param client Client that disconnected from the server
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  serverUI.display("A client: " + client + " has disconnected to the server.");
  }
  
  /**
   * Called when a client disconnects due to an error
   * Prints a message announcing that the client has disconnected and prints the stack trace of the error
   * @param client Client that disconnected from the server
   */
  synchronized protected void clientException( ConnectionToClient client, Throwable exception) {
	  serverUI.display("A client: " + client + " has disconnected to the server due to the following error:");
	  serverUI.display(exception.getStackTrace().toString());
  }
  
  public void handleMessageFromServerUI(String message){
	  char[] chrArray = message.toCharArray();
	  if (chrArray[0] == '#') {
		  String[] splitStr = message.split(" ");
		  if (splitStr.length == 1) {
			  switch(splitStr[0]) {
			  	case "#quit":
			  		try {
		  				close();
		  			} catch (Exception e){
		  				serverUI.display("Error, couldn't close");
		  			}
			  		System.exit(0);
			  		break;
			  	case "#stop":
		  			stopListening();
		  			break;
		  		case "#close":
		  			try {
		  				close();
		  			} catch (Exception e){
		  				serverUI.display("Error, couldn't close");
		  			}
		  			break;
		  		case "#start":
		  			try{
		  				listen();
		  			} catch (IOException e){
		  				serverUI.display("Error, couldn't listen");
		  			}
		  			break;
		  		case "#getport":
		  			serverUI.display(Integer.toString(getPort()));
		  			break;
		  		default:
		  			serverUI.display("Invalid command");
			  }
		  } else if (splitStr.length == 2){
			  switch(splitStr[1]) {
			  	case "#setport":
			  		if (closed()) {
			  			serverUI.display("Error, already connected");
			  		} else {
			  			try {
			  				setPort(Integer.parseInt(splitStr[1]));
			  			} catch (Exception e){
			  				serverUI.display("Error, port not a string");
			  			}
			  		}
			  		break;
			  	default:
			  		System.out.println("Invalid command");
			  }
		  } else {
			  System.out.println("Invalid command");
		  }
	  } else {
		  System.out.println("SERVER MSG>" + message);
		  this.sendToAllClients("SERVER MSG>" + message);
	  }
  }
  
  public boolean closed() {
	  return (isListening() && getNumberOfClients() == 0);
  }
}
//End of EchoServer class
