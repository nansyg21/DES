import java.math.*;
import java.util.*;




public class KeyHandler {

	String key; //the given key
	int[][] firstPermutationTable; //1st key permutation positions
	int[][] secondPermutationTable; //2nd key permutation positions
	int[][] sboxPermutationTable; //sbox permutation positions
	int[][] finalPermutationTable; //final permutation positions
	ArrayList<int[][]>sboxes; //all the sboxes
	int[][] sB1,sB2,sB3,sB4,sB5,sB6,sB7,sB8; //each sbox element position
	int[] shiftTable; //how many left shifts will be performed in every iteration
	char[] keyChars; //split key into chars
	String keyBits; //key characters as bitstream
	String keyBinary=""; //the whole key as a bitstream
	char[] keyNoParity; //key with no parity bits
	char[][] key1stPermutation; //key table after 1st permutation
	char[][] key2ndPermutation; //key table after 2nd permutation
	char[][] sBoxPermutation; //Sbox table after permutation
	char[][] C, D; //arrays to split the first permutation key
	int iterationNum=0; //goes from 0 to 15 to produce 16 keys
	ArrayList<char[][]> cTables; //all c tables
	ArrayList<char[][]> dTables; //all d tables
	ArrayList<char[][]> kTables; //all key tables
	char[] totalCD; //the table after shift -- helps in second permutation
	char[][] total2D; //the table after shift -- helps in display
	int[][]xorTable; //XOR E(R) with K
	char[][] newR; //the new R after XOR
	ArrayList<String[]> sBoxConvertions; //the result after SBox convertions
	String [] sBoxConverted;
	char[]lToXor; //XOR L with Sbox permutation table
	TextHandler texthandler;
	int iterations=1;
	char[][] encryptedTable;//the table with the results
	int loop=0;
	String helpS="";
	ArrayList<String> messages; //holds the messages to display


	public KeyHandler(String text, String key)
	{
	
		
		this.key=key;
		sboxes=new ArrayList<int[][]>();
		cTables=new ArrayList<char[][]>();
		dTables=new ArrayList<char[][]>();
		kTables=new ArrayList<char[][]>();
		total2D=new char[8][7];
		sBoxConverted=new String[8];
		sBoxConvertions=new ArrayList<String[]>();
		lToXor=new char[32];
		messages=new ArrayList<String>();
		texthandler=new TextHandler(text,this);

	}

	public void triggerEncryption()
	{
		permutationTableInitialization(); //populate the position tables
		populateSboxes(); //create Sboxes


		firstPermutation(); //perform 1st permutation for the initial table

		secondPermutation(key1stPermutation); //perform 2nd permutation for the initial table
		
		/*Perform the rest 2nd permutation for the rest tables*/
		while(iterationNum<16)
		{
			secondPermutation(total2D);
		}
		xorRWithK(0);

		finalPermutation(texthandler.getrTables(15),texthandler.getlTables(15));
	}

