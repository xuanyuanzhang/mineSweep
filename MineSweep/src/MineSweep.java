import java.util.Random;


public class MineSweep {

	private  int[][] map;
	private  int numOfMines;
	
	public MineSweep(int size, int numOfMines){
		this.numOfMines = numOfMines;
		initialize(size);
		map = setMines(size,numOfMines);
		setUpInstructions(map);
		System.out.println(map[1][1]);
	}
	
	private void initialize(int size){
		map = new int[size][size];
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				map[i][j] = 0;
			}
		}
	}
	
	//Output a matrix displays the map.
	public void output(){
		System.out.println(map.length+"\n");
		for(int i=0;i<map.length;i++){
			for(int j=0;j<map[0].length;j++){
				System.out.print(map[i][j]+" ");
			}
			System.out.println("\n");
		}
	}
	
	//Help function to set mines. Called in constructor.
	private int[][] setMines(int size, int numOfMines){
		int[][] output = new int[size][size];
		RandomNumberGenerator r = new RandomNumberGenerator(size*size-1);
		if(numOfMines<0)
			return null;
		while(numOfMines>0){
			int i,j;
			int index = r.next();
			i = index/size;
			j = index%size;
			System.out.print("Index i is: "+i);
			System.out.print("Index j is: "+j);
			System.out.print("\n");
			output[i][j] = 9;
			numOfMines--;
		}
		
		return output;
	}
	
	//Set up the rest of the map after mines have been set.
	private void setUpInstructions(int[][] input){
		int[] iIndex = new int[]{-1, -1, -1, 0, 1, 1, 1, 0};
		int[] jIndex = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};
		int height = input.length;
		int width = input[0].length;
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				int neighborMines = 0;
				for(int k=0;k<8;k++){
					if(input[i][j]==9)
						;
					else if((i+iIndex[k])<0||(i+iIndex[k]>height-1)||(j+jIndex[k]<0)||(j+jIndex[k]>width-1))
						;
					else{
						if(input[i+iIndex[k]][j+jIndex[k]]==9)
							neighborMines++;
					}
				}
				if(input[i][j]!=9)
				input[i][j] = neighborMines;
			}
		}
	}
	
	public static void main(String[] args){
		//System.out.println("Hello!");
		MineSweep project = new MineSweep(6, 11);
		project.output();
	}
}
