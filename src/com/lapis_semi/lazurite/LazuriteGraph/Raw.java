package com.lapis_semi.lazurite.LazuriteGraph;

import com.lapis_semi.lazurite.io.Liblazurite;
import java.util.Formatter;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Queue;
import java.util.LinkedList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

public class Raw {
	private Queue<Byte> queue;
	private InputStream in;
	private int rxBufSize = 2048;
	private int interval;
	private RawEventListener listener;

	public MainThread th;
	// Liblazurite
	private Liblazurite lz = new Liblazurite();


	public Raw() throws IOException{
	}
	public void open(byte ch, short panid, byte baud, byte pwr, short txaddr) throws IOException {
		lz.init();
		lz.begin(ch,panid,baud,pwr);
		lz.link(txaddr);
		lz.rxEnable();
		queue = new LinkedList<Byte>();
		in = new RawInputStream(queue);
		th = new MainThread();
		th.start();
		//System.out.println("end of open");
	}

	public void close() throws IOException {
		th.Stop();
	}

	public void addEventListener(RawEventListener listener) {
		this.listener = listener;
		//System.out.println("addEventListener");
	}

	public void removeEventListener() {
		this.listener = null;
		//System.out.println("removeEventListener");
	}

	// Event from Raw
	public void DataAvailable(){
		//System.out.println("DataAvailable");
		if(this.listener!=null) {
			this.listener.RawEvent( new RawEventObject(RawEventObject.RAW_DATA_AVAILABLE));
		}
	}

	public java.io.InputStream getInputStream() {
		return in;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
	public void stop() 
	{
		//System.out.println("subghz.stop()");
		th.Stop();
	}

	public class RawInputStream extends InputStream {
		Queue<Byte> buffer;
		public RawInputStream(Queue<Byte> buf) {
			buffer = buf;
		}

		public int read() {
			Byte data;
			int retval;
			try {
				data = buffer.poll();
				retval = data.intValue() & 0xff;
			} catch (Exception e) {retval = -1;}
			return retval;
		}

		public int read(byte b[]) {
			return read(b,0,b.length);
		}

		public int read(byte b[], int off, int len) {
			int i;
			Byte data;
			String test = "";
			if(len > b.length) len = b.length - off;
			for(i = 0; i < len; i++) {
				try {
					data = buffer.poll();
					b[off+i] = data.byteValue();
				} catch (Exception e) {
					break;
				}
			}
			return i;
		}

		public int available() throws IOException {
			int size = buffer.size();
			return buffer.size();
		}
	}
	class MainThread extends Thread {
		private int interval;
		private boolean start;
		//private RawEventListener listener;
		public MainThread() {
			start = false;
		}
		public void setInterval(int interval)
		{
			this.interval = interval;
		}
		public void run() {
			boolean sent = false;
			start = true;
			//System.out.println("thread start");
			try {
				while(start) {
					short[] length = new short[1];
					byte[] data = new byte[256];
					do {
						lz.readLink(data,length);
						if(length[0] > 0) sent = true;
						else break;
						data[length[0]]=0;
						//System.out.println(new String(data,"UTF-8"));
						for(int i=0;i<length[0];i++) {
							queue.add(data[i]);
						}
					} while(length[0]>0);
					if(sent == true)
					{
						//System.out.println("receiving");
						sent = false;
						DataAvailable();
					}
					Thread.sleep(interval);
				}
			} catch(Exception e) {
				start = false;
			}
			//System.out.println("finalized process in thread");
			start = false;
			try {
				//System.out.println("raw.close from thread");
				lz.rxDisable();
			} catch (IOException e) {
				System.out.println("error rxDisable");
			}
			try {
				//System.out.println("raw.close from thread");
				lz.close();
			} catch (IOException e) {
				System.out.println("error close");
			}
			try {
				lz.remove();
			} catch (IOException e) {
				System.out.println("error remove");
			}
		}
/*		public void addEventListener(RawEventListener listener){
			this.listener = listener;
		}
		public void removeEventListener(RawEventListener listener){
			this.listener = null;
		}
		*/
		public void Stop() {
			this.start = false;
		}
	}
}

