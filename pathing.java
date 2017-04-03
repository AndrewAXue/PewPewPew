package PewPewPew;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JFrame;

//Made by Andrew Xue
//a3xue@edu.uwaterloo.ca
//Path drawing helper program for PewPewPew
//NO KNOWN BUGS ATM.

public class pathing {
	JFrame window = new JFrame();
	int count=0;
	int cooldown=10;
	int index=0;
	boolean done = false;
	ArrayList<Integer> pathi = new ArrayList<Integer>();
	ArrayList<Integer> mirrorpathi = new ArrayList<Integer>();
	public static void main(String[] args) {
		new pathing().go();
	}
	
	private void go(){
		window.setSize(1000, 1000);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setResizable(false);
		window.add(new pathigrid());
		window.addMouseMotionListener(new mouseevent());
		window.addKeyListener(new keyevent());
		goe();
	}
	
	private void printlst(){
		if (pathi.size()==1){
			System.out.println("{"+pathi.get(0)+"};");
		}
		else{
			System.out.print("{");
			for (int i=pathi.size()/2-1;i>=1;i--){
			System.out.print(pathi.get(2*i)+","+pathi.get(2*i+1)+",");
			}
			System.out.print(pathi.get(0)+","+pathi.get(1)+"};");
		}
		System.out.println();
		if (mirrorpathi.size()==1){
			System.out.println("{"+mirrorpathi.get(0)+"};");
		}
		else{
			System.out.print("{");
			for (int i=mirrorpathi.size()/2-1;i>=1;i--){
			System.out.print(mirrorpathi.get(2*i)+","+mirrorpathi.get(2*i+1)+",");
			}
			System.out.print(mirrorpathi.get(0)+","+mirrorpathi.get(1)+"};");
		}
		
	}
	
	private void goe(){
		while (!done){
			try{Thread.sleep(50);}
			catch(Exception ex){}
			window.repaint();
		}
		printlst();
	}
	
	private class keyevent implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_E){
				done = true;
			}
			
		}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
		
	}
	
	private class pathigrid extends JComponent {
		public void paintComponent(Graphics g){
			Graphics2D grap = (Graphics2D) g;
			grap.setColor(Color.BLACK);
			grap.fillRect(0, 0, 1000, 1000);
			grap.setColor(Color.WHITE);
			for (int i=0;i<pathi.size();i+=2){
				grap.fillOval(pathi.get(i), pathi.get(i+1), 5, 5);
			}
		}
	}
	
	private class mouseevent implements MouseMotionListener{
		public void mouseDragged(MouseEvent e) {
			if (count==cooldown){
			pathi.add(e.getX()-5);
			pathi.add(e.getY()-35);
			mirrorpathi.add(1000-e.getX()+5);
			mirrorpathi.add(e.getY()-35);
			count=0;}
			else{
				count++;
			}
			
		}
		public void mouseMoved(MouseEvent e) {
		}
	}
}
