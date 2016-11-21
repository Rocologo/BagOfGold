package one.lindegaard.MobHunting.mobs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.util.Misc;

import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager.Profession;

public enum MinecraftMob {
	// PlayerName and texture can be found here:
	// http://heads.freshcoal.com/index.php

	// ******************************************************************
	// Minecraft 1.11
	// ******************************************************************

	// Llama
	Llama("LLAMA", "", "", "Llama Head", ""),

	// Evoker
	Evoker("EVOKER", "", "", "Evoker Head", ""),

	// Vindicator
	Vindicator("VINDICATOR", "", "", "Vindicator Head", ""),

	// Vex
	Vex("VEX", "", "", "Vex Head", ""),

	// Nitwit Villager
	Nitwit("NITWIT", "", "", "Nitwit Head", ""),

	// Nitwit Villager
	Cartographer("CARTOGRAFER", "", "", "Cartographer Head", ""),

	// Donkey
	Donkey("DONKEY", "", "", "Donkey Head", ""),

	// Mule
	Mule("MULE", "", "", "Mule Head", ""),

	// ZombieHorse
	ZombieHorse("ZOMBIEHORSE", "", "", "ZombieHorse Head", ""),

	// SkeletonHorse
	SkeletonHorse("SKELETONHORSE", "", "", "SkeletonHorse Head", ""),

	// ******************************************************************
	// Minecraft 1.10
	// ******************************************************************
	// Polar Bear
	/// give @p skull 1 3 {display:{Name:"Polar
	// Bear"},SkullOwner:{Id:"87324464-1700-468f-8333-e7779ec8c21e",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ2ZDIzZjA0ODQ2MzY5ZmEyYTM3MDJjMTBmNzU5MTAxYWY3YmZlODQxOTk2NjQyOTUzM2NkODFhMTFkMmIifX19"}]}}}
	PolarBear("POLAR_BEAR", "Enderr_", "87324464-1700-468f-8333-e7779ec8c21e", "Polar Bear Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ2ZDIzZjA0ODQ2MzY5ZmEyYTM3MDJjMTBmNzU5MTAxYWY3YmZlODQxOTk2NjQyOTUzM2NkODFhMTFkMmIifX19"),
	// Husk
	// give @p skull 1 3
	// {display:{Name:"Husk"},SkullOwner:{Id:"d550dd2a-6da3-4de1-8ac9-d64f3578a126",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlMjgyMjk1YjJhN2VhNWVkOWUyNDk3NjRjMzhjYTE4OTEyYWUwMjdkY2VhOWRkZDZkZDAzZDVhNmJmZTgwIn19fQ=="}]}}}
	Husk("HUSK", "Gabriel333", "d550dd2a-6da3-4de1-8ac9-d64f3578a126", "Husk Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlMjgyMjk1YjJhN2VhNWVkOWUyNDk3NjRjMzhjYTE4OTEyYWUwMjdkY2VhOWRkZDZkZDAzZDVhNmJmZTgwIn19fQ=="),
	// Stray
	/// give @p skull 1 3
	// {display:{Name:"Stray"},SkullOwner:{Id:"d9f06a7c-e958-4ed5-8db7-f91de23696cd",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc4MGQxZjAyMTZlMjRlMmEzM2ZjYTQ3YjNlZjJjYmM0ZjM3Njg5ZGFkNTNmYWFmZDM0NDUxZDBkYmY4MTFmIn19fQ=="}]}}}
	// give @p skull 1 3
	// {display:{Name:"Stray"},SkullOwner:{Id:"c1798c29-982c-4107-be32-a0646a729fb2",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc4MGQxZjAyMTZlMjRlMmEzM2ZjYTQ3YjNlZjJjYmM0ZjM3Njg5ZGFkNTNmYWFmZDM0NDUxZDBkYmY4MTFmIn19fQ=="}]}}}
	// Frozen Zombie: /give @p skull 1 3
	// {display:{Name:"frozenzombie"},SkullOwner:{Id:"cb6dad6a-20ea-41a6-a0a5-450ab51cb5e6",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTljNTlkOWI4ODNmMjkyNWFjZmZlNDkzZWE4MzI3NmFkYTM3NzlkM2I0OTI0ZjRlZWI2MDdkYTRkYzI2NGIifX19"}]}}}
	Stray("STRAY", "JeansenDK", "d9f06a7c-e958-4ed5-8db7-f91de23696cd", "Stray Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc4MGQxZjAyMTZlMjRlMmEzM2ZjYTQ3YjNlZjJjYmM0ZjM3Njg5ZGFkNTNmYWFmZDM0NDUxZDBkYmY4MTFmIn19fQ=="),
	// ******************************************************************
	// Minecraft 1.9
	// *******************************************************************
	// Shulker
	// give @p skull 1 3
	// {display:{Name:"Shulker"},SkullOwner:{Id:"66524d4d-0b92-420c-acf5-78fe9ae57bbd",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFkMzUzNGQyMWZlODQ5OTI2MmRlODdhZmZiZWFjNGQyNWZmZGUzNWM4YmRjYTA2OWU2MWUxNzg3ZmYyZiJ9fX0="}]}}}
	Shulker("SHULKER", "MHF_Shulker", "66524d4d-0b92-420c-acf5-78fe9ae57bbd", "Shulker Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFkMzUzNGQyMWZlODQ5OTI2MmRlODdhZmZiZWFjNGQyNWZmZGUzNWM4YmRjYTA2OWU2MWUxNzg3ZmYyZiJ9fX0="),
	// ******************************************************************
	// Minecraft 1.8
	// *******************************************************************
	// Endermite
	// give @p skull 1 3
	// {display:{Name:"Endermite"},SkullOwner:{Id:"887e5189-4653-4cc1-9221-cfaa2e81f1c4",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThjNmNiOGNlYWFkNWEyYWQ1Y2M5YTY3YmNlNmQ1YmRiZjVjYmI3ZTMxMjk1NWNjZjlmMTYyNTA5MzU1YjEifX19"}]}}}
	Endermite("ENDERMITE", "MHF_Endermite", "887e5189-4653-4cc1-9221-cfaa2e81f1c4", "Entermite Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThjNmNiOGNlYWFkNWEyYWQ1Y2M5YTY3YmNlNmQ1YmRiZjVjYmI3ZTMxMjk1NWNjZjlmMTYyNTA5MzU1YjEifX19"),

