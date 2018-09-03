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
 - In the View Properties (N) set the Grid Scale to 0.0625 (16) or 0.03125 (32) then snapping vertexes in edit mode will work nicely (magnet)
   The subvisions are only visible in ortho view mode (toggle with Numpad 5) and then use one of the ortho views.

Some helpfull blender tips:
 - press T and Set Origin to change objects center point for rotation
 - press G + X,Y,Z to move a face
 - press R + X,Y,Z to rotate an object

UV Editing (is pain in blender)
 - in 3D View screen switch to edit mode, select a face and click Mesh -> UV Map -> Reset
 - switch to UV editor panel
 - turn snap to grid off (magnet) but select UV -> Snap to pixels
 - select the texture image
 - click on each vertex and move as desired (or edit manually in N menu)
