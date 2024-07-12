package my_cooperation;

import sim.util.Bag;
import sim.util.distribution.Normal;
import sim.util.distribution.Beta;
import model.SimDataCollection;

public class Environment extends SimDataCollection {

	//experimental parameters pre 7/11/24
//	public int num_groups = 13; //number of groups that exist
//	public int num_agents_per_group = 5; // number of agents per group
//	public int max_strikes = 3; //num strikes before leaving
//	public double mean_tolerance = 1.0f;
//	public double std_tolerance = 0.2f;
//	public double mean_value = 1.0f;
//	public double std_value = 0.2f;
//	public double agent_std_value = 0.1f;
//	public double prop_deviant = 0.2f;
//	public double deviant_mean_tolerance = 2.0f;
	
	//experimental parameters post 7/11/24
	// experimental condition related parameters
	public String prefix; // Where to save all these data to
	public boolean letter_grades = false; // use buckets?
	
	public double grading_error_alpha;
	public double grading_error_beta;
	
	public double divorce_constant; // constant modifier to the cost of divorce
	public int max_strikes; // number of strikes before divorce is considered
	
	// Agent related parameters
	public int num_agents;
	public float agent_tolerance_alpha;
	public float agent_tolerance_beta;
	public float agent_effort_alpha;
	public float agent_effort_beta;
	public float agent_std_effort; // one letter grade std? this is within agent std since always drawn from normal for agent
	
	
	// Group related parameters
	public int min_agents_per_group;
	public int max_agents_per_group;
	

	//GUI related parameters
	public int gridHeight = 100;
	public int gridWidth = 100;
	public int min_distance_groups = 5;

	//not tracked or input
	Bag groups = new Bag();
	Bag agents = new Bag();
	
	public boolean charts = false;
	

	//data to collect
	public int num_groups;
	public int num_deviants = 0;
	public int num_standards = 0;
	public double avg_deviant_payoff; //average payoff across all deviants
	public double  avg_standard_payoff; //average payoff across all standard agents

	public static void main(String[] args) {
		System.out.println("A");
		Environment environment = new Environment("Python/RunFiles/new_model_test.txt");
		System.out.println("B");
	}
	
	//constructor 
	public Environment(String filename) {
		super(filename);
	}

	public Environment() {
		super();
	}

	@Override
	public void setClasses() {
		this.subclass = Environment.class;
		this.agentclass = Agent.class;
	}

	public void make_groups() {
		// make the groups first
		this.groups = new Bag();
		num_groups = num_agents / min_agents_per_group;
		
		for(int i = 1; i <= num_groups; i++) {
			int random_x = random.nextInt(gridWidth);
			int random_y = random.nextInt(gridHeight);

			//			Bag b = sparseSpace.getMooreNeighbors(random_x, random_y, min_distance_groups, sparseSpace.BOUNDED, false);
			//			
			//			while(!b.isEmpty()) {
			//				random_x = random.nextInt(gridWidth);
			//				random_y = random.nextInt(gridHeight);
			//				b = sparseSpace.getMooreNeighbors(random_x, random_y, min_distance_groups, sparseSpace.BOUNDED, false);
			//			}
			
			String[] group_tracked = {"group_count", "group_payoff"};
			String[] group_header = {"num_agents", "min_agents_per_group", "max_agents_per_group"};
			String group_write_directory = "new_model_test_Group-";
			
			Group g = new Group(random_x, random_y, i);
			groups.add(g);
			schedule.scheduleRepeating(g);
			Reporter r = new Reporter(this, g, Group.class, Integer.toString(i), group_tracked, group_header, group_write_directory);
			//			sparseSpace.setObjectLocation(g, random_x, random_y);
		}
	}

	public void make_agents() {
		this.agents = new Bag();
		Beta tolerance_beta = new Beta(agent_tolerance_alpha, agent_tolerance_beta, random);
		
		Beta effort_beta = new Beta(agent_effort_alpha, agent_effort_beta, random);

		
		int i = 1;
		for(Object a:this.groups) {
			Group g = (Group) a;
			for(int j = 0; j < min_agents_per_group; j++) {
				System.out.println("F");
				int random_x = random.nextInt(5) + g.x;
				int random_y = random.nextInt(5) + g.y;

				//				Bag b = sparseSpace.getObjectsAtLocation(random_x, random_y);
				//				
				//				while(!(b == null)) {
				//					random_x = random.nextInt(gridWidth);
				//					random_y = random.nextInt(gridHeight);
				//					b = sparseSpace.getObjectsAtLocation(random_x, random_y);
				//				}

				//Agent(Environment state, float tolerance, float mean_value, float std_value, int x, int y, int id, int group_id) {
				System.out.println("G");
				double tolerance = tolerance_beta.nextDouble();
				System.out.println(tolerance); //TODO YOOOOOOOOOOOOOOOOOOOOOOOOO BETA IS BROKEN
				double mean_value = effort_beta.nextDouble();
				System.out.println("H");
				Agent agent = new Agent(this, tolerance, mean_value, agent_std_effort, random_x, random_y, i, g.group_id, this.prefix);
				agents.add(agent);
				g.add_agent(agent);
				
				String prefix = "new_model_test_Agent-";
				String write_directory = "Python/Agent_data/" + prefix;
				String[] tracked = {"group_count","group_id","id","num_standards_in_group","num_deviants_in_group","accumulated_payoff","mean_value","tolerance","num_groups"};
				String[] header = {"num_agents", "min_agents_per_group", "max_agents_per_group"};
				Reporter r = new Reporter(this, agent, Agent.class, Integer.toString(i), tracked, header, write_directory);
				i++;
				System.out.println(i);
				
				schedule.scheduleRepeating(agent, 1, 1.0);
				schedule.scheduleRepeating(r, 2, 1.0);
				//				sparseSpace.setObjectLocation(agent, random_x, random_y);
			}
		}
	}
	
	public void make_new_group(Agent a) {
		//Make a new group, and add the agent to it
		int random_x = random.nextInt(gridWidth);
		int random_y = random.nextInt(gridHeight);
		
		String[] group_tracked = {};
		String[] group_header = {};
		String group_write_directory = "new_model_test_Group-";
		
		int i = this.groups.numObjs + 1;
		Group g = new Group(random_x, random_y, i);
		groups.add(g);
		schedule.scheduleRepeating(g);
		Reporter r = new Reporter(this, g, Group.class, Integer.toString(i), group_tracked, group_header, group_write_directory);
		g.add_agent(a);
		a.group_id = i;
		this.num_groups ++;
		System.out.println("Made group");
	}


	public void start() {
		super.start();
		//		this.makeSpace(gridWidth, gridHeight);
		//System.out.println("Made space");
		make_groups();
		System.out.println("C");
		make_agents();
		System.out.println("D");
		// initialize the experimenter by calling initialize in the parent class
		Experimenter e = new Experimenter();
		e.event = schedule.scheduleRepeating(e, 3, 1.0);
	}

}
