size(1024, 500);

background(#ffffff);

colorMode(HSB);

for (int i = 0; i < 50; ++i) {

  int h = (int)random(-60, 80);
  int s = (int)random(100, 255);
  int b = (int)random(255, 255);
  
  float x = random(0, width);
  float y = random(0, height);
  float rr = random(10, 500);
  
  noStroke();
  fill(h, s, b, 20);
  
  ellipse(x, y, rr, rr);
}