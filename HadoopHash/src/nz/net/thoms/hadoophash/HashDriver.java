package nz.net.thoms.hadoophash;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.TextOutputFormat;

public class HashDriver {

	public static void main(String[] args) {
		JobClient client = new JobClient();
		JobConf conf = new JobConf(nz.net.thoms.hadoophash.HashDriver.class);

		// TODO: specify output types
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		// TODO: specify input and output DIRECTORIES (not files)
		conf.setInputFormat(HashInput.class);
		conf.setMapperClass(nz.net.thoms.hadoophash.HashMapper.class);
		
		conf.setOutputFormat(TextOutputFormat.class);
		DateFormat df = new SimpleDateFormat("mm_hh");

		FileOutputFormat.setOutputPath(conf, new Path("output" + df.format(new Date())));
		conf.set("hash", "1dba3a19a3a8d98dc26c4da74e11db70");
		client.setConf(conf);
		try {
			JobClient.runJob(conf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
