import java.io.*; 
import java.util.*;
public class ProcessData {
	HashMap<String,Integer> Location_Num = new HashMap();
	HashMap<Double,Integer> Distance_Num = new HashMap();
	HashMap<String,Integer> Date_Num = new HashMap();
	HashMap<String,Integer> Hour_Num = new HashMap();
	ArrayList<Integer> Distance_Distribution = new ArrayList();
	ArrayList<String> Trajectory = new ArrayList();
	
	//int count = 0;
	
	void Distance_Dis()throws IOException{
		FileReader read1 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//train.csv");
		BufferedReader rd = new BufferedReader(read1);
		FileWriter writer1 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//dis_distribution.txt");
		BufferedWriter wt = new BufferedWriter(writer1);
		String row = null;
		rd.readLine();
		GeoHash geohash = new GeoHash();
		Distance dis = new Distance();
		while((row = rd.readLine())!=null){
			count++;
			String[] arr = row.split(",");
			String[] arr1 = arr[4].split(" ");
			if(this.Date_Num.containsKey(arr1[0])){
				int num = this.Date_Num.get(arr1[0]);
				this.Date_Num.put(arr1[0], num+1);
			}else{
				this.Date_Num.put(arr1[0], 1);
			}
			
			if(this.Hour_Num.containsKey(arr1[1].substring(0, 2))){
				int num = this.Hour_Num.get(arr1[1].substring(0, 2));
				this.Hour_Num.put(arr1[1].substring(0, 2), num+1);
			}else{
				this.Hour_Num.put(arr1[1].substring(0, 2), 1);
			}
			//String start = arr[5];
			//String end = arr[6];
			double[] start = geohash.decode(arr[5]);
			double[] end = geohash.decode(arr[6]);
			double d = dis.GetDistance(start[1], start[0], end[1], end[0]);
			if(this.Distance_Num.containsKey(d)){
				int num = this.Distance_Num.get(d);
				this.Distance_Num.put(d, num+1);
			}else{
				this.Distance_Num.put(d, 1);
			}
		}
		for(int i=0;i<10;i++){
			this.Distance_Distribution.add(0);
		}
		Iterator entries = this.Distance_Num.entrySet().iterator();
		while(entries.hasNext()){
			Map.Entry entry = (Map.Entry) entries.next();  
		    Double key = (Double) entry.getKey();  
		    Integer value = (Integer)entry.getValue();
		    wt.write(key + "," + value + "\n");
		    wt.flush();
		    
		    if(key<=1000){
		    	this.Distance_Distribution.set(0,this.Distance_Distribution.get(0) + value);
		    	continue;
		    }else if(key<=2000){
		    	this.Distance_Distribution.set(1,this.Distance_Distribution.get(1) + value);
		    	continue;
		    }else if(key<=3000){
		    	this.Distance_Distribution.set(2,this.Distance_Distribution.get(2) + value);
		    	continue;
		    }else if(key<=4000){
		    	this.Distance_Distribution.set(3,this.Distance_Distribution.get(3) + value);
		    	continue;
		    }else if(key<=5000){
		    	this.Distance_Distribution.set(4,this.Distance_Distribution.get(4) + value);
		    	continue;
		    }else if(key<=6000){
		    	this.Distance_Distribution.set(5,this.Distance_Distribution.get(5) + value);
		    	continue;
		    }else if(key<=7000){
		    	this.Distance_Distribution.set(6,this.Distance_Distribution.get(6) + value);
		    	continue;
		    }else if(key<=8000){
		    	this.Distance_Distribution.set(7,this.Distance_Distribution.get(7) + value);
		    	continue;
		    }else if(key<=9000){
		    	this.Distance_Distribution.set(8,this.Distance_Distribution.get(8) + value);
		    	continue;
		    }else if(key<=10000){
		    	this.Distance_Distribution.set(9,this.Distance_Distribution.get(9) + value);
		    	continue;
		    }
		}
	}
	
	void Search_Bike_Trajectory(String Bike_ID)throws IOException{//Find all the trip trajectories of the bike with ID 
		FileReader read1 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//train.csv");
		BufferedReader rd = new BufferedReader(read1);
		FileWriter writer1 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//"+Bike_ID+"_trajectory.txt");
		BufferedWriter wt = new BufferedWriter(writer1);
		String row = null;
		rd.readLine();
		while((row = rd.readLine())!=null){
			String[] arr = row.split(",");
			GeoHash geohash = new GeoHash();
			
			if(arr[2].equals(Bike_ID)){
				//this.Trajectory.add(e)
				double[] start = geohash.decode(arr[5]);
				wt.write(start[1]+","+start[0]+"\n");
			    wt.flush();
			}
		}
	}
	
