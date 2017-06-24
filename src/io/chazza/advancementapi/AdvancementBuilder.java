package io.chazza.advancementapi;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;

/**
 * Simplify the creation of advancements for Minecraft 1.12 in the style of a builder-pattern class.
 * <p>
 * The original project on which this class was based on can be found open sourced on GitHub at
 * <a href="https://github.com/Chazmondo/AdvancementAPI/">
 *     https://github.com/Chazmondo/AdvancementAPI/
 * </a>,
 * or the SpigotMC forum thread at
 * <a href="https://www.spigotmc.org/threads/advancementapi-create-custom-advancements.240462/">
 *     https://www.spigotmc.org/threads/advancementapi-create-custom-advancements.240462/
 * </a>
 * 
 * @author Chazmondo: Original Author
 * @author Parker Hawke - 2008Choco: Nearly completely rewritten
 */
public class AdvancementBuilder {
	
	/* Advancement JSON Format:
	 * For reference, see http://minecraft.gamepedia.com/Advancements#JSON_Format
	 * 
	 * {
	 *     display: {
	 *         icon: {
	 *             item: Integer
	 *             data: Integer
	 *         },
	 *         title: String,
	 *         title: {
	 *             // a JSON component representing the text (optional)
	 *         },
	 *         frame: String... "challenge", "goal" or "task",
	 *         background: String,
	 *         description: String,
	 *         description: {
	 *             // a JSON component representing the text (optional)
	 *         },
	 *         show_toast: Boolean,
	 *         announce_to_chat: Boolean,
	 *         hidden: Boolean
	 *     },
	 *     parent: String,
	 *     criteria: {
	 *         <criteriaName>: {
	 *             trigger: String,
	 *             conditions: { TODO
	 *                 // possible conditions. Varies depending on the trigger
	 *             }
	 *         }, ... etc.
	 *     },
	 *     requirements: [
	 *         String...
	 *     ],
	 *     rewards: {
	 *         recipes: [
	 *             String...
	 *         ],
	 *         loot: [
	 *             String...
	 *         ],
	 *         experience: Integer,
	 *         function: String
	 *     }
	 * }
	 */
	
	private static final Gson GSON = new Gson();

	private final JsonObject advancementData = new JsonObject();
    private final NamespacedKey id;
	
	/* Display */
	private NamespacedKey iconMaterial = NamespacedKey.minecraft("stone");
	private int iconData = 0;
	private String title = "Advancement Title", description = "Advancement Description";
	private NamespacedKey background = NamespacedKey.minecraft("textures/gui/advancements/backgrounds/stone.png");
	private FrameType frame = FrameType.TASK;
    private boolean announceToChat = true, showToast = true, hidden = false;
    
    /* Criteria */
    private final List<Criteria> criteria = new ArrayList<>();
    
    /* Rewards */
    private NamespacedKey[] recipes = new NamespacedKey[0], loot = new NamespacedKey[0];
    private int experience;
    private String function;
    
    /* Root */
    private String[] requirements = new String[0];
    private String parent;

    /**
     * Construct a new AdvancementBuilder under the given identification
     * 
     * @param id the advancement namespace
     */
    public AdvancementBuilder(NamespacedKey id) {
        this.id = id;
    }

    /**
     * Get the key in which this advancement is referenced
     * 
     * @return the namespaced key
     */
    public NamespacedKey getID() {
        return id;
    }
    
    /**
     * Include an icon to the TOAST notification
     * 
     * @param material the material of the icon
     * @param data the data of the icon
     * 
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder withIcon(NamespacedKey material, int data) {
    	this.iconMaterial = material;
    	this.iconData = data;
    	return this;
    }
    
    /**
     * Include an icon to the TOAST notification with a default data value of 0
     * 
     * @param material the material of the icon
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder withIcon(NamespacedKey material) {
    	this.iconMaterial = material;
    	return this;
    }

    /**
     * Get the icon material of this advancement displayed in the TOAST notification
     * 
     * @return the icon material
     */
    public NamespacedKey getIconMaterial() {
        return iconMaterial;
    }
    
    /**
     * Get the icon data of this advancement displaye in the TOAST notification
     * 
     * @return the icon data
     */
    public int getIconData() {
		return iconData;
	}
    
    /**
     * Include a description to the TOAST notification
     * 
     * @param description the description to set
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
    
    /**
     * Get the descripton of this advancement displayed in the TOAST notification
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Include a background to the TOAST notification
     * 
     * @param background the background URL to set
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder withBackground(NamespacedKey background) {
        this.background = background;
        return this;
    }

    /**
     * Get the background of this advancement displayed in the TOAST notification
     * 
     * @return the background
     */
    public NamespacedKey getBackground() {
        return background;
    }

    /**
     * Include a title to the TOAST notification
     * 
     * @param title the title to set
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder withTitle(String title) {
        this.title = title;
        return this;
    }
    
    /**
     * Get the title of this advancement displayed in the TOAST notification
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Set the frame of the advancement in the interface
     * 
     * @param frame the frame to set
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder withFrame(FrameType frame) {
        this.frame = frame;
        return this;
    }

    /**
     * Get the frame of the advancement in the interface
     * 
     * @return the frame
     */
    public FrameType getFrame() {
        return frame;
    }
    
