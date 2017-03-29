package PewPewPew;

//Made by Andrew Xue
//a3xue@edu.uwaterloo.ca
//Bullethell game.
//NO KNOWN BUGS ATM.

//UNIMPLEMENTED:
//  Dying.
//  Explosion graphics.

import java.awt.Color;
import java.awt.Font;
import java.lang.Math;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.util.Random;

public class PewPewPew {
	
	// Initializing the window and player's ship
	JFrame window = new JFrame();
	playership player = new playership();
	
	// Lists of enemies, enemy bullets, player's bullets
	ArrayList<enemy> enemylst = new ArrayList<enemy>();
	ArrayList<enemybullet> enemybullets = new ArrayList<enemybullet>();
	ArrayList<playerbullet> playerbullets = new ArrayList<playerbullet>();
	
	// Array of colours to be picked randomly for enemy bullets
	Color[] colourlst={Color.RED,Color.BLUE,Color.GREEN,Color.ORANGE,Color.CYAN};
	Random colourpick = new Random();
	
	// To be used for enemy spawns at random positions
	Random spawnpick = new Random();
	
	// To be used for random pews
	Random pewpick = new Random();
	int pewx;
	// pewy used for placing pews above or below the ship
	Random pewypick = new Random();
	int pewy;
	
	// Integers controlling the timing of enemy spawns
	int spawncooldown=300;
	int spawncount=0;
	
	// Function for creating a path that goes straight up and down
	int[] straightdown(int x, int y, int dist){
		int[] answer = {x,-50,x,y+dist,x,y};
		return answer;
	}
	
	// Basic curved path
	int [] curve = {934, 27, 862, 76, 837, 109, 822, 137, 805, 175, 794, 212, 776, 277, 763, 361, 758, 412, 758, 467, 763, 517, 770, 562, 783, 605, 802, 653, 817, 698, 827, 732, 836, 763, 847, 799, 868, 845, 886, 879, 905, 910, 915, 925};

	
	// Detects collision between two objects
	boolean collision(int x1, int y1, int x2, int y2,int siz1, int siz2){
		if (x1+siz1>=x2&&x1<=x2+siz2&&y1+siz1>=y2&&y1<=y2+siz2){
			return true;
		}
		return false;
	}
	
	// Highest class, only stores the x and ycoordinates of the object, the size, and whether it is out of the game screen
	private class entity{
		double x,y;
		boolean remove=false;
		int size;
		entity(double xcoord, double tempy, int tempsiz){
			x=xcoord;
			y=tempy;
			size=tempsiz;
		}
	}
	
	// Class to be used for enemy ships and the player. Stores properties that every ship has such as bullet cooldown time(time between
	//   shots), count (to be used in conjunction with bullet cooldown time), health of the ship, speed of the ship, damage of the ship's
	//   bullets, speed of the ship's bullets.
	private class ship extends entity{
		int bullcool,count,health,speed,bulletdamage,bulletspeed;
		ship(double tempx,double tempy,int tempsize,int tempbullcool,int temphealth,int tempspeed,int tempbulletdamage,int tempbulletspeed){
			super(tempx,tempy,tempsize);
			bullcool=tempbullcool;
			count=0;
			health=temphealth;
			speed=tempspeed;
			bulletdamage=tempbulletdamage;
			bulletspeed=tempbulletspeed;
		}
	}
	
	// Subclass for the player.
	
	private class playership extends ship{
		// These two variables used for following the ship.
		private ImageIcon temp = new ImageIcon("Untitled.png");
		private Image img = temp.getImage();
		
		int mousex,mousey;
		double angletomouse;
		private playership(){
			// Initializes superclass components
			super(100,900,20,40,100,4,10,15);
		}
		void shipmove(){
			// If the mouse is in the ship, no movement
			if (Math.pow(mousey-40-y,2)+Math.pow(mousex-12-x,2)>20){
				angletomouse=Math.atan2(mousey-40-y, mousex-15-x);
				x+=Math.cos(angletomouse)*speed;
				y+=Math.sin(angletomouse)*speed;
			}
		}
		// Make the player's ship shoot.
		void shipshoot(){
			// If the bullet is not on cooldown, shoot
			if (count==bullcool){
				// Create one playerbullet on the left of the ship, and one to the right, using properties of the Ship superclass
				playerbullets.add(new playerbullet(x,y-5,5,bulletdamage,bulletspeed));
				playerbullets.add(new playerbullet(x+15,y-5,5,bulletdamage,bulletspeed));
				count=0;}
			else{
				// Reduce the bullet cooldown time
				count++;
			}
		}
		// Unimplemented, will be called when ship's health is <=0
		public void die() {
		}
	}
	
