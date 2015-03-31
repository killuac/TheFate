/* 
  Grayscale.fsh
  TheFate

  Created by Killua Liu on 4/24/14.
  Copyright (c) 2014 Apportable. All rights reserved.
*/

precision lowp float;

varying vec2 v_texCoord;

uniform sampler2D u_texture;

void main()
{
    vec4 col=texture2D(u_texture,v_texCoord);
    
    col.r=col.g=col.b=(col.r+col.g+col.b)/3.0;
    
    gl_FragColor=col;
}