	// Guardian
	// give @p skull 1 3
	// {display:{Name:"Guardian"},SkullOwner:{Id:"2e9c0a67-92b3-4949-9be1-faf12318ee88",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTMyYzI0NTI0YzgyYWIzYjNlNTdjMjA1MmM1MzNmMTNkZDhjMGJlYjhiZGQwNjM2OWJiMjU1NGRhODZjMTIzIn19fQ=="}]}}}
	// This had same look like elder guardian but different colors. /give @p
	// skull 1 3
	// {display:{Name:"Guardian"},SkullOwner:{Id:"e57e58c0-bb9c-4cb0-a0a2-c137dfc91164",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZiNjc1Y2I1YTdlM2ZkMjVlMjlkYTgyNThmMjRmYzAyMGIzZmE5NTAzNjJiOGJjOGViMjUyZTU2ZTc0In19fQ=="}]}}}
	Guardian("GUARDIAN", "MHF_Guardian", "2e9c0a67-92b3-4949-9be1-faf12318ee88", "Guardian Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTMyYzI0NTI0YzgyYWIzYjNlNTdjMjA1MmM1MzNmMTNkZDhjMGJlYjhiZGQwNjM2OWJiMjU1NGRhODZjMTIzIn19fQ=="),

	// Elder Guardian
	// /give @p skull 1 3 {display:{Name:"Elder
	// Guardian"},SkullOwner:{Id:"4d43444c-2455-4f6e-b2bd-924b7e5050f3",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGFkYzRhNmY1M2FmYTExNjAyN2I1MWQ2ZjJlNDMzZWU3YWZhNWQ1OWIyZmZhMDQ3ODBiZTQ2NGZhNWQ2MWEifX19"}]}}}
	// /give @p skull 1 3 {display:{Name:"Elder
	// Guardian"},SkullOwner:{Id:"f2e933a7-614f-44e0-bf18-289b102104ab",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM3OTc0ODJhMTRiZmNiODc3MjU3Y2IyY2ZmMWI2ZTZhOGI4NDEzMzM2ZmZiNGMyOWE2MTM5Mjc4YjQzNmIifX19"}]}}}
	// /give @p skull 1 3 {display:{Name:"Elder
	// Guardian"},SkullOwner:{Id:"1132b69e-5ca8-4393-819c-7b2a9cb68d0d",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzIzZDU4ODU5YTFhYTIyYmYwZWY5NjZiYjI5NDVjYzM5NjJiYjRiZTVmZWQ0ZTM2OWU3ZjExMjg1NzM1MWYyMiJ9fX0="}]}}}
	ElderGuardian("ELDER_GUARDIAN", "MHF_EGuardian", "4d43444c-2455-4f6e-b2bd-924b7e5050f3", "Elder Guardian Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGFkYzRhNmY1M2FmYTExNjAyN2I1MWQ2ZjJlNDMzZWU3YWZhNWQ1OWIyZmZhMDQ3ODBiZTQ2NGZhNWQ2MWEifX19"),

	// KillerRabbit
	// give @p skull 1 3 {display:{Name:"Killer
	// Rabbit"},SkullOwner:{Id:"b9aa36fd-8584-4d2d-948c-278a58ac1a54",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzA4OGNkNjE0MTg5NDU4ZDk5YzJmYmVkNTg0NDg4OTVlYTZiMjZmYzY2N2EyYzU5MTlmNzE0Y2VlNjQ4ZDExIn19fQ=="}]}}}
	KillerRabbit("KILLERRABBIT", "MHF_KillerRabbit", "b9aa36fd-8584-4d2d-948c-278a58ac1a54", "Killer Rabbit Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzA4OGNkNjE0MTg5NDU4ZDk5YzJmYmVkNTg0NDg4OTVlYTZiMjZmYzY2N2EyYzU5MTlmNzE0Y2VlNjQ4ZDExIn19fQ=="),

