package nz.net.thoms.hadoophash;

import java.io.IOException;
import java.util.ArrayList;

import nz.net.thoms.hash.shared.Util;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

public class HashInput implements InputFormat<WritableComparable, HashValue> {
	private static int POSTFIX_LENGTH = 4;

	@Override
	public RecordReader<WritableComparable, HashValue> getRecordReader(
			InputSplit arg0, JobConf arg1, Reporter arg2) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputSplit[] getSplits(JobConf job, int numSplits) throws IOException {
		int prefix_length = 5 - POSTFIX_LENGTH;
		if  (prefix_length < 0) {
			prefix_length = 0;
		}
		
		HashInputSplit[] splits = new HashInputSplit[numSplits];
		ArrayList<String> prefixes = Util.permute(Util.chars, prefix_length);
		int count = 0;
		int prefixesPerSplit = prefixes.size() / numSplits;
		if (!prefixes.isEmpty()) {
			for (int i = 0; i < numSplits -1; i++) {
				for (int j = 0; j < prefixesPerSplit; j++) {
					splits[count] = new HashInputSplit()
					count++;
				}
			}
		} else {
			Util.putEntity(hash, "", width);
		}
	}
}
