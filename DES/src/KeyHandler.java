import java.math.*;
import java.util.*;




public class KeyHandler {
	
	String key;
	int[][] firstPermutationTable;
	int[][] secondPermutationTable;
	int[][] sboxPermutationTable;
	ArrayList<int[][]>sboxes;
	int[][] sB1,sB2,sB3,sB4,sB5,sB6,sB7,sB8;
	int[] shiftTable;
	char[] keyChars;
	String keyBits;
	String keyBinary="";
	char[] keyNoParity;
	char[][] key1stPermutation;
	char[][] key2ndPermutation;
	char[][] sBoxPermutation;
	char[][] C, D; //arrays to split the first permutation key
	int iterationNum=0;
	ArrayList<char[][]> cTables;
	ArrayList<char[][]> dTables;
	ArrayList<char[][]> kTables;
	char[] totalCD;
	char[][] total2D;
	int[][]xorTable;
	ArrayList<String[]> sBoxConvertions;
	String [] sBoxConverted;
	TextHandler texthandler=new TextHandler("MOSCHOUA");
	
	public KeyHandler(String key)
	{
		this.key=key;
		sboxes=new ArrayList<int[][]>();
		cTables=new ArrayList<char[][]>();
		dTables=new ArrayList<char[][]>();
		kTables=new ArrayList<char[][]>();
		total2D=new char[8][7];
		sBoxConverted=new String[8];
		sBoxConvertions=new ArrayList<String[]>();
		permutationTableInitialization();
		populateSboxes();
		firstPermutation();
		secondPermutation(key1stPermutation);
		
		while(iterationNum<16)
		{
			secondPermutation(total2D);
		}
		xorRWithK();
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
		//	System.out.println(""+keyBinary);
		}
		
		/*Remove the parity bits
		 * Populate a new table with the key by excluding every 8th bit from the key
		 */
		char[] help=keyBinary.toCharArray();
		for(int i=0;i<help.length;i++)
		{
			if((i+1)%8!=0)
			{
				keyNoParity[j]=help[i];
				j++;
			}
		}
		
