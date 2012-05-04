package nz.net.thoms.hadoophash;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobID;
import org.apache.hadoop.mapred.JobTracker;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.twmacinta.util.MD5;

public class HashMapper extends MapReduceBase implements Mapper<WritableComparable, HashValue, Text, Text> {
	private JobConf job;
	@Override
	public void configure(JobConf job) {
		this.job = job;
	}
	
	private final HashExecutor hashExecutor = new HashcatHashExecutor();
	
	@Override
	public void map(WritableComparable key, HashValue value,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String hash = value.getHash();
		String prefix = value.getPrefix();
		int length = value.getLength();
		System.out.println("Mapping key: " + key + " hash: " + hash + " prefix: " + prefix + " length: " + length);
		HashResult result = hashExecutor.executeHashes(hash, prefix, length);
		if (result.password != null) {
			System.out.println("Password is " + result.password);
			output.collect(new Text(hash), new Text(result.password));
		}
        reporter.getCounter("Hashes", "Checked").increment(result.checked);
	}

//	JobID jobId = JobID.forName(job.get("xpatterns.hadoop.content.job.id"));
//	JobTracker tracker;
//	try {
//		tracker = JobTracker.startTracker(job);
//		tracker.killJob(jobId);
//		tracker.stopTracker();
//	} catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}

}
