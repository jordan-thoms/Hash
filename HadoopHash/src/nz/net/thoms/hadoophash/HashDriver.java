package nz.net.thoms.hadoophash;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.Reducer;

public class HashDriver {

	public static void main(String[] args) {
		JobClient client = new JobClient();
		JobConf conf = new JobConf(nz.net.thoms.hadoophash.HashDriver.class);

		// TODO: specify output types
		conf.setOutputKeyClass(String.class);
		conf.setOutputValueClass(String.class);

		// TODO: specify input and output DIRECTORIES (not files)
		conf.setInputFormat(HashInput.class);
		conf.setMapperClass(nz.net.thoms.hadoophash.HashMapper.class);
		
		conf.set("hash", "HASH");
		client.setConf(conf);
		try {
			JobClient.runJob(conf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
