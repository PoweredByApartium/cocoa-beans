# 👥 DisplayTeam

## Introduction

The `DisplayTeam` api is a packet-level NMS-driven team display controller designed for Minecraft. It integrates seamlessly with UI systems by leveraging Observable state objects to ensure efficient updates across player clients.

Unlike typical Bukkit or API-level teams, `DisplayTeam` is optimized for direct manipulation via packets, making it extremely responsive and suitable for custom teams, overlays, or nametag modifications in real time.
While giving you more free control over who see your teams

## Showcase

![team-tab-priority.png](team-tab-priority.png)

<sub><a href="#sorting-tab-list">Sorting Tab-list</a></sub>

![image.png](image.png)

<sub>Ability to set viewers</sub>

![tab-animation.gif](tab-animation.gif)

<sub>Animation support using Observable</sub>

## Usage

I will show how to use it in Spigot here
**Beware** that you will need to create your own manager and decide how to manage teams
I will keep it simple here is simple manager

<code-block lang="java" src="scoreboard-spigot/CodeSnippets.java" include-symbol="TeamManager"/>

Now how will we create simple ranks prefix in tab and next to name

<sub>For this a example I will just create a simple enum for ranks (Please use LuckPerm or other permission base system)</sub>

<code-block lang="java" src="scoreboard-spigot/CodeSnippets.java" include-symbol="Rank"/>

<code-block lang="java" src="scoreboard-spigot/CodeSnippets.java" include-symbol="createRankTeams"/>

<sub>We will also need to added them as viewer & make them join a team as entity</sub>

<code-block lang="java" src="scoreboard-spigot/CodeSnippets.java" include-symbol="onJoin"/>


## Sorting Tab-list

In Minecraft, the **tab list** <sub>(player list)</sub> can be sorted using teams, where the team name determines the sort priority.

### 🔍 How Tab List Sorting Works

Minecraft sorts players in the tab list based on their **team name in lexicographical (alphabetical) order**. Players on teams with names that come earlier in the alphabet will appear higher in the list.

To control priority using numbers, you can **prefix team names with numbers**, such as `"0001_team"`, `"0002_team"`, etc. Lower numbers = higher priority in the tab list.

### Code snippet

<code-block lang="java" src="scoreboard-spigot/CodeSnippets.java" include-symbol="formatTeamName"/>

### Notes
For **LuckPerms** we would like to invert the number because there priority go up
so we will do as follow `9999 - priority` typical priority is lower than 10,000 if it's higher we can change are code