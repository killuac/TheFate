/* 
  Grayscale.vsh
  TheFate

  Created by Killua Liu on 4/24/14.
  Copyright (c) 2014 Apportable. All rights reserved.
*/

attribute vec4 a_position;

attribute vec2 a_texCoord;

varying vec2 v_texCoord;

void main()
{
    gl_Position=CC_MVPMatrix * a_position;
    
    v_texCoord=a_texCoord;
}