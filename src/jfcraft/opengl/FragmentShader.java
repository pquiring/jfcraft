package jfcraft.opengl;

/** Fragment Shader
 *
 * @author pquiring
 *
 * Created : Sept 17, 2013
 */

/*
 * uniform = set with glUniform...()
 * varying = passed from vertex shader to fragment shader (shared memory)
 */

public class FragmentShader {
  public static String source =
"#version 330\n" +
"varying vec2 vTextureCoord;\n" +
"varying vec2 vTextureCoord2;\n" +
"varying float vLength;\n" +
"varying float vSunLightPercent;\n" +
"varying float vBlockLightPercent;\n" +
"varying vec3 vLightColor;\n" +
"\n" +
"uniform float uSunLightNow;\n" +
"uniform float uAlphaFactor;\n" +
"uniform bool uUseTextures;\n" +
"uniform bool uUseFog;\n" +
"uniform vec4 uFogColor;\n" +
"uniform float uFogNear;\n" +
"uniform float uFogFar;\n" +
"uniform bool uUseTint;\n" +
"uniform vec4 uTintColor;\n" +
"uniform bool uUseHorsePattern;\n" +
"uniform bool uUseHorseArmor;\n" +
"\n" +    //there is usually at least 16 texture units available
"uniform sampler2D uTexture;\n" +  //unit 0
"uniform sampler2D uCrack;\n" +  //unit 1
"uniform sampler2D uHorsePattern;\n" +  //unit 2
"uniform sampler2D uHorseArmor;\n" +  //unit 3
"\n" +
"void main() {\n" +
"  float sunPercent = vSunLightPercent * uSunLightNow;\n" +
"  float percent = max(sunPercent, vBlockLightPercent);\n" +
"  percent = max(percent, 0.01);\n" +  //min lighting
"  vec3 color = vLightColor * percent;\n" +
"  vec4 textureColor;" +
"  vec4 textureColor2;" +
"  if (uUseTextures) {\n" +
"    textureColor = texture2D(uTexture, vTextureCoord);\n" +
"    if (textureColor.a == 0.0) discard;\n" +
"    if (vTextureCoord2.x != 0.0 && vTextureCoord2.y != 0.0) {\n" +
"      textureColor2 = texture2D(uCrack, vTextureCoord2);\n" +
"      if (textureColor2.a != 0.0) {\n" +
"        textureColor = textureColor2;\n" +
"      }\n" +
"    }\n" +
"    if (uUseHorsePattern) {\n" +
"      textureColor2 = texture2D(uHorsePattern, vTextureCoord);\n" +
"      if (textureColor2.a != 0.0) {\n" +
"        float negAlpha = 1.0 - textureColor2.a;" +
"        textureColor.rgb = textureColor.rgb * negAlpha + textureColor2.rgb * textureColor2.a ;\n" +
"      }\n" +
"    }\n" +
"    if (uUseHorseArmor) {\n" +
"      textureColor2 = texture2D(uHorseArmor, vTextureCoord);\n" +
"      if (textureColor2.a != 0.0) {\n" +
"        float negAlpha = 1.0 - textureColor2.a;" +
"        textureColor.rgb = textureColor.rgb * negAlpha + textureColor2.rgb * textureColor2.a ;\n" +
"      }\n" +
"    }\n" +
"    textureColor.rgb *= color.rgb;\n" +
"  } else {\n" +
"    textureColor.rgb = color.rgb;\n" +
"    textureColor.a = 1.0;" +
"  }\n" +
"  textureColor.a *= uAlphaFactor;\n" +
"  if (uUseFog && vLength > uFogNear) {\n" +
"    float fogFactor;\n" +
"    fogFactor = clamp((vLength - uFogNear) / (uFogFar - uFogNear), 0.0, 1.0);\n" +    //simple linear fog
"    textureColor = mix(textureColor, uFogColor, fogFactor);\n" +
"  }\n" +
"  if (uUseTint) {\n" +
"    textureColor = mix(textureColor, uTintColor, 1.0f);" +
"  }" +
"  gl_FragColor = textureColor;\n" +
"}\n";
}
