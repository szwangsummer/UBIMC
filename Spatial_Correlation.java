import java.io.*; 
import java.util.*;

public class Spatial_Correlation {
	ArrayList<ArrayList<Double>> Pearson = new ArrayList();
	ArrayList<ArrayList<Double>> ValueSim = new ArrayList();
	
	void Initial(){
		for(int i=0;i<30;i++){
			this.Pearson.add(null);
			this.ValueSim.add(null);
		}
	}
	
	void Calculate_Pear_Corr() throws IOException{
		this.Initial();
		ArrayList<ArrayList<Integer>> In = new ArrayList();
		ArrayList<ArrayList<Integer>> Out = new ArrayList();
		FileReader read1 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Flow_in.txt");
	    BufferedReader rd1 = new BufferedReader(read1);
	    FileReader read2 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Flow_out.txt");
	    BufferedReader rd2 = new BufferedReader(read2);
	    String row = null;
	    while((row = rd1.readLine())!=null){
	    	String[] arr = row.split(",");
	    	ArrayList<Integer> temp = new ArrayList();
	    	for(int i=5;i<22;i++){
	    		temp.add(Integer.parseInt(arr[i]));
	    	}
	    	In.add(temp);
	    }
	    
	    while((row = rd2.readLine())!=null){
	    	String[] arr = row.split(",");
	    	ArrayList<Integer> temp = new ArrayList();
	    	for(int i=5;i<22;i++){
	    		temp.add(Integer.parseInt(arr[i]));
	    	}
	    	Out.add(temp);
	    }
	    for(int i=0;i<28;i++){
	    	for(int j=0;j<27;j++){
	    		for(int k=j+1;k<28;k++){
	    			if(this.getAvgRating(Out.get(i*28+j)) == 0 || this.getAvgRating(In.get(i*28+k)) == 0){
	    				continue;
	    			}
	    			double cor = this.getDistance(Out.get(i*28+j), In.get(i*28+k));
	    			int index = k-j;
	    			if(this.Pearson.get(index) == null){
	    				ArrayList<Double> t = new ArrayList();
	    				t.add(cor);
	    				this.Pearson.set(index, t);
	    			}else{
	    				ArrayList<Double> t = this.Pearson.get(index);
	    				t.add(cor);
	    				this.Pearson.set(index, t);
	    			}
	    		}
	    	}
	    }
	}
	
	void Calculate_Value_Corr() throws IOException{
		this.Initial();
		ArrayList<ArrayList<Integer>> In = new ArrayList();
		ArrayList<ArrayList<Integer>> Out = new ArrayList();
		FileReader read1 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Flow_in.txt");
	    BufferedReader rd1 = new BufferedReader(read1);
	    FileReader read2 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Flow_out.txt");
	    BufferedReader rd2 = new BufferedReader(read2);
	    String row = null;
	    while((row = rd1.readLine())!=null){
	    	String[] arr = row.split(",");
	    	ArrayList<Integer> temp = new ArrayList();
	    	for(int i=5;i<22;i++){
	    		temp.add(Integer.parseInt(arr[i]));
	    	}
	    	In.add(temp);
	    }
	    
	    while((row = rd2.readLine())!=null){
	    	String[] arr = row.split(",");
	    	ArrayList<Integer> temp = new ArrayList();
	    	for(int i=5;i<22;i++){
	    		temp.add(Integer.parseInt(arr[i]));
	    	}
	    	Out.add(temp);
	    }
	    for(int i=0;i<28;i++){
	    	for(int j=0;j<27;j++){
	    		for(int k=j+1;k<28;k++){
	    			if(this.getAvgRating(Out.get(i*28+j)) == 0 || this.getAvgRating(In.get(i*28+k)) == 0){
	    				continue;
	    			}
	    			double cor = this.getValueDistance(Out.get(i*28+j), In.get(i*28+k));
	    			int index = k-j;
	    			if(this.ValueSim.get(index) == null){
	    				ArrayList<Double> t = new ArrayList();
	    				t.add(cor);
	    				this.ValueSim.set(index, t);
	    			}else{
	    				ArrayList<Double> t = this.ValueSim.get(index);
	    				t.add(cor);
	    				this.ValueSim.set(index, t);
	    			}
	    		}
	    	}
	    }
	}
	
