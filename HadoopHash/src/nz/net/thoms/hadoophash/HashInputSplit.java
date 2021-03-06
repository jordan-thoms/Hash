package nz.net.thoms.hadoophash;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.mapred.InputSplit;

public class HashInputSplit implements InputSplit {
	private List<String> prefixes;
	private int passwordLength;
		
	
	public static HashInputSplit newSplit(List<String> _prefixes, int _length) {
		HashInputSplit split = new HashInputSplit();
		split.setPrefixes(_prefixes);
		split.setPasswordLength(_length);
		return split;
	}
	@Override
	public void readFields(DataInput in) throws IOException {
		passwordLength = in.readInt();
		int count = in.readInt();
		prefixes = new ArrayList<String>(count);
		for (int i=0; i < count; i++) {
			prefixes.add(in.readUTF());
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(passwordLength);
		out.writeInt(prefixes.size());
		for (int i=0; i < prefixes.size(); i++) {
			out.writeUTF(prefixes.get(i));
		}
	}

	@Override
	public long getLength() throws IOException {
		// As the hadoop definitive guide states, this is just used for ordering the splits so it doesn't have to be accuriate.
		return prefixes.size() * 4;
	}

	@Override
	public String[] getLocations() throws IOException {
        return new String[]{};
	}

	public List<String> getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(List<String> prefixes) {
		this.prefixes = prefixes;
	}

	public void setPasswordLength(int length) {
		this.passwordLength = length;
	}
	
	public int getPasswordLength() {
		return this.passwordLength;
	}

}
