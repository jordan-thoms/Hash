package nz.net.thoms.hadoophash;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class HashValue implements Writable {

	private String hash;
	private String prefix;
	private int length;

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(hash);
		out.writeChars(prefix);
		out.writeInt(length);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		hash = in.readUTF();
		prefix = in.readUTF();
		length = in.readInt();
	}


	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
