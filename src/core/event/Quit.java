package core.event;

import java.util.regex.Matcher;

/**
 * This is a quit object, it contains all the relevant information for when a user is kicked.
 * 
 * @author Tom Rosier(XeTK)
 */
public class Quit {
	// Variables we need for this operation
	private String user_     = new String();
	private String host_     = new String();
	private String channel_  = new String();
	private String exitType_ = new String();
	private String message_  = new String();

	/**
	 * Default constructor takes in are Regex matcher and turn it into the relevant strings
	 * 
	 * @param m this is the Regex passed into the constructor
	 */
	public Quit(Matcher m) {
		user_        = m.group(1);
		host_        = m.group(2);
		exitType_    = m.group(3);
		
		if (exitType_.equals("PART"))
			channel_ = m.group(4);
		
		message_     = m.group(5);
		
	}

	// Getters

	public String getUser() {
		return user_;
	}

	public String getHost() {
		return host_;
	}

	public String getChannel() {
		return channel_;
	}

	public String getExitType() {
		return exitType_;
	}

	public String getMessage() {
		return message_;
	}
	

}