    /**
     * Set whether the advancement should be announced to chat or not
     * 
     * @param announceToChat the announcement state
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder setAnnounceToChat(boolean announceToChat) {
    	this.announceToChat = announceToChat;
    	return this;
    }
    
    /**
     * Whether the advancement will be announced or not
     * 
     * @return true if should announce
     */
    public boolean shouldAnnounceToChat() {
		return announceToChat;
	}
    
    /**
     * Set whether the advancement should show a TOAST notification or not
     * 
     * @param showToast whether to show the TOAST
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder setShowToast(boolean showToast) {
    	this.showToast = showToast;
    	return this;
    }
    
    /**
     * Whether the advancement will display a TOAST notification or not
     * 
     * @return true if should display TOAST
     */
    public boolean shouldShowToast() {
		return showToast;
	}
    
    /**
     * Set whether the advancement should be hidden in the interface or not (i.e. it
     * must be discovered)
     * 
     * @param hidden whether it should be hidden
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder setHidden(boolean hidden) {
    	this.hidden = hidden;
    	return this;
    }
    
    /**
     * Whether the advancement is hidden in the interface or not
     * 
     * @return true if hidden
     */
    public boolean isHidden() {
    	return hidden;
    }
    
    /**
     * Add a new criteria to the advancement. Criteria must be optionally met before
     * the advancement can be awarded to the player
     * 
     * @param criteria the criter to add
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder addCriteria(Criteria criteria) {
    	this.criteria.add(criteria);
    	return this;
    }
    
    /**
     * Get a list of all criteria for this advancement
     * 
     * @return the criteria
     */
    public List<Criteria> getCriteria() {
    	return ImmutableList.copyOf(criteria);
    }
    
    /**
     * Set the parent advancement
     * 
     * @param parent the parent to set
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder withParent(String parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Get the parent advancement if one exists
     * 
     * @return the parent advancement. null if none exists
     */
    public String getParent() {
        return parent;
    }
    
    /**
     * Set the required criteria that should be completed for this advancement.
     * 
     * @param requirements to requirements to set
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder setRequirements(String... requirements) {
		this.requirements = requirements;
		return this;
	}
    
    /**
     * Add a criteria requirement
     * 
     * @param requirement the requirement to add
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder addRequirement(String requirement) {
    	ArrayUtils.add(requirements, requirement);
    	return this;
    }
    
    /**
     * Get the criteria requirements for this advancement
     * 
     * @return the requirements
     */
    public String[] getRequirements() {
		return requirements;
	}
    
    /**
     * Set the recipes that should be given to the player upon completing the advancement.
     * 
     * @param recipes the recipes to set
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder setRecipes(NamespacedKey... recipes) {
		this.recipes = recipes;
		return this;
	}
    
    /**
     * Add a recipe to the rewards
     * 
     * @param recipe the recipe to add
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder addRecipe(NamespacedKey recipe) {
    	ArrayUtils.add(recipes, recipe);
    	return this;
    }
    
    /**
     * Get the recipe rewards for this advancement
     * 
     * @return the recipe rewards
     */
    public NamespacedKey[] getRecipes() {
		return recipes;
	}
    
    /**
     * Set the loot tables that should be evaluated upon completing the advancement.
     * 
     * @param loot the loot tables to add
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder setLootTables(NamespacedKey... loot) {
		this.loot = loot;
		return this;
	}
    
    /**
     * Add a loot table to the rewards
     * 
     * @param loot the loot table to add
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder addLootTables(NamespacedKey loot) {
    	ArrayUtils.add(this.loot, loot);
    	return this;
    }
    
    /**
     * Get the loot table rewards for this advancement
     * 
     * @return the loot table rewards
     */
    public NamespacedKey[] getLootTables() {
		return loot;
	}
    
    /**
     * Set the experience that should be awarded to the player upon completing 
     * the advancement
     * 
     * @param experience the experience reward
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder setExperience(int experience) {
		this.experience = experience;
		return this;
	}
    
    /**
     * Get the experience reward for this advancement
     * 
     * @return the experience reward
     */
    public int getExperience() {
		return experience;
	}
    
    /**
     * Set the function that should be executed upon completing the advancement
     * 
     * @param function the function to execute
     * @return this instance. Allows for chained method calls
     */
    public AdvancementBuilder setFunction(String function) {
		this.function = function;
		return this;
	}
    
    /**
     * Get the function that will be executed
     * 
     * @return the function to be executed
     */
    public String getFunction() {
		return function;
	}

