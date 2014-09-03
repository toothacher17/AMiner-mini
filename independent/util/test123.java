package util;

import java.awt.*;

import javax.swing.JFrame;

public class test123 extends JFrame{
	
	public test123(){
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		GridBagConstraints c1 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();
		
		c1.fill = GridBagConstraints.BOTH;
		c2.fill = GridBagConstraints.BOTH;
		
		
		
		Button b1 = new  Button("bt1");
		
		Button b2 = new  Button("bt2");
		
		c1.weighty = 1;
		c2.weighty = 1;
		
		c1.weightx = 10.0;
		c2.weightx = 1;
		
		layout.setConstraints(b1, c1);
		layout.setConstraints(b2, c2);
		add(b1);
		add(b2);
		
		
		setSize(300, 100);
		setVisible(true);
	}
	
	
	public static void main(String [] args){
		new test123();
	}
	
	
}
