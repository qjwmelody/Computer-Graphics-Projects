import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 
import ddf.minim.effects.*; 
import ddf.minim.signals.*; 
import ddf.minim.spi.*; 
import ddf.minim.ugens.*; 
import ddf.minim.*; 
import ddf.minim.analysis.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class lizi extends PApplet {










//import java.util.Random;

Minim minim;
AudioPlayer song;
BeatDetect beat;
//FFT fft;

PSystem system;
PVector gravity=new PVector(0,0.1f);
Repeller r;
boolean move;


float movieFPS = 30;
float soundDuration = 60.03f;

public void setup(){
  background(0); 
  
  system=new PSystem(new PVector(width/2,height/2));
  r=new Repeller(width/2,height/2);
  
  minim=new Minim(this);
  // song = minim.loadFile("Beautiful.mp3");
  song = minim.loadFile("Huahuo.mp3");
  // song = minim.loadFile("Draw Me A Sheep.mp3");
  // song = minim.loadFile("Liangzhu.mp3");
  // song = minim.loadFile("Without You I Am Dying.mp3");
  song.play();
  //fft = new FFT(song.bufferSize(), song.sampleRate());
  beat = new BeatDetect();
  beat.setSensitivity(50); 
  move=false;

}


public void draw(){
  background(30);
  beat.detect(song.mix);
  if(beat.isOnset()){
    //fft.forward(song.mix);
    //float a=fft.getBand(500);
    float b=song.mix.level();
    println(b);
    system.applyOut(r,b);
  }
  
  system.applyIn(r); 
  system.applyFric(r);
  system.applyOutAll(r);
  system.run(); 
  
  r.display();
  if(move){
    r.move(mouseX,mouseY);
  }

}

public void mousePressed(){
  move = !move;
}

class PSystem{
  PVector center;
  ArrayList<Particle> ps;
  
  PSystem(PVector v){
    center=v.get();
    ps=new ArrayList<Particle>();
    for(int i=0;i<8000;i++){
      //Random num=new Random();
      float angle=random(TWO_PI);
      //float dis=120+30*(float)num.nextGaussian();
      float dis=120+30*randomGaussian();
      float x=dis*sin(angle)+center.x;
      float y=dis*cos(angle)+center.y;
      ps.add(new Particle(new PVector(x,y)));
    }
  }
   
  public void applyFric(Repeller r){
    for(Particle p: ps){
      PVector f=r.fric(p);
      p.applyForce(f);
    }
  }
  
  public void applyOut(Repeller r,float b){
    for(Particle p: ps){
//      PVector dist=PVector.sub(r.location,p.location);
//     if(dist.mag()<r.dim){
//       b=0.12;
//      }
      PVector f=r.out(p,b);
      p.applyForce(f);
    }
  }
  
  public void applyOutAll(Repeller r){
    for(Particle p: ps){
        PVector f=r.outAll(p);
         p.applyForce(f);
      }
  }
    
  
  public void applyIn(Repeller r){
    for(Particle p: ps){
      PVector f=r.in(p);
       p.applyForce(f);
     }
  }
    
  public void run(){
    for(int i=ps.size()-1;i>=0;i--){
      Particle p=ps.get(i);
      p.run();
    }
  }
}
  
class Particle{
  PVector location;
  PVector velocity;
  PVector acceleration;
  float mass;

  Particle(PVector l){
    location=l.get();
    velocity= new PVector(0,0);
    acceleration= new PVector();
    mass=random(0.5f,1);
  }
  
  public void run(){
    update();
    display();
  }
  
  
  public void applyForce(PVector force){
    PVector f=PVector.div(force,mass);
    acceleration.add(f);
  }
  
  public void update(){
    velocity.add(acceleration);
    location.add(velocity);
    acceleration.set(0,0);
  }
  
  public void display(){
    stroke(255,random(100,255));
    strokeWeight(2);
    point(location.x,location.y);
  }
 
}

class Repeller{
  PVector location;
  float dim=20;
  
  Repeller(float x,float y){
    location=new PVector(x,y);
  }
  
  public void move(float mousex,float mousey){
    PVector mouse=new PVector(mousex,mousey);
    location.lerp(mouse,0.05f);
  }
  
  public PVector out(Particle p,float b){
    PVector dir=PVector.sub(location,p.location);
    float d=constrain(dir.mag(),50,300);
    dir.normalize();
    //Random generator =new Random();
    //float a=(float)generator.nextGaussian();
    float a=randomGaussian();
    a=1.5f+0.8f*a;
    a=constrain(a,0.2f,3);
    
    b=constrain(b,0.12f,0.6f);    
    b=map(b,0.12f,0.6f,0.5f,2.5f);
    float force=-1*500/(d)*a*b;
    dir.mult(force);
    return dir;
  }
  
  public PVector in(Particle p){
    PVector dir=PVector.sub(location,p.location);
    float d=constrain(dir.mag(),20,800);
    d=map(d,5,300,10,300);
    dir.normalize();
    float force=0.001f*d*p.mass;
    dir.mult(force);
    return dir;
  }
  
  public PVector outAll(Particle p){
    PVector dir=PVector.sub(location,p.location);
    float d=constrain(dir.mag(),5,100);
    dir.normalize();
    float force=-150/(d*d);
    dir.mult(force);
    return dir;
  }
        
  public PVector fric(Particle p){
    //PVector dir=PVector.sub(location,p.location);
    //float c =dir.mag();
    float c=0.01f;
    PVector fric=p.velocity.get();
    float speed=fric.mag();
    c=c*speed*speed;
    fric.mult(-1);
    fric.normalize();
    return fric.mult(c);
  }
  
  public void display(){
    noStroke();
    fill(255);
    ellipse(location.x,location.y,dim,dim);
  }
}
  public void settings() {  size(1200,800,P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "lizi" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
