import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
	JButton stepBt;
	JButton clear;
	int messageNum=0;
	
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
		
		/*Button to trigger full encryption*/
		encryptBt=new JButton("Encryption");
		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridwidth=1;
		c.weighty=1.0;
		c.gridx=1;
		c.gridy=2;
		panel.add(encryptBt,c);
		
		EncryptionBtListener encrBtL=new EncryptionBtListener();
		encryptBt.addActionListener(encrBtL);
		
		/*Print Next Step*/
		stepBt=new JButton("Next Step");
		c.gridwidth=1;
		c.weighty=1.0;
		c.gridx=2;
		c.gridy=2;
		panel.add(stepBt,c);
				
		StepByStepBtListener sbsBtL=new StepByStepBtListener();
		stepBt.addActionListener(sbsBtL);
		
		/*Clear Results*/
		clear=new JButton("Clear");
		c.gridwidth=1;
		c.weighty=1.0;
		c.gridx=3;
		c.gridy=2;
		panel.add(clear,c);
		
		ClearBtListener cBtL=new ClearBtListener();
		clear.addActionListener(cBtL);
		
		/*Display Results*/
		resultsTextArea=new JTextArea("Results Here");
		resultsTextArea.setRows(25);
		resultsTextArea.setColumns(50);
		resultsTextArea.setLineWrap(true);
		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridx=1;
		c.gridwidth=3;
		c.gridy=3;
		c.ipady=40;
		
		panel.add(resultsTextArea,c);
		
		//Add scroll in results text area	
		scroll= new JScrollPane (resultsTextArea, 
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel.add(scroll,c);
		
		this.setContentPane(panel);
		this.setSize(700,700);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//"F49F989455498554"
		//new TextHandler("MOSCHOUA");
		
	}
	/*Initialize a new key handler with the given text and key
	 * Trigger the encryption and print the results from the begining to the end
	 * */
	class EncryptionBtListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			kh=new KeyHandler(plainText.getText(),keyTxt.getText());
			kh.triggerEncryption();
			ArrayList<String> message=kh.getMessages();
			resultsTextArea.setText("");
			for(String s:message)
			{
			
			resultsTextArea.setText(resultsTextArea.getText()+s);
			}
		}
	}
	/*If there is no key handler then initialize one and trigger the encryption
	 * If there is key handler then just print the next step on the screen
	 * */
	class StepByStepBtListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			if(kh==null)
			{
				kh=new KeyHandler(plainText.getText(),keyTxt.getText());
				kh.triggerEncryption();
				resultsTextArea.setText("Initializing... Click again for next Step\n\n");
			}
			else
			{
				String message=kh.getMessages().get(messageNum);
				
				resultsTextArea.setText(resultsTextArea.getText()+message);
				messageNum++;
				
			}	
		}
	}
	/*Clear the screen and set key handler to null
	 * */
	class ClearBtListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			resultsTextArea.setText("");
			kh=null;
			messageNum=0;
			
		}
	}
}
