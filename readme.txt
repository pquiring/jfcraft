jfCraft/0.27
============

Yet another implementation of the MineCraft game.
Written in Java.
Includes 0% Minecraft source or resources.
Uses the Faithful Texture pack.
Is not compatible with the official game in anyway.

Some parts of the game are complete but much is still left to do.
See todo.txt and bugs.txt for a full list.

The server includes a VoIP communication system.
Hold in the right CTRL key to talk (PTT).

Commands:
---------
/give me item [count]
  - Gives you an item
/tp x y z
  - Teleports you
/time tick
  - Sets current time in ticks (0=midnight 600=6am 1800=6pm etc)
/healme
  - "Heal me I am in need of assistance" - Palley
  - Restores all your health and hunger
/clear
  - Empties your inventory
/fill x1 y1 z1 x2 y2 z2 block
  - fills area with block
/spawn entity
  - spawns entity within your area
/export x1 y1 z1 x2 y2 z2 filename
  - exports area to a blueprint
/import x1 y1 z1 filename [mx | my] [r90 | r180 | r270]
  - import a blueprint

Compiling:
----------
jfCraft is built on top of JavaForce, so check it out first:
  git clone https://github.com/pquiring/javaforce.git
Then checkout jfCraft within it:
  git clone https://github.com/pquiring/jfcraft.git javaforce/games/jfcraft
Compile Javaforce and it's native libraries (see javaforce/readme.txt)
  cd javaforce
  ant
  cd javaforce\native\windows
  ant
Then build jfCraft.
  cd javaforce\games\jfcraft
  ant

This is alpha software so please do not open bug requests.

Thanks.

By : Peter Quiring (pquiring@gmail.com)
Web : http://pquiring.github.io/jfcraft
Source : https://github.com/pquiring/jfcraft
