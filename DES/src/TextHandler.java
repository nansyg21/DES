import java.util.*;


public class TextHandler {

	private String text;
	private String binaryText;
	private int[][] textPermutationInit;
	private int[][] expansion;
	private char[][] textPermutationTable;
	ArrayList<char[][]> rTables;
	ArrayList<char[][]> lTables;
	private char[][] expansionR;

	
	
	public TextHandler(String text)
	{
		this.text=text;
		textPermutationTable=new char[8][8];
		rTables=new ArrayList<char[][]>();
		lTables=new ArrayList<char[][]>();
		permutationTableInitialization();
		convertToBitStream(text);
		textPermutation(binaryText);
		leftAndRightParts(textPermutationTable);
	}


	public char[][] getExpansionR() {
		return expansionR;
	}


	public void setExpansionR(char[][] expansionR) {
		this.expansionR = expansionR;
	}


	public void convertToBitStream(String s)
	{
		text=s;
		byte[] bytes = text.getBytes();
		StringBuilder binary = new StringBuilder();
		for (byte b : bytes)
		{
			int val = b;
			for (int i = 0; i < 8; i++)
			{
				binary.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
			binary.append(' ');
		}
		//binaryText=binary.toString();
		//binaryText=binaryText.replaceAll("\\p{Z}","");
		binaryText="0001011010111101010000001001010010110100010100111000101001111011";
		
		/*If the block length is less than 64 add 0 to the end*/
		while(binaryText.length()<64)
		{
			binaryText+="0";
		}
	//	System.out.println("'" + text + "' to binary: " + binary);
	}
	
	public void textPermutation(String s)
	{
		char[] help=s.toCharArray();
		for(int i=0;i<8;i++)
		{
			for(int h=0;h<8;h++)
			{
				textPermutationTable[i][h]=help[textPermutationInit[i][h]-1]; 
				/* -1 Because the number in the step by step manual starts from 1
				 * However the array numeration begins with 0*/
				
	//			System.out.println(textPermutationInit[i][h]+"----"+textPermutationTable[i][h]+"-");
			}
	//		System.out.println("----------------------------");
		}
	}
	/*Split the array in left and right use help array to expand afterwards*/
	public void leftAndRightParts(char[][] charArrayLR)
	{
		char[][] R, L;
		R=new char[4][8];
		L=new char[4][8];
		char[] helpR=new char[32];
		int j=0;
		
		for(int i=0;i<8;i++)
		{
			for(int h=0;h<8;h++)
			{
				if(i<4)
				{
					L[i][h]=charArrayLR[i][h];
	//				System.out.println("L: "+L[i][h]);
				}
				else
				{
					R[i-4][h]=charArrayLR[i][h];
					helpR[j]=charArrayLR[i][h];
					j++;
	//				System.out.println("R: "+R[i-4][h]);
				}
			}
		}
		expandR(helpR);
	}
	
	/*Expand the first R array*/
	public void expandR(char[] ER)
	{
		char help[]=ER;
		expansionR=new char[8][6];
		for(int i=0;i<8;i++)
		{
			for(int h=0;h<6;h++)
			{
				expansionR[i][h]=help[expansion[i][h]-1]; 
				/* -1 Because the number in the step by step manual starts from 1
				 * However the array numeration begins with 0*/
				
	//			System.out.println(expansion[i][h]+"----"+expansionR[i][h]+"-");
			}
	//		System.out.println("----------------------------");
		}
	}
	
	public void permutationTableInitialization()
	{
		textPermutationInit = new int[][]{
				{58,50,42,34,26,18,10,2},
				{60,52,44,36,28,20,12,4},
				{62,54,46,38,30,22,14,6},
				{64,56,48,40,32,24,16,8},
				{57,49,41,33,25,17,9,1},
				{59,51,43,35,27,19,11,3},
				{61,53,45,37,29,21,13,5},
				{63,55,47,39,31,23,15,7}
				};
		
		expansion=new int[][]{
				{32,1,2,3,4,5},
				{4,5,6,7,8,9},
				{8,9,10,11,12,13},
				{12,13,14,15,16,17},
				{16,17,18,19,20,21},
				{20,21,22,23,24,25},
				{24,25,26,27,28,29},
				{28,29,30,31,32,1}
				};
		
	}
}
