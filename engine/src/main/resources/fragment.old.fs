#version 330

in  vec2 outTexCoord;
in vec3 vecNormal;
in vec3 vecPos;

out vec4 fragColor;

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
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
#define NR_POINT_LIGHTS 4
uniform PointLight pointLights[NR_POINT_LIGHTS];
// This way textures will work
adsHolder setupMaterialColors(Material material, vec2 textCoord){
    vec4 ambientC = vec4(1.0, 1.0, 1.0, 1.0);
    vec4 diffuseC = vec4(1.0, 1.0, 1.0, 1.0);
    vec4 speculrC = vec4(1.0, 1.0, 1.0, 1.0);
    if (material.hasTexture == 1)
    {
        ambientC = texture(texture_sampler, textCoord);
        diffuseC = ambientC;
        speculrC = ambientC;
    }
    else
    {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        speculrC = material.specular;
    }
    return adsHolder(ambientC, diffuseC, speculrC);
}

// Calculate the Directional Light
vec4 CalculateDirLight(DirLight light, vec3 normal, vec3 viewDir, adsHolder holder){
    vec3 lightDir = normalize(-light.direction);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    //NOTICE: find material and normal.
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.reflectance);
    // combine results
    //vec4 ambient  = light.ambient * holder.ambient;
    vec4 ambient  = holder.ambient;
    //vec4 diffuse  = light.diffuse * diff * holder.diffuse;
    vec4 diffuse  = diff * holder.diffuse;
    //vec4 specular = light.specular * spec * holder.specular;
    vec4 specular = spec * holder.specular;
    return (ambient + diffuse + specular);
}

// Calculate the data for the point light
vec4 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir, adsHolder holder)
{
    vec3 lightDir = normalize(light.position - fragPos);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.reflectance);
    // attenuation
    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance +
  			     light.quadratic * (distance * distance));
    // combine results
    //vec4 ambient  = light.ambient  * holder.ambient;
    vec4 ambient  = holder.ambient;
    //vec4 diffuse  = light.diffuse  * diff * holder.diffuse;
    vec4 diffuse  = diff * holder.diffuse;
    //vec4 specular = light.specular * spec * holder.specular;
    vec4 specular = spec * holder.specular;
    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    return (ambient + diffuse + specular);
}


void main()
{
    vec3 norm = normalize(vecNormal);
    vec3 viewDir = normalize(viewPos - vecPos);

    //absHolder holder = setupMaterialColors(material, outTexCoord);

    vec4 result = CalculateDirLight(dirLight, norm, viewDir, setupMaterialColors(material, outTexCoord));
    for(int i = 0; i < NR_POINT_LIGHTS; i++)
            result += CalcPointLight(pointLights[i], norm, vecPos, viewDir, setupMaterialColors(material, outTexCoord));

    //fragColor = vec4(result, 1.0);
    fragColor = result;
}