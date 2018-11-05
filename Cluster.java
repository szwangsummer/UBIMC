import java.io.*; 
import java.util.*;

public class Cluster {
	static final double alpha = 0.3;
	static final int num_cluster = 50;
	//public int F_in[] = new int[784];
	//public int F_out[] = new int[784];
	public Map<String,Integer> F = new HashMap();//traffic flow between clusers：（id1,id2;id3:num）
	public int Region[][] = new int [784][2];//check-out/check-in bike number in each region数量
	public ArrayList<ArrayList<Integer>> cluster = new ArrayList();
	
	void Initial_Cluster(){//Initialize the clustering
		for(int i=0;i<784;i++){
			ArrayList<Integer> item = new ArrayList();
			item.add(i);
			this.cluster.add(item);
		}
	}
	
	void Initial_Region(){//Initialize the traffic flow matrix
		for(int i=0;i<784;i++){
			for(int j=0;j<2;j++){
				Region[i][j] = 0;
			}
		}
	}
	
	void Read_F()throws IOException{//read the traffic flow matrix among regions
		FileReader read1 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Flow.txt");
		BufferedReader rd = new BufferedReader(read1);
		String row = null;
		int row_index = 0;
		while((row=rd.readLine())!=null){
			String arr[] = row.split(" ");
			for(int i=0;i<arr.length;i++){
				int num = Integer.parseInt(arr[i]);
				if(num>0){
					this.F.put(row_index + ";" + i, num);
				}
			}
			row_index++;
		}
	}
	
	void Get_Region_Count()throws IOException{//Calculate the check-in/out bike number in each region
	    FileReader read1 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//0810mobi.txt");
	    BufferedReader rd = new BufferedReader(read1);
	    String row = null;
	    this.Initial_Region();
	    rd.readLine();
	    while((row = rd.readLine())!=null){
			String[] arr = row.split(",");
			int out_row = Integer.parseInt(arr[7]);
			int out_col = Integer.parseInt(arr[8]);
			int in_row = Integer.parseInt(arr[9]);
			int in_col = Integer.parseInt(arr[10]);
			if(out_row>-1 && out_row<28 && out_col>-1 && out_col<28 && in_row<28 && in_row>-1 && in_col<28 && in_col>-1){
				this.Region[out_row*28+out_col][0]++;
				this.Region[in_row*28+in_col][1]++;
			}
	    } 
	}
	
	double Calculate_Region_Sim(ArrayList<Integer> a1,ArrayList<Integer> a2){//calculate the similarity between two regions
		double sim = 0;
		int sum_flow_a1_in = 0;
		int sum_flow_a1_out = 0;
		int sum_flow_a2_in = 0;
		int sum_flow_a2_out = 0;
		int sum_flow_a1_2_a2 = 0;
		int sum_flow_a2_2_a1 = 0;
		for(int i=0;i<a1.size();i++){
			sum_flow_a1_in += this.Region[a1.get(i)][1];
			sum_flow_a1_out += this.Region[a1.get(i)][0];
		}
		for(int j=0;j<a2.size();j++){
			sum_flow_a2_in += this.Region[a2.get(j)][1];
			sum_flow_a2_out += this.Region[a2.get(j)][0];
		}
		for(int i=0;i<a1.size();i++){
			for(int j=0;j<a2.size();j++){
				if(this.F.containsKey(a1.get(i)+";"+a2.get(j))){
					sum_flow_a1_2_a2 += this.F.get(a1.get(i)+";"+a2.get(j));
				}
				if(this.F.containsKey(a2.get(j)+";"+a1.get(i))){
					sum_flow_a2_2_a1 += this.F.get(a2.get(j)+";"+a1.get(i));
				}
			}
		}
		if(sum_flow_a1_2_a2==0 && sum_flow_a2_2_a1==0){
			return 0;
		}else{
		    sim = 0.25*(double)(sum_flow_a1_2_a2+1)/(sum_flow_a1_out+1) 
			    + 0.25*(double)(sum_flow_a1_2_a2+1)/(sum_flow_a2_in+1) 
			    + 0.25*(double)(sum_flow_a2_2_a1+1)/(sum_flow_a2_out+1) 
			    + 0.25*(double)(sum_flow_a2_2_a1+1)/(sum_flow_a1_in+1);
		    return sim;
		}
	}
	
	void MergeCluster(int index1,int index2){//merger two clusters
		ArrayList<Integer> temp = new ArrayList();
		ArrayList<Integer> m1 = this.cluster.get(index1);
		ArrayList<Integer> m2 = this.cluster.get(index2);
		for(int i=0;i<m1.size();i++){
			temp.add(m1.get(i));
		}
		for(int i=0;i<m2.size();i++){
			temp.add(m2.get(i));
		}
		this.cluster.remove(m1);
		this.cluster.remove(m2);
		this.cluster.add(temp);
	}
	
	void ConstructCluster() throws IOException{//hierarchical clustering 
		this.Get_Region_Count();
		this.Read_F();
		this.Initial_Cluster();
		double max_similarity = 0;
		int ite = 800;
		for(int i=0;i<ite;i++){
			int len = this.cluster.size();
			if(len<this.num_cluster){
				break;
			}
			int index_1 = 0;
			int index_2 = 1;
			for(int j=0;j<len-1;j++){
				for(int k=j+1;k<len;k++){
					double sim = this.Calculate_Region_Sim(this.cluster.get(j),this.cluster.get(k));//calculate the similarity between two regions计
					if(sim>max_similarity){
						index_1 = j;
						index_2 = k;
						max_similarity = sim;
					}
				}
			}
			this.MergeCluster(index_1,index_2);//merge two clusters
			max_similarity = 0;
		}
	}
	
	void Write_Out_Cluster() throws IOException{
		FileWriter writer1 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//paper//test//cluster.txt");
		FileWriter writer2 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//paper//test//cluster_matrix.txt");
		BufferedWriter wt = new BufferedWriter(writer1);
		BufferedWriter wt2 = new BufferedWriter(writer2);
		int matrix[][] = new int[784][this.num_cluster];
		for(int i=0;i<784;i++){
			for(int j=0;j<this.num_cluster;j++){
				matrix[i][j] = 0;
			}
		}
		for(int i=0;i<this.cluster.size();i++){
			ArrayList<Integer> temp = this.cluster.get(i);
			wt.write(this.cluster.get(i)+"\n");
			wt.flush();
			for(int j=0;j<temp.size();j++){
				matrix[temp.get(j)][i] = 1;
			}
		}
		
		for(int i=0;i<784;i++){
			for(int j=0;j<this.num_cluster;j++){
				wt2.write(Integer.toString(matrix[i][j])+',');
				wt2.flush();
			}
			wt2.write('\n');
			wt2.flush();
		}
	}
	
	public static void main(String[] args) throws IOException
    {
        Cluster test = new Cluster();
        test.ConstructCluster();
        test.Write_Out_Cluster();
        System.out.print("Over");
    }
}
