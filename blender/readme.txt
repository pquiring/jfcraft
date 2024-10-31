jfCraft entities are now Blender files which are then converted to jf3d files to load in game.

To edit these files in Blender:

 - Unzip base.zip and jfassets.zip into this folder (assets folder should be in this folder)

 - Unzip assets folder for each plugins\*.jar into this folder

 - In Blender user preferences:
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
   The subvisions are only visible in ortho view mode (toggle with Numpad 5) and then use one of the ortho views (numpad 7).

Some helpfull blender tips:
 - press G + X,Y,Z to move an object
 - press S + X,Y,Z to scale an object
 - press R + X,Y,Z to rotate an object

UV Editing (is pain in blender)
 - switch to UV editor panel (left:Image Editor, right:3D Viewport)
 - right side : switch to edit mode (TAB), set select mode to face, select a face and click UV -> Reset
 - right side : turn snap to grid off (magnet)
 - left side : select the texture image
 - left side : switch to UV editor
 - left side : select UV sync selection
 - left side : select show overlays (Shift Alt Z)
 - right side : click on each face (use face mode)
 - left side : click (N) to show UV Vertex : move as desired (or edit manually)
 - left side : use (G) to move or (S) + (X,Y) to scale on X or Y axis (use 3D cursor as scale point)
 - left side : when done right click and select snap -> selected to pixels

Changing object pivot point:
 - switch to Object Mode (TAB)
 - press (N) then View tab
 - edit 3D Cursor Location
 - click on Object to select it
 - click on menu Object -> Set Origin -> Origin to 3D Cursor