	void ExtractData() throws IOException{
		FileReader read1 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Spatial_Sim_OutIn.txt");
	    BufferedReader rd1 = new BufferedReader(read1);
	    FileWriter writer1 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Spatial_Sim_OutIn10.txt");
	    BufferedWriter wt1 = new BufferedWriter(writer1);
	    String row = null;
	    double r = 0;
	    /*
	    while((row = rd1.readLine())!=null){
	    	String[] arr = row.split(" ");
	    	if(arr.length<100){
	    		break;
	    	}else{
	    		for(int i=0;i<100;i++){
	    			Double t = Double.parseDouble(arr[i]) - r*Math.random()/10; 
	    			wt1.write(t+" ");
	    			wt1.flush();
	    		}
	    		wt1.write("\n");
	    		wt1.flush();
	    	}
	    	if(r<7){
	    	    r = r + 3;
	    	}
	    }
	    */
	    r = 6;
	    while((row = rd1.readLine())!=null){
	    	String[] arr = row.split(" ");
	    	if(arr.length<100){
	    		break;
	    	}else{
	    		for(int i=0;i<100;i++){
	    			Double t = Double.parseDouble(arr[i]) - r*Math.random()/10; 
	    			wt1.write(t+" ");
	    			wt1.flush();
	    		}
	    		wt1.write("\n");
	    		wt1.flush();
	    	}
	    	if(r>0){
	    	    r = r - 3;
	    	}
	    }
	}
	
	void PrintOut() throws IOException{
		FileWriter writer1 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//Spatial_Sim_OutIn.txt");
	    BufferedWriter wt1 = new BufferedWriter(writer1);
	    for(int i=0;i<this.ValueSim.size();i++){
	    	ArrayList<Double> temp = this.ValueSim.get(i);
	    	if(temp==null){
	    		continue;
	    	}
	    	for(int j=0;j<temp.size();j++){
	    	    wt1.write(temp.get(j).toString()+" ");
	    	}
	    	wt1.write("\n");
	    	wt1.flush();
	    	
	    }
	}

	    	public double getDistance(ArrayList<Integer> lr1, ArrayList<Integer> lr2) {
	    		//List<Rating> lr1 = reader.readUserItemRatings(id1);
	    		//List<Rating> lr2 = reader.readUserItemRatings(id2);
	    		
	    		double avgX = getAvgRating(lr1);
	    		double avgY = getAvgRating(lr2);
	    		double sumXY = 0, sumX = 0, sumY = 0;
	    		
	    		for (int i = 0; i < lr1.size(); i++) {
	    			double rating1 = lr1.get(i);
	    			sumX += (rating1 - avgX) * (rating1 - avgX);
	    		}
	    		for (int j = 0; j < lr2.size(); j++) {
	    			double rating2 = lr2.get(j);
	    			sumY += (rating2 - avgY) * (rating2 - avgY);
	    		}
	    		for (int i = 0; i < lr1.size(); i++) {
	    			double rating1 = lr1.get(i);
	    			//for (int j = 0; j < lr2.size(); j++) {
	    				double rating2 = lr2.get(i);
	    				//if (lr1.get(i).getItemid() == lr2.get(j).getItemid()) {
	    			sumXY += (rating1 - avgX) * (rating2 - avgY);
	    				//}
	    			//}
	    		}
	    		return sumXY / (Math.sqrt(sumX * sumY));
	    	}
	    	
	    	public double getValueDistance(ArrayList<Integer> lr1, ArrayList<Integer> lr2) {
	    		double cor = 0;
	    		for(int i=0;i<lr1.size();i++){
	    			if(lr1.get(i)==0 && lr2.get(i)==0)continue;
	    			double dif = Math.abs(lr1.get(i)-lr2.get(i));
	    			dif = dif/(this.Max(lr1.get(i),lr2.get(i)));
	    			cor = (float) (cor + dif);
	    		}
	    		cor = cor/lr1.size();
				return cor;
	    	}
	    	
	    	public double Max(double a1,double a2){
	    		if(a1>a2) return a1;
	    		return a2;
	    	}

	    	/**
	    	 * 计算用户的rating总和
	    	 * 
	    	 * @param userid
	    	 */
	    	double getAvgRating(ArrayList<Integer> lr) {
	    		double sum = 0.0;
	    		for (int i=0;i<lr.size();i++) {
	    			sum += lr.get(i);
	    		}
	    		return sum/lr.size();
	    	}
	    
	    	public static void main(String[] args) throws IOException{
	    		Spatial_Correlation test = new Spatial_Correlation();
	    		//test.Calculate_Pear_Corr();
	    		//test.PrintOut();
	    		//test.ExtractData();
	    		test.Calculate_Value_Corr();
	    		test.PrintOut();
	    		test.ExtractData();
	    		System.out.print("Over");
	    	}

}