	// Subclass of entity, player's bullets. Has properties of the bullet's damage, speed and size
	private class playerbullet extends entity{
		int damage,speed;
		int siz=5;
		private playerbullet(double xcoord, double ycoord, int size,int dmg,int spe){
			// Initializes superclass components
			super(xcoord,ycoord,size);
			siz=size;
			damage=dmg;
			speed=spe;
		}
		// Moves the bullets straight upwards by its speed
		void move(){
			y-=speed;
		}
		// Detects if the bullets collides with any enemy.
		boolean hit(){
			// Iterates through the list of enemies and finds which, if any, it struck. Uses the collision helper function.
			for (int i=0;i<enemylst.size();i++){
				if (collision((int)x,(int)y,(int)enemylst.get(i).x,(int)enemylst.get(i).y,siz,enemylst.get(i).size)){
					// Removes the enemy if they are hit, else they take damage
					enemylst.get(i).health-=damage;
					enemylst.get(i).recenthit=150;
					if (enemylst.get(i).health<=0){
						enemylst.remove(i);}
					return true;
				}
			}
			return false;
		}
	}
	
	// Subclass of the ship class, with two extra elements, the index, angle and pathing array. These are all used to move the ship.
	private class enemy extends ship{
		int index;
		double angle;
		int recenthit=0;
		int pathing[];
		private ImageIcon tempimage = new ImageIcon("enemy.png");
		private Image pic = tempimage.getImage();
		private enemy(int x,int y,int hea,int siz,int spe,int bulletspeedtemp,int pathig[],int bulletdamagetemp,int bullcooltemp){
			super(x,y,siz,bullcooltemp,hea,spe,bulletdamagetemp,bulletspeedtemp);
			pathing=pathig;
			index=pathig.length-1;
			bulletspeed=bulletspeedtemp;
			// Sets the initial angle of travel for the ship
			angle=Math.atan2(pathing[index]-(double)y, pathing[index-1]-(double)x);
		}

		// Movement is done by iterating through the pathing array backwards. It takes the angle that it needs to move from its current
		//   point to the point in the pathing array, and moves in that direction by its speed.
		// The pathing array can be created in the pathing.java file by simply drawing. Custom patterns can be implemented.
		private void move(){
			if (index>=1){
				double xdist = (x-pathing[index-1])*(x-pathing[index-1]);
				double ydist = (y-pathing[index])*(y-pathing[index]);
				// If the distance between its current position and its target is less then a threshold, it moves on to the next point
				//   calculating its next angle of travel.
				if (xdist+ydist<=30){
					index-=2;
					if (index>=1){angle=Math.atan2(pathing[index]-(double)y, pathing[index-1]-(double)x);}}
				x+= Math.cos(angle)*speed;
				y+= Math.sin(angle)*speed;}
			else{
				// If it has finished its path, it is removed
				remove=true;
			}
			if (recenthit>=0)recenthit--;
		}
		// Make the ship fire.
		private void shoot(){
			// If the ship's bullet is not on cooldown, it fires
			if (count==bullcool){
			// Takes the angle between the ship and the player's ship, and creates a bullet moving in that direction with the bullet's damage
			//   and speed taken from the superclass ship. Also randomizes their colour.
			enemybullets.add(new enemybullet(x+size/2,y+size/2,bulletspeed,bulletdamage,15,colourlst[colourpick.nextInt(4)],
					Math.atan2(player.y+player.size/2-(double)(y+size/2), player.x+player.size/2-(double)(x+size/2))));
			count=0;}
			else{
				// Reduces the bullet's cooldown time
				count++;
			}
		}
	}
	
	// Enemy projectiles subclass. Has extra properties of speed, damage, colour, and angle of travel
	private class enemybullet extends entity{
		int speed,damage;
		Color colour;
		double angle;
		private enemybullet(double xtemp,double ytemp,int speedtemp,int damagetemp,int sizetemp, Color colourtemp,double angletemp){
			super(xtemp,ytemp,sizetemp);
			speed=speedtemp;
			damage=damagetemp;
			colour=colourtemp;
			angle=angletemp;
		}
		// Moves the bullet at its angle multiplied by its speed
		private void move(){
			y+=Math.sin(angle)*speed;
			x+=Math.cos(angle)*speed;
		}
		// Checks if the bullet hits the player.
		private boolean hit(){
			if (collision((int)x,(int)y,(int)player.x,(int)player.y,size,player.size)){
				int temphealth = player.health;
				// If the bullet hits the player, the player's health is reduced. Should the player's health fall below 0 it dies.
				if (damage>temphealth){
					player.die();
				}
				else{
					player.health-=damage;
				}
				return true;
			}
			return false;
		}
	}
	
