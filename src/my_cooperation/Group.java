package my_cooperation;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

public class Group implements Steppable {
	
	public Bag curr_agents = new Bag();
	public int group_id;
	public int x;
	public int y;
	
	public float group_payoff;

	public Group(int x, int y, int group_id) {
		super();
		this.x = x;
		this.y = y;
		this.group_id = group_id;
	}
	
	public void add_agent(Agent a) {
		this.curr_agents.add(a);
	}
	
	public void remove_agent(Agent a) {
		if (this.curr_agents.contains(a)) {
			this.curr_agents.remove(a);
		}
	}
	
	public void calc_group_payoff() {
		
		float sum = 0;
		
		if(!this.curr_agents.isEmpty()) {
			for (Object b: this.curr_agents) {
				Agent a  = (Agent) b;
				sum += a.contribute();
			}
		}
		this.group_payoff = sum;
	}
	
	@Override
	public void step(SimState state) {
		calc_group_payoff();
		
	}

}
