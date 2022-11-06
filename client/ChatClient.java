// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  String loginID; //Identifier for the client
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    if (loginID == "") { //loginID not defined
    	quit();
    }
    this.loginID = loginID;
    this.clientUI = clientUI;
    openConnection();
    sendToServer("#login " + loginID);
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message){
	  char[] chrArray = message.toCharArray();
	  if (chrArray[0] == '#') {
		  String[] splitStr = message.split(" ");
		  if (splitStr.length == 0) {
			  switch(message) {
			  	case "#quit":
			  		quit();
			  		break;
			  	case "#logoff":
		  			logout();
		  			break;
		  		case "#login":
		  			 try {
		  				 openConnection();
		  			 } catch (IOException e) {
		  				System.out.println("Error, already connected");
		  				e.printStackTrace();
		  			 }
		  			break;
		  		case "#gethost":
		  			System.out.println(getHost());
		  			break;
		  		case "#getport":
		  			System.out.println(getPort());
		  			break;
		  		default:
		  			System.out.println("Invalid command");
			  }
		  } else if (splitStr.length == 1){
			  switch(splitStr[0]) {
			  	case "#sethost":
			  		if (isConnected()) {
			  			System.out.println("Error, already connected");
			  		} else {
			  			setHost(splitStr[1]);
			  		}
			  		break;
			  	case "#setport":
			  		if (isConnected()) {
			  			System.out.println("Error, already connected");
			  		} else {
			  			try {
			  				setPort(Integer.parseInt(splitStr[1]));
			  			} catch (Exception e){
			  				System.out.println("Error, port not a string");
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
		  try {
			  sendToServer(message);
		  }
		  catch(IOException e) {
			  clientUI.display ("Could not send message to server.  Terminating client.");
			  quit();
		  }
	  }
  }
  
  public void logout() {
	  try {
		  closeConnection();
	  } catch(IOException e) {
		  e.printStackTrace();
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit() {
    logout();
    System.exit(0);
  }
  
  /**
   * Is called and prints string message when the connection with the server is closed
   */
  @Override
  protected void connectionClosed() {
	  System.out.println("Connection with the server has closed.");
  }
  
  /**
   * Is called when an error with the server causes disconnection
   * Prints out the StackTrace of the exception related to the disconnection from the server
   * @param exception Error that cause the connection with the server to be disconnected.
   */
  @Override
  protected void connectionException(Exception exception) {
	  System.out.println("Connection with the server has closed due to the following error:");
	  exception.printStackTrace();
  }
}
//End of ChatClient class
