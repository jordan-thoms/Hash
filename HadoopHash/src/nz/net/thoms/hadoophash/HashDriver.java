package nz.net.thoms.hadoophash;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextOutputFormat;

public class HashDriver {
	public static JobClient client = new JobClient();
	public static void main(String[] args) throws URISyntaxException {
		String hash =  args[0];
		int max_length = Integer.parseInt(args[1]);
		boolean bench = false;
		if (args.length == 3) {
			bench = true;
		}
		int start;
		if (!bench) {
			start = 1;
		} else {
			start = max_length;
		}
		for (int i= start; i<=max_length; i++) {
			System.out.println("Running for length " + i);
			String output = run(hash, i, bench);
			if (output != null) {
				System.out.println(output);
				break;
			}
		}

	}

	private static String run(String hash, int length, boolean bench) throws URISyntaxException {
		if (length < 4) {
			// As an optimization, just run it locally. (it takes about 20 seconds in overhead for the hadoop jobs).
			HashExecutor executor = new SimpleHashExecutor();
			HashResult result = executor.executeHashes(hash, "", length);
			if (result.password != null) {
				return hash + "   " + result.password;
			} else {
				return null;
			}
		}
		System.out.println("Setting up job");
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

		DistributedCache.addCacheArchive(new URI("/app/hashcat.zip#hashcat"), conf);
		DistributedCache.createSymlink(conf);
		DateFormat df = new SimpleDateFormat("mm_hh_ss");
		Path outputPath = new Path("/app/output" + df.format(new Date()));
		FileOutputFormat.setOutputPath(conf, outputPath);
		conf.set("hash",  hash);
		conf.setInt("password_length", length);
		conf.setInt("tasks", 12);
		if (bench) {
			conf.setBoolean("bench", true);
		}
		client.setConf(conf);
		System.out.println("Running job on hadoop");
		try {
			RunningJob job = JobClient.runJob(conf);
			if(job.getCounters().findCounter("Hashes", "Found").getValue() == 0) {
				System.out.println("Finished job, not found");
				return null;
			} else {
				System.out.println("Finished job, found");
				Configuration config = new Configuration();
				FileSystem hdfs = FileSystem.get(config);
				Path path = new Path(outputPath.toString() + "/part-00000");
				if(hdfs.exists(path)) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(hdfs.open(path)));
					System.out.println("Found file at " + path.toString());
					return reader.readLine();
				} else {
					System.out.println("Coult not find file");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
