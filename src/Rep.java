import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import addons.RepList;
import addons.Reputation;

import plugin.PluginTemp;
import program.IRC;
import program.IRCException;
import program.JSON;


public class Rep implements PluginTemp
{
	private RepList repList = new RepList();
	private final String cfgFile = "Rep.json";
	
	@Override
	public void onCreate(String in_str) throws IRCException, IOException 
	{
		System.out.println("\u001B[37mRep Plugin Loaded");
		if (new File(cfgFile).exists())
			repList = (RepList)JSON.loadGSON(cfgFile, RepList.class);
		else
			JSON.saveGSON(cfgFile, repList);
			
	}
	@Override
	public void onTime(String in_str) throws IRCException, IOException {}

	@Override
	public void onMessage(String in_str) throws IRCException, IOException
	{
		Matcher m = 
				Pattern.compile(":(.*)!.*@(.*) PRIVMSG (#.*) :(.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
		if (m.find())
		{
			String user = m.group(1).toLowerCase(), host = m.group(2), channel = m.group(3), message = m.group(4);
			
			if (message.charAt(message.length() - 1 ) == ' ')
				message = message.substring(0, message.length() -1);
			
			IRC irc = IRC.getInstance();
			if (message.matches("(^[a-zA-Z0-9]*)([\\s+-]*)([\\s\\d]*)"))
			{
				Matcher r = Pattern.compile("(^[a-zA-Z0-9]*)([\\s+-]*)([\\s\\d]*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
				if (r.find())
				{
					String item = r.group(1),
							type = r.group(2),
							ammount = r.group(3);
					if (!type.equals(""))
					{
						int iAmmount = 0;
						
						if (type.equals("++"))
						{
							iAmmount = 1;
						}
						else if(type.equals("--"))
						{
							iAmmount = -1;
						}
						else
						{
							type = type.trim();
							ammount = ammount.trim();
							if (type.equals("+"))
								iAmmount = Integer.valueOf(ammount);
							else	
								iAmmount = Integer.valueOf(type + ammount);
						}
						if (iAmmount > 100||iAmmount < -100)
						{
							irc.sendServer("PRIVMSG " + channel + " You cant do that its to much rep...");
						}
						else
						{
							Reputation tRep = repList.getRep(item);
							tRep.modRep(iAmmount);
							irc.sendServer("PRIVMSG " + channel + " " + item + ": Rep = " + tRep.getRep() + "!");
						}
					}
				}
			}
			else if (message.matches("\\.rep [A-Za-z0-9#]+$"))
			{
				String[] t = message.split(" ");
				if (t.length > 0||t[1] != null)
					irc.sendServer("PRIVMSG " + channel + " " + t[1] + ": Rep = " + repList.getRep(t[1]).getRep() + "!");
 			}
			else if(message.matches("^\\.help") || message.matches("^\\."))
			{
				irc.sendServer("PRIVMSG " + channel + " REP: " +
						".rep *Item* - view the reputation of a item : " +
						"*Item*--/++ - increment or decrement the rep of a desired item / : " +
						"*Item* +/- *Ammount - increment or decrement the rep of a set item by a set amount : "
						);
			}
		}
		JSON.saveGSON(cfgFile, repList);
	}

	@Override
	public void onJoin(String in_str) throws IRCException, IOException {}
	@Override
	public void onQuit(String in_str) throws IRCException, IOException {}
	
}