	// Player
	// give @p skull 1 3
	// {display:{Name:"Player"},SkullOwner:{Id:"65b24bc2-0774-49cf-9118-18e826418a3a",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNiMDk4OTY3MzQwZGFhYzUyOTI5M2MyNGUwNDkxMDUwOWIyMDhlN2I5NDU2M2MzZWYzMWRlYzdiMzc1MCJ9fX0="}]}}}
	PvpPlayer("PLAYER", "MHF_Alex", "65b24bc2-0774-49cf-9118-18e826418a3a", "Player Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNiMDk4OTY3MzQwZGFhYzUyOTI5M2MyNGUwNDkxMDUwOWIyMDhlN2I5NDU2M2MzZWYzMWRlYzdiMzc1MCJ9fX0="),
	// ******************************************************************
	// Minecraft 1.7
	// ******************************************************************
	// Slime
	// give @p skull 1 3
	// {display:{Name:"Slime"},SkullOwner:{Id:"9aca565d-105c-4e8c-81fc-740545cb74b2",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZhZDIwZmMyZDU3OWJlMjUwZDNkYjY1OWM4MzJkYTJiNDc4YTczYTY5OGI3ZWExMGQxOGM5MTYyZTRkOWI1In19fQ=="}]}}}
	Slime("SLIME", "MHF_Slime", "9aca565d-105c-4e8c-81fc-740545cb74b2", "Slime Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZhZDIwZmMyZDU3OWJlMjUwZDNkYjY1OWM4MzJkYTJiNDc4YTczYTY5OGI3ZWExMGQxOGM5MTYyZTRkOWI1In19fQ=="),
	// MagmaCube
	// give @p skull 1 3 {display:{Name:"Lava
	// Slime"},SkullOwner:{Id:"96aced64-5b85-4b99-b825-53cd7a9f9726",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg5NTdkNTAyM2M5MzdjNGM0MWFhMjQxMmQ0MzQxMGJkYTIzY2Y3OWE5ZjZhYjM2Yjc2ZmVmMmQ3YzQyOSJ9fX0="}]}}}
	MagmaCube("MAGMA_CUBE", "MHF_LavaSlime", "96aced64-5b85-4b99-b825-53cd7a9f9726", "Lava Slime Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg5NTdkNTAyM2M5MzdjNGM0MWFhMjQxMmQ0MzQxMGJkYTIzY2Y3OWE5ZjZhYjM2Yjc2ZmVmMmQ3YzQyOSJ9fX0="),
	// Ghast
	// give @p skull 1 3
	// {display:{Name:"Ghast"},SkullOwner:{Id:"807f287f-6499-4e93-a887-0a298ab3091f",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI2YTcyMTM4ZDY5ZmJiZDJmZWEzZmEyNTFjYWJkODcxNTJlNGYxYzk3ZTVmOTg2YmY2ODU1NzFkYjNjYzAifX19"}]}}}
	Ghast("GHAST", "MHF_Ghast", "807f287f-6499-4e93-a887-0a298ab3091f", "Ghast Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI2YTcyMTM4ZDY5ZmJiZDJmZWEzZmEyNTFjYWJkODcxNTJlNGYxYzk3ZTVmOTg2YmY2ODU1NzFkYjNjYzAifX19"),
	// Blaze
	// give @p skull 1 3
	// {display:{Name:"Blaze"},SkullOwner:{Id:"0432ca66-5c5e-48fa-910e-851e0452683e",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ=="}]}}}
	Blaze("BLAZE", "MHF_Blaze", "0432ca66-5c5e-48fa-910e-851e0452683e", "Blaze Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ=="),
	// Creeper
	// give @p skull 1 3
	// {display:{Name:"Creeper"},SkullOwner:{Id:"87c6310c-4748-4110-ae59-0b58ad75296a",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk1ZWY4MzYzODlhZjk5MzE1OGFiYTI3ZmYzN2I2NTY3MTg1ZjdhNzIxY2E5MGZkZmViOTM3YTdjYjU3NDcifX19"}]}}}
	Creeper("CREEPER", "MHF_Creeper", "87c6310c-4748-4110-ae59-0b58ad75296a", "Creeper Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk1ZWY4MzYzODlhZjk5MzE1OGFiYTI3ZmYzN2I2NTY3MTg1ZjdhNzIxY2E5MGZkZmViOTM3YTdjYjU3NDcifX19"),
	// Enderman
	// give @p skull 1 3
	// {display:{Name:"Enderman"},SkullOwner:{Id:"0de98464-1274-4dd6-bba8-370efa5d41a8",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0="}]}}}
	Enderman("ENDERMAN", "MHF_Enderman", "0de98464-1274-4dd6-bba8-370efa5d41a8", "Enderman Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0="),
	// Silverfish
	// RegularFish: give @p skull 1 3 {display:{Name:"Regular
	// Fish"},SkullOwner:{Id:"8f718637-6901-4301-bd98-ebde0cc19ed8",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmY5OWI1ODBkNDVhNzg0ZTdhOTY0ZTdkM2IxZjk3Y2VjZTc0OTExMTczYmQyMWMxZDdjNTZhY2RjMzg1ZWQ1In19fQ=="}]}}}
	Silverfish("SILVERFISH", "MHF_Silverfish", "8f718637-6901-4301-bd98-ebde0cc19ed8", "Silverfish Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmY5OWI1ODBkNDVhNzg0ZTdhOTY0ZTdkM2IxZjk3Y2VjZTc0OTExMTczYmQyMWMxZDdjNTZhY2RjMzg1ZWQ1In19fQ=="),
	// Skeleton
	// give @p skull 1 3
	// {display:{Name:"Skeleton"},SkullOwner:{Id:"0e201014-896f-40a9-ba36-e8dc8a6c8cfd",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmU1YmU2YTNjMDE1OWQyYzFmM2IxZTRlMWQ4Mzg0YjZmN2ViYWM5OTNkNThiMTBiOWY4OTg5Yzc4YTIzMiJ9fX0="}]}}}
	Skeleton("SKELETON", "MHF_Skeleton", "0e201014-896f-40a9-ba36-e8dc8a6c8cfd", "Skeleton Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmU1YmU2YTNjMDE1OWQyYzFmM2IxZTRlMWQ4Mzg0YjZmN2ViYWM5OTNkNThiMTBiOWY4OTg5Yzc4YTIzMiJ9fX0="),
	// WitherSkeleton
	// give @p skull 1 3 {display:{Name:"Wither
	// Skeleton"},SkullOwner:{Id:"fed85923-6ff4-417a-b920-5dec8950bdec",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjMzYjQxZmE3OWNkNTNhMjMwZTJkYjk0Mjg2Mzg0MzE4M2E3MDQwNDUzM2JiYzAxZmFiNzQ0NzY5YmNiIn19fQ=="}]}}}
	WitherSkeleton("WITHERSKELETON", "MHF_WSkeleton", "fed85923-6ff4-417a-b920-5dec8950bdec", "Wither Skeleton Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjMzYjQxZmE3OWNkNTNhMjMwZTJkYjk0Mjg2Mzg0MzE4M2E3MDQwNDUzM2JiYzAxZmFiNzQ0NzY5YmNiIn19fQ=="),
	// Spider
	// give @p skull 1 3
	// {display:{Name:"Spider"},SkullOwner:{Id:"8bdb71d0-4724-48b2-9344-e79480424798",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q1NDE1NDFkYWFmZjUwODk2Y2QyNThiZGJkZDRjZjgwYzNiYTgxNjczNTcyNjA3OGJmZTM5MzkyN2U1N2YxIn19fQ=="}]}}}
	Spider("SPIDER", "MHF_Spider", "8bdb71d0-4724-48b2-9344-e79480424798", "Spider Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q1NDE1NDFkYWFmZjUwODk2Y2QyNThiZGJkZDRjZjgwYzNiYTgxNjczNTcyNjA3OGJmZTM5MzkyN2U1N2YxIn19fQ=="),
	// CaveSpider
	// give @p skull 1 3 {display:{Name:"Cave
	// Spider"},SkullOwner:{Id:"39173a7a-c957-4ec1-ac1a-43e5a64983df",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE2NDVkZmQ3N2QwOTkyMzEwN2IzNDk2ZTk0ZWViNWMzMDMyOWY5N2VmYzk2ZWQ3NmUyMjZlOTgyMjQifX19"}]}}}
	CaveSpider("CAVE_SPIDER", "MHF_CaveSpider", "39173a7a-c957-4ec1-ac1a-43e5a64983df", "Cave Spider Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE2NDVkZmQ3N2QwOTkyMzEwN2IzNDk2ZTk0ZWViNWMzMDMyOWY5N2VmYzk2ZWQ3NmUyMjZlOTgyMjQifX19"),
	// Witch
	// give @p skull 1 3
	// {display:{Name:"Witch"},SkullOwner:{Id:"b6b73b37-7097-4130-b36b-15a2c459aa3d",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWVjNThiNWVmZDJiYjcyODEzYTRlYWMwZDY2YTgyMTcyZmQ0NjY0YTE0MzNkYjUxOTU5MzRiNzY0YjM5NyJ9fX0="}]}}}
	Witch("WITCH", "ScrafBrothers4", "b6b73b37-7097-4130-b36b-15a2c459aa3d", "Witch Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWVjNThiNWVmZDJiYjcyODEzYTRlYWMwZDY2YTgyMTcyZmQ0NjY0YTE0MzNkYjUxOTU5MzRiNzY0YjM5NyJ9fX0="),
	// Wither
	// give @p skull 1 3
	// {display:{Name:"Wither"},SkullOwner:{Id:"4e999de5-7096-469e-b0a8-ffa913b4109b",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RmNzRlMzIzZWQ0MTQzNjk2NWY1YzU3ZGRmMjgxNWQ1MzMyZmU5OTllNjhmYmI5ZDZjZjVjOGJkNDEzOWYifX19"}]}}}
	Wither("WITHER", "MHF_Wither", "4e999de5-7096-469e-b0a8-ffa913b4109b", "Wither Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RmNzRlMzIzZWQ0MTQzNjk2NWY1YzU3ZGRmMjgxNWQ1MzMyZmU5OTllNjhmYmI5ZDZjZjVjOGJkNDEzOWYifX19"),
	// Zombie Pigman
	// give @p skull 1 3
	// {display:{Name:"Pigzombie"},SkullOwner:{Id:"6540c046-d6ea-4aff-9766-32a54ebe6958",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ=="}]}}}
	ZombiePigman("PIG_ZOMBIE", "MHF_PigZombie", "6540c046-d6ea-4aff-9766-32a54ebe6958", "Zombie Pig Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ=="),
	// Zombie
	// give @p skull 1 3
	// {display:{Name:"Zombie"},SkullOwner:{Id:"290f6aeb-b185-451c-9930-e441070b5901",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZmYzg1NGJiODRjZjRiNzY5NzI5Nzk3M2UwMmI3OWJjMTA2OTg0NjBiNTFhNjM5YzYwZTVlNDE3NzM0ZTExIn19fQ=="}]}}}
	Zombie("ZOMBIE", "MHF_Zombie", "290f6aeb-b185-451c-9930-e441070b5901", "Zombie Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZmYzg1NGJiODRjZjRiNzY5NzI5Nzk3M2UwMmI3OWJjMTA2OTg0NjBiNTFhNjM5YzYwZTVlNDE3NzM0ZTExIn19fQ=="),
	// ZombieVillager
	// give @p skull 1 3
	// {display:{Name:"Zombie"},SkullOwner:{Id:"290f6aeb-b185-451c-9930-e441070b5901",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZmYzg1NGJiODRjZjRiNzY5NzI5Nzk3M2UwMmI3OWJjMTA2OTg0NjBiNTFhNjM5YzYwZTVlNDE3NzM0ZTExIn19fQ=="}]}}}
	ZombieVillager("ZOMBIE_VILLAGER", "MHF_Zombie", "290f6aeb-b185-451c-9930-e441070b5901", "ZombieVillager Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZmYzg1NGJiODRjZjRiNzY5NzI5Nzk3M2UwMmI3OWJjMTA2OTg0NjBiNTFhNjM5YzYwZTVlNDE3NzM0ZTExIn19fQ=="),
	// BonusMob
	// Herobrine /give @p skull 1 3
	// {display:{Name:"Herobrine"},SkullOwner:{Id:"d0b15454-36fa-43e4-a247-f882bb9fe288",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThiN2NhM2M3ZDMxNGE2MWFiZWQ4ZmMxOGQ3OTdmYzMwYjZlZmM4NDQ1NDI1YzRlMjUwOTk3ZTUyZTZjYiJ9fX0="}]}}}
	BonusMob("BonusMob", "Herobrine", "d0b15454-36fa-43e4-a247-f882bb9fe288", "Herobrine Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThiN2NhM2M3ZDMxNGE2MWFiZWQ4ZmMxOGQ3OTdmYzMwYjZlZmM4NDQ1NDI1YzRlMjUwOTk3ZTUyZTZjYiJ9fX0="),
	// Golem
	// give @p skull 1 3
	// {display:{Name:"Golem"},SkullOwner:{Id:"7cb6e9a5-994f-40d5-9bfc-4ba5d796d21e",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODkwOTFkNzllYTBmNTllZjdlZjk0ZDdiYmE2ZTVmMTdmMmY3ZDQ1NzJjNDRmOTBmNzZjNDgxOWE3MTQifX19"}]}}}
	IronGolem("IRON_GOLEM", "MHF_Golem", "7cb6e9a5-994f-40d5-9bfc-4ba5d796d21e", "Golem Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODkwOTFkNzllYTBmNTllZjdlZjk0ZDdiYmE2ZTVmMTdmMmY3ZDQ1NzJjNDRmOTBmNzZjNDgxOWE3MTQifX19"),

