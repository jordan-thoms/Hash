package nz.net.thoms.hadoophash;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.mapred.InputSplit;

public class HashInputSplit implements InputSplit {
	private List<String> prefixes;
	private int length;
		
	@Override
	public void readFields(DataInput in) throws IOException {
		length = in.readInt();
		int count = in.readInt();
		prefixes = new ArrayList<String>(count);
		for (int i=0; i < count; i++) {
			prefixes.add(in.readUTF());
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(length);
		out.writeInt(prefixes.size());
		for (int i=0; i < prefixes.size(); i++) {
			out.writeUTF(prefixes.get(i));
		}
	}

	@Override
	public long getLength() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] getLocations() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(List<String> prefixes) {
		this.prefixes = prefixes;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
