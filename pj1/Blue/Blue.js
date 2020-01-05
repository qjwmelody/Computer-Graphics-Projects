var sound, amp, fft, img, vid;

function preload() {
  sound = loadSound('Beautiful.mp3');
}

function setup() {
  createCanvas(windowWidth, windowHeight,WEBGL);
  sound.play();
  fft = new p5.FFT(0.3,16);
  amp = new p5.Amplitude(0.5);
  amp.setInput(sound);
  img = loadImage("2.png");
}

function draw() {
  background(15,76,129);
  
  // Get Spectrum
  var spectrum = fft.analyze();
  for (var i=0; i<spectrum.length; i++) {
    var barWidth = width / spectrum.length;
    var xPos = barWidth * i; 
    noStroke();
    fill(255,0,0);
    ellipse(xPos-(width/2)+100,spectrum[i],10,10);
  }

  // Get Volume
  var volMain = amp.getLevel(); 
  var volLeft = amp.getLevel(0);
  var volRight = amp.getLevel(1);
  
  noStroke();
  fill(106,90,205);
  
  
  // Center Circle
  ellipse(0,0,volMain * 1000, volMain* 1000);
  ellipse(-500,0,volLeft * 500,volLeft * 500);
  ellipse(500,0,volRight * 500,volRight * 500);
  
  noStroke();
  fill(106,90,205);
  
  ellipse(0,0,volMain * 1000, volMain* 1000);
  ellipse(-500,0,volLeft * 100,volLeft * 100);
  ellipse(+500,0,volRight * 100,volRight * 100);  
  
  stroke(106,90,205);
  rotateX(frameCount*0.01);
  rotateY(frameCount*0.01);
  texture(img);
  sphere(volMain*400,20,20);
  
  
  // 3D Boxes
  noFill();
  strokeWeight(2);
  stroke(106,90,205);
  rotateX(frameCount*0.01);
  rotateY(frameCount*0.01);
  box(200);
  
  noFill();
  strokeWeight(2);
  stroke(106,90,205);
  rotateX(frameCount*0.015);
  rotateY(frameCount*0.015);
  box(200);
  
  noFill();
  strokeWeight(2);
  stroke(106,90,205);
  rotateX(frameCount*0.012);
  rotateY(frameCount*0.012);
  box(200);
  
  texture(img);
  rotateX(frameCount*0.01);
  rotateY(frameCount*0.01);
  sphere(100,50,10);
  sphere(100,50,30);
}
