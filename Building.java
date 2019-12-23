
public class Building {
	public int building_num;
	public int executed_time;
	public int total_time;
	
	//parameterized constructor to initialize the building after its inserted
	public Building(int building_num, int executed_time, int total_time) {
		this.building_num=building_num;
		this.executed_time=executed_time;
		this.total_time=total_time;
	}
}