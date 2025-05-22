# OpenGL-Realtime-RT
This Java application renders raytraced 3D objects with reflections, refractions and shadows in realtime.
The rendering is handled through software raytracing via OpenGL.

<p align="left">
  <img src="images/Lamp_Room.png" width="45%" alt="Screenshot of a room with mirrors, a brick wall, a reflective wood floor and a ceiling lamp."/>
  <img src="images/Refraction.png" width="45%" alt="Screenshot of a orange sphere and a glass sphere through which the refracted view of a green sphere is seen."/>
  <img src="images/CSG0.png" width="45%" alt="Screenshot of a thick circle with a hole throwing a shadow on a red and green 3D shape."/>
  <img src="images/CSG1.png" width="45%" alt="Screenshot of a yellow and purple 3D shape, a pole like 3D shape and a sphere."/>
</p>

If you want to try the program out without downloading the repository and compiling the program,
you can download [this zip archive](https://1drv.ms/u/c/a225810b411f051b/EaiviYQSUQpLkFYNQyHDpO4BTcS41JS3FmwQuADKWUgudw?e=4lzzD3) extract it and run the executable contained within.
If you want to compile the program yourself, you will need a Java 17 JDK.

# Controls



Thanks go to my computer graphics teacher [Prof. Dr. Tobias Lenz](https://www.htw-berlin.de/hochschule/personen/person/?eid=9042) from the HTW, for getting me into computer graphics and helping me realizing the project.
Thanks as well to my friend [Bernd Reusch](https://www.linkedin.com/in/bernd-reusch/) for helping to implement the [WavefrontObjectReader class](https://github.com/Tizcommand/OpenGL-Realtime-RT/blob/main/src/io/WavefrontObjectReader.java).
