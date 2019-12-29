#version 330

in vec2 outTexCoord;
in vec3 vecNormal;
in vec3 vecPos;

out vec4 fragColor;

struct Material
{
    sampler2D diffuse;
    sampler2D specular;
    int hasTexture;
    float reflectance;
};

//holds Ambient, Diffuse, and Specular values
struct adsHolder{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
};

// Handle directional lighting
struct DirLight {
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

// Handle point lighting
struct PointLight{
    vec3 position;

    float constant;
    float linear;
    float quadratic;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

uniform sampler2D texture_sampler;
uniform DirLight dirLight;
// The position of the camera.
uniform vec3 viewPos;
uniform Material material;
#define NR_POINT_LIGHTS 1
uniform PointLight pointLights[NR_POINT_LIGHTS];

// Calculate the Directional Light
vec3 CalcDirLight(DirLight light, vec3 normal, vec3 viewDir)
{
    vec3 lightDir = normalize(-light.direction);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.reflectance);
    // combine results
    vec3 ambient  = light.ambient  * vec3(texture(material.diffuse, outTexCoord));
    vec3 diffuse  = light.diffuse  * diff * vec3(texture(material.diffuse, outTexCoord));
    vec3 specular = light.specular * spec * vec3(texture(material.specular, outTexCoord));
    return (ambient + diffuse + specular);
}

// Calculate the data for the point light
vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    vec3 lightDir = normalize(light.position - fragPos);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.reflectance);
    // attenuation
    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance +
  			     light.quadratic * (distance * distance));
    // combine results
    vec3 ambient  = light.ambient  * vec3(texture(material.diffuse, outTexCoord));
    vec3 diffuse  = light.diffuse  * diff * vec3(texture(material.diffuse, outTexCoord));
    vec3 specular = light.specular * spec * vec3(texture(material.specular, outTexCoord));
    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    return (ambient + diffuse + specular);
}


void main()
{
    vec3 norm = normalize(vecNormal);
    vec3 viewDir = normalize(viewPos - vecPos);

    vec3 result = CalcDirLight(dirLight, norm, viewDir);
    for(int i = 0; i < NR_POINT_LIGHTS; i++)
            result += CalcPointLight(pointLights[i], norm, vecPos, viewDir);

    fragColor = vec4(result, 1.0);
}