package nz.net.thoms.hadoophash;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.Counters.Counter;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.JobTracker;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.RunningJob;
import org.eclipse.core.runtime.jobs.Job;

import com.twmacinta.util.MD5;

public class HashMapper extends MapReduceBase implements Mapper<WritableComparable, HashValue, Text, Text> {
	private JobConf job;
	private Counter foundCounter;
	@Override
	public void configure(JobConf job) {
		this.job = job;
		try {
			JobClient client = new JobClient(job);
			RunningJob parentJob;
			parentJob = client.getJob(JobID.forName( job.get("mapred.job.id") ));
			foundCounter = parentJob.getCounters().findCounter("Hashes", "Found");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private final HashExecutor hashExecutor = new HashcatHashExecutor();

	@Override
	public void map(WritableComparable key, HashValue value,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		if (foundCounter == null || foundCounter.getValue() == 0 || job.getBoolean("bench", false)) {
			String hash = value.getHash();
			String prefix = value.getPrefix();
			int length = value.getLength();
			System.out.println("Mapping key: " + key + " hash: " + hash + " prefix: " + prefix + " length: " + length);
			HashResult result = hashExecutor.executeHashes(hash, prefix, length);
			if (result.password != null) {
				System.out.println("Password is " + result.password);
				output.collect(new Text(hash), new Text(result.password));
				reporter.getCounter("Hashes", "Found").increment(1);
			}
			reporter.getCounter("Hashes", "Checked").increment(result.checked);
		}
	}


}
