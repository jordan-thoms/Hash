package nz.net.thoms.hadoophash;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextOutputFormat;

public class HashDriver {

	public static void main(String[] args) {
		JobClient client = new JobClient();
		JobConf conf = new JobConf(nz.net.thoms.hadoophash.HashDriver.class);

		// TODO: specify output types
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);
		conf.setJarByClass(HashMapper.class);

		// TODO: specify input and output DIRECTORIES (not files)
		conf.setInputFormat(HashInput.class);
		conf.setMapperClass(nz.net.thoms.hadoophash.HashMapper.class);
		conf.setNumTasksToExecutePerJvm(1000);
		conf.setOutputFormat(TextOutputFormat.class);
		DateFormat df = new SimpleDateFormat("mm_hh");

		FileOutputFormat.setOutputPath(conf, new Path("output" + df.format(new Date())));
		conf.set("hash", "594f803b380a41396ed63dca39503542");
		conf.setInt("password_length", 5);
		conf.setInt("tasks", 300);

		client.setConf(conf);
		try {
			RunningJob job = JobClient.runJob(conf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
