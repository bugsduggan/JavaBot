package plugin.users;

import java.io.File;
import java.io.IOException;

import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.Plugin;
import core.utils.Details;
import core.utils.IRC;
import core.utils.IRCException;
import core.utils.JSON;

public class Users extends Plugin {
	private final String DB_FILE = "Users.json";

	private final String NEW_USER = "Hello %s and welcome! Don't break things!";
	private final String USER_JOINED = "%s Has joined %s times";
	private final String USER_QUIT = "%s Has quit %s times";
	private final String USER_KICKED = "%s Has be kicked %s times";

	private final IRC irc_ = IRC.getInstance();

	private String dbFile_ = new String();

	public void onCreate(Channel inChannel) throws Exception {
		dbFile_ = inChannel.getPath() + DB_FILE;
		if (new File(dbFile_).exists())
			UserList.setInstance((UserList) JSON.load(dbFile_, UserList.class));
		else
			JSON.save(dbFile_, UserList.getInstance());
	}

	public void onMessage(Message inMessage) throws IRCException, IOException {
		if (!inMessage.isPrivMsg()) {
			JSON.save(dbFile_, UserList.getInstance());
			UserList.getInstance().msgSent(inMessage);
		}
	}

	public void onJoin(Join inJoin) throws Exception {
		JSON.save(dbFile_, UserList.getInstance());
		User user = UserList.getInstance().getUser(inJoin.getUser());
		if (user != null) {
			user.incjoins(inJoin.getHost());
			String msg = String.format(USER_JOINED,
					inJoin.getUser(),user.getJoins());
			
			irc_.sendPrivmsg(inJoin.getChannel(), msg);
		} else {
			String botName = Details.getInstance().getNickName();
			if (!botName.equalsIgnoreCase(inJoin.getUser())) {
				String msg = String.format(NEW_USER, inJoin.getUser());
				irc_.sendPrivmsg(inJoin.getChannel(), msg);
			}
		}
	}

	public void onQuit(Quit in_quit) throws Exception {
		JSON.save(dbFile_, UserList.getInstance());

		User userOBJ = UserList.getInstance().getUser(in_quit.getUser());
		if (userOBJ != null) {
			userOBJ.incQuits();
			String msg = String.format(USER_QUIT,
					in_quit.getUser(), userOBJ.getQuits());
			
			irc_.sendPrivmsg(in_quit.getChannel(), msg);		
		}
	}

	public void onKick(Kick in_kick) throws Exception {
		IRC irc = IRC.getInstance();

		User userOBJ = UserList.getInstance().getUser(in_kick.getKicked());
		if (userOBJ != null) {
			userOBJ.incKicks();

			String msg = String.format(USER_KICKED, 
					in_kick.getKicked(), userOBJ.getKicks());
					
			irc.sendPrivmsg(in_kick.getChannel(), msg);
		}
	}

	public String getHelpString() {
		return "USERS: \n"
				+ "\tThis class does not have any commands.";
	}
}