    /**
     * Update the {@link JsonObject} stored within this AdvancementBuilder. This is not necessary
     * to be called externally, as {@link #save(World)} will invoke this method regardless
     * 
     * @return the updated JsonObject
     * @see #getAdvancementData()
     */
	public JsonObject updateJSON() {
		// "display"
		JsonObject display = new JsonObject();
		
		JsonObject icon = new JsonObject();
		icon.addProperty("item", this.iconMaterial.toString());
		icon.addProperty("data", this.iconData);
		
		display.add("icon", icon);
		display.addProperty("title", title);
		display.addProperty("frame", frame.toString());
		display.addProperty("background", background.toString());
		display.addProperty("description", description);
		display.addProperty("show_toast", showToast);
		display.addProperty("announce_to_chat", announceToChat);
		display.addProperty("hidden", hidden);
		
		// "criteria"
		JsonObject criteriaData = new JsonObject();
		for (Criteria criteria : this.criteria) {
			// "<criteriaName>"
			JsonObject individualCriteria = new JsonObject();
			
			individualCriteria.addProperty("trigger", criteria.getTrigger());
			// TODO: "conditions"
			
			criteriaData.add(criteria.getName(), individualCriteria);
		}
		
		// "requirements"
		JsonArray requirements = new JsonArray();
		JsonArray requirementsNested = new JsonArray();
		for (String requirement : this.requirements) {
			requirementsNested.add(requirement);
		}
		requirements.add(requirementsNested);
		
		// "rewards"
		JsonObject rewards = new JsonObject();
		
		/* "recipes" */
		JsonArray recipesData = new JsonArray();
		for (NamespacedKey recipe : this.recipes) {
			recipesData.add(recipe.toString());
		}
		
		/* "loot" */
		JsonArray lootData = new JsonArray();
		for (NamespacedKey loot : this.loot) {
			lootData.add(loot.toString());
		}
		
		if (recipes.length > 0) rewards.add("recipes", recipesData);
		if (loot.length > 0) rewards.add("loot", lootData);
		if (experience > 0) rewards.addProperty("experience", experience);
		if (function != null) rewards.addProperty("function", function);
		
		// Root
		this.advancementData.add("display", display);
		if (parent != null) this.advancementData.addProperty("parent", parent);
		this.advancementData.add("criteria", criteriaData);
		this.advancementData.add("requirements", requirements);
		if (rewards.size() > 0) this.advancementData.add("rewards", rewards);
		
		return advancementData;
	}
	
	/**
	 * Get the {@link JsonObject} that represents this advancement and optionally
	 * update it
	 * 
	 * @param update whether the object should be updated before being returned
	 * @return the json representation of the advancement
	 * 
	 * @see #updateJSON()
	 */
	public JsonObject getAdvancementData(boolean update) {
		if (update) return this.updateJSON();
		return advancementData;
	}
	
	/**
	 * Get the {@link JsonObject} that represents this advancement
	 * 
	 * @return the json representation of the advancement
	 */
	public JsonObject getAdvancementData() {
		return this.getAdvancementData(false);
	}
	
	/**
	 * Save this advancement and return the representing Bukkit {@link Advancement} equivalent
	 * 
	 * @return the saved advancement. null if an error occurred
	 */
    @SuppressWarnings("deprecation")
	public Advancement save() {
    	// Impossible advancement criteria check
    	if (criteria.size() == 0) {
    		this.criteria.add(new Criteria("impossible", "minecraft:impossible"));
    	}
    	if (requirements.length == 0) {
    		this.setRequirements("impossible");
    	}
    	
    	Advancement existingAdvancement = Bukkit.getAdvancement(id);
    	if (existingAdvancement == null) {
        	try {
        		return Bukkit.getUnsafe().loadAdvancement(id, GSON.toJson(this.getAdvancementData(true)));
        	} catch (Exception e) {
        		e.printStackTrace();
        		return null;
        	}
    	}
    	
    	return existingAdvancement;
    }
    
    
    /**
     * The different types of frames
     */
    public enum FrameType {
    	
    	/**
    	 * A basic advancement (square shaped)
    	 */
    	TASK("task"),
    	
    	/**
    	 * A goal advancement (oval shaped)
    	 */
    	GOAL("goal"),
    	
    	/**
    	 * A challenge advancement (star shaped)
    	 */
    	CHALLENGE("challenge");
    	
    	private final String name;
    	
    	private FrameType(String name){
    		this.name = name;
    	}  
    	
    	@Override
    	public String toString(){
    		return name;
    	}
    }
    
    /**
     * Represents a criteria to be accomplished in an advancement. Criteria contain various
     * types of triggers and conditions which much be completed / met in order to achieve 
     * the advancement
     *
     * @author Parker Hawke - 2008Choco
     */
    public static class Criteria {
    	
    	private String name, trigger;
    	
    	/**
    	 * Construct a new criteria
    	 * 
    	 * @param name the name of the criteria
    	 * @param trigger the criteria trigger
    	 */
    	public Criteria(String name, String trigger) {
    		this.name = name;
    		this.trigger = trigger;
    	}
    	
    	/**
    	 * Get the name of the criteria
    	 * 
    	 * @return the criteria name
    	 */
    	public String getName() {
			return name;
		}
    	
    	/**
    	 * Get the trigger for this criteria
    	 * 
    	 * @return the criteria trigger
    	 */
    	public String getTrigger() {
			return trigger;
		}
    	
    }
}