	// Passive Mobs
	// Bat
	// give @p skull 1 3 {display:{Name:"Bat
	// Head"},SkullOwner:{Id:"4ec40126-5a2d-4d6a-a0a3-125744548f0d",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZmZDgwOGY4MTI3YjRhZDQ1OGQ5ZDJlMTgxYzY5MGFkZjQ4OWE2YWQzMmVlMmFhNGFjZmE2MzQxZmU4NDIifX19"}]}}}
	Bat("BAT", "Bat", "4ec40126-5a2d-4d6a-a0a3-125744548f0d", "Bat Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZmZDgwOGY4MTI3YjRhZDQ1OGQ5ZDJlMTgxYzY5MGFkZjQ4OWE2YWQzMmVlMmFhNGFjZmE2MzQxZmU4NDIifX19"),
	// Chicken
	// give @p skull 1 3
	// {display:{Name:"Chicken"},SkullOwner:{Id:"7d3a8ace-e045-4eba-ab71-71dbf525daf1",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTYzODQ2OWE1OTljZWVmNzIwNzUzNzYwMzI0OGE5YWIxMWZmNTkxZmQzNzhiZWE0NzM1YjM0NmE3ZmFlODkzIn19fQ=="}]}}}
	Chicken("CHICKEN", "MHF_Chicken", "7d3a8ace-e045-4eba-ab71-71dbf525daf1", "Chicken Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTYzODQ2OWE1OTljZWVmNzIwNzUzNzYwMzI0OGE5YWIxMWZmNTkxZmQzNzhiZWE0NzM1YjM0NmE3ZmFlODkzIn19fQ=="),
	// Cow
	// give @p skull 1 3
	// {display:{Name:"Cow"},SkullOwner:{Id:"97ddf3b3-9dbe-4a3b-8a0f-1b19ddeac0bd",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ2YzZlZGE5NDJmN2Y1ZjcxYzMxNjFjNzMwNmY0YWVkMzA3ZDgyODk1ZjlkMmIwN2FiNDUyNTcxOGVkYzUifX19"}]}}}
	Cow("COW", "MHF_Cow", "97ddf3b3-9dbe-4a3b-8a0f-1b19ddeac0bd", "Cow Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ2YzZlZGE5NDJmN2Y1ZjcxYzMxNjFjNzMwNmY0YWVkMzA3ZDgyODk1ZjlkMmIwN2FiNDUyNTcxOGVkYzUifX19"),
	// Horse
	// give @p skull 1 3 {display:{Name:"Horse
	// Head"},SkullOwner:{Id:"39023647-b293-4555-8b0f-b55157417a1a",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjE5MDI4OTgzMDg3MzBjNDc0NzI5OWNiNWE1ZGE5YzI1ODM4YjFkMDU5ZmU0NmZjMzY4OTZmZWU2NjI3MjkifX19"}]}}}
	Horse("HORSE", "Lion", "39023647-b293-4555-8b0f-b55157417a1a", "Horse Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjE5MDI4OTgzMDg3MzBjNDc0NzI5OWNiNWE1ZGE5YzI1ODM4YjFkMDU5ZmU0NmZjMzY4OTZmZWU2NjI3MjkifX19"),
	// Mushroom
	// give @p skull 1 3 {display:{Name:"Mushroom
	// Cow"},SkullOwner:{Id:"e206ac29-ae69-475b-909a-fb523d894336",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDBiYzYxYjk3NTdhN2I4M2UwM2NkMjUwN2EyMTU3OTEzYzJjZjAxNmU3YzA5NmE0ZDZjZjFmZTFiOGRiIn19fQ=="}]}}}
	MushroomCow("MUSHROOM_COW", "MHF_MushroomCow", "e206ac29-ae69-475b-909a-fb523d894336", "Mushroom Cow Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDBiYzYxYjk3NTdhN2I4M2UwM2NkMjUwN2EyMTU3OTEzYzJjZjAxNmU3YzA5NmE0ZDZjZjFmZTFiOGRiIn19fQ=="),
	// Ocelot
	// give @p skull 1 3
	// {display:{Name:"Ocelot"},SkullOwner:{Id:"664dd492-3fcd-443b-9e61-4c7ebd9e4e10",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY1N2NkNWMyOTg5ZmY5NzU3MGZlYzRkZGNkYzY5MjZhNjhhMzM5MzI1MGMxYmUxZjBiMTE0YTFkYjEifX19"}]}}}
	Ocelot("OCELOT", "MHF_Ocelot", "664dd492-3fcd-443b-9e61-4c7ebd9e4e10", "Ocelot Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY1N2NkNWMyOTg5ZmY5NzU3MGZlYzRkZGNkYzY5MjZhNjhhMzM5MzI1MGMxYmUxZjBiMTE0YTFkYjEifX19"),
	// Pig
	// give @p skull 1 3
	// {display:{Name:"Pig"},SkullOwner:{Id:"e1e1c2e4-1ed2-473d-bde2-3ec718535399",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0="}]}}}
	Pig("PIG", "MHF_Pig", "e1e1c2e4-1ed2-473d-bde2-3ec718535399", "Pig Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0="),
	// Rabbit
	// give @p skull 1 3
	// {display:{Name:"Rabbit"},SkullOwner:{Id:"15908cee-0f9c-45ca-bf9c-fad9b05edded",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM3YTMxN2VjNWMxZWQ3Nzg4Zjg5ZTdmMWE2YWYzZDJlZWI5MmQxZTk4NzljMDUzNDNjNTdmOWQ4NjNkZTEzMCJ9fX0="}]}}}
	PassiveRabbit("RABBIT", "MHF_Rabbit", "15908cee-0f9c-45ca-bf9c-fad9b05edded", "Rabbit Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM3YTMxN2VjNWMxZWQ3Nzg4Zjg5ZTdmMWE2YWYzZDJlZWI5MmQxZTk4NzljMDUzNDNjNTdmOWQ4NjNkZTEzMCJ9fX0="),
	// Sheep
	// give @p skull 1 3
	// {display:{Name:"Sheep"},SkullOwner:{Id:"fa234925-9dbe-4b8f-a544-7c70fb6b6ac5",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjMxZjljY2M2YjNlMzJlY2YxM2I4YTExYWMyOWNkMzNkMThjOTVmYzczZGI4YTY2YzVkNjU3Y2NiOGJlNzAifX19"}]}}}
	Sheep("SHEEP", "MHF_Sheep", "fa234925-9dbe-4b8f-a544-7c70fb6b6ac5", "Sheep Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjMxZjljY2M2YjNlMzJlY2YxM2I4YTExYWMyOWNkMzNkMThjOTVmYzczZGI4YTY2YzVkNjU3Y2NiOGJlNzAifX19"),
	// Snowman
	// /give @p skull 1 3
	// {display:{Name:"Snowhead"},SkullOwner:{Id:"673db4c6-b7ea-421e-ae35-d7ab65e8b35e",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZkZmQxZjc1MzhjMDQwMjU4YmU3YTkxNDQ2ZGE4OWVkODQ1Y2M1ZWY3MjhlYjVlNjkwNTQzMzc4ZmNmNCJ9fX0="}]}}}
	// give @p skull 1 3
	// {display:{Name:"Snowhead"},SkullOwner:{Id:"0b5c7a8a-45bd-4054-8816-80291ac3316c",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTUyOGRmMTY1Mzk2MmU0ZTk5NWRmZDA2NGE3MmIyY2JmZjliNzE5NzkxMjg4MDE0M2Y5NDFhMzdkYjQ2YyJ9fX0="}]}}}
	// give @p skull 1 3
	// {display:{Name:"Snowhead"},SkullOwner:{Id:"673db4c6-b7ea-421e-ae35-d7ab65e8b35e",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZkZmQxZjc1MzhjMDQwMjU4YmU3YTkxNDQ2ZGE4OWVkODQ1Y2M1ZWY3MjhlYjVlNjkwNTQzMzc4ZmNmNCJ9fX0="}]}}}
	Snowman("SNOWMAN", "Snowman", "673db4c6-b7ea-421e-ae35-d7ab65e8b35e", "Snowman Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZkZmQxZjc1MzhjMDQwMjU4YmU3YTkxNDQ2ZGE4OWVkODQ1Y2M1ZWY3MjhlYjVlNjkwNTQzMzc4ZmNmNCJ9fX0="),
	// Squid
	// give @p skull 1 3
	// {display:{Name:"Squid"},SkullOwner:{Id:"f95d9504-ea2b-4b89-b2d0-d400654a7010",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDE0MzNiZTI0MjM2NmFmMTI2ZGE0MzRiODczNWRmMWViNWIzY2IyY2VkZTM5MTQ1OTc0ZTljNDgzNjA3YmFjIn19fQ=="}]}}}
	Squid("SQUID", "MHF_Squid", "f95d9504-ea2b-4b89-b2d0-d400654a7010", "Squid Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDE0MzNiZTI0MjM2NmFmMTI2ZGE0MzRiODczNWRmMWViNWIzY2IyY2VkZTM5MTQ1OTc0ZTljNDgzNjA3YmFjIn19fQ=="),
	// Villager
	// give @p skull 1 3
	// {display:{Name:"Villager"},SkullOwner:{Id:"0a9e8efb-9191-4c81-80f5-e27ca5433156",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIyZDhlNzUxYzhmMmZkNGM4OTQyYzQ0YmRiMmY1Y2E0ZDhhZThlNTc1ZWQzZWIzNGMxOGE4NmU5M2IifX19"}]}}}
	Villager("VILLAGER", "MHF_Villager", "0a9e8efb-9191-4c81-80f5-e27ca5433156", "Villager Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIyZDhlNzUxYzhmMmZkNGM4OTQyYzQ0YmRiMmY1Y2E0ZDhhZThlNTc1ZWQzZWIzNGMxOGE4NmU5M2IifX19"),
	// Wolf
	// give @p skull 1 3 {display:{Name:"Angry
	// Wolf"},SkullOwner:{Id:"9a7c65fb-309f-4c1a-96a1-4522c197b7fe",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk1Y2JiNGY3NWVhODc2MTdmMmY3MTNjNmQ0OWRhYzMyMDliYTFiZDRiOTM2OTY1NGIxNDU5ZWExNTMxNyJ9fX0="}]}}}
	Wolf("WOLF", "MHF_Wolf", "9a7c65fb-309f-4c1a-96a1-4522c197b7fe", "Wolf Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk1Y2JiNGY3NWVhODc2MTdmMmY3MTNjNmQ0OWRhYzMyMDliYTFiZDRiOTM2OTY1NGIxNDU5ZWExNTMxNyJ9fX0="),

