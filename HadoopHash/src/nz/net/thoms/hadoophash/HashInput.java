package nz.net.thoms.hadoophash;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

public class HashInput implements InputFormat<WritableComparable, HashValue> {
	private static int POSTFIX_LENGTH = 4;
	private static int PASSWORD_LENGTH = 5;

	@Override
	public RecordReader<WritableComparable, HashValue> getRecordReader(
			InputSplit split, JobConf job, Reporter reporter) throws IOException {
		return new HashRecordReader(job.get("hash"), ((HashInputSplit) split).getPrefixes(), ((HashInputSplit) split).getPasswordLength());
	}

	@Override
	public InputSplit[] getSplits(JobConf job, int numSplits) throws IOException {
		int prefix_length = PASSWORD_LENGTH - POSTFIX_LENGTH;
		if  (prefix_length < 0) {
			prefix_length = 0;
		}
		
		ArrayList<HashInputSplit> splits = new ArrayList<HashInputSplit>(numSplits);
		ArrayList<String> prefixes = Util.permute(Util.chars, prefix_length);
		int prefixesPerSplit = prefixes.size() / numSplits;
		if (!prefixes.isEmpty()) {
			int lastPos = 0;
			for (int i = 0; i < numSplits -1; i++) {
				splits.add(HashInputSplit.newSplit(prefixes.subList(lastPos, lastPos + prefixesPerSplit), PASSWORD_LENGTH));
				lastPos = lastPos + prefixesPerSplit;
			}
			splits.add(HashInputSplit.newSplit(prefixes.subList(lastPos, prefixes.size()), PASSWORD_LENGTH));
		} else {
			splits.add(HashInputSplit.newSplit(prefixes, PASSWORD_LENGTH));
		}
		
		return splits.toArray(new InputSplit[splits.size()]);
	}
}
