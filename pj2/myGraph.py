from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLUT import *
import PIL.Image as Image

import numpy


class myImage():
    def __init__(self):

        self.vertexes = [[-0.5, -0.5, 0], [0.5, -0.5, 0], [0.5, 0.5, 0], [-0.5, 0.5, 0],
                         [-0.5, -0.5, -0.3], [0.5, -0.5, -0.3], [0.5, 0.5, -0.3], [-0.5, 0.5, -0.3]]
        self.coords = [[0.0, 0.0], [1.0, 0.0], [1.0, 1.0], [0.0, 1.0],
                       [0.0, 0.0], [1.0, 0.0], [1.0, 1.0], [0.0, 1.0]]
        self.faces = [[0, 1, 2, 3], [3, 2, 6, 7], [0, 3, 7, 4]]

        self.bindTexture()
        self.light()


    def bindTexture(self):
        # read texture
        img = Image.open("./red.jpg")
        img = numpy.asarray(img, dtype=numpy.uint8)
        self.textures = glGenTextures(1)
        glBindTexture(GL_TEXTURE_2D, self.textures)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, img.shape[0], img.shape[1], 0, GL_RGB, GL_UNSIGNED_BYTE, img)

        # depth test
        glEnable(GL_TEXTURE_2D)
        # set texture
        glBindTexture(GL_TEXTURE_2D, self.textures)


    def light(self):
        light_position = [-0.8, 0.8, 1.1, 1.0]
        glShadeModel(GL_SMOOTH)
        glLightfv(GL_LIGHT0, GL_DIFFUSE, [1.0, 1.0, 1.0, 1.0])
        glLightfv(GL_LIGHT0, GL_POSITION, light_position)
        glEnable(GL_LIGHTING)
        glEnable(GL_LIGHT0)
        glEnable(GL_DEPTH_TEST)
        glDisable(GL_COLOR_MATERIAL)


    def drawWall(self):
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
        glPushMatrix()

        glMatrixMode(GL_MODELVIEW)
        glRotatef(15.0, 0.0, 1.0, 0.0)
        glRotatef(30.0, 1.0, 0.0, 0.0)

        glBegin(GL_QUADS)
        # front
        glNormal3f(0.0, 0.0, 1.0)
        for pt in self.faces[0]:
            glTexCoord2fv(self.coords[pt])
            glVertex3fv(self.vertexes[pt])
        # top
        glNormal3f(0.0, 1.0, 0.0)
        for pt in self.faces[1]:
            glTexCoord2fv(self.coords[pt])
            glVertex3fv(self.vertexes[pt])
        glNormal3f(-1.0, 0.0, 0.0)
        # left
        for pt in self.faces[2]:
            glTexCoord2fv(self.coords[pt])
            glVertex3fv(self.vertexes[pt])
        glEnd()

        glPopMatrix()
        glFlush()


def main():
    glutInit()
    glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB | GLUT_DEPTH)
    glutInitWindowSize(400, 400)
    glutCreateWindow("Realistic Image")
    i = myImage()
    glutDisplayFunc(i.drawWall)
    glutMainLoop()


if __name__ == "__main__":
    main()