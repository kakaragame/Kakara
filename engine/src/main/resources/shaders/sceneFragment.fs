#version 400

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

in vec2 outTexCoord;
in vec3 outVertexNormal;
in vec3 outVertexPos;

out vec4 fragColor;

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

struct PointLight
{
    vec3 color;
    vec3 position;
    float intensity;
    Attenuation att;
};

struct SpotLight
{
    PointLight pl;
    vec3 conedir;
    float cutoff;
};

struct DirectionalLight
{
    vec3 color;
    vec3 direction;
    float intensity;
};

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;

    sampler2D overlayTextures[5];
    int numberOfOverlays;
};

// Handles the settings for the fog
//struct Fog
//{
   // int activeFog;
   // vec3 color;
    //float density;
//};

uniform sampler2D texture_sampler;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform DirectionalLight directionalLight;

//uniform Fog fog;


vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material, vec2 textCoord){
    if(material.hasTexture == 1){
        ambientC = texture(texture_sampler, textCoord);
        diffuseC = ambientC;
        specularC = ambientC;
    }else{
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}


//vec4 calcFog(vec3 pos, vec4 color, Fog fog, DirLight dirLight);

vec4 calcLightColor(vec3 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specColor = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColor = diffuseC * vec4(light_color, 1.0) * light_intensity * diffuseFactor;

    // Specular Light
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir , normal));
    float specularFactor = max( dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColor = specularC * light_intensity  * specularFactor * material.reflectance * vec4(light_color, 1.0);

    return (diffuseColor + specColor);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec4 light_color = calcLightColor(light.color, light.intensity, position, to_light_dir, normal);

    // Apply Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance +
        light.att.exponent * distance * distance;
    return light_color / attenuationInv;
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.pl.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec3 from_light_dir  = -to_light_dir;
    float spot_alfa = dot(from_light_dir, normalize(light.conedir));

    vec4 color = vec4(0, 0, 0, 0);

    if ( spot_alfa > light.cutoff )
    {
        color = calcPointLight(light.pl, position, normal);
        color *= (1.0 - (1.0 - spot_alfa)/(1.0 - light.cutoff));
    }
    return color;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}


void main()
{
    //CalcDiffAndSpec();

    setupColors(material, outTexCoord);

    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, outVertexPos, outVertexNormal);

        for (int i=0; i<MAX_POINT_LIGHTS; i++)
        {
            if ( pointLights[i].intensity > 0 )
            {
                diffuseSpecularComp += calcPointLight(pointLights[i], outVertexPos, outVertexNormal);
            }
        }

        for (int i=0; i<MAX_SPOT_LIGHTS; i++)
        {
            if ( spotLights[i].pl.intensity > 0 )
            {
                diffuseSpecularComp += calcSpotLight(spotLights[i], outVertexPos, outVertexNormal);
            }
        }

        fragColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;

    //fragColor = result;

     //if ( fog.activeFog == 1 )
        //{
            //fragColor = calcFog(vecViewPos, fragColor, fog, dirLight);
        //}
}

//Calculate the fog in the scene.
//vec4 calcFog(vec3 pos, vec4 color, Fog fog, DirLight dirLight)
//{
    //vec3 fogColor = fog.color * (dirLight.ambient);
    //float distance = length(pos);
    //float fogFactor = 1.0 / exp( (distance * fog.density)* (distance * fog.density));
    //fogFactor = clamp( fogFactor, 0.0, 1.0 );

    //vec3 resultColor = mix(fogColor, color.xyz, fogFactor);
    //return vec4(resultColor.xyz, color.w);
//}