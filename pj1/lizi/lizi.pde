import ddf.minim.*;
import ddf.minim.analysis.*;
//import java.util.Random;

Minim minim;
AudioPlayer song;
BeatDetect beat;
//FFT fft;

PSystem system;
PVector gravity=new PVector(0, 0.1);
Repeller r;
boolean move;

float movieFPS = 30;
float soundDuration = 60.03;

void setup() {
  background(0); 
  size(1920,1080);
  //size(1200, 800, P2D);
  system=new PSystem(new PVector(width/2, height/2));
  r=new Repeller(width/2, height/2);

  minim=new Minim(this);
  // song = minim.loadFile("Beautiful.mp3");
  song = minim.loadFile("Huahuo.mp3");
  // song = minim.loadFile("Draw Me A Sheep.mp3");
  // song = minim.loadFile("Liangzhu.mp3");
  //song = minim.loadFile("Without You I Am Dying.mp3");
  song.play();
  //fft = new FFT(song.bufferSize(), song.sampleRate());
  beat = new BeatDetect();
  beat.setSensitivity(50); 
  move=false;
}

void draw() {
  background(30);
  beat.detect(song.mix);
  if (beat.isOnset()) {
    //fft.forward(song.mix);
    //float a=fft.getBand(500);
    float b=song.mix.level();
    println(b);
    system.applyOut(r, b);
  }

  system.applyIn(r); 
  system.applyFric(r);
  system.applyOutAll(r);
  system.run(); 

  r.display();
  if (move) {
    r.move(mouseX, mouseY);
  }
}

void mousePressed() {
  move = !move;
}


class PSystem {
  PVector center;
  ArrayList<Particle> ps;

  PSystem(PVector v) {
    center=v.get();
    ps=new ArrayList<Particle>();
    for (int i=0; i<8000; i++) {
      //Random num=new Random();
      float angle=random(TWO_PI);
      //float dis=120+30*(float)num.nextGaussian();
      float dis=120+30*randomGaussian();
      float x=dis*sin(angle)+center.x;
      float y=dis*cos(angle)+center.y;
      ps.add(new Particle(new PVector(x, y)));
    }
  }

  void applyFric(Repeller r) {
    for (Particle p : ps) {
      PVector f=r.fric(p);
      p.applyForce(f);
    }
  }

  void applyOut(Repeller r, float b) {
    for (Particle p : ps) {
      PVector f=r.out(p, b);
      p.applyForce(f);
    }
  }

  void applyOutAll(Repeller r) {
    for (Particle p : ps) {
      PVector f=r.outAll(p);
      p.applyForce(f);
    }
  }

  void applyIn(Repeller r) {
    for (Particle p : ps) {
      PVector f=r.in(p);
      p.applyForce(f);
    }
  }

  void run() {
    for (int i=ps.size()-1; i>=0; i--) {
      Particle p=ps.get(i);
      p.run();
    }
  }
}


class Particle {
  PVector location;
  PVector velocity;
  PVector acceleration;
  float mass;

  Particle(PVector l) {
    location=l.get();
    velocity= new PVector(0, 0);
    acceleration= new PVector();
    mass=random(0.5, 1);
  }

  void run() {
    update();
    display();
  }

  void applyForce(PVector force) {
    PVector f=PVector.div(force, mass);
    acceleration.add(f);
  }

  void update() {
    velocity.add(acceleration);
    location.add(velocity);
    acceleration.set(0, 0);
  }

  void display() {
    stroke(255, random(100, 255));
    strokeWeight(2);
    point(location.x, location.y);
  }
}


class Repeller {
  PVector location;
  float dim=20;

  Repeller(float x, float y) {
    location=new PVector(x, y);
  }

  void move(float mousex, float mousey) {
    PVector mouse=new PVector(mousex, mousey);
    location.lerp(mouse, 0.05);
  }

  PVector out(Particle p, float b) {
    PVector dir=PVector.sub(location, p.location);
    float d=constrain(dir.mag(), 50, 300);
    dir.normalize();
    //Random generator =new Random();
    //float a=(float)generator.nextGaussian();
    float a=randomGaussian();
    a=1.5+0.8*a;
    a=constrain(a, 0.2, 3);

    b=constrain(b, 0.12, 0.6);    
    b=map(b, 0.12, 0.6, 0.5, 2.5);
    float force=-1*500/(d)*a*b;
    dir.mult(force);
    return dir;
  }

  PVector in(Particle p) {
    PVector dir=PVector.sub(location, p.location);
    float d=constrain(dir.mag(), 20, 800);
    d=map(d, 5, 300, 10, 300);
    dir.normalize();
    float force=0.001*d*p.mass;
    dir.mult(force);
    return dir;
  }

  PVector outAll(Particle p) {
    PVector dir=PVector.sub(location, p.location);
    float d=constrain(dir.mag(), 5, 100);
    dir.normalize();
    float force=-150/(d*d);
    dir.mult(force);
    return dir;
  }

  PVector fric(Particle p) {
    float c=0.01;
    PVector fric=p.velocity.get();
    float speed=fric.mag();
    c=c*speed*speed;
    fric.mult(-1);
    fric.normalize();
    return fric.mult(c);
  }

  void display() {
    noStroke();
    fill(255);
    ellipse(location.x, location.y, dim, dim);
  }
}
