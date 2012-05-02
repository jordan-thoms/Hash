package nz.net.thoms.hadoophash;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.RecordReader;

public class HashRecordReader implements RecordReader<WritableComparable, HashValue> {
	private String hash;
	private List<String> prefixes;
	private int length;
	private int pos;
	
	public HashRecordReader(String hash, List<String> prefixes, int length) {
		this.hash = hash;
		this.prefixes = prefixes;
		this.length = length;
	}

	@Override
	public WritableComparable createKey() {
		return new LongWritable();
	}

	@Override
	public HashValue createValue() {
		return new HashValue();
	}

	@Override
	public long getPos() throws IOException {
		return pos;
	}

	@Override
	public float getProgress() throws IOException {
		return pos / prefixes.size();
	}

	@Override
	public boolean next(WritableComparable key, HashValue value)
			throws IOException {
		if (pos < prefixes.size()) {
			value.setHash(hash);
			value.setPrefix(prefixes.get(pos));
			value.setLength(length);
			pos++;
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