		/*Practice first permutation*/
		for(int i=0;i<8;i++)
		{
			for(int h=0;h<7;h++)
			{
				key1stPermutation[i][h]=help[firstPermutationTable[i][h]-1]; 
				/* -1 Because the number in the step by step manual starts from 1
				 * However the array numeration begins with 0*/
				
				//System.out.println(FirstPermutationTable[i][h]+"----"+key1stPermutation[i][h]+"-");
			}
			//System.out.println("----------------------------");
		}
		
	}
	
	/*Performs the second permutation*/
	public void secondPermutation(char[][] charArray1st)
	{
		C=new char[4][7];
		D=new char[4][7];
		totalCD=new char[56];
		
		/*Split the table for the first 28 bits populate C and for the rest 28 bits populate D*/
		for(int i=0;i<8;i++)
		{
			for(int h=0;h<7;h++)
			{
				if(i<4)
				{
					C[i][h]=charArray1st[i][h];
				//	System.out.println("C: "+C[i][h]);
				}
				else
				{
					D[i-4][h]=charArray1st[i][h];
				//	System.out.println("D: "+D[i-4][h]);
				}
			}
		}
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
			
			/*Reorder the help tables with left circular shift*/
//			char hc, hd;
//			hc=helpShiftC[0];
//			hd=helpShiftD[0];
//			for(int i=1;i<4;i++)
//			{
//				helpShiftC[i-1]=helpShiftC[i];
//				helpShiftD[i-1]=helpShiftD[i];
//			}
//			helpShiftC[3]=hc;
//			helpShiftD[3]=hd;
			
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
			
		/*	for(int i=0;i<4;i++)
			{
				for(int h=0;h<7;h++)
				{
					System.out.println("C- "+cTable[i][h]);
				}
				System.out.println("-----------------");
			}
			
			for(int i=0;i<4;i++)
			{
				for(int h=0;h<7;h++)
				{
					System.out.println("D- "+dTable[i][h]);
				}
				System.out.println("-----------------");
			}*/
			
			if(shiftTable[iterationNum]==1)
			{
				int j=0;
				/*Combine C and D table to one*/				
				for(int i=0;i<4;i++)
				{
					for(int h=0;h<7;h++)
					{
						//1D to perform 2nd permutation
						totalCD[j]=cTable[i][h];
				//		System.out.println("CD - "+totalCD[j]);
						totalCD[j+28]=dTable[i][h];
						//2D to be used as input for the next iteration
						total2D[i][h]=cTable[i][h];
						total2D[i+4][h]=dTable[i][h];
						j++;
					}
				}
				
		/*	for(int i=0;i<56;i++)
				{
					for(int h=0;h<7;h++)
					{
						System.out.println("CD - "+totalCD[i]);
					}
					System.out.println("---------------------------");
				} */
				
				/*Perform permutation to the combined table*/
				secondPermutationTable(totalCD);
				cTables.add(cTable);
				dTables.add(dTable);
			//	System.out.println("IterationNum: "+iterationNum);
			//	System.out.println("Cs: "+cTables.size());
			//	System.out.println("Ds: "+dTables.size());
				iterationNum++;
			}
			else if(shiftTable[iterationNum]==2)
			{
				shiftTable[iterationNum]=1;
			//	System.out.println("Double.............");
				leftShift(cTable,dTable);
			}
			
		
	}
			
	public void secondPermutationTable(char[] totalTable)
	{
		key2ndPermutation=new char[8][7];
	//	System.out.println("Total length: "+totalTable.length);
		for(int i=0;i<8;i++)
		{
			for(int h=0;h<6;h++)
			{
				key2ndPermutation[i][h]=totalTable[secondPermutationTable[i][h]-1]; 
				/* -1 Because the number in the step by step manual starts from 1
				 * However the array numeration begins with 0*/
				
	//			System.out.println(secondPermutationTable[i][h]+"----"+key2ndPermutation[i][h]+"-");
			}
	//		System.out.println("----------------------------");
		}
		kTables.add(key2ndPermutation);
		
	}
	
	/*Perform XOR between R and K*/
	public void xorRWithK()
	{
		char[][] expansionR=texthandler.getExpansionR();
		char[][] kTable=kTables.get(0);
		ArrayList<int[]>rows=new ArrayList<int[]>();
		
		xorTable=new int[8][6];
		for(int i=0;i<8;i++)
		{
			int[] row=new int[8];
			for(int h=0;h<6;h++)
			{
				/*Build the xor table and also the xor rows to use them in sBoxes*/
				xorTable[i][h]=expansionR[i][h]^kTable[i][h];
				row[h]=xorTable[i][h];
		//		System.out.println("XOR:   "+xorTable[i][h]);
			}
			System.out.println("row "+row[0]);
			rows.add(row);
			
			
			
		//	int decimal = Integer.parseInt(hexNumber, 16);
		//	System.out.println("------------");
		}
		
		sBoxes(rows);
	//	System.out.println(""+rows.size());
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
		//	sBoxConverted[j]=currentSbox[sRows[j]][sColumns[j]];
			sBoxConverted[j]=Integer.toBinaryString(currentSbox[sRows[j]][sColumns[j]]);
			while(sBoxConverted[j].length()<4)
			{
				sBoxConverted[j]="0"+sBoxConverted[j];
			}
		//	System.out.println(sRows[j]+" row");
		//	System.out.println(sColumns[j]+" column");
			helpString+=sBoxConverted[j];
			j++;
		}
		char[] sBoxHelp=helpString.toCharArray();
		sBoxPermutation=new char[8][4];
		for(int i=0;i<8;i++)
		{
			for(int h=0;h<4;h++)
			{
				sBoxPermutation[i][h]=sBoxHelp[sboxPermutationTable[i][h]-1]; 
				/* -1 Because the number in the step by step manual starts from 1
				 * However the array numeration begins with 0*/
				
				System.out.println(sboxPermutationTable[i][h]+"----"+sBoxPermutation[i][h]+"-");
			}
			System.out.println("----------------------------");
		}
		
	//	for(int i=0;i<8;i++)
	//	{
	//		System.out.println("Sbox value: "+sBoxConverted[i]);
	//	}
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
