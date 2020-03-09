#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;

out vec2 outTexCoord;
out vec3 outVertexNormal;
out vec3 outVertexPos;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    vec4 pos = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * pos;
    outTexCoord = texCoord;
    outVertexNormal = normalize(modelViewMatrix * vec4(vertexNormal, 0.0)).xyz;
    outVertexPos = pos.xyz;
}