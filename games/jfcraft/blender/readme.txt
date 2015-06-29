jfCraft entities are now Blender files which are then converted to jf3d files to load in game.

To edit these files:

 - Unzip base.zip and jfassets.zip into this folder (assets folder should be in this folder)

 - Unzip assets folder for each plugins\*.jar into the folder

 - In user preferences:
   - under input tab change orbit mode to trackball (to make rotating with Y up easier)
   - under file tab set texture path to this folder (NOTE: It's case sensitive - even in Windows)

Then you can work with these files.
Use convert.bat to convert blend files to jf3d files used in the game, and rerun ant in the main folder.

When creating a new model:
 - under object properties (N) under Shading check 'Texture Solid' and 'Backface Culling'.

Notes:
 - You may notice some models have the wrong texture used (portal, fire, etc.) that's because the texture is animated
   so I needed to use something that's square.  The texture names are not used in game anyways.
 - Set the Grid Subvisions to 16 or 32 and then snapping vertexes in edit mode will work nicely.
   The subvisions are only visible in ortho view mode (toggle with Numpad 5) and then use one of the ortho views.