	void Read_Raw_Speed_Date(String date,int checkout) throws IOException {//extract the check-out data based on date
		FileReader read1 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//train.csv");
		BufferedReader rd = new BufferedReader(read1);
		FileWriter writer1 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//"+date+"_"+checkout+".txt");
		BufferedWriter wt = new BufferedWriter(writer1);
		String row = null;
		rd.readLine();
		while((row = rd.readLine())!=null){
			String[] arr = row.split(",");
			String[] arr1 = arr[4].split(" ");
			if(arr1[0].equals(date)){
				if(checkout==1){//extract the check-out data
				    if(!this.Location_Num.containsKey(arr[5])){
					    this.Location_Num.put(arr[5], 1);
				    }else{
					    int pre_num = this.Location_Num.get(arr[5]);
					    this.Location_Num.put(arr[5], pre_num+1);
				    }
				}else{//extract the check-in data
					if(!this.Location_Num.containsKey(arr[6])){
					    this.Location_Num.put(arr[6], 1);
				    }else{
					    int pre_num = this.Location_Num.get(arr[6]);
					    this.Location_Num.put(arr[6], pre_num+1);
				    }
				}
			}
		}
		
		GeoHash geohash = new GeoHash();
		Iterator entries = this.Location_Num.entrySet().iterator();
		while(entries.hasNext()){
			Map.Entry entry = (Map.Entry) entries.next();  
		    String key = (String) entry.getKey();  
		    double[] geo = geohash.decode(key);
		    Integer value = (Integer)entry.getValue();
		    wt.write("{\"lng\":"+geo[1]+",\"lat\":"+geo[0]+",\"count\":"+value+"},\n");
		    wt.flush();
		}
	}
	
	void Read_Raw_Speed_Hour(String hour,int checkout) throws IOException {//extract the check-out data based on hour of a day(May-15)
		FileReader read1 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//MOBIKE_CUP_2017//train.csv");
		BufferedReader rd = new BufferedReader(read1);
		FileWriter writer1 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//"+hour+"_"+checkout+"hour.txt");
		BufferedWriter wt = new BufferedWriter(writer1);
		String row = null;
		rd.readLine();
		while((row = rd.readLine())!=null){
			String[] arr = row.split(",");
			String[] arr1 = arr[4].split(" ");
			String[] arr2 = arr1[1].split(":");
			if(arr2[0].equals(hour)&&(arr1[0].equals("2017-05-15")||arr1[0].equals("2017-05-16")||arr1[0].equals("2017-05-17")||arr1[0].equals("2017-05-18")||arr1[0].equals("2017-05-19"))){
				if(checkout==1){//提取checkout数据
				    if(!this.Location_Num.containsKey(arr[5])){
					    this.Location_Num.put(arr[5], 1);
				    }else{
					    int pre_num = this.Location_Num.get(arr[5]);
					    this.Location_Num.put(arr[5], pre_num+1);
				    }
				}else{//extract the check-in data
					if(!this.Location_Num.containsKey(arr[6])){
					    this.Location_Num.put(arr[6], 1);
				    }else{
					    int pre_num = this.Location_Num.get(arr[6]);
					    this.Location_Num.put(arr[6], pre_num+1);
				    }
				}
			}
		}
		
		GeoHash geohash = new GeoHash();
		Iterator entries = this.Location_Num.entrySet().iterator();
		while(entries.hasNext()){
			Map.Entry entry = (Map.Entry) entries.next();  
		    String key = (String) entry.getKey();  
		    double[] geo = geohash.decode(key);
		    Integer value = (Integer)entry.getValue();
		    wt.write("{\"lng\":"+geo[1]+",\"lat\":"+geo[0]+",\"count\":"+value+"},\n");
		    wt.flush();
		}
	}
	
	ArrayList<String> POI_types = new ArrayList();
	int count = 0;
	void Read_POI()throws IOException {//extract Beijing POI
		FileReader read1 = new FileReader("//Users//wangsenzhang//Desktop//bike papers//data//BJ_POI.txt");
		BufferedReader rd = new BufferedReader(read1);
		FileWriter writer1 = new FileWriter("//Users//wangsenzhang//Desktop//bike papers//data//POI_车站.txt");
		BufferedWriter wt = new BufferedWriter(writer1);
		String row = null;
		while((row = rd.readLine())!=null){
			String[] arr = row.split("	");
			if(!POI_types.contains(arr[4])){
				POI_types.add(arr[4]);
			}
			//||arr[4].equals("便利店")
			//if(arr[4].equals("超市")||arr[4].equals("商铺")||arr[4].equals("星级酒店")||arr[4].equals("中餐厅")||arr[4].equals("小吃快餐店")){
			if(arr[4].equals("地铁站")||arr[4].equals("公交车站")){    
			    count++;
				int index = row.indexOf("\"lat\"");
			    String lat = row.substring(index+7, index+13);
			    if(lat.contains("{")||lat.contains("}")){
			    	continue;
			    }
			    index = row.indexOf("\"lng\"");
			    String lon = row.substring(index+7, index+14);
			    if(lon.contains("{")||lon.contains("}")){
			    	continue;
			    }
			    if(this.Location_Num.containsKey(lon+","+lat)){
			    	int num = this.Location_Num.get(lon+","+lat);
			    	this.Location_Num.put(lon+","+lat, num+1);
			    }else{
			    	this.Location_Num.put(lon+","+lat, 1);
			    }
			}
		}
		System.out.print(count);
		Iterator entries = this.Location_Num.entrySet().iterator();
		while(entries.hasNext()){
			Map.Entry entry = (Map.Entry) entries.next();  
		    String key = (String) entry.getKey();  
		    String[] geo = key.split(",");
		    Integer value = (Integer)entry.getValue();
		    wt.write("{\"lng\":"+geo[0]+",\"lat\":"+geo[1]+",\"count\":"+value+"},\n");
		    wt.flush();
		}
	}
	public static void main(String[] args) throws IOException{
		ProcessData test = new ProcessData();
		//test.Search_Bike_Trajectory("175815");
		//test.Read_Raw_Speed_Date("2017-05-15");
		//test.Read_Raw_Speed_Hour("18",0);
		//test.Read_POI();
		test.Distance_Dis();
		//System.out.print(test.Date_Num);
		//System.out.print(test.Hour_Num);
		System.out.print("Over");

	}
}
