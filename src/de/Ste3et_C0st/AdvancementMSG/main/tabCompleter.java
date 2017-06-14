package de.Ste3et_C0st.AdvancementMSG.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class tabCompleter implements TabCompleter {

	private List<String> str = new ArrayList<String>();
	private List<String> str2 = new ArrayList<String>();
	
	public tabCompleter() {
		str.add("help");
		str.add("list");
		str.add("say");
		str.add("next");
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args) {
		if(cmd!=null&&cmd.getName().equalsIgnoreCase("ad")){
			if(args.length == 1){
				String s = args[0];
				return getTabCompleter(s, this.str);
			}else if(args.length == 2){
				if(args[0].equalsIgnoreCase("next")){
					String s = args[1];
					Iterator<Advancement> adIterator = Bukkit.advancementIterator();
					while (adIterator.hasNext()) {
						Advancement ad = adIterator.next();
						if(ad.getKey().getNamespace().toLowerCase().startsWith("dice")){
							if(ad.getCriteria().size() > 1){
								str2.add(ad.getKey().toString());
							}
						}
					}
					return getTabCompleter(s, this.str2);
				}
			}
		}
		return null;
	}

	private List<String> getTabCompleter(String s, List<String> strL) {
		List<String> strAL = new ArrayList<String>();
		for(String str : strL){
			if(strAL.contains(str)){continue;}
			if(str.toLowerCase().startsWith(s.toLowerCase())){strAL.add(str);}
		}
		return strAL;
	}

}
