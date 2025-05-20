//? #version 330 core
#define PI 3.1415926535897932384626433832795

/* Distances Struct */

struct Distances {
    float closerDistance;
    float fartherDistance;
};

/* Functions */

float max3(vec3 v) {
  return max(max(v.x, v.y), v.z);
}

float componentSum(vec3 v) {
    return v.x + v.y + v.z;
}

float square(float x) {
    return x * x;
}

vec3 squareComponents(vec3 v) {
    v.x = v.x * v.x;
    v.y = v.y * v.y;
    v.z = v.z * v.z;

    return v;
}

vec3 powComponents(vec3 v, float power) {
    v.x = pow(v.x, power);
    v.y = pow(v.y, power);
    v.z = pow(v.z, power);

    return v;
}

Distances calculateDistances(float A, float B, float C) {
    float p = B / A;
    float q = C / A;
    
    if(square(p / 2) - q >= 0) {
        float closerDistance;
        float fartherDistance;
        
        if(A == 0 || A == -0) {
            closerDistance = fartherDistance = -C / B;
        } else {
            int bSign;
            float k;
            
            if(B > 0) bSign = 1;
            if(B < -0) bSign = -1;

            k = (-B - bSign * sqrt(square(B) - 4 * A * C)) / 2;
            closerDistance = C / k;
            fartherDistance = k / A;
        }

        return Distances(closerDistance, fartherDistance);
    } else {
        return Distances(-1, -1);
    }
}