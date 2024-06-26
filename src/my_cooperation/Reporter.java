package my_cooperation;

import sim.engine.SimState;
import sim.engine.Steppable;

import java.io.BufferedWriter;
import java.io.File;
// Just Manually Writing stuff
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

public class Reporter implements Steppable {
	
	Object reported;
	Class reported_class;
	String unique_id;
	String[] tracked;
	
	// All the file stuff
	String file_prefix;
	String file_path;
	FileWriter write_to;
	File file;
	
	
	// Object reported - The reported object, typically an Agent, passed as "this"
	// Class reported_class - Class of the reported object, e.g. Agent
	// String unique_id - Some unique id the user can assign to the reporter 
	// String[] tracked - A string array of which variables you want to track, should have public get functions for all
	// String file_prefix - Where you want the file saved to relative to the project, as file_prefix + unique_id.txt
	
	public Reporter(Object reported, Class reported_class, String unique_id, String[] tracked, String file_prefix) {
		this.reported  = reported;
		this.reported_class = reported_class;
		this.unique_id = unique_id;
		this.tracked = tracked;
		this.file_prefix = file_prefix;
		create_file();
	}
	
	public void create_file() {
		try {
			this.file_path = this.file_prefix + this.unique_id +  ".txt";
			
			//check
			this.file = new File(this.file_path);
			
			if (!file.exists()) { // Check if the file doesn't exist
		        this.write_to = new FileWriter(this.file_path);
		        this.write_to.write(String.join(",", this.tracked) + '\n');
		        this.write_to.close();
		    } else {
		    	try(FileWriter fw = new FileWriter(this.file_path, true);
					    BufferedWriter bw = new BufferedWriter(fw);
					    PrintWriter out = new PrintWriter(bw))
					{
					//group_count,group_id,id,type,num_standards_in_group,num_deviants_in_group,accumulated_payoff
					    out.println(String.join(",", this.tracked));
					    out.close();
					} catch (IOException e) {
					    //exception handling left as an exercise for the reader
					}
		    }
		    } catch (IOException e) {
		    System.out.println("An error occurred.");
		    e.printStackTrace();
		    }
	}
	
	// pulling from syntax from Aviva's code but simpler just to get things to work - Timo
	public String get_result(String res) {
		String p = "";
		if(this.reported != null) {
			try {
				Field f = this.reported_class.getField(res);
				// things that don't handle this well will just have to be dealt with elsewhere
				p = String.valueOf(f.get(this.reported)); //Real simple like, simply don't pass your data as arrays - Timo
			} catch(NoSuchFieldException e) {
				// that's okay, this will just have to be dealt with in the subclass
			} catch(IllegalAccessException e) {
				// this is also hypothetically okay, but should tell the user
				System.out.println("Unable to access " + res + ".");
			}
		}
		return(p);
	}
	
	public void write_results() {
		String results = "";
		for(int i = 0; i<this.tracked.length; i++) {
			if(i==0) {
				results += get_result(this.tracked[i]);
			}
			else {
				results += "," + get_result(this.tracked[i]);
			}
		}
		
		
		try(FileWriter fw = new FileWriter(this.file_path, true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			//group_count,group_id,id,type,num_standards_in_group,num_deviants_in_group,accumulated_payoff
			    out.println(results);
			    out.close();
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
	}
	
	@Override
	// step, should be called last, one level before the Experimenter, I guess :shrug: - Timo
	public void step(SimState arg0) {
		write_results();
		
	}

}