	public ArrayList<String> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<String> messages) {
		this.messages = messages;
	}



	public int[][] getSboxPermutationTable() {
		return sboxPermutationTable;
	}


	public void setSboxPermutationTable(int[][] sboxPermutationTable) {
		this.sboxPermutationTable = sboxPermutationTable;
	}


	public ArrayList<int[][]> getSboxes() {
		return sboxes;
	}


	public void setSboxes(ArrayList<int[][]> sboxes) {
		this.sboxes = sboxes;
	}


	public ArrayList<char[][]> getkTables() {
		return kTables;
	}


	public void setkTables(ArrayList<char[][]> kTables) {
		this.kTables = kTables;
	}


	/*Performs the first permutation*/
	public void firstPermutation()
	{

		key=key.replaceAll("\\p{Z}",""); //Remove all white spaces
		keyChars=key.toCharArray();
		int j=0; //separate position indicator for keyNoParity array
		keyNoParity=new char[56]; //key with no parity bits
		key1stPermutation=new char[8][7]; //key after first permutation
		for(int i=0;i<keyChars.length;i++)
		{

			keyBits=new BigInteger(Character.toString(keyChars[i]), 16).toString(2);
			while(keyBits.length()<4)
			{
				keyBits="0"+keyBits; //every hex has 4 bits
			}

			keyBinary+=keyBits; //display the whole key in binary

		}

		helpS+="\n"+"Key in Binary: "+keyBinary +"\n";
		helpS+="\n";
		messages.add(helpS);
		helpS="";

		/*Remove the parity bits
		 * Populate a new table with the key by excluding every 8th bit from the key
		 */
		char[] help=keyBinary.toCharArray();

		helpS+="Key Table Without Parity Bit: \n";
		for(int i=0;i<help.length;i++)
		{
			if((i+1)%8!=0)
			{
				keyNoParity[j]=help[i];	
				helpS+=""+keyNoParity[j]+" ";
				j++;
			}
			else
			{
				helpS+="\n";
			}
		}
		messages.add(helpS);
		helpS="";

		/*Practice first permutation*/

		helpS+="\n"+"First Key Permutation:\n";

		for(int i=0;i<8;i++)
		{
			for(int h=0;h<7;h++)
			{
				key1stPermutation[i][h]=help[firstPermutationTable[i][h]-1]; 
				/* -1 Because the number in the step by step manual starts from 1
				 * However the array numeration begins with 0*/

				helpS+=""+key1stPermutation[i][h]+" ";

			}

			helpS+="\n";

		}
		messages.add(helpS);
		helpS="";
	}

	/*Performs the second permutation*/
	public void secondPermutation(char[][] charArray1st)
	{
		char[][] C=new char[4][7];
		char[][] D=new char[4][7];
		totalCD=new char[56];

		helpS+="\n"+"Iteration Number: "+iterationNum+"\n";
		helpS+="Split the table in two parts C and D with 28 bits each \n";
		helpS+="\n"+"C["+iterationNum+"] Table:"+"\n";

		/*Split the table for the first 28 bits populate C and for the rest 28 bits populate D*/
		for(int i=0;i<8;i++)
		{
			if(i==4)
			{
				helpS+="\n"+"D["+iterationNum+"] Table:"+"\n";
			}
			for(int h=0;h<7;h++)
			{
				if(i<4)
				{
					C[i][h]=charArray1st[i][h];
					helpS+=C[i][h]+" ";
				}
				else
				{
					D[i-4][h]=charArray1st[i][h];
					helpS+=D[i-4][h]+" ";
				}
			}
			helpS+="\n";
		}
		helpS+="\n";
		messages.add(helpS);
		helpS="";
		/*Perform left shift*/
		leftShift(C,D);


	}

	public void leftShift(char[][] cTable, char[][] dTable)
	{
		/*Shift by one*/

		/*Tables to help us reorder the columns*/
		char[] helpShiftC=new char[4];
		char[] helpShiftD=new char[4];

		/*Populate the help tables with the first column*/
		for(int i=0;i<4;i++)
		{
			helpShiftC[i]=cTable[i][0];
			helpShiftD[i]=dTable[i][0];
		}

		/*Reorder the help tables with left circular shift
		 * exists in cryptool visualization*/
		/*	char hc, hd;
			hc=helpShiftC[0];
			hd=helpShiftD[0];
			for(int i=1;i<4;i++)
			{
				helpShiftC[i-1]=helpShiftC[i];
				helpShiftD[i-1]=helpShiftD[i];
			}
			helpShiftC[3]=hc;
			helpShiftD[3]=hd;
		 */
		
		/*Simulate the left shift for the main 2 tables*/
		for(int i=0;i<4;i++)
		{
			for(int h=1;h<7;h++)
			{
				cTable[i][h-1]=cTable[i][h];
				dTable[i][h-1]=dTable[i][h];

			}
			/*Add the last column from the help tables*/
			cTable[i][6]=helpShiftC[i];
			dTable[i][6]=helpShiftD[i];



		}


/*If there is one iteration according to the table print the results and perform second permutation with the new results
 * */
		if(shiftTable[iterationNum]==1)
		{
			helpS+="\n"+"Left Shift C["+iterationNum+"] Table \n";
			for(int i=0;i<4;i++)
			{
				for(int h=0;h<7;h++)
				{
					helpS+=cTable[i][h]+" ";
				
				}
				helpS+="\n";
			
			}
			helpS+="\n"+"Left Shift D["+iterationNum+"] Table \n";
			
			for(int i=0;i<4;i++)
			{
				for(int h=0;h<7;h++)
				{
					helpS+=dTable[i][h]+" ";
					
				}
				helpS+="\n";
			
			}
			messages.add(helpS);
			helpS="";
			
			int j=0;
			/*Combine C and D table to one*/				
			for(int i=0;i<4;i++)
			{
				for(int h=0;h<7;h++)
				{
					//1D to perform 2nd permutation
					totalCD[j]=cTable[i][h];
					totalCD[j+28]=dTable[i][h];
					//2D to be used as input for the next iteration
					total2D[i][h]=cTable[i][h];
					total2D[i+4][h]=dTable[i][h];
					j++;
				}
			}


			helpS+="\n"+"Combined Table For C and D: \n";
			for(int i=0;i<8;i++)
			{
				for(int h=0;h<7;h++)
				{
					helpS+=total2D[i][h]+" ";
				}
				helpS+="\n";
			} 
			messages.add(helpS);
			helpS="";
			
			/*Perform permutation to the combined table*/
			secondPermutationTable(totalCD);
			cTables.add(cTable);
			dTables.add(dTable);
			iterationNum++;
		}
		/*If there are 2 shifts perform the shift once more to the previous result*/
		else if(shiftTable[iterationNum]==2)
		{
			shiftTable[iterationNum]=1;
			leftShift(cTable,dTable);
		}


	}

	/*Perform 2nd permutation*/
	public void secondPermutationTable(char[] totalTable)
	{
		key2ndPermutation=new char[8][7];
		helpS+="\n"+"Second Permutation\n";

		for(int i=0;i<8;i++)
		{
			for(int h=0;h<6;h++)
			{
				key2ndPermutation[i][h]=totalTable[secondPermutationTable[i][h]-1]; 
				/* -1 Because the number in the step by step manual starts from 1
				 * However the array numeration begins with 0*/
				helpS+=key2ndPermutation[i][h]+" ";
			}
			helpS+="\n";
		}
		kTables.add(key2ndPermutation);
		messages.add(helpS);
		helpS="";

	}

	/*Perform XOR between R and K*/
	public void xorRWithK(int Iteration)
	{
		char[][] expansionR=texthandler.getExpansionR();
		char[][] kTable=kTables.get(Iteration);
		ArrayList<int[]>rows=new ArrayList<int[]>();
		helpS+="\n"+"Perform XOR With R And K"+"\n";
		xorTable=new int[8][6];
		for(int i=0;i<8;i++)
		{
			int[] row=new int[8];
			for(int h=0;h<6;h++)
			{
				/*Build the xor table and also the xor rows to use them in sBoxes*/
				xorTable[i][h]=expansionR[i][h]^kTable[i][h];
				row[h]=xorTable[i][h];
				helpS+=xorTable[i][h]+" ";
			}
			helpS+="\n";
			rows.add(row);
			messages.add(helpS);
			helpS="";
		}
		sBoxes(rows);
	}

	/*Find value from Sboxes*/
	public void sBoxes(ArrayList<int[]> rows)
	{
		int[] sColumns=new int [8];
		int[] sRows=new int[8];
		int[][] currentSbox;
		int j=0;
		String helpString="";

		for(int[] r: rows)
		{
			/*Get the first and last bit from the row
			 * convert from binary string to decimal to find the row*/
			String rSbox=Integer.toString(r[0])+Integer.toString(r[5]);
			sRows[j]=Integer.parseInt(rSbox,2);
			/*Get the middle 4 bits from the row
			 * conver from binary string to decimal to find the column*/
			String cSbox=Integer.toString(r[1])+Integer.toString(r[2])+Integer.toString(r[3])+Integer.toString(r[4]);
			sColumns[j]=Integer.parseInt(cSbox,2);
			/*Get the current SBox and find the value of transformation using the row and the column from above*/
			currentSbox=sboxes.get(j);

			sBoxConverted[j]=Integer.toBinaryString(currentSbox[sRows[j]][sColumns[j]]);
			while(sBoxConverted[j].length()<4)
			{
				sBoxConverted[j]="0"+sBoxConverted[j];
			}

			helpS+="\n"+"S Box "+j+ ":\n";
			helpS+="S Box Row: "+sRows[j]+"\n";
			helpS+="S Box Column: "+sColumns[j]+ "\n";
			helpS+="S Box Value (in binary): "+sBoxConverted[j]+ "\n";
			messages.add(helpS);
			helpS="";
			
			helpString+=sBoxConverted[j];
			j++;
		}
		char[] sBoxHelp=helpString.toCharArray();
		sBoxPermutation=new char[8][4];
		helpS+="\n"+"S Box Permutation: \n";
		for(int i=0;i<8;i++)
		{
			for(int h=0;h<4;h++)
			{
				sBoxPermutation[i][h]=sBoxHelp[sboxPermutationTable[i][h]-1]; 
				/* -1 Because the number in the step by step manual starts from 1
				 * However the array numeration begins with 0*/
				helpS+=sBoxPermutation[i][h]+" ";
			}
			helpS+="\n";

		}
		
		messages.add(helpS);
		
		xorSboxWithL(loop);
		
	}
	public void xorSboxWithL(int l)
	{
		char[][] helpXor=texthandler.getlTables(loop);
		loop++;
		
		char[] helpExpand=new char[32];
		newR=new char[4][8];
		int j=0;
		/*Convert the 4*8 table to 1D table to XOR it with a 8*4 table*/
		for (int i=0;i<8;i++)
		{
			for(int h=0;h<4;h++)
			{
				lToXor[j]=sBoxPermutation[i][h];
				j++;
			}
		}

		j=0;
		helpS+="\n"+"Perform XOR With L Table and S Box Permuted Table: \n";

		for(int i=0;i<4;i++)
		{
			for(int h=0;h<8;h++)
			{
				newR[i][h]=Character.forDigit((lToXor[j]^helpXor[i][h]),10);
				helpExpand[j]=newR[i][h];
				helpS+=newR[i][h]+" ";
				j++;
			}
			helpS+="\n";
		}
		messages.add(helpS);
		helpS="";
		texthandler.getrTables().add(newR);

		texthandler.getlTables().add(texthandler.getrTables(loop-1));
		/*Perform the same procedure for the rest 15 keys*/
		while(iterations<16)
		{
			iterations++;
			texthandler.expandR(helpExpand);
			xorRWithK(iterations-1);
		}


	}

	/*Perform Final Permutation*/
	public void finalPermutation(char[][] RTable, char[][] LTable)
	{
		char[] finalTable=new char[64];
		int j=0;
		encryptedTable=new char[8][8];
		helpS+="\n"+"Final Table:\n";
		for(int i=0;i<8;i++)
		{
			for(int h=0;h<8;h++)
			{
				if(i<4)
				{
					finalTable[j]=RTable[i][h];
				}
				else
				{
					finalTable[j]=LTable[i-4][h];
				}
				helpS+=finalTable[j]+" ";
				j++;
			}
			helpS+="\n";
		}
		messages.add(helpS);
		helpS="";
		helpS+="\n"+"Final Permutation: \n";
		for(int i=0;i<8;i++)
		{
			for(int h=0;h<8;h++)
			{
				encryptedTable[i][h]=finalTable[finalPermutationTable[i][h]-1]; 
				/* -1 Because the number in the step by step manual starts from 1
				 * However the array numeration begins with 0*/
				helpS+=encryptedTable[i][h]+" ";
			}
			helpS+="\n";
		}
		messages.add(helpS);
		helpS="";
		String helpString="";
		char nextChar;
		helpS+="\n"+"Encrypted Message: \n";
		for(int i=0;i<8;i++)
		{
			helpString="";
			for(int h=0;h<8;h++)
			{
				helpString=helpString+encryptedTable[i][h];
			}

			nextChar = (char)Integer.parseInt(helpString, 2);
			helpS+=nextChar;
		}
		messages.add(helpS);
		helpS="";
	}
	/*Initialize the tables that are used as indicators to perform the permutations for the key*/
	public void permutationTableInitialization()
	{
		firstPermutationTable= new int[][]{
				{57,49,41,33,25,17,9},
				{1,58,50,42,34,26,18},
				{10,2,59,51,43,35,27},
				{19,11,3,60,52,44,36},
				{63,55,47,39,31,23,15},
				{7,62,54,46,38,30,22},
				{14,6,61,53,45,37,29},
				{21,13,5,28,20,12,4}
		};

		shiftTable=new int[] {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};

		secondPermutationTable=new int[][]{
				{14,17,11,24,1,5},
				{3,28,15,6,21,10},
				{23,19,12,4,26,8},
				{16,7,27,20,13,2},
				{41,52,31,37,47,55},
				{30,40,51,45,33,48},
				{44,49,39,56,34,53},
				{46,42,50,36,29,32}
		};

		sboxPermutationTable=new int[][]{
				{16,7,20,21},
				{29,12,28,17},
				{1,15,23,26},
				{5,18,31,10},
				{2,8,24,14},
				{32,27,3,9},
				{19,13,30,6},
				{22,11,4,25}
		};

		finalPermutationTable=new int[][]{
				{40, 8, 48, 16, 56, 24, 64, 32},
				{39, 7, 47, 15, 55, 23, 63, 31},
				{38, 6, 46, 14, 54, 22, 62, 30},
				{37, 5, 45, 13, 53, 21, 61, 29},
				{36, 4, 44, 12, 52, 20, 60, 28},
				{35, 3, 43, 11, 51, 19, 59, 27},
				{34, 2, 42, 10, 50, 18, 58, 26},
				{33, 1, 41, 9, 49, 17, 57, 25}
		};

	}

	/*Create the tables used for s-box transformations*/
	public void populateSboxes()
	{
		sB1=new int[][]{
				{14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
				{0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
				{4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
				{15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}
		};
		sboxes.add(sB1);

		sB2=new int[][]{
				{15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
				{3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
				{0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
				{13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}
		};
		sboxes.add(sB2);

		sB3=new int[][]{
				{10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
				{13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
				{13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
				{1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}
		};
		sboxes.add(sB3);

		sB4=new int[][]{
				{7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
				{13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
				{10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
				{3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}
		};
		sboxes.add(sB4);

		sB5=new int[][]{
				{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
				{14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
				{4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
				{11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}
		};
		sboxes.add(sB5);

		sB6=new int[][]{
				{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
				{10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
				{9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
				{4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}
		};
		sboxes.add(sB6);

		sB7=new int[][]{
				{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
				{13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
				{1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
				{6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}
		};
		sboxes.add(sB7);

		sB8=new int[][]{
				{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
				{1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
				{7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
				{2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}
		};
		sboxes.add(sB8);

	}

}
