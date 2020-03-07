#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;

out vec2 outTexCoord;
out vec3 vecNormal;
out vec3 vecPos;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

void main()
{
    vec4 fragPos = modelMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * fragPos;
    outTexCoord = texCoord;
    vecNormal = normalize(modelMatrix * vec4(vertexNormal, 0.0)).xyz;
    vecPos = fragPos.xyz;
}