	public static void main(String[] args) {
		// Starts the program.
		new PewPewPew().go();
	}
	
	// Sets the various properties of window, adds a MouseMotion listener that responds to the mouse.
	private void go(){
		window.setSize(1000, 1000);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setResizable(false);
		window.add(new PewGrid());
		window.addMouseMotionListener(new mouseevent());
		move();
	}
	
	// Starts the program
	private void move(){
				//(int x,int y,int hea,int siz,int spe,int bulletspeedtemp,int pathig[],int bulletdamagetemp,int bullcooltemp)
		while (true){
			// Spawn enemies at set intervals
			if (spawncount==spawncooldown){
				// Create an enemy that follows a preset path
				enemylst.add(new enemy(curve[0],curve[1],100,50,1,2,curve,25,100));
				spawncount=0;
			}
			else{
				spawncount++;
			}
			window.repaint();
			// Set frame rate
			try{Thread.sleep(5);}
			catch(Exception exp){
				System.out.println("Runtime Error");
			}
		}
	}
	
	// JComponent with all the game elements
	private class PewGrid extends JComponent {
		public void paintComponent(Graphics g){
			Graphics2D grap = (Graphics2D) g;
			// Background is black
			grap.setColor(Color.BLACK);
			grap.fillRect(0,0,1000,1000);
			// Player ship is white
			grap.setColor(Color.WHITE);
			// Draw player ship
			//grap.drawImage(player.img, (int)player.x,(int)player.y,player.size+30,player.size+30, this);
			grap.fillRect((int)player.x,(int)player.y,player.size,player.size);
			
			
			// Make the player's ship shoot
			player.shipshoot();
				
			// Make the player's ship move
			player.shipmove();
			
			// Draw player bullets and go through logic for if the bullets hit something
			for (int i=0;i<playerbullets.size();i++){
				grap.fillRect((int)playerbullets.get(i).x, (int)playerbullets.get(i).y, playerbullets.get(i).siz, playerbullets.get(i).siz);
				playerbullets.get(i).move();
				// Remove the bullet if it hits something
				if (playerbullets.get(i).hit()){
					playerbullets.remove(i);
				}
			}
			
			// Draw enemy bullets and go through logic for if the bullets hit something
			for (int i=0;i<enemybullets.size();i++){
				grap.setColor(enemybullets.get(i).colour);
				grap.fillRect((int)enemybullets.get(i).x, (int)enemybullets.get(i).y, enemybullets.get(i).size, enemybullets.get(i).size);
				// Move the bullets based on their initial angle
				enemybullets.get(i).move();
				if (enemybullets.get(i).hit()){
					// Remove the bullets if they hit something
					enemybullets.remove(i);
				}
			}
			grap.setColor(Color.WHITE);
			// Draw all the enemies and make them move, shoot, and die
			for (int i=0;i<enemylst.size();i++){
				grap.drawImage(enemylst.get(i).pic, (int)enemylst.get(i).x, (int)enemylst.get(i).y-15,(int)enemylst.get(i).size,(int)enemylst.get(i).size, this);
				// Make the enemy move
				enemylst.get(i).move();
				// Make the enemy shoot
				enemylst.get(i).shoot();
				// If the enemy has the boolean remove as true, it will be removed. This boolean will be true if they has no health or have left
				//    the game screen
				if (enemylst.get(i).remove){
					enemylst.remove(i);
				}
				// If the enemy has been recently hit, then display the health bar above it
				if (enemylst.get(i).recenthit>0){
					grap.setColor(Color.RED);
					grap.fillRect((int)enemylst.get(i).x, (int)enemylst.get(i).y-15, (int)enemylst.get(i).size, 5);
					grap.setColor(Color.GREEN);
					grap.fillRect((int)enemylst.get(i).x, (int)enemylst.get(i).y-15, (int)(enemylst.get(i).size*((float)enemylst.get(i).health/100)), 5);
				}				
			}
		}
	}
	
	// Take the coordinates of where the player's mouse is
	private class mouseevent implements MouseMotionListener{
		public void mouseDragged(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {
			player.mousex=e.getX();
			player.mousey=e.getY();
		}
	}
}
