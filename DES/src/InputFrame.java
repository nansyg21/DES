import java.awt.*;

import javax.swing.*;


public class InputFrame extends JFrame {

	/*Window components*/
	JTextArea plainText;
	JTextArea resultsTextArea;
	JTextField keyTxt;
	JButton encryptBt;
	JLabel resultTitleLbl;
	JLabel resultLbl;
	JPanel panel;
	GridBagLayout gbLO;
	GridBagConstraints c;
	KeyHandler kh;
	JScrollPane scroll;
	
	public InputFrame()
	{
		super("DES ECB Encryption");
		
		/*Initialization of layout*/
		panel=new JPanel();
		gbLO=new GridBagLayout();
		c=new GridBagConstraints();
		panel.setLayout(gbLO);
		
		/*Textfield for plain text*/
		plainText=new JTextArea("Write your text here");
		plainText.setRows(5);
		plainText.setColumns(40);
		plainText.setLineWrap(true);
		
		c.fill=GridBagConstraints.HORIZONTAL;
		c.ipady=40;
		c.gridwidth=3;
		c.gridx=0;
		c.gridy=0;
		panel.add(plainText,c);
		

		
		/*Textfield for key*/
		keyTxt=new JTextField("00 00 00 00 00 00 00 00");
		c.fill=GridBagConstraints.HORIZONTAL;
		c.ipady=0;
		c.gridwidth=3;
		c.weighty=1.0;
		c.gridx=0;
		c.gridy=1;
		panel.add(keyTxt,c);
		
		/*Button to trigger encryption*/
		encryptBt=new JButton("Encryption");
		c.gridwidth=1;
		c.weighty=1.0;
		c.gridx=1;
		c.gridy=2;
		panel.add(encryptBt,c);
		
		resultsTextArea=new JTextArea("Results Here");
		resultsTextArea.setRows(25);
		resultsTextArea.setColumns(55);
		resultsTextArea.setLineWrap(true);
		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridx=1;
	//	c.gridheight=5;
		c.gridy=3;
		c.ipady=40;
		
		panel.add(resultsTextArea,c);
		
		/*Result Title Label*/
/*		resultTitleLbl=new JLabel("Encryption result");
		c.weighty=1.0;
		c.gridx=1;
		c.gridy=3;
		panel.add(resultTitleLbl,c);*/
		
		/*Result Label*/
	/*	resultLbl=new JLabel("Your result will be displayed here");
		c.weighty=1.0;
		c.gridx=0;
		c.gridwidth=3;
		c.gridy=4;
		panel.add(resultLbl,c);*/
	
		scroll= new JScrollPane (resultsTextArea, 
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel.add(scroll,c);
		this.setContentPane(panel);
		this.setSize(700,700);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		kh=new KeyHandler("F49F989455498554");
		resultsTextArea.setText(kh.getTextToDisplay());
		//new TextHandler("MOSCHOUA");
		
	}
}
