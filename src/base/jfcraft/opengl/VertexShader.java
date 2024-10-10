package jfcraft.opengl;

/** Vertex Shader
 *
 * @author pquiring
 *
 * Created : Sept 17, 2013
 */

/*
 * uniform = set with glUniform...()
 * attribute = points to input array with glVertexAttribPointer()
 * varying = passed from vertex shader to fragment shader (shared memory)
 */

public class VertexShader {
  public static String source =
"#version 330\n" +
"attribute vec2 aTextureCoord;\n" +
"attribute vec2 aTextureCoord2;\n" +  //used to show "cracking" overlay
"attribute vec3 aVertexPosition;\n" +
"attribute float aSunLightPercent;\n" +
"attribute float aBlockLightPercent;\n" +
"attribute vec3 aLightColor;\n" +
"\n" +
"uniform mat4 uPMatrix;\n" +
"uniform mat4 uVMatrix;\n" +
"uniform mat4 uMMatrix;\n" +
"\n" +
"varying vec2 vTextureCoord;\n" +
"varying vec2 vTextureCoord2;\n" +
"varying float vLength;\n" +
"varying float vSunLightPercent;\n" +
"varying float vBlockLightPercent;\n" +
"varying vec3 vLightColor;\n" +
"\n" +
"void main() {\n" +
"  gl_Position = uPMatrix * uVMatrix * uMMatrix * vec4(aVertexPosition, 1.0);\n" +  //NOTE:order of matrix multiply matters
"  vTextureCoord = aTextureCoord;\n" +
"  vTextureCoord2 = aTextureCoord2;\n" +
"  vSunLightPercent = aSunLightPercent;\n" +
"  vBlockLightPercent = aBlockLightPercent;\n" +
"  vLightColor = aLightColor;\n" +
"  vLength = length(gl_Position);\n" +
"}\n";
}
