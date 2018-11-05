import java.io.*; 
import java.util.*;

public class RegionExtract {
	public int Region[][] = new int [784][2];//check-out/check-in bike number in each region
	public int Region_Hour_All[][][] = new int [784][24][2];//average check-out/check-in bike number in each region and each hour
	public int Region_Hour_Ave[][][] = new int [784][24][2];
	public int F[][] = new int[784][784];
	public int Flag_in[][] = new int[784][24];//whether it is true check-in bike number in each region and each hour
	public int Flag_out[][] = new int[784][24];//whether it is true check-out bike number in each region and each hour
	
	void Initial(){
		for(int i=0;i<784;i++){
			for(int j=0;j<2;j++){
				Region[i][j] = 0;
			}
		}
	}
	void Initial_F(){
		for(int i=0;i<784;i++){
			for(int j=0;j<784;j++){
				F[i][j] = 0;
			}
		}
	}
	void Get_Region_Count(String date)throws IOException{
	    FileReader read1 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//mobi.txt");
	    BufferedReader rd = new BufferedReader(read1);
	    //FileWriter writer1 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Region_ID_train.txt");
	    //BufferedWriter wt = new BufferedWriter(writer1);
	    String row = null;
	    this.Initial();
	    rd.readLine();
	    while((row = rd.readLine())!=null){
			String[] arr = row.split(",");
			String[] arr1 = arr[4].split(" ");
			String[] arr2 = arr1[1].split(":");
			if(arr1[0].equals(date)&&arr2[0].equals("08")){
			    int out_row = Integer.parseInt(arr[7]);
			    int out_col = Integer.parseInt(arr[8]);
			    int in_row = Integer.parseInt(arr[9]);
			    int in_col = Integer.parseInt(arr[10]);
			    if(out_row>-1 && out_col>-1 && out_row<28 && out_col<28){
				    this.Region[out_row*28+out_col][0]++;
			    }
			    if(in_row>-1 && in_col>-1 && in_row<28 && in_col<28){
				    this.Region[in_row*28+in_col][1]++;
			    }
			}
	    } 
	}
	
	void Get_Region_Count_Ave()throws IOException{
	    FileReader read1 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//mobi.txt");
	    BufferedReader rd = new BufferedReader(read1);
	    FileWriter writer1 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Flow_in.txt");
	    BufferedWriter wt1 = new BufferedWriter(writer1);
	    FileWriter writer2 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Flow_out.txt");
	    BufferedWriter wt2 = new BufferedWriter(writer2);
	    String row = null;
	    this.Initial();
	    rd.readLine();
	    while((row = rd.readLine())!=null){
			String[] arr = row.split(",");
			String[] arr1 = arr[4].split(" ");
			String[] arr2 = arr1[1].split(":");
			int hour = Integer.parseInt(arr2[0]);
			int out_row = Integer.parseInt(arr[7]);
			int out_col = Integer.parseInt(arr[8]);
			int in_row = Integer.parseInt(arr[9]);
			int in_col = Integer.parseInt(arr[10]);
			if(out_row>-1 && out_col>-1 && out_row<28 && out_col<28){
		        this.Region_Hour_All[out_row*28+out_col][hour][0]++;
			}
			if(in_row>-1 && in_col>-1 && in_row<28 && in_col<28){
			    this.Region_Hour_All[in_row*28+in_col][hour][1]++;
			}
	    }
	    for(int i=0;i<784;i++){
	    	for(int j=0;j<24;j++){
	    	    this.Region_Hour_Ave[i][j][0] = this.Region_Hour_All[i][j][0]/10;
	    	    this.Region_Hour_Ave[i][j][1] = this.Region_Hour_All[i][j][1]/10;
	    	    wt1.write(this.Region_Hour_Ave[i][j][1]+",");
	    	    wt2.write(this.Region_Hour_Ave[i][j][0]+",");
	    	}
	    	wt1.write("\n");
	    	wt2.write("\n");
	    	wt1.flush();
	    	wt2.flush();
	    }
	}
	
