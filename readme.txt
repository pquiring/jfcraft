jfCraft/0.29
============

Yet another implementation of the MineCraft game.
Written in Java.
Includes 0% Minecraft source or resources.
Uses the Faithful Texture pack.
Is not compatible with the official game in anyway.

The server includes a VoIP push to talk (PTT) communications system.

Controls:
---------
A/S/D/W = move character
SPACE = jump
LEFT_CTRL = run
LEFT_SHIFT = sneak
E = inventory
RIGHT_CTRL = talk (if PTT enabled)
T = type message (chat)
C = toggle creative mode (if enabled)
R/F = fly up/down when in creative mode (no clipping)
mouse = look around
B1 = use item in hand
B2 = use block / hold up shield
wheel = select item
1-9 = select item
Q = drop item

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

Downloading:
------------
Windows installer is available at http://jfcraft.sourceforge.net

Assets:
-------
You can override assets by placing them in %APPDATA%\.jfcraft\assets

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