	// Minecraft 1.0.0
	// Giant is unsupported by in the original game and Giants can only be
	// spawnwed through plugins.
	// Giant = Zombie Head
	// give @p skull 1 3
	// {display:{Name:"Giant"},SkullOwner:{Id:"7192f8d2-0439-4cc8-8156-cba5716130ed",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTViNDczNGY4MTY5ZjE5NDU4NjJjNzU1YjM5NWE4YTIxMmIzMzhmYmY1MDc4MDMyYzFkNjNhYTlhZGFlZiJ9fX0="}]}}}
	Giant("GIANT", "MHF_Giant", "290f6aeb-b185-451c-9930-e441070b5901", "Giant Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZmYzg1NGJiODRjZjRiNzY5NzI5Nzk3M2UwMmI3OWJjMTA2OTg0NjBiNTFhNjM5YzYwZTVlNDE3NzM0ZTExIn19fQ=="),
	// EnderDragon
	// give @p skull 1 3
	// {display:{Name:"Enderdragon"},SkullOwner:{Id:"433562fa-9e23-443e-93b0-d67228435e77",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlY2MwNDA3ODVlNTQ2NjNlODU1ZWYwNDg2ZGE3MjE1NGQ2OWJiNGI3NDI0YjczODFjY2Y5NWIwOTVhIn19fQ=="}]}}}
	EnderDragon("ENDER_DRAGON", "MHF_EnderDragon", "433562fa-9e23-443e-93b0-d67228435e77", "Ender Dragon Head",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlY2MwNDA3ODVlNTQ2NjNlODU1ZWYwNDg2ZGE3MjE1NGQ2OWJiNGI3NDI0YjczODFjY2Y5NWIwOTVhIn19fQ==");

	// OBS values() used for tableName in Database IE. EnderDragon ~
	// "Enderdragon_kill" & "Enderdragon_assist"
	// private String mColumnDB; // Database column name
	private String mType; // Minecraft mob.getType() name
	private String mPlayerProfile; // Player profile name
	private String mPlayerId; // Profile Id
	private String mDisplayName; // Normal DisplayName
	private String mTexture; // Texture value

	private MinecraftMob(String type, String playerName, String playerId, String displayName, String texture) {
		mType = type;
		mPlayerProfile = playerName;
		mPlayerId = playerId;
		mDisplayName = displayName;
		mTexture = texture;
	}

	public String getName() {
		return name();
	}

	public String getDisplayName() {
		return mDisplayName;
	}

	public String getExtendedMobType() {
		return mType;
	}

	public int getMax() {
		switch (this) {
		case Giant:
			return MobHunting.getConfigManager().giantLevel1;
		case EnderDragon:
			return MobHunting.getConfigManager().enderdragonLevel1;
		case Slime:
			return MobHunting.getConfigManager().slimeLevel1;
		case MagmaCube:
			return MobHunting.getConfigManager().magmaCubeLevel1;
		case Ghast:
			return MobHunting.getConfigManager().ghastLevel1;
		case Blaze:
			return MobHunting.getConfigManager().blazeLevel1;
		case Creeper:
			return MobHunting.getConfigManager().creeperLevel1;
		case Enderman:
			return MobHunting.getConfigManager().endermanLevel1;
		case Silverfish:
			return MobHunting.getConfigManager().silverfishLevel1;
		case Skeleton:
			return MobHunting.getConfigManager().skeletonLevel1;
		case WitherSkeleton:
			return MobHunting.getConfigManager().witherSkeletonLevel1;
		case Spider:
			return MobHunting.getConfigManager().spiderLevel1;
		case CaveSpider:
			return MobHunting.getConfigManager().caveSpiderLevel1;
		case Witch:
			return MobHunting.getConfigManager().witchLevel1;
		case Wither:
			return MobHunting.getConfigManager().witherLevel1;
		case ZombiePigman:
			return MobHunting.getConfigManager().zombiePigmanLevel1;
		case Zombie:
			return MobHunting.getConfigManager().zombieLevel1;
		case BonusMob:
			return MobHunting.getConfigManager().bonusMobLevel1;
		case IronGolem:
			return MobHunting.getConfigManager().ironGolemLevel1;
		case Bat:
			return MobHunting.getConfigManager().batLevel1;
		case Chicken:
			return MobHunting.getConfigManager().chickenLevel1;
		case Cow:
			return MobHunting.getConfigManager().cowLevel1;
		case Horse:
			return MobHunting.getConfigManager().horseLevel1;
		case MushroomCow:
			return MobHunting.getConfigManager().mushroomCowLevel1;
		case Ocelot:
			return MobHunting.getConfigManager().ocelotLevel1;
		case Pig:
			return MobHunting.getConfigManager().pigLevel1;
		case PassiveRabbit:
			return MobHunting.getConfigManager().rabbitLevel1;
		case Sheep:
			return MobHunting.getConfigManager().sheepLevel1;
		case Snowman:
			return MobHunting.getConfigManager().snowmanLevel1;
		case Squid:
			return MobHunting.getConfigManager().squidLevel1;
		case Villager:
			return MobHunting.getConfigManager().villagerLevel1;
		case Wolf:
			return MobHunting.getConfigManager().wolfLevel1;
		case Endermite:
			return MobHunting.getConfigManager().endermiteLevel1;
		case Guardian:
			return MobHunting.getConfigManager().guardianLevel1;
		case ElderGuardian:
			return MobHunting.getConfigManager().elderGuardianLevel1;
		case KillerRabbit:
			return MobHunting.getConfigManager().killerRabbitLevel1;
		case PvpPlayer:
			return MobHunting.getConfigManager().pvpPlayerLevel1;
		case Shulker:
			return MobHunting.getConfigManager().shulkerLevel1;
		case PolarBear:
			return MobHunting.getConfigManager().polarBearLevel1;
		case Stray:
			return MobHunting.getConfigManager().strayLevel1;
		case Husk:
			return MobHunting.getConfigManager().huskLevel1;
		case Cartographer:
			return MobHunting.getConfigManager().cartographerLevel1;
		case Donkey:
			return MobHunting.getConfigManager().donkeyLevel1;
		case Evoker:
			return MobHunting.getConfigManager().evokerLevel1;
		case Llama:
			return MobHunting.getConfigManager().llamaLevel1;
		case Mule:
			return MobHunting.getConfigManager().muleLevel1;
		case Nitwit:
			return MobHunting.getConfigManager().nitwitLevel1;
		case Vex:
			return MobHunting.getConfigManager().vexLevel1;
		case Vindicator:
			return MobHunting.getConfigManager().vindicatorLevel1;
		case ZombieHorse:
			return MobHunting.getConfigManager().zombieHorseLevel1;
		case SkeletonHorse:
			return MobHunting.getConfigManager().skeletonHorseLevel1;
		}
		return 100;
	}

	public boolean matches(Entity entity) {
		if (Misc.isMC111())
			if (this == Llama)
				return entity instanceof org.bukkit.entity.Llama;
			else if (this == Evoker)
				return entity instanceof org.bukkit.entity.Evoker;
			else if (this == Vindicator)
				return entity instanceof org.bukkit.entity.Vindicator;
			else if (this == Stray)
				return entity instanceof org.bukkit.entity.Stray;
			else if (this == Husk)
				return entity instanceof org.bukkit.entity.Husk;
			else if (this == Mule)
				return entity instanceof org.bukkit.entity.Mule;
			else if (this == Donkey)
				return entity instanceof org.bukkit.entity.Donkey;
			else if (this == Husk)
				return entity instanceof org.bukkit.entity.Husk;
			else if (this == ZombieHorse)
				return entity instanceof org.bukkit.entity.ZombieHorse;
			else if (this == SkeletonHorse)
				return entity instanceof org.bukkit.entity.SkeletonHorse;
			else if (this == ElderGuardian)
				return entity instanceof org.bukkit.entity.ElderGuardian;
			else if (this == Villager)
				return entity instanceof org.bukkit.entity.Villager;
			else if (this == Zombie)
				return entity instanceof org.bukkit.entity.Zombie && ((Zombie) entity).getVillagerProfession() == null;
			else if (this == ZombieVillager)
				return entity instanceof ZombieVillager;
					//&& ((ZombieVillager) entity).getVillagerProfession() == null;

		// else if (this == Zombie)
		// return entity instanceof org.bukkit.entity.Zombie
		// && ((Zombie) entity).getVillagerProfession() == null;

		// else if (this == Nitwit)
		// return entity instanceof org.bukkit.entity.ZombieVillager &&
		// ((org.bukkit.entity.ZombieVillager) entity).);
		// TODO: Villagers: Nitwit, Cartographer, Buther, Liberian, Farmer

		if (Misc.isMC110OrNewer())
			if (this == PolarBear)
				return entity instanceof org.bukkit.entity.PolarBear;
			else if (this == Stray)
				return entity instanceof Skeleton && (((Skeleton) entity).getSkeletonType() == SkeletonType.STRAY);
			else if (this == Husk)
				return entity instanceof Zombie && ((Zombie) entity).getVillagerProfession() == Profession.HUSK;
			else if (this == Zombie)
				return entity instanceof Zombie && (((Zombie) entity).getVillagerProfession() == Profession.NORMAL
						|| ((Zombie) entity).getVillagerProfession() == null);
		if (Misc.isMC19OrNewer())
			if (this == Shulker)
				return entity instanceof org.bukkit.entity.Shulker;

		if (Misc.isMC18OrNewer())
			if (this == KillerRabbit)
				return entity instanceof Rabbit && (((Rabbit) entity).getRabbitType()) == Rabbit.Type.THE_KILLER_BUNNY;
			else if (this == PassiveRabbit)
				return entity instanceof Rabbit && (((Rabbit) entity).getRabbitType()) != Rabbit.Type.THE_KILLER_BUNNY;
			else if (this == ElderGuardian) {
				return (entity instanceof org.bukkit.entity.Guardian)
						&& (((org.bukkit.entity.Guardian) entity).isElder());
			}

		// MC 1.7.10 and older entities
		if (this == WitherSkeleton)
			return entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType() == SkeletonType.WITHER;
		else if (this == Skeleton)
			return entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType() == SkeletonType.NORMAL;
		else if (this == Zombie)
			return entity instanceof Zombie;
		else if (this == BonusMob)
			return entity.hasMetadata("MH:hasBonus");
		else
			return entity.getType().toString().equals(mType);
	}

	public String getFriendlyName() {
		return Messages.getString("mobs." + name() + ".name");
	}

	public static MinecraftMob getExtendedMobType(Entity entity) {
		for (MinecraftMob type : values())
			if (type.matches(entity))
				return type;
		Bukkit.getLogger().severe("ERROR!!! - Unhandled Entity: " + entity.getName() + "(" + entity.getCustomName()
				+ ") Type:" + entity.getType().toString());
		return null;
	}

	public static MinecraftMob getExtendedMobType(String name) {
		for (MinecraftMob type : values())
			if (type.getFriendlyName().replace(" ", "_").equalsIgnoreCase(name)
					|| type.getDisplayName().replace(" ", "_").equalsIgnoreCase(name)
					|| type.name().equalsIgnoreCase(name))
				return type;
		return null;
	}

	public String getCommandString() {
		switch (this) {
		case PvpPlayer:
		case Blaze:
		case CaveSpider:
		case Chicken:
		case Cow:
		case Creeper:
		case Enderman:
		case Ghast:
		case IronGolem:
		case MagmaCube:
		case Slime:
		case MushroomCow:
		case Ocelot:
		case Pig:
		case Sheep:
		case PassiveRabbit:
		case Shulker:
		case Skeleton:
		case Spider:
		case Squid:
		case WitherSkeleton:
		case Zombie:
		case ZombiePigman:
			return "minecraft:give {player} skull {amount} 3 {SkullOwner:\"{playername}\",display:{Name:\"{displayname}\",Lore:[{lore}]}}";
		default:
			return "minecraft:give {player} skull {amount} 3 {SkullOwner:{Id:\"{playerid}\",Properties:{textures:[{Value:\"{texturevalue}\"}]}},display:{Name:\"{displayname}\",Lore:[{lore}]}}";
		}
	}

	public ItemStack getHead(String name) {
		switch (this) {
		case Skeleton:
			return new ItemStack(Material.SKULL_ITEM, 1, (short) 0);
		case WitherSkeleton:
			return new ItemStack(Material.SKULL_ITEM, 1, (short) 1);
		case Zombie:
			return new ItemStack(Material.SKULL_ITEM, 1, (short) 2);
		case PvpPlayer:
			if (name != null)
				return getSkullFromOtherPlayer(name);
			else
				return new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		case Creeper:
			return new ItemStack(Material.SKULL_ITEM, 1, (short) 4);
		default:
			return getSkullFromPlayerName();
		}
	}

	private ItemStack getSkullFromPlayerName() {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(mPlayerProfile);
		skull.setItemMeta(skullMeta);
		return skull;
	}

	/**
	 * @return the mPlayerProfile
	 */
	public String getPlayerProfile() {
		return mPlayerProfile;
	}

	private ItemStack getSkullFromOtherPlayer(String name) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(name);
		skull.setItemMeta(skullMeta);
		return skull;
	}

	public CharSequence getTextureValue() {
		return mTexture;
	}

	public CharSequence getPlayerId() {
		return mPlayerId;
	}

}