	void Calculate_Flags() throws IOException{
		FileWriter writer1 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//I_in.txt");
	    BufferedWriter wt1 = new BufferedWriter(writer1);
	    FileWriter writer2 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//I_out.txt");
	    BufferedWriter wt2 = new BufferedWriter(writer2);
	    
		this.Get_Region_Count_Ave();
	    for(int i=0;i<784;i++){//calculate the flag_out value (如果每个区域在每个时间段，checkin的自行车数>checkout自行车*系数，则认为checkout是真实的)
	    	for(int j=0;j<24;j++){
	    	    if(this.Region_Hour_All[i][j][1]>(1.2*this.Region_Hour_All[i][j][0]) || (this.Region_Hour_All[i][j][0]<10 && this.Region_Hour_All[i][j][0]<10)){
	    	    	this.Flag_out[i][j] = 1;
	    	    }else{
	    	    	this.Flag_out[i][j] = 0;
	    	    }
	    	}
	    }
	    
	    for(int i=0;i<784;i++){//calculate the flag_in value (如果一个区域在某个时间段，其周围区域的checkout数是真实的，那么该区域的checkin值也是真实的)
	    	for(int j=0;j<24;j++){
	    		if(i>0 && j>0 && i<783 && j<23){
	    			if(Flag_out[i-1][j-1]==1 && Flag_out[i-1][j]==1 && Flag_out[i-1][j+1]==1 && Flag_out[i][j-1]==1 && Flag_out[i][j+1]==1 && Flag_out[i+1][j-1]==1 && Flag_out[i+1][j]==1 && Flag_out[i+1][j+1]==1){
	    				Flag_in[i][j] = 1;
	    			}else{
	    				Flag_in[i][j] = 0;
	    			}
	    			}else if(i==0 && j>0 && j<23){
	    			if(Flag_out[i][j-1]==1 && Flag_out[i][j+1]==1 && Flag_out[i+1][j-1]==1 && Flag_out[i+1][j]==1 && Flag_out[i+1][j+1]==1){
	    				Flag_in[i][j] = 1;
	    			}else{
	    				Flag_in[i][j] = 0;
	    			}
	    		}else if(i>0 && j==0 && i<23){
	    			if(Flag_out[i-1][j]==1 && Flag_out[i-1][j+1]==1 && Flag_out[i][j+1]==1 && Flag_out[i+1][j]==1 && Flag_out[i+1][j+1]==1){
	    				Flag_in[i][j] = 1;
	    			}else{
	    				Flag_in[i][j] = 0;
	    			}
	    		}else if(j>0 && i==783 && j<23){
	    			if(Flag_out[i-1][j-1]==1 && Flag_out[i-1][j]==1 && Flag_out[i-1][j+1]==1 && Flag_out[i][j-1]==1 && Flag_out[i][j+1]==1){
	    				Flag_in[i][j] = 1;
	    			}else{
	    				Flag_in[i][j] = 0;
	    			}
	    		}else if(i>0 && i<783 && j==23){
	    			if(Flag_out[i-1][j-1]==1 && Flag_out[i-1][j]==1 && Flag_out[i][j-1]==1 && Flag_out[i+1][j-1]==1 && Flag_out[i+1][j]==1){
	    				Flag_in[i][j] = 1;
	    			}else{
	    				Flag_in[i][j] = 0;
	    			}
	    		}
	    		else{
	    			Flag_in[i][j] = 0;
	    		}
	    	}
	    }
	    
	    for(int i=0;i<784;i++){
	    	for(int j=0;j<24;j++){
	    		wt1.write(Integer.toString(this.Flag_in[i][j])+",");
	    		wt2.write(Integer.toString(this.Flag_out[i][j])+",");
	    	}
	    	wt1.write("\n");
	    	wt1.flush();
	    	wt2.write("\n");
	    	wt2.flush();
	    }
	}
	
	void Calculate_Ubalance_Rate() throws IOException{
		this.Get_Region_Count("2017-05-15");
	    FileWriter writer1 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Ubalance_Rate.txt");
	    BufferedWriter wt = new BufferedWriter(writer1);
		Float[] Region_Rate = new Float[784];
		float sum = 0;
		for(int i=0;i<784;i++){
			int out = this.Region[i][0];
			int in = this.Region[i][1];
			if(out==in){
				Region_Rate[i] = (float) 0;
				continue;
			}
			float rate = (1+(float)Math.abs(out-in))/(1+(float)(out>in?in:out));
			Region_Rate[i] = rate;
			sum +=rate;
			wt.write(i+","+rate+"\n");
			wt.flush();
		}
		float average;
		average = sum/784;
		System.out.print(average);
	}
	
	void ConstructG()throws IOException{
		FileReader read1 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//0810mobi.txt");
	    BufferedReader rd = new BufferedReader(read1);
	    FileWriter writer1 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Flow_in.txt");
	    BufferedWriter wt1 = new BufferedWriter(writer1);
	    //FileWriter writer2 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Flow_out.txt");
	    //BufferedWriter wt2 = new BufferedWriter(writer2);
	    //FileWriter writer3 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Flow.txt");
	    //BufferedWriter wt3 = new BufferedWriter(writer3);
	    String row = null;
	    this.Initial_F();
	    rd.readLine();
	    while((row = rd.readLine())!=null){
			String[] arr = row.split(",");
			int out_row = Integer.parseInt(arr[7]);
		    int out_col = Integer.parseInt(arr[8]);
		    int in_row = Integer.parseInt(arr[9]);
		    int in_col = Integer.parseInt(arr[10]);
			if(out_row>-1 && out_row<28 && out_col>-1 && out_col<28 && in_row<28 && in_row>-1 && in_col<28 && in_col>-1){
				this.F[out_row*28+out_col][in_row*28+in_col]++;
			}
	    }
	    
	    for(int i=0;i<784;i++){
	    	for(int j=0;j<784;j++){
	    		wt1.write(F[i][j]+" ");
	    		wt1.flush();
	    	}
	    	wt1.write("\n");
	    	wt1.flush();
	    }
	}
	
	public static void main(String[] args) throws IOException
    {
    // TODO 自动生成方法存根
        RegionExtract test = new RegionExtract();
        //test.Get_RegionID(2017-05-15);
        //test.Calculate_Ubalance_Rate();
        //test.ConstructG();
        //test.Get_Region_Count_Ave();
        test.Calculate_Flags();
        System.out.print("Over");
    }

}
