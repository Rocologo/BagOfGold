package one.lindegaard.MobHunting.achievements;

public interface Achievement
{
	public String getName();
	public String getID();
	
	public String getDescription();
	
	public double getPrize();
	
	public String getPrizeCmd();
	public String getPrizeCmdDescription